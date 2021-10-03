package kr.co.bomz.mw.comm;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.bomz.mw.db.ReporterMapper;
import kr.co.bomz.mw.db.Tag;
import kr.co.bomz.mw.db.TagList;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.QueueManagerService.QueueInfo;
import kr.co.bomz.util.timer.BomzTimer;
import kr.co.bomz.util.timer.Timer;
import kr.co.bomz.util.timer.TimerStopType;

/**
 * 
 * 	�̵���� ������ ������ �߻�Ŭ����
 * 	��� ������ ����̹��� �ش� Ŭ������ �����ؾ� �Ѵ�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public abstract class AbstractReporter implements Runnable, AbstractDefault{

	/**		������ �α�		*/
	protected static final Logger logger = LoggerFactory.getLogger("Reporter");
	
	/**		������ ���̵�		*/
	private int reporterId;
	
	/**		������ �̸�		*/
	private String reporterName;
	
	/**		������ ������		*/
	private String reporterIp;
	
	/**		������ ��Ʈ		*/
	private int reporterPort;
	
	/**		Ÿ�̸� ��� ����		*/
	private boolean timer;
	
	/**		Ÿ�̸� ���� ���� �ֱ�(�ʴ���)		*/
	private int timerRepeatTime;
	
	/**		����� ���� �Ķ����		*/
	private String userParameter;
	
	/**		������ ť ��		*/
	private final Object queueLock = new Object();
	
	/**		������ ť		*/
	private QueueInfo[] queueInfo;
	
	/**		������ Ŀ���� ����ȭ�� ���� ��		*/
	private final Object reporterConnectLock = new Object();
	
	/**		exit() �� ȣ��Ǹ� false �� ����Ǹ� �ش� ������ ����		*/
	private boolean running = true;
	
	/**		������ ������ ��ü		*/
	private Thread threadObj;
	
	/**
	 * 		������ ���� ���� ���� �� �����ͺ��̽��� ����� ������ �����ϱ� ���� ���ȴ�
	 */
	private Boolean lastConnectState;
	
	private BomzTimer bomzTimer;
	
	public synchronized final void reporterStart(){
		if( this.threadObj == null ){
			this.threadObj = new Thread(this);
			this.threadObj.start();
		}
	}
	
	@Override
	public final void run() {
		
		boolean isConnectState;
		
		while( this.running ){
			
			synchronized( this.reporterConnectLock ){
				isConnectState = this.isConnect();
			}
			
			if( this.lastConnectState == null || this.lastConnectState != isConnectState ){
				this.lastConnectState = isConnectState;
				DatabaseService.getInstance().updateReporterConnectState(this.reporterId, isConnectState);
				
				/*
				 *  ���� ���°� '�����'���� ����Ǿ��� ��� ��� �ִ� ���۽��� �α� ������ �˻��Ͽ� �����Ѵ�
				 *  ���� ���� �Ǵ� ��� ���� �� �ڵ� ���� ����ǹǷ� continue ����
				 */
				if( isConnectState && !this.executeWriteFailList() )		continue;
			}
			
			
			// �����Ͱ� ������� ���� ������ ��� ��� �޽� �� �ٽ� ó��
			if( !isConnectState ){
				// ť�� �����Ͱ� �׿��� ��� �ش� �����͸� ���� �̵���Ų�� (�޸� ����)
				this.dbLogTagList( this.getLogicalGroupQueueDatas() );
				try{		Thread.sleep(1000);		}catch(Exception e){}
				continue;
			}
			
			// Ÿ�̸� �̿��� �ƴ� ��쿡�� ���� �޼ҵ� ȣ��
			if( !this.timer )			this.executeData();
			
			// CPU ���� ����
			try{		Thread.sleep(10);		}catch(Exception e){}
		}
		
		/*		exit() �� �ι� ȣ����� �ʱ� ���� �˻�	*/
		if( this.running )		this.exit();
		
		synchronized( this.queueLock ){
			this.queueInfo = null;
		}
		
	}
	
	/**	�������� ��������� ����Ǿ��� ��� ��� ����� ������� �α׸� �˻��Ͽ� ���� �� ����		*/
	private boolean executeWriteFailList(){
		SqlSession session = null;
		try{
			session = DatabaseService.getInstance().openSession();
			ReporterMapper mapper = session.getMapper(ReporterMapper.class);
			List<Tag> tagList;
			while( true ){
				tagList = mapper.selectReporterWriteFailTagLog(this.reporterId);
				if( tagList == null || tagList.isEmpty() )		break;
				
				this.removeWriteFailLogList(mapper, tagList);		// ������ ���۽��� �α� ��� ����
				
				if( this.execute( tagList ) ){		// �α� ���� ó��
					// ���� ���� �� ��� Ŀ��
					session.commit();
				}else{
					// ���� ���� �� ��� �ѹ� �� ���� ����
					session.rollback();
					this.close();
					try{		Thread.sleep(1000);		}catch(Exception e){}
					return false;
				}
			}
			
		}catch(Exception e){
			if( session != null )		session.rollback();
			logger.error("������ ���۽��� �α� ���� �� ��� ���� [������ID:{}, �������̸�:{}]", this.reporterId, this.reporterName, e);
			try{		Thread.sleep(1000);		}catch(Exception e1){}
			return false;
		}finally{
			if( session != null )		session.close();
		}
		
		return true;
	}
		
	/**		������ ���� ���зα� ��� ����		*/
	private void removeWriteFailLogList(ReporterMapper mapper, List<Tag> tagList) throws Exception{
		StringBuilder buffer = new StringBuilder();
		final int size = tagList.size();
		for(int i=0; i < size; i++){
			if( i != 0 )		buffer.append(",");
			buffer.append(tagList.get(i).getLogId());
		}
		
		mapper.removeReporterWriteFailTagLog(buffer.toString());
	}
	
	/**	������ ������ ó��		*/
	private void executeData(){
		List<Tag> tagList = this.getLogicalGroupQueueDatas();
		if( tagList == null )		return;
		
		if( !this.execute(tagList) ){
			// false ���� �� ��ſ� ������ ����ɷ� �ľ��ϰ� ť�� �����͸� ��� ��� �ִ´�
			this.close();
			
			// ��� �±� ����
			this.dbLogTagList(tagList);
		}
		
		tagList = null;
	}
	
	/**	���� ���� �Ǵ� ����ȵ� ���¿��� �±� ���� ���� ��� ����		*/
	private void dbLogTagList(List<Tag> tagList){
		if( tagList == null || tagList.isEmpty() )		return;
		
		// ���� ���� �� �±� ������ ��� ����
		try{
			TagList tList = new TagList();
			tList.setReporterId(this.reporterId);
			tList.setReporterName(this.reporterName);
			tList.setTagList(tagList);
			
			DatabaseService.getInstance().insertReporterWriteFailTagLog(tList);
			if( logger.isInfoEnabled() )	logger.info("���� ���� ������ ���α� ���� ���� [�� {}��]", tagList.size());
		}catch(Exception e){
			// ��� ���� �� �α� ����
			if( logger.isWarnEnabled() )		logger.warn("���� ���� ������ ���α� ���� ���� [�� {}��]", tagList.size(), e);
			
			// ��� ���� �����̹Ƿ� ���Ϸα� ����
			this.fileLogWriteFailTagList(tagList);
		}
	}
	
	/**	���� ���� �±׸� ��� �α� ���忡�� ������ ��� �α����Ͽ� ���� �� ��������		*/
	private void fileLogWriteFailTagList(List<Tag> tagList){
		if( tagList == null || tagList.isEmpty() )		return;
		
		Logger fileLogger = LoggerFactory.getLogger("WriteFail");
		for(Tag tag : tagList){
			fileLogger.info("������ ���� ���� [�±�:{}, �ð�:{}, ��ġID:{}, ��ġ��:{}, ��ġ���׳�:{}, �׷�ID:{}, �׷��:{}, ������ID{}, �����͸�:{}",
						tag.getTagId(), tag.getReadTime(), tag.getDeviceId(), tag.getDeviceName(), tag.getAntenna(),
						tag.getLogicalGroupId(), tag.getLogicalGroupName(), this.reporterId, this.reporterName
					);
		}
		
	}
	
	/**	
	 * 	������ ������ ó��
	 * 	ó�� ���� �� true ����
	 * 	false ���� �� ��� ������ �ľ��ϰ� ������ �����Ѵ�
	 **/
	abstract public boolean execute(List<Tag> tagList);
	
	/**	������ ��� ����		*/
	@Override
	public final void exit() {
		if( this.bomzTimer != null )		this.bomzTimer.closeTimer();
		this.running = false;
		this.close();
		this.reporterIp = null;
		this.threadObj = null;
		
		// ���۽��� �α� ��� ����
		DatabaseService.getInstance().clearReporterWriteFailTagLog(this.reporterId);
	}

	/**		������ �⺻ ����		*/
	public void setReporterInfo(int reporterId, String reporterName){
		this.reporterId = reporterId;
		this.reporterName = reporterName;
	}
	
	/**		������ ��ſ� �ʿ��� ���� ����		*/
	public void setParameters(String ip, int port, boolean timer, int timerRepeatTime, String userParameter) {
		synchronized( this.reporterConnectLock ){
			this.disconnect();		// ���� ���� ����
			
			this.reporterIp = ip;
			this.reporterPort = port;
			this.timer = timer;
			this.timerRepeatTime = timerRepeatTime;
			this.userParameter = userParameter;
			
			if( this.bomzTimer != null )		this.bomzTimer.closeTimer();
			
			if( this.timer ){
				this.bomzTimer = new BomzTimer();
				this.bomzTimer.addTimer(new TimerSub(this.timerRepeatTime * 1000) );		// �� �����̹Ƿ� ���ϱ� 1000 ó��
			}else{
				this.bomzTimer = null;
			}
		}
		
	}
	
	/**		ť�� �ִ� ������ ��� ����		 */
	private List<Tag> getLogicalGroupQueueDatas(){
		synchronized( this.queueLock ){
			
			if( this.queueInfo == null || this.queueInfo.length == 0 )		return null;
			
			java.util.ArrayList<Tag> resultList = new java.util.ArrayList<Tag>();
			
			java.util.Queue<Tag> queue;
			Tag tag;
			int queueSize;
			
			for(QueueInfo queueInfo : this.queueInfo){
				queue = queueInfo.getQueue();
								
				queueSize = queue.size();
				
				for(int i=0; i < queueSize; i++){
					tag = queue.poll();
					
					Tag resultTag = new Tag();
					resultTag.setAntenna( tag.getAntenna() );
					resultTag.setDeviceId( tag.getDeviceId() );
					resultTag.setDeviceName( tag.getDeviceName() );
					resultTag.setTagId( tag.getTagId() );
					resultTag.setReadTime( tag.getReadTime() );
					resultTag.setLogicalGroupId( queueInfo.getLogicalGroupId() );
					resultTag.setLogicalGroupName( queueInfo.getLogicalGroupName() );
					
					resultList.add( resultTag );
				}
				
			}
			
			return resultList;
		}
		
	}
		
	/**		������ ���� ����		*/
	protected abstract boolean isConnect();
	
	/**		������ ��� ���� ���� ó��		*/
	protected abstract void disconnect();
	
	/**		������ ��� ���� �� ���� ó��		 */
	protected void close(){
		synchronized( this.reporterConnectLock ){
			this.disconnect();
		}
	}
	
	/**		�������� ť ����		*/
	public void setReporterQueue(QueueInfo[] queueInfo){
		synchronized( this.queueLock ){
			this.queueInfo = queueInfo;
		}
	}
	
	/**		������ ť ����		*/
	public QueueInfo[] getReporterQueue(){
		return this.queueInfo;
	}
	
	public int getReporterId() {
		return reporterId;
	}

	public String getReporterName() {
		return reporterName;
	}

	public String getReporterIp() {
		return reporterIp;
	}

	public int getReporterPort() {
		return reporterPort;
	}

	public boolean isTimer() {
		return timer;
	}

	public int getTimerRepeatTime() {
		return timerRepeatTime;
	}

	public String getUserParameter() {
		return userParameter;
	}

	/**
	 * 	Ÿ�̸� �̿�� Ÿ�̸� ȣ�� 
	 * 
	 * @author Bomz
	 * @version 1.0
	 * @since 1.0
	 *
	 */
	class TimerSub extends Timer{

		public TimerSub(long repeatPeriod) {
			super(repeatPeriod);
		}

		@Override
		public void execute() {
			executeData();
		}

		@Override
		public void stopTimer(TimerStopType type) {}


	}
}
