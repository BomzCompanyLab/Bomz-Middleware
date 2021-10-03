package kr.co.bomz.mw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.DeviceLogicalGroupMap;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.LogicalGroupDeviceMap;
import kr.co.bomz.mw.db.LogicalGroupReporterMap;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.db.ReporterLogicalGroupMap;
import kr.co.bomz.util.resource.ResourceBundle;

/**
 * 	�����׷� ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class LogicalGroupService{

	private static final LogicalGroupService _this = new LogicalGroupService();
	
	/**	
	 * 		�����׷� ��
	 * 	Key		:	�����׷� ���̵�
	 * 	Value	:	�����׷� ����
	 */
	private final Map<Integer, LogicalGroup> logicalGroupMap = new HashMap<Integer, LogicalGroup>();
	
	private LogicalGroupService(){}
	
	public static final LogicalGroupService getInstance(){
		return _this;
	}
	
	/**		�����׷� �� ��ġ/������ ���� ���� �ʱ�ȭ		*/
	public void initLogicalGroup(List<LogicalGroup> logicalGroupList) throws Exception{
		if( logicalGroupList == null )		return;
		
		for(LogicalGroup lg : logicalGroupList)		this.addLogicalGroup(lg, false);
	}
	
	/**
	 * 	�����׷� ���
	 * 
	 * @param logicalGroup			�����׷� ����
	 * @throws Exception				�̹� ��ϵ� �����׷� ���̵��� ��� �߻�
	 */
	void addLogicalGroup(LogicalGroup logicalGroup, boolean callControllerService) throws Exception{
		int logicalGroupId = logicalGroup.getLogicalGroupId();
		if( this.logicalGroupMap.containsKey(logicalGroupId) )	// �̹� ��ϵ� �����׷� ���̵�� ��� ��û [ID:XX]
			throw new Exception(this.makeResourceBundle().getResourceValue("ERR_1") + " [ID:" + logicalGroupId + "]");
		
		this.logicalGroupMap.put(logicalGroupId, logicalGroup);
		
		if( callControllerService ){		// ��Ʈ�ѷ����񽺿��� ȣ������ ��츸 ��ġ �� �������� �����׷� ���� �� ���� ����
			// ��ġ�� �����׷� �������� �߰�
			this.parsingAddLogicalGroupDeviceMap(logicalGroup);
			
			// �����Ϳ� �����׷� �������� �߰�
			this.parsingAddLogicalGroupReporterMap(logicalGroup);
		}
	}
	
	/**		�����׷� ��� �� ��ġ���׳������ ���� �м�/ó��		*/
	private void parsingAddLogicalGroupDeviceMap(LogicalGroup logicalGroup){
		int logicalGroupId = logicalGroup.getLogicalGroupId();
		
		// ���������� ���� ������ �ߺ� ������ ������ Map ����
		Map<Integer, LogicalGroupDeviceMap> deviceAntennaMap = this.toLogicalGroupDeviceAntennaMap(logicalGroup.getLogicalGroupDeviceMapList()); 
		
		// ��ġ ���׳� ���� �߰�
		this.parsingNewLogicalGroupDeviceMap(logicalGroupId, deviceAntennaMap);
	}
	
	/**		�����׷� ��� �� �����Ϳ���� ���� �м�/ó��		*/
	private void parsingAddLogicalGroupReporterMap(LogicalGroup logicalGroup){
		
		Set<Integer> reporterIdSet = this.toLogicalGroupReporterIdSet(logicalGroup.getLogicalGroupReporterMapList());
		
		for(Reporter reporter : ReporterService.getInstance().getReporterAll()){
			if( !reporterIdSet.contains(reporter.getReporterId()) )		continue;
			
			// ����ϴ� �������� ��� �����׷� ���� �߰�
			this.mergeLogicalGroupReporterMap(reporter, null, logicalGroup);
		}
		
	}
	
	/**
	 * 	�����׷� ����
	 * 
	 * @param logicalGroupId				�����׷� ���̵�
	 * @param beforeLogicalGroup		���� �����׷� ����
	 * @param afterLogicalGroup			�ű� �����׷� ����
	 * @throws Exception						��ϵ��� ���� �����׷� ���̵��� ��� �߻�
	 */
	void updLogicalGroup(LogicalGroup logicalGroup) throws Exception{
		LogicalGroup oLogicalGroup = this.logicalGroupMap.remove(logicalGroup.getLogicalGroupId());
		
		// ��ϵ��� ���� �����׷� ���̵�� ���� ��û [ID:XX]
		if( oLogicalGroup == null )		throw new Exception(this.makeResourceBundle().getResourceValue("ERR_5") + " [ID=" + logicalGroup.getLogicalGroupId() + "]");
		
		try{
			this.addLogicalGroup(logicalGroup, false);		// �ű� �����׷� ���
		}catch(Exception e){
			this.logicalGroupMap.put(logicalGroup.getLogicalGroupId(), oLogicalGroup);
			throw e;
		}
		
		System.out.println("================ start 11 =============");
		for(Reporter rep : ReporterService.getInstance().getReporterAll() ){
			System.out.print("�̸�::" + rep.getReporterName() + "  >>  ");
			if( !rep.isNullLogicalGroupMapList() ){
				for(ReporterLogicalGroupMap map : rep.getLogicalGroupMapList() ){
					System.out.print(map.getLogicalGroupId() + "(" + map.getLogicalGroupName() + ") , ");
				}
			}
			System.out.println();
		}
		System.out.println("================ end 11 =============");
		
		// ���� ��ġ�� �����׷� ���� ����
		this.parsingUpdLogicalGroupDeviceMap(oLogicalGroup, logicalGroup);
		
		// ť ���񽺿��� �������� ť ������ �����׷�� ����
		QueueManagerService.getInstance().updateLogicalGroupName(logicalGroup.getLogicalGroupId(), logicalGroup.getLogicalGroupName());
		
		// ���� �������� �����׷� ���� ����
		this.parsingUpdLogicalGroupReporterMap(
				logicalGroup, 
				this.toLogicalGroupReporterIdSet(oLogicalGroup.getLogicalGroupReporterMapList()), 
				this.toLogicalGroupReporterIdSet(logicalGroup.getLogicalGroupReporterMapList())
			);
		
		System.out.println("================ start 22 =============");
		for(Reporter rep : ReporterService.getInstance().getReporterAll() ){
			System.out.print("�̸�::" + rep.getReporterName() + "  >>  ");
			if( !rep.isNullLogicalGroupMapList() ){
				for(ReporterLogicalGroupMap map : rep.getLogicalGroupMapList() ){
					System.out.print(map.getLogicalGroupId() + "(" + map.getLogicalGroupName() + ") , ");
				}
			}
			System.out.println();
		}
		System.out.println("================ end 22 =============");
	}
	
	
	/**		�����׷��� �����Ϳ���� ������ �����;��̵� Ű �������Ͽ� Set ����		*/
	private Set<Integer> toLogicalGroupReporterIdSet(List<LogicalGroupReporterMap> logicalGroupReporterMap){
		if( logicalGroupReporterMap == null )		return new HashSet<Integer>(0);
		
		Set<Integer> reporterIdSet = new HashSet<Integer>(logicalGroupReporterMap.size());
		for(LogicalGroupReporterMap lgrMap : logicalGroupReporterMap)
			reporterIdSet.add(lgrMap.getReporterId());
		
		return reporterIdSet;
	}
	
	private Map<Integer, LogicalGroupDeviceMap> toLogicalGroupDeviceAntennaMap(List<LogicalGroupDeviceMap> list){
		if( list == null )		new HashMap<Integer, LogicalGroupDeviceMap>(0);
		
		Map<Integer, LogicalGroupDeviceMap> map = new HashMap<Integer, LogicalGroupDeviceMap>(list.size());
		for(LogicalGroupDeviceMap lgdMap : list)	map.put(lgdMap.getItemId(), lgdMap);
		
		return map;
	}

	private Map<Integer, LogicalGroupDeviceMap> toLogicalGroupDeviceAntennaMap(Map<Integer, LogicalGroupDeviceMap> beforeLogicalGroupDeviceMap, List<LogicalGroupDeviceMap> list){
		if( list == null )		new HashMap<Integer, LogicalGroupDeviceMap>(0);
		
		Map<Integer, LogicalGroupDeviceMap> map = new HashMap<Integer, LogicalGroupDeviceMap>(list.size());
		for(LogicalGroupDeviceMap lgdMap : list){
			
			if( beforeLogicalGroupDeviceMap.containsKey(lgdMap.getItemId()) )	// ���� �����׷���ġ������ �ߺ��Ǵ� ������ ���� ��� �ߺ� ���� ����
				beforeLogicalGroupDeviceMap.remove(lgdMap.getItemId());
			else
				map.put(lgdMap.getItemId(), lgdMap);		// ���� ������ �ߺ����� �ʴ´ٸ� ���Ӱ� �߰�
		}
		
		return map;
	}
	
	/**		�����׷� ���� �� ��ġ���׳������ ���� �м�/ó��		*/
	private void parsingUpdLogicalGroupDeviceMap(LogicalGroup oLogicalGroup, LogicalGroup logicalGroup){
		int logicalGroupId = oLogicalGroup.getLogicalGroupId();
		
		// ���������� ���� ������ �ߺ� ������ ������ Map ����
		Map<Integer, LogicalGroupDeviceMap> beforeDeviceAntennaMap = this.toLogicalGroupDeviceAntennaMap(oLogicalGroup.getLogicalGroupDeviceMapList()); 
		Map<Integer, LogicalGroupDeviceMap> newDeviceAntennaMap = this.toLogicalGroupDeviceAntennaMap(beforeDeviceAntennaMap, logicalGroup.getLogicalGroupDeviceMapList());
		
		// ���翡�� ���� �������� �ִ� ��ġ ���׳� ���� ����
		this.parsingBeforeLogicalGroupDeviceMap(logicalGroupId, beforeDeviceAntennaMap);
		
		// �������� ���� ���翡�� �ִ� ��ġ ���׳� ���� �߰�
		this.parsingNewLogicalGroupDeviceMap(logicalGroupId, newDeviceAntennaMap);
	}
	
	/**		������ �����׷�� ��ġ���׳� ���� ���� �߰�	*/
	private void parsingNewLogicalGroupDeviceMap(int logicalGroupId, Map<Integer, LogicalGroupDeviceMap> newDeviceAntennaMap){
		Device device;
		List<DeviceLogicalGroupMap> dlgMapList;
		int antennaId;
		boolean equalsFlag;
		
		// ���� ���� ����
		for(LogicalGroupDeviceMap lgdMap : newDeviceAntennaMap.values()){
			device = DeviceService.getInstance().getDevice(lgdMap.getDeviceId());
			if( device == null )				continue;		// ������ ���� ó��
			
			antennaId = lgdMap.getAntennaId();
			dlgMapList = device.getLogicalGroupMapList();
			if( dlgMapList == null ){
				// ������ �ƿ� ������ ������ ���
				dlgMapList = new ArrayList<DeviceLogicalGroupMap>();
				device.setLogicalGroupMapList(dlgMapList);
				
				// �ű� ���� �߰�
				dlgMapList.add(new DeviceLogicalGroupMap(antennaId, logicalGroupId));
			}else{
				// ���� ������ ���� ��� ������ ������ �ִ��� Ȯ�� �� ���ٸ� �߰�
				equalsFlag = false;
				for(DeviceLogicalGroupMap dlgMap : dlgMapList){
					if( dlgMap.getLogicalGroupId() == logicalGroupId && dlgMap.getAntennaId() == antennaId ){
						equalsFlag = true;		// ������ ������ ���� ���
						break;
					}
				}
				
				// ������ ������ ���ٸ� �ű� ���� �߰�
				if( !equalsFlag )		dlgMapList.add(new DeviceLogicalGroupMap(antennaId, logicalGroupId));
			}
		}
		
	}
	
	/**		���� �����׷�� ��ġ���׳� ���� ���� ����		*/
	private void parsingBeforeLogicalGroupDeviceMap(int logicalGroupId, Map<Integer, LogicalGroupDeviceMap> beforeDeviceAntennaMap){
		Device device;
		List<DeviceLogicalGroupMap> dlgMapList;
		int antennaId;
		
		// ���� ���� ����
		for(LogicalGroupDeviceMap lgdMap : beforeDeviceAntennaMap.values()){
			device = DeviceService.getInstance().getDevice(lgdMap.getDeviceId());
			if( device == null )				continue;		// ������ ���� ó��
			
			dlgMapList = device.getLogicalGroupMapList();
			if( dlgMapList == null )		continue;		// ������ ���� ó��
			
			antennaId = lgdMap.getAntennaId();
			for(DeviceLogicalGroupMap dlgMap : dlgMapList){
				if( dlgMap.getLogicalGroupId() != logicalGroupId )		continue;		// �����׷���̵� �ٸ� ��� ó�� ����
				if( dlgMap.getAntennaId() != antennaId )					continue;		// ���׳� ��ȣ�� �ٸ� ��� ó�� ����
				
				dlgMapList.remove(dlgMap);		// �����׷���̵�� ���׳���ȣ�� ���� ��� �ش� ���� ����
				break;
			}
		}
	}
	
	/**		�����׷� ���� �� �����Ϳ���� ���� �м�/ó��		*/
	private void parsingUpdLogicalGroupReporterMap(LogicalGroup logicalGroup, Set<Integer> beforeLogicalGroupReporterIdSet, Set<Integer> newLogicalGroupReporterIdSet){
		
		for(Reporter reporter : ReporterService.getInstance().getReporterAll()){
			if( beforeLogicalGroupReporterIdSet.contains(reporter.getReporterId()) ){
				// ������ �����Ϳ� �����׷��� ����Ǿ� ���� ���
				this.parsingUpdLogicalGroupReporterMap1(reporter, logicalGroup, newLogicalGroupReporterIdSet);
				
			}else{
				// ������ �����Ϳ� �����׷��� ����Ǿ� ���� �ʾ��� ���
				this.parsingUpdLogicalGroupReporterMap2(reporter, logicalGroup, newLogicalGroupReporterIdSet);
			}
		}
		
	}
	
	/**		������ �����Ϳ� ������� �ʾҴ� �����׷� ���� �� �����Ϳ���� ���� �м�/ó��		*/
	private void parsingUpdLogicalGroupReporterMap2(Reporter reporter, LogicalGroup logicalGroup, Set<Integer> newLogicalGroupReporterIdSet){
		if( newLogicalGroupReporterIdSet.contains(reporter.getReporterId()) ){
			// �������� ����Ǿ� ���� �ʾ����� ����� ����� ��� ���Ӱ� �߰�
			this.mergeLogicalGroupReporterMap(reporter, null, logicalGroup);
		}else{
			// ������ ���� ��� ����Ǿ� ���� ���� ��� ���ٸ� ó�� ����
		}
		
	}
	
	/**		������ �����Ϳ� ����Ǿ��� �����׷� ���� �� �����Ϳ���� ���� �м�/ó��		*/
	private void parsingUpdLogicalGroupReporterMap1(Reporter reporter, LogicalGroup logicalGroup, Set<Integer> newLogicalGroupReporterIdSet){
		
		if( newLogicalGroupReporterIdSet.contains(reporter.getReporterId()) ){
			// ������ ���� ��� �����׷쿡 ����Ǿ� ���� ��� �����׷� �̸��� ����
			this.mergeLogicalGroupReporterMap(reporter, this.getReporterLogicalGroupMap(logicalGroup.getLogicalGroupId(), reporter.getLogicalGroupMapList()), logicalGroup);
			
		}else{
			// �������� ����Ǿ� �־����� ����� ������ ��� ���� ���� ���� ����
			this.removeReporterLogicalGroupMap(logicalGroup.getLogicalGroupId(), reporter.getLogicalGroupMapList());
		}
	}
	
	/**		�����Ͱ� �������ִ� �����׷�� �������� �ش� �����׷� ���� ����		*/
	private void removeReporterLogicalGroupMap(int logicalGroupId, List<ReporterLogicalGroupMap> list){
		if( list == null )		return;
		
		for(int i=list.size() - 1; i >= 0; i--){
			if( list.get(i).getLogicalGroupId() == logicalGroupId )		list.remove(i);
		}
	}
	
	/**		�����Ͱ� ������ �ִ� �ش� �����׷�� ���� �˻�		*/
	private ReporterLogicalGroupMap getReporterLogicalGroupMap(int logicalGroupId, List<ReporterLogicalGroupMap> list){
		if( list == null )		return null;
		
		for(ReporterLogicalGroupMap rlgMap : list){
			if( rlgMap.getLogicalGroupId() == logicalGroupId )		return rlgMap;
		}
		
		return null;
	}
	
	/**	�������� �����׷쿬���ʿ��� �ش� �����׷� ���� �߰� �Ǵ� ����		*/
	private void mergeLogicalGroupReporterMap(Reporter reporter, ReporterLogicalGroupMap reporterLogicalGroupMap, LogicalGroup logicalGroup){
		if( reporterLogicalGroupMap == null ){
			// ������ ���� ó�� ����
			reporterLogicalGroupMap = new ReporterLogicalGroupMap();
			reporterLogicalGroupMap.setLogicalGroupId(logicalGroup.getLogicalGroupId());
			
			List<ReporterLogicalGroupMap> list = reporter.getLogicalGroupMapList();
			if( list == null ){
				list = new ArrayList<ReporterLogicalGroupMap>();
				reporter.setLogicalGroupMapList(list);
			}
			
			list.add(reporterLogicalGroupMap);
		}
		
		reporterLogicalGroupMap.setLogicalGroupName(logicalGroup.getLogicalGroupName());
	}
	
	/**		�����׷� ����		*/
	void delLogicalGroup(int logicalGroupId) throws Exception{
		// ��ġ ť ����
		LogicalGroup logicalGroup = this.logicalGroupMap.remove(logicalGroupId);
		
		// ��ϵ��� ���� �����׷� ���̵�� ���� ��û [LG ID:XX]
		if( logicalGroup == null )		throw new Exception(this.makeResourceBundle().getResourceValue("ERR_4") + " [LG ID:" + logicalGroupId + "]");
		
		// ���� ��ġ�� �����׷� ���� ����
		this.delLogicalGroupDeviceAntennaMap(logicalGroupId);
		
		// ���� �������� �����׷� ���� ����
		this.delLogicalGroupReporterMap(logicalGroupId);
	}
	
	/**		������ �����׷�� ��ġ���׳� ���� ���� �߰�	*/
	private void delLogicalGroupDeviceAntennaMap(int logicalGroupId){
		List<DeviceLogicalGroupMap> dlgMapList;
		
		for(Device device : DeviceService.getInstance().getDeviceAll() ){
			if( device.isNullLogicalGroupMapList() )		continue;
			
			dlgMapList = device.getLogicalGroupMapList();
			for(int i=dlgMapList.size() - 1; i >= 0; i--){
				if( dlgMapList.get(i).getLogicalGroupId() == logicalGroupId )
					dlgMapList.remove(i);
			}
		}
		
	}
	
	/**		������ �����׷�� ��ġ���׳� ���� ���� �߰�	*/
	private void delLogicalGroupReporterMap(int logicalGroupId){
		List<ReporterLogicalGroupMap> rlgMapList;
		
		for(Reporter reporter : ReporterService.getInstance().getReporterAll() ){
			if( reporter.isNullLogicalGroupMapList() )		continue;
			
			rlgMapList = reporter.getLogicalGroupMapList();
			for(int i=rlgMapList.size() - 1; i >= 0; i--){
				if( rlgMapList.get(i).getLogicalGroupId() == logicalGroupId )
					rlgMapList.remove(i);
			}
		}
		
	}
	
	/**		��ϵ� ��� �����׷� ���� �˻�		*/
	public LogicalGroup[] getLogicalGroupAll(){
		return this.logicalGroupMap.values().toArray(new LogicalGroup[this.logicalGroupMap.size()]);
	}
	
	/**		�����׷� ��� ���� �˻�		*/
	public LogicalGroup getLogicalGroup(int logicalGroupId){
		return this.logicalGroupMap.get(logicalGroupId);
	}
	
	/**		�ٱ��� ���� ǥ�ÿ� ���ҽ����� ����		*/
	private ResourceBundle makeResourceBundle(){
		return new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_rep.conf");
	}
}
