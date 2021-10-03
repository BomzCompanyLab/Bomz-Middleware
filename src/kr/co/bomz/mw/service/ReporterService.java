package kr.co.bomz.mw.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import kr.co.bomz.mw.comm.AbstractReporter;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.db.ReporterMapper;
import kr.co.bomz.mw.driver.DriverLoader;
import kr.co.bomz.mw.util.DeviceOrReporterExitUtil;
import kr.co.bomz.util.resource.ResourceBundle;

/**
 * 	������ ���� ����
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class ReporterService {

	private static final ReporterService _this = new ReporterService();
	
	/**		
	 * 		������ ���� ��
	 * 	Key	:	������ ���̵�
	 */
	private final Map<Integer, Reporter> reporterMap = new HashMap<Integer, Reporter>();
	
	private ReporterService(){}
	
	public static final ReporterService getInstance(){
		return _this;
	}
	
	public Reporter[] getReporterAll(){
		return this.reporterMap.values().toArray(new Reporter[this.reporterMap.size()]);
	}
	
	/**		������ ���� �˻�		*/
	public Reporter getReporter(int reporterId){
		return this.reporterMap.get(reporterId);
	}
	
	/**		��ϵ� ������ ���̵����� �˻�. true �� ��� ��ϵ� �����;��̵�		*/
	public boolean validationReporterId(int reporterId){
		return this.reporterMap.containsKey(reporterId);
	}
	
	public void initReporter(SqlSession session, List<Reporter> reporterList) throws Exception{
		if( reporterList == null )		return;
		
		ReporterMapper mapper = session.getMapper(ReporterMapper.class);
		
		for(Reporter reporter : reporterList){
			reporter.setLogicalGroupMapList(mapper.selectReporterLogicalGroupMap(reporter));
			this.addReporter(reporter, DatabaseService.getInstance().selectReporterDriver(session, reporter).getDriverJarFile(), true);
		}
		
	}
	
	/**
	 * 	������ ���
	 * @param reporter			������ ����
	 * @param autoReporterStart	������ ���� �� �ڵ� ���� ����. true �� ��� �ڵ� ����
	 * @throws Exception		������ ����̹� ���� ���� �Ǵ� �߸��� �����׷� ���� ��û�� ��� �߻�
	 */
	void addReporter(Reporter reporter, byte[] driverFile, boolean autoReporterStart) throws Exception{
		if( this.reporterMap.containsKey(reporter.getReporterId()) )		// �̹� ��ϵ� ������ ���̵�� ��� ��û [ID:XX]
			throw new Exception(this.makeResourceBundle().getResourceValue("ERR_1") + " [ID:" + reporter.getReporterId() + "]");
		
		reporter.setReporter( this.newInstanceReporter(reporter, driverFile) );		// ������ ����ü ����
		this.reporterMap.put(reporter.getReporterId(), reporter);			// ������ ������ �̻��� �����Ƿ� �޸� ���
		
		if( autoReporterStart )		reporter.getReporter().reporterStart();		// ������ ����
	}
	
	/**		������ ����̹� ���� ����		*/
	private AbstractReporter newInstanceReporter(Reporter reporter, byte[] driverFile) throws Exception{
		DriverLoader driverLoader = new DriverLoader();
		try{
			AbstractReporter result = driverLoader.newInstanceIReporter(reporter.getDriverId(), driverFile);
			result.setReporterInfo(reporter.getReporterId(), reporter.getReporterName());
			
			int timerRepeatTime = reporter.getTimerRepeatTime();
			result.setParameters(reporter.getReporterIp(), reporter.getReporterPort(), timerRepeatTime > 0, timerRepeatTime, reporter.getReporterParam());
			
			return result;
		}catch(Throwable e){
			throw new Exception(e.getMessage(), e);
		}
	}
	
	/**
	 * 	������ ����
	 * @param reporter		���ο� ������ ����
	 * @throws Exception	��ϵ��� ���� ������ ���̵�, �߸��� �����׷� ���̵� �Ǵ� �߸��� ������ ����̹� ���̵��� ��� �߻�
	 */
	void updReporter(Reporter reporter, byte[] driverFile) throws Exception{
		Reporter oReporter = this.reporterMap.remove(reporter.getReporterId());
		if( oReporter == null )		throw new Exception(this.makeResourceBundle().getResourceValue("ERR_2") + " [ID:" + reporter.getReporterId() + "]");
		
		try{
			this.addReporter(reporter, driverFile, false);			// �ű� ������ ���
		}catch(Exception e){
			// ��� ���� �� ���� ���� �ٽ� ���
			this.reporterMap.put(reporter.getReporterId(), oReporter);
			throw e;
		}
		
		oReporter.getReporter().exit();				// ���� ������ ����
		reporter.getReporter().reporterStart();		// �ű� ������ ����
	}
	
	/**
	 * 	������ ����
	 * @param reporterId		������ ������ ���̵�
	 * @throws Exception		��ϵ��� ���� ������ ���̵��� ��� �߻�
	 */
	void delReporter(int reporterId) throws Exception{
		Reporter reporter = this.reporterMap.remove(reporterId);
		
		// ��ϵ��� ���� ������ ���̵�� ������ ���� ��û [ID:XX]
		if( reporter == null )		throw new Exception(this.makeResourceBundle().getResourceValue("ERR_3") + " [ID:" + reporterId + "]");
	
		reporter.getReporter().exit();
	}
	
	/**		����̹��� ����Ǿ��� ��� �ش� ����̹��� ����ϴ� ��ġ ���� �� �ٽ� ����		*/
	void reloadDriverUpdate(int driverId, byte[] driverFile) throws Exception{
		List<Integer> exitCheckList = Collections.synchronizedList(new ArrayList<Integer>()); 
		List<DeviceOrReporterExitUtil> devList = new ArrayList<DeviceOrReporterExitUtil>();
		
		AbstractReporter beforeReporter, afterReporter;
		for(Reporter reporter : this.reporterMap.values() ){
			if( reporter.getDriverId() != driverId )		continue;		// �ٸ� ����̹��� ����� ��� ���ٸ� ó�� ����
			
			beforeReporter = reporter.getReporter();							// ���� ����̹� ����
			afterReporter = this.newInstanceReporter(reporter, driverFile);	// �ű� ����̹� ����
			
			afterReporter.setReporterQueue( beforeReporter.getReporterQueue() );		// ť ����
			
			reporter.setReporter(afterReporter);		// �ű� �ε� ���Ϸ� ����
			
			devList.add(new DeviceOrReporterExitUtil(exitCheckList, beforeReporter, afterReporter));	// exit() ȣ��ó���� ������� ���� 
		}
		
		for(DeviceOrReporterExitUtil util : devList)		util.exitCheckStart();		// ������� exit() ȣ��	
		
		// ��� exit() ȣ�� ó���� ���������� ���
		int size = devList.size();
		while( exitCheckList.size() < size )	try{		Thread.sleep(300);	}catch(Exception e){}
	}
	
	/**		�ٱ��� ���� ǥ�ÿ� ���ҽ����� ����		*/
	private ResourceBundle makeResourceBundle(){
		return new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_rep.conf");
	}
}
