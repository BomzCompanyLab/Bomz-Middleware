package kr.co.bomz.mw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.DeviceLogicalGroupMap;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.LogicalGroupDeviceMap;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.db.ReporterLogicalGroupMap;
import kr.co.bomz.mw.db.Tag;
import kr.co.bomz.util.collection.BomzBlockingQueue;

/**
 * 	��ġ�� �����׷�, ������ �� ť ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class QueueManagerService {
	
	private static final QueueManagerService _this = new QueueManagerService();
	
	/**
	 * 		�̵����� ���Ǵ� ť�� �����Ѵ�
	 * 		Key		:	�������̸�
	 * 		Value	:	�����׷� ���̵�� ���� ť ��ü�� ����ִ� Ŭ���� ����Ʈ	
	 */
	private final HashMap<Integer, List<QueueInfo>> queueInfoMap = new HashMap<Integer, List<QueueInfo>>();
	
	private QueueManagerService(){}
	
	public static final QueueManagerService getInstance(){
		return _this;
	}
	
	/**		�����׷� ���� �� �������� ť ������ �̸� ����		*/
	public final void updateLogicalGroupName(int logicalGroupId, String logicalGroupName){
		for(List<QueueInfo> infoList : this.queueInfoMap.values()){
			if( infoList == null )		continue;
			
			for(QueueInfo info : infoList){
				if( info.logicalGroupId != logicalGroupId )		continue;
				info.logicalGroupName = logicalGroupName;
			}
		}
		
	}
	
	/**
	 * 		��ġ �� �������� ������ ����Ǿ��� ��� ť ����� ������Ʈ �Ѵ�
	 * 		Controller ���� ����ȭ�� �ɷ������Ƿ� ���� ����ȭ �� �ʿ�� ����
	 */
	public final void updateQueues(){
		this.updateReporterQueues();
		this.updateDeviceQueues();
	}
	
	/**		��ġ ť ���� ������Ʈ		*/
	private void updateDeviceQueues(){
		List<QueueInfo> allQueueInfoList = this.getAllQueueInfoList();	// ���� ��� QueueInfo �� ����Ʈ�� �ִ´�
	
		// ��ġ�� �����׷� ť�� �����Ѵ�
		Device[] deviceList = DeviceService.getInstance().getDeviceAll();
		if( deviceList == null || deviceList.length == 0 )		return;
		
		List<DeviceLogicalGroupMap> dlgMapList;
		
		for(Device dev : deviceList){
			// ����� �����׷� ������ �����´�
			dlgMapList = dev.getLogicalGroupMapList();
			
			/*		���� ��ġ���� ������� ��� ť�� �ʱ�ȭ */
			dev.getDevice().clearQueue();
			
			if( dlgMapList == null || dlgMapList.isEmpty() )		continue;
			
			for(DeviceLogicalGroupMap dlgMap : dlgMapList)
				this.updateDeviceQueue(dev, dlgMap, allQueueInfoList);
		}
		
	}
	
	/**		��ġ�� ����ϴ� ť ���� ����		*/
	private void updateDeviceQueue(
			Device device, DeviceLogicalGroupMap dlgMap, List<QueueInfo> allQueueInfoList){
		
//		List<LogicalGroupDeviceMap> lgdMapList;
		
		for(QueueInfo info : allQueueInfoList){
			if( info.getLogicalGroupId() != dlgMap.getLogicalGroupId() )	continue;
			
//			// ť�� ����� ��ġ�� ���� ��� ������ ó�� ����
//			lgdMapList = info.getLgdMapList();
//			if( lgdMapList == null )		continue;
//			
//			for(LogicalGroupDeviceMap lgdMap : lgdMapList)
//				device.getDevice().addLogicalGroupQueue(lgdMap.getAntennaId(), info.getQueue());
			device.getDevice().addLogicalGroupQueue(dlgMap.getAntennaId(), info.getQueue());
		}
		
	}
	
	/*		�����Ϳ��� ���ǰ� �ִ� ��� ť ����� ����Ʈ�� ����� �����Ѵ�		*/
	private List<QueueInfo> getAllQueueInfoList(){
		List<QueueInfo> allQueueInfoList = new ArrayList<QueueInfo>();	// ���� ��� QueueInfo �� ����Ʈ�� �ִ´�
		
		// ��� QueueInfo �� allQueueInfoList �� �ִ´�
		List<QueueInfo> reporterQueueInfoList;
		java.util.Iterator<List<QueueInfo>> queueInfoIterator = this.queueInfoMap.values().iterator();
		while( queueInfoIterator.hasNext() ){
			reporterQueueInfoList = queueInfoIterator.next();
			for(QueueInfo queueInfo : reporterQueueInfoList)		allQueueInfoList.add( queueInfo );
		}
		
		return allQueueInfoList;
	}
	
	/**
	 * 		������ ť ������ ������Ʈ�Ѵ�
	 */
	private void updateReporterQueues(){
		Reporter[] reporterList = ReporterService.getInstance().getReporterAll();
		if( reporterList == null || reporterList.length == 0 )		return;
		
		List<ReporterLogicalGroupMap> logicalGroupList;
		for( Reporter reporter : reporterList ){
			
			List<QueueInfo> queueInfoList = this.queueInfoMap.get(reporter.getReporterId());
			if( queueInfoList == null ){
				queueInfoList = new ArrayList<QueueInfo>();		/*	NULL �� ��� ���� */
				this.queueInfoMap.put(reporter.getReporterId(), queueInfoList);	/* ���� ������ ����Ʈ�� �߰�	*/
			}
			
			logicalGroupList = reporter.getLogicalGroupMapList();
			if( logicalGroupList == null || logicalGroupList.isEmpty() ){
				// ����ϴ� �����׷��� ���� ��� IReporter �� ť ��ϵ� �ʱ�ȭ
				reporter.getReporter().setReporterQueue( null );
				
				// ����ϴ� �����׷��� �����Ƿ� queueInfoMap �� ����� �����׷� ť ������ �����Ѵ�
				queueInfoList.clear();			
				
			}else{
				// ����ϴ� �����׷��� ���� ���				
				List<QueueInfo> updateQueueInfoList = this.getUpdateQueueInfo(logicalGroupList, queueInfoList);
				
				// ť ������ ������ ���
				queueInfoList.clear();		// �ʱ�ȭ �� �ٽ� ���
				int index = 0;
				QueueInfo[] reporterQueue = new QueueInfo[ updateQueueInfoList.size() ];		// IReporter �� ������ ť �迭
				for(QueueInfo info : updateQueueInfoList){
					queueInfoList.add(info);
					reporterQueue[index++] = info;
				}
				
				reporter.getReporter().setReporterQueue(reporterQueue);		// ���ΰ� ����� ť ����
				
			}
		}
		
	}
	
	private List<QueueInfo> getUpdateQueueInfo(List<ReporterLogicalGroupMap> logicalGroupList, List<QueueInfo> queueInfoList){
		boolean flag;		// ���� �����׷� �̸��� ����ϴ� QueueInfo �� �ִ��� �Ǵ��ϱ� ���� �÷���
		ArrayList<QueueInfo> resultQueueInfoList = new ArrayList<QueueInfo>(logicalGroupList.size());
						
		for(ReporterLogicalGroupMap rlgMap : logicalGroupList){
			flag = false;
			
			for(QueueInfo info : queueInfoList){		// ���� �����׷� ���̵� ����ϴ� QueueInfo �� �˻�
				if( rlgMap.getLogicalGroupId() == info.getLogicalGroupId() ){
					resultQueueInfoList.add(info);
					flag = true;
					break;
				}
			}
			
			if( !flag ){
				// ���� �Դٸ� �ش� �����׷� �̸��� ����ϴ� QueueInfo �� ���� ����̹Ƿ� ���Ӱ� ����
				LogicalGroup lg = LogicalGroupService.getInstance().getLogicalGroup(rlgMap.getLogicalGroupId());
				if( lg != null ){
					resultQueueInfoList.add( new QueueInfo(rlgMap.getLogicalGroupId(), rlgMap.getLogicalGroupName(), lg.getLogicalGroupDeviceMapList()) );
				}else{
					// ����� ������ �ʰ����� ������ ���� ó��
					Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
					logger.warn("��ϵ��� ���� �����׷� ���̵�� ���� ť ������ ��û [�����׷���̵�:{}]", rlgMap.getLogicalGroupId());
				}
				 
			}
			
		}
		
		return resultQueueInfoList;
	}
		
	public class QueueInfo{
		private final int logicalGroupId;
		private final List<LogicalGroupDeviceMap> lgdMapList;
		private final Queue<Tag> queue = new BomzBlockingQueue<Tag>(600);
		private String logicalGroupName;
		
		QueueInfo(int logicalGroupId, String logicalGroupName, List<LogicalGroupDeviceMap> lgdMapList){
			this.logicalGroupId = logicalGroupId;
			this.logicalGroupName = logicalGroupName;
			this.lgdMapList = lgdMapList;
		}
		
		public int getLogicalGroupId() {
			return logicalGroupId;
		}
		
		public List<LogicalGroupDeviceMap> getLgdMapList() {
			return lgdMapList;
		}

		public Queue<Tag> getQueue() {
			return queue;
		}

		public String getLogicalGroupName() {
			return logicalGroupName;
		}

		public void setLogicalGroupName(String logicalGroupName) {
			this.logicalGroupName = logicalGroupName;
		}
		
	}
}
