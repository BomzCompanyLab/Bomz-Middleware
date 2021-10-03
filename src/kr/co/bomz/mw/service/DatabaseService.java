package kr.co.bomz.mw.service;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.bomz.mw.db.ControlResponse;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.DeviceMapper;
import kr.co.bomz.mw.db.Driver;
import kr.co.bomz.mw.db.DriverMapper;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.LogicalGroupMapper;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.db.ReporterMapper;
import kr.co.bomz.mw.db.SelectTerms;
import kr.co.bomz.mw.db.Setting;
import kr.co.bomz.mw.db.SettingMapper;
import kr.co.bomz.mw.db.Tag;
import kr.co.bomz.mw.db.TagList;
import kr.co.bomz.util.resource.ResourceBundle;

/**
 * 	�����ͺ��̽� ó�� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DatabaseService {

	private final Logger logger = LoggerFactory.getLogger("Database");
	
	private static final DatabaseService _this = new DatabaseService();
	
	private SqlSessionFactory sqlFactory;
	
	private DatabaseService(){
		try {
			this.initSqlFactory();				// ��� ���� ó��
		} catch (Exception e) {
			this.logger.error("�����ͺ��̽� �ʱ� ���� ����", e);
		}
	}
	
	public static final DatabaseService getInstance(){
		return _this;
	}
	
	/**		�����ͺ��̽� ���� �ʱ�ȭ		*/
	private void initSqlFactory() throws Exception{
		Reader reader = null;
		try{
			reader = Resources.getResourceAsReader("mw_sqlConfig.xml");
			this.sqlFactory = new SqlSessionFactoryBuilder().build(reader, this.getInitProperties());
		}finally{
			if( reader != null )		try{		reader.close();		}catch(Exception e){}
			reader = null;
		}
	}
	
	/**		�����ͺ��̽� ������ ���� ������ ��� ����		*/
	private Properties getInitProperties(){
		ResourceBundle resource = new ResourceBundle("conf", "jdbc.properties");
		
		Properties dbProperties = new Properties();
		dbProperties.put("driverClassName", resource.getResourceValue("driverClassName"));
		dbProperties.put("dbUrl", resource.getResourceValue("dbUrl"));
		dbProperties.put("dbId", resource.getResourceValue("dbId"));
		dbProperties.put("dbPw", resource.getResourceValue("dbPw"));
		
		return dbProperties;
	}
	
	/**	SQL ���� ����		*/
	public SqlSession openSession() throws Exception{
		if( this.sqlFactory == null ){			// �����ڿ��� �ʱ�ȭ�� �������� ��� ȣ��
			this.initSqlFactory();					// �ʱ�ȭ ���� �� ���� �߻�
		}
		
		return this.sqlFactory.openSession();
	}
	
	
	/**
	 * 	��ġ ��� ������� ����
	 * @param deviceId		��ġ ���̵�
	 * @param deviceConnectState		��ġ ���� ����. true �� ��� �����
	 */
	public void updateDeviceConnectState(int deviceId, boolean deviceConnectState){
		SqlSession session = null;
		
		try{
			session = this.openSession();
			
			DeviceMapper mapper = session.getMapper(DeviceMapper.class);
			
			Device device = new Device();
			device.setDeviceId(deviceId);
			device.setCommState( deviceConnectState ? Device.COMM_STATE_YES : Device.COMM_STATE_NO );
			
			mapper.updateDeviceConnectState(device);
			session.commit();
			
			if( this.logger.isInfoEnabled() )
				this.logger.info("��ġ ������� ��� ���� ���� [��ġ���̵�={}, ����={}]", deviceId, deviceConnectState ? Device.COMM_STATE_YES : Device.COMM_STATE_NO);
			
		}catch(Exception e){
			if( session != null )		session.rollback();
			
			this.logger.error("��ġ ������� ��� ���� ���� [��ġ���̵�={}, ����={}]", deviceId, deviceConnectState ? Device.COMM_STATE_YES : Device.COMM_STATE_NO);
		}finally{
			if( session != null )		session.close();
		}
		
	}
	
	/**
	 * 	������ ��� ������� ����
	 * @param deviceId		������ ���̵�
	 * @param deviceConnectState		������ ���� ����. true �� ��� �����
	 */
	public void updateReporterConnectState(int reporterId, boolean reporterConnectState){
		SqlSession session = null;
		
		try{
			session = this.openSession();
			
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			
			Reporter reporter = new Reporter();
			reporter.setReporterId(reporterId);
			reporter.setCommState( reporterConnectState ? Reporter.COMM_STATE_YES : Reporter.COMM_STATE_NO );
			
			mapper.updateReporterConnectState(reporter);
			session.commit();
			
			if( this.logger.isInfoEnabled() )
				this.logger.info("������ ������� ��� ���� ���� [�����;��̵�={}, ����={}]", reporterId, reporterConnectState ? Reporter.COMM_STATE_YES : Reporter.COMM_STATE_NO);
			
		}catch(Exception e){
			if( session != null )		session.rollback();
			
			this.logger.error("������ ������� ��� ���� ���� [�����;��̵�={}, ����={}]", reporterId, reporterConnectState ? Reporter.COMM_STATE_YES : Reporter.COMM_STATE_NO);
		}finally{
			if( session != null )		session.close();
		}
		
	}
	
	/**		��� ��ġ ���� �˻�		*/
	public List<Device> selectAllDeviceList() throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			return this.selectAllDeviceList(session);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		��� ��ġ ���� �˻�		*/
	public List<Device> selectAllDeviceList(SqlSession session) throws Exception{
		DeviceMapper mapper = session.getMapper(DeviceMapper.class);
		return mapper.selectAllDeviceList();
	}
	
	/**		��Ģ�� �´� ��ġ ���� �˻�		*/
	public List<Device> selectDeviceListToTerm(SelectTerms terms) throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			DeviceMapper mapper = session.getMapper(DeviceMapper.class);
			return mapper.selectDeviceListToTerm(terms);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		��ġ ���� ���		*/
	void addDevice(SqlSession session, Device device) throws Exception{
		try{
			DeviceMapper mapper = session.getMapper(DeviceMapper.class);
			mapper.addDevice(device);									// ��ġ ���� ���
			
			if( !device.getLogicalGroupMapList().isEmpty() )
				mapper.addDeviceLogicalGroupMap(device);		// ��ġ�� �����׷� ���� ���� ���
		}catch(Exception e){
			this.logger.error("��ġ ��� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_DEV") + " " +
					languageResourceBundle.getResourceValue("ERR_REG") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		��ġ ���� ����		*/
	void updDevice(SqlSession session, Device device) throws Exception{
		try{
			DeviceMapper mapper = session.getMapper(DeviceMapper.class);
			mapper.updDevice(device);									// ��ġ ���� ����
			mapper.delDeviceLogicalGroupMap(device);		// ���� ��ġ�� �����׷� ���� ���� ����
			
			if( !device.getLogicalGroupMapList().isEmpty() )
				mapper.addDeviceLogicalGroupMap(device);		// �ű� ��ġ�� �����׷� ���� ���� ���
		}catch(Exception e){
			this.logger.error("��ġ ���� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_DEV") + " " +
					languageResourceBundle.getResourceValue("ERR_UPD") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		��ġ ���� ����		*/
	void delDevice(SqlSession session, int deviceId) throws Exception{
		try{
			DeviceMapper mapper = session.getMapper(DeviceMapper.class);
			
			Device device = new Device();
			device.setDeviceId(deviceId);
			
			mapper.delDeviceLogicalGroupMap(device);	// ���� ��ġ�� �����׷� ���� ���� ����
			mapper.delDevice(device);								// ��ġ ���� ����
		}catch(Exception e){
			this.logger.error("��ġ ���� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_DEV") + " " +
					languageResourceBundle.getResourceValue("ERR_DEL") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
		
	}
	
	/**		Ư�� ��ġ ���� �˻�		*/
	public Device selectDeviceInfo(int deviceId) throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			DeviceMapper mapper = session.getMapper(DeviceMapper.class);
			
			// �⺻���� �˻�
			Device device = mapper.selectDeviceInfo(deviceId);
			// ��ġ�� ����� �����׷� ��� �˻�
			device.setLogicalGroupMapList(mapper.selectDeviceLogicalGroupMap(device));
			
			return device;
		}catch(Exception e){
			this.logger.error("Ư����ġ ���� �˻� ���� [��ġ���̵�={}]", deviceId, e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_DEV") + " " +
					languageResourceBundle.getResourceValue("ERR_SEL") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		�ش� ��ġ�� ����̹� ���� �˻�		*/
	public Driver selectDeviceDriver(SqlSession session, Device device) throws Exception{
		DriverMapper mapper = session.getMapper(DriverMapper.class);
		return mapper.selectDeviceDriverJar(device);
	}
	
	/**		�ش� ��ġ ����̹� ���� �˻�		*/
	public Driver selectDeviceDriverInfo(int driverId){
		SqlSession session = null;
		
		try{
			session = this.openSession();
			
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			return mapper.selectDeviceDriverInfo(driverId);
		}catch(Exception e){
			this.logger.error("Ư�� ��ġ����̹� ���� �˻� ���� [����̹����̵�={}]", driverId, e);
			return null;
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		�ش� ������ ����̹� ���� �˻�		*/
	public Driver selectReporterDriverInfo(int driverId){
		SqlSession session = null;
		
		try{
			session = this.openSession();
			
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			return mapper.selectReporterDriverInfo(driverId);
		}catch(Exception e){
			this.logger.error("Ư�� �����͵���̹� ���� �˻� ���� [����̹����̵�={}]", driverId, e);
			return null;
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		��� �����׷� �� �����׷� ���� ��ġ/������ ���� �˻�		*/
	public List<LogicalGroup> selectAllLogicalGroupDetailList(SqlSession session) throws Exception{
		LogicalGroupMapper mapper = session.getMapper(LogicalGroupMapper.class);
		
		// �⺻���� �˻�
		List<LogicalGroup> list = mapper.selectAllLogicalGroupList();
		
		if( list == null || list.isEmpty() )		return list;
		
		for(LogicalGroup lg : list){
			lg.setLogicalGroupDeviceMapList( mapper.selectLogicalGroupDeviceMapList(lg) );		// ��ġ���� �������� �߰�
			lg.setLogicalGroupReporterMapList( mapper.selectLogicalGroupReporterMapList(lg) );	// �����Ϳ��� �������� �߰�
		}
		
		return list;
	}
	
	/**		��Ģ�� �´� ��ġ ���� �˻�		*/
	public List<LogicalGroup> selectLogicalGroupListToTerm(SelectTerms terms) throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			LogicalGroupMapper mapper = session.getMapper(LogicalGroupMapper.class);
			return mapper.selectLogicalGroupListToTerm(terms);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	
	/**		�����׷� ���� ���		*/
	void addLogicalGroup(SqlSession session, LogicalGroup logicalGroup) throws Exception{
		try{
			LogicalGroupMapper mapper = session.getMapper(LogicalGroupMapper.class);
			
			mapper.addLogicalGroup(logicalGroup);							// �����׷� ���� ���
			
			if( !logicalGroup.getLogicalGroupDeviceMapList().isEmpty() )
				mapper.addLogicalGroupDeviceMap(logicalGroup);		// �����׷�� ��ġ ���� ���� ���
			
			if( !logicalGroup.getLogicalGroupReporterMapList().isEmpty() )
				mapper.addLogicalGroupReporterMap(logicalGroup);	// �����׷�� ������ ���� ���� ���
		}catch(Exception e){
			this.logger.error("�����׷� ��� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_LOGI") + " " +
					languageResourceBundle.getResourceValue("ERR_REG") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		�����׷� ���� ����		*/
	void updLogicalGroup(SqlSession session, LogicalGroup logicalGroup) throws Exception{
		try{
			LogicalGroupMapper mapper = session.getMapper(LogicalGroupMapper.class);
			
			mapper.updLogicalGroup(logicalGroup);							// �����׷� ���� ���
			
			mapper.delLogicalGroupDeviceMap(logicalGroup);			// ���� �����׷�� ��ġ ���� ���� ����
			if( !logicalGroup.getLogicalGroupDeviceMapList().isEmpty() )
				mapper.addLogicalGroupDeviceMap(logicalGroup);		// �ű� �����׷�� ��ġ ���� ���� ���
			
			mapper.delLogicalGroupReporterMap(logicalGroup);		// ���� �����׷�� ������ ���� ���� ����
			if( !logicalGroup.getLogicalGroupReporterMapList().isEmpty() )
				mapper.addLogicalGroupReporterMap(logicalGroup);	// �ű� �����׷�� ������ ���� ���� ���
		}catch(Exception e){
			this.logger.error("�����׷� ���� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_LOGI") + " " +
					languageResourceBundle.getResourceValue("ERR_UPD") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		�����׷� ���� ����		*/
	void delLogicalGroup(SqlSession session, int logicalGroupId) throws Exception{
		try{
			LogicalGroupMapper mapper = session.getMapper(LogicalGroupMapper.class);
	
			LogicalGroup logicalGroup = new LogicalGroup();
			logicalGroup.setLogicalGroupId(logicalGroupId);
			
			mapper.delLogicalGroupDeviceMap(logicalGroup);			// �����׷�� ��ġ ���� ���� ����
			mapper.delLogicalGroupReporterMap(logicalGroup);		// �����׷�� ������ ���� ���� ����
			mapper.delLogicalGroup(logicalGroup);							// �����׷� ���� ���
		}catch(Exception e){
			this.logger.error("�����׷� ���� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_LOGI") + " " +
					languageResourceBundle.getResourceValue("ERR_DEL") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		Ư�� �����׷� ���� �˻�		*/
	public LogicalGroup selectLogicalGroupInfo(int logicalGroupId) throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			LogicalGroupMapper mapper = session.getMapper(LogicalGroupMapper.class);
			
			// �⺻���� �˻�
			LogicalGroup lg = mapper.selectLogicalGroupInfo(logicalGroupId);
			// �����׷쿡 ����� ��ġ ���׳� ��� �˻�
			lg.setLogicalGroupDeviceMapList(mapper.selectLogicalGroupDeviceMapList(lg));
			// �����׷쿡 ����� ������ ��� �˻�
			lg.setLogicalGroupReporterMapList(mapper.selectLogicalGroupReporterMapList(lg));
			
			return lg;
		}catch(Exception e){
			this.logger.error("�����׷� �� ���� �˻� ���� [�����׷���̵�={}]", logicalGroupId, e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_LOGI") + " " +
					languageResourceBundle.getResourceValue("ERR_SEL") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		��� ������ ���� �˻�		*/
	public List<Reporter> selectAllReporterList() throws Exception{
		SqlSession session = null;
		
		try{
			session = this.openSession();
			return this.selectAllReporterList(session);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		��� ������ ���� �˻�		*/
	public List<Reporter> selectAllReporterList(SqlSession session) throws Exception{
		ReporterMapper mapper = session.getMapper(ReporterMapper.class);
		
		return mapper.selectAllReporterList();
	}
	
	/**		��Ģ�� �´� ������ ���� �˻�		*/
	public List<Reporter> selectReporterListToTerm(SelectTerms terms) throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			return mapper.selectReporterListToTerm(terms);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		������ ���� ���		*/
	void addReporter(SqlSession session, Reporter reporter) throws Exception{
		try{
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			
			mapper.addReporter(reporter);								// ������ ���� ���
			if( !reporter.getLogicalGroupMapList().isEmpty() )
				mapper.addReporterLogicalGroupMap(reporter);	// �����Ϳ� �����׷� ���� ���� ���
		}catch(Exception e){
			this.logger.error("������ ��� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_REP") + " " +
					languageResourceBundle.getResourceValue("ERR_REG") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		������ ���� ����		*/
	void updReporter(SqlSession session, Reporter reporter) throws Exception{
		try{
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			
			mapper.updReporter(reporter);								// ������ ���� ���
			mapper.delReporterLogicalGroupMap(reporter);		// ���� �����Ϳ� �����׷� ���� ���� ���
			if( !reporter.getLogicalGroupMapList().isEmpty() )
				mapper.addReporterLogicalGroupMap(reporter);	// �ű� �����Ϳ� �����׷� ���� ���� ���
		}catch(Exception e){
			this.logger.error("������ ���� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_REP") + " " +
					languageResourceBundle.getResourceValue("ERR_UPD") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		������ ���� ����		*/
	void delReporter(SqlSession session, int reporterId) throws Exception{
		try{
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			
			Reporter reporter = new Reporter();
			reporter.setReporterId(reporterId);
			
			mapper.delReporterLogicalGroupMap(reporter);		// ���� �����Ϳ� �����׷� ���� ���� ���
			mapper.delReporter(reporter);									// ������ ���� ���
		}catch(Exception e){
			this.logger.error("������ ���� ����", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_REP") + " " +
					languageResourceBundle.getResourceValue("ERR_DEL") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		Ư�� ������ ���� �˻�		*/
	public Reporter selectReporterInfo(int reporterId) throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			
			// �⺻���� �˻�
			Reporter reporter = mapper.selectReporterInfo(reporterId);
			// ��ġ�� ����� �����׷� ��� �˻�
			reporter.setLogicalGroupMapList(mapper.selectReporterLogicalGroupMap(reporter));
			
			return reporter;
		}catch(Exception e){
			this.logger.error("Ư�������� ���� �˻� ���� [�����;��̵�={}]", reporterId, e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_REP") + " " +
					languageResourceBundle.getResourceValue("ERR_SEL") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}finally{
			if( session != null )		session.close();
		}
	}

	/**		�ش� �������� ����̹� ���� �˻�		*/
	public Driver selectReporterDriver(SqlSession session, Reporter reporter) throws Exception{
		DriverMapper mapper = session.getMapper(DriverMapper.class);
		return mapper.selectReporterDriverJar(reporter);
	}
	
	/**		��� ��ġ ����̹� ��� �˻�		*/
	public List<Driver> selectDeviceDriverAllList() throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			
			return mapper.selectDeviceDriverAllList();
		}finally{
			if( session != null )		session.close();
		}
		
	}
	
	/**		��� ������ ����̹� ��� �˻�		*/
	public List<Driver> selectReporterDriverAllList() throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			
			return mapper.selectReporterDriverAllList();
		}finally{
			if( session != null )		session.close();
		}
		
	}
	
	/**		��ϵ� ��ü �����׷� ��� �˻�		*/
	public List<LogicalGroup> selectAllLogicalGroupList() throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			LogicalGroupMapper mapper = session.getMapper(LogicalGroupMapper.class);
			
			return mapper.selectAllLogicalGroupList();
		}finally{
			if( session != null )		session.close();
		}
		
	}
	
	/**		����̹� ���� ����		*/
	void delDriver(SqlSession session, Driver driver) throws Exception{
		try{
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			mapper.delDriver(driver);							// ����̹� ���� ����
			
		}catch(Exception e){
			this.logger.error("{} ����̹� ���� ����", driver.getDriverTarget() == ControlResponse.TARGET_DEVICE_DRIVER?"��ġ":"������", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_DR") + " " +
					languageResourceBundle.getResourceValue("ERR_DEL") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		��Ģ�� �´� ��ġ ���� �˻�		*/
	public List<Driver> selectDriverListToTerm(SelectTerms terms) throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			return mapper.selectDriverListToTerm(terms);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		��Ģ�� �´� ����̹� ���� �˻�		*/
	public Driver selectDriverInfo(Driver driver) throws Exception{
		SqlSession session = null;
		try{
			session = this.openSession();
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			return mapper.selectDriverInfo(driver);
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		����̹� ���� ���		*/
	void addDriver(SqlSession session, Driver driver) throws Exception{
		try{
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			mapper.addDriver(driver);									// ����̹� ���� ���
		}catch(Exception e){
			this.logger.error("{} ����̹� ��� ����", driver.getDriverTarget() == ControlResponse.TARGET_DEVICE_DRIVER?"��ġ":"������", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_DR") + " " +
					languageResourceBundle.getResourceValue("ERR_REG") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**		����̹� ���� ���		*/
	void updDriver(SqlSession session, Driver driver) throws Exception{
		try{
			DriverMapper mapper = session.getMapper(DriverMapper.class);
			mapper.updDriver(driver);									// ����̹� ���� ����
		}catch(Exception e){
			this.logger.error("{} ����̹� ���� ����", driver.getDriverTarget() == ControlResponse.TARGET_DEVICE_DRIVER?"��ġ":"������", e);
			ResourceBundle languageResourceBundle = new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_db.conf");
			throw new Exception(
					languageResourceBundle.getResourceValue("NM_DR") + " " +
					languageResourceBundle.getResourceValue("ERR_UPD") + " " +
					languageResourceBundle.getResourceValue("ERR_DB")
				);
		}
	}
	
	/**	��ϵ� ��� ���� ���� �˻�		*/
	List<Setting> selectSettingInfoList(SqlSession session) throws Exception{
		SettingMapper mapper = session.getMapper(SettingMapper.class);
		return mapper.selectSettingInfoList();
	}
	
	/**	���� ���� ��� �Ǵ� ����		*/
	public void mergeSetting(SqlSession session, Setting st) throws Exception{
		SettingMapper mapper = session.getMapper(SettingMapper.class);
		mapper.mergeSetting(st);
	}
	
	/**	������ ���۽��� ������ �α� ����		*/
	public void insertReporterWriteFailTagLog(TagList tagList) throws Exception{
		if( tagList == null || tagList.getTagList() == null || tagList.getTagList().isEmpty() )		return;
		
		SqlSession session = null;
		try{
			session = this.openSession();
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			
			final int listMaxSize = 50;
			// �ѹ��� �뷮�� ������ ���� �� ���������� �߻��ϹǷ� �ѹ��� �ִ� ����ŭ ����
			if( tagList.getTagList().size() <= listMaxSize ){
				mapper.insertReporterWriteFailTagLog(tagList);
			}else{
				// �±װ� 50���� �Ѵ´ٸ� 50���� ������ ����
				this.insertReporterWriteFailTagLog(mapper, listMaxSize, tagList);
			}
			
			session.commit();
		}catch(Exception e){
			if( session != null )		session.rollback();
			throw e;
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		������ ���� ���� �±׸� ���� �� ��ŭ ������ ����		*/
	private void insertReporterWriteFailTagLog(ReporterMapper mapper, int listMaxSize, TagList tagList) throws Exception{
		List<Tag> list = tagList.getTagList();
		List<Tag> tmpList = new ArrayList<Tag>(listMaxSize);
		tagList.setTagList(tmpList);
		int count = 0;
		
		for(Tag tag : list){
			tmpList.add(tag);
			
			if( ++count >= listMaxSize ){
				// �ִ����ŭ ����� �±׵����� ��� �߰�
				mapper.insertReporterWriteFailTagLog(tagList);
				tmpList.clear();
				count = 0;
			}
		}

		// ������ ������ ��� �߰�
		if( !tmpList.isEmpty() )		mapper.insertReporterWriteFailTagLog(tagList);		
	}
	
	/**		Ư�� �������� ���۽��� �α׸� ��� �����Ѵ�		*/
	public void clearReporterWriteFailTagLog(int reporterId){
		SqlSession session = null;
		try{
			session = this.openSession();
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			mapper.clearReporterWriteFailTagLog(reporterId);
			session.commit();
		}catch(Exception e){
			if( session != null )		session.rollback();
		}finally{
			if( session != null )		session.close();
		}
	}
	
}
