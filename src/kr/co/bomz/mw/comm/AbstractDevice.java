package kr.co.bomz.mw.comm;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.comm.connect.DeviceConnect;
import kr.co.bomz.mw.comm.connect.DeviceSerialConnect;
import kr.co.bomz.mw.comm.connect.DeviceTcpClientConnect;
import kr.co.bomz.mw.comm.connect.DeviceTcpConnect;
import kr.co.bomz.mw.comm.connect.DeviceTcpServerConnect;
import kr.co.bomz.mw.db.Tag;
import kr.co.bomz.mw.service.DatabaseService;


/**
 * 
 * 	�̵���� ��ġ ������ �߻�Ŭ����
 * 	��� ��ġ ����̹��� �ش� Ŭ������ �����ؾ� �Ѵ�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public abstract class AbstractDevice implements Runnable, AbstractDefault{

	/**		��ġ �α�		*/
	protected static final Logger logger = LoggerFactory.getLogger("Device");
	
	/**		��ġ ���׳� 1�� ���̵�		*/
	public static final int ANTENNA_1_ID = 0;
	
	/**		��ġ ���׳� 2�� ���̵�		*/
	public static final int ANTENNA_2_ID = 1;
	
	/**		��ġ ���׳� 3�� ���̵�		*/
	public static final int ANTENNA_3_ID = 2;
	
	/**		��ġ ���׳� 4�� ���̵�		*/
	public static final int ANTENNA_4_ID = 3;
	
	
	/**		��ġ ���̵�		*/
	private int deviceId;
	
	/**		��ġ �̸�		*/
	private String deviceName;
	
	/**		��ġ ��� ���� ��ü		*/
	private DeviceConnect deviceConnect;
	
	/**		exit() �� ȣ��Ǹ� false �� ����Ǹ� �ش� ������ ����		*/
	private boolean running = true;
	
	/**		������ ������ ��ü		*/
	private Thread threadObj;
	
	
	/**		
	 * 		�����׷� ť�� �����ϴ� ��
	 * 		
	 * 		Key		: ���׳� ��ȣ
	 * 		Value	: ť ���
	 */
	private Map<Integer, List<Queue<Tag>>> queueMap;
		
	/**		�����׷� ť ���� �� ����ȭ�� ���� ��		*/
	private final Object queueLock = new Object();
	
	/**		��ġĿ���� ����ȭ�� ���� ��		*/
	private final Object deviceConnectLock = new Object();
	
	private Boolean lastConnectState = null;
	
	public AbstractDevice(){}
	
	public synchronized final void deviceStart(){
		
		if( this.threadObj == null ){
			this.threadObj = new Thread(this);
			this.threadObj.start();
		}
	}
	
	@Override
	public final void run() {
		
		// ���� ��ġ ���� ����
		boolean isConnectState;
		
		while( this.running ){
			synchronized( this.deviceConnectLock ){
				// ��ġ���������� �������� �ʾ��� ��� 1�� �� �ٽ� ����
				if( this.deviceConnect == null ){
					try{		Thread.sleep(1000);		}catch(Exception e){}
					continue;
				}
				
				isConnectState = this.deviceConnect.connect();
			}
			
			if( this.lastConnectState == null || this.lastConnectState != isConnectState ){
				this.lastConnectState = isConnectState;
				DatabaseService.getInstance().updateDeviceConnectState(this.deviceId, isConnectState);
			}
			
			// ��ġ�� ������� ���� ������ ��� ��� �޽� �� �ٽ� ó��
			if( !isConnectState ){
				try{		Thread.sleep(1000);		}catch(Exception e){}
				continue;
			}
			
			// ������ ó��
			if( !this.execute() )		this.disconnect();
			
			// CPU ���� ����
			try{		Thread.sleep(10);		}catch(Exception e){}
		}
		
		/*		exit() �� �ι� ȣ����� �ʱ� ���� �˻�	*/
		if( this.running )		this.exit();
	}
	
	public abstract boolean execute();
	
	public final void clearQueue(){
		synchronized( this.queueLock ){
			if( this.queueMap != null )		this.queueMap.clear();
		}
	}
	
	/**		���� �±� ������ ť�� �߰�		*/
	public final void insertIntoQueue(Tag[] tags) {
		if( tags == null )		return;
		
		synchronized( this.queueLock ){
			this._insertIntoQueue(tags);
		}
	}
	
	/**		���� �±� ������ ť�� �߰�		*/
	public final void insertIntoQueue(Tag tag) {
		if( tag == null )		return;
		
		synchronized( this.queueLock ){
			this._insertIntoQueue(tag);
		}
	}
	
	/*		ť�� ������ �ֱ�		*/
	private void _insertIntoQueue(Tag[] tags){
		if( this.queueMap == null )		return;
		
		for(Tag tag : tags)		this._insertIntoQueue(tag);
	}

	/*		ť�� ������ �ֱ�		*/
	private void _insertIntoQueue(Tag tag){
		if( this.queueMap == null )		return;
		if( tag == null )		return;
		
		tag.setDeviceId(this.deviceId);
		tag.setDeviceName(this.deviceName);
		
		List<Queue<Tag>> queueList = this.queueMap.get( tag.getAntenna() );
		if( queueList == null )		return;
		
		for(Queue<Tag> queue : queueList)		queue.add( tag );
	}
	
	/**		��ġ ���̵� ����		*/
	public final void setDeviceInfo(int deviceId, String deviceName){
		this.deviceId = deviceId;
		this.deviceName = deviceName;
	}
	
	/**		TCP ���� ����� ��� TCP �Ķ���� ����		*/
	public final void setTcpParameters(String ip, int port, boolean serverMode) {
		DeviceTcpConnect tcpConnect = 
				serverMode ?	new DeviceTcpServerConnect(this.deviceId, this.deviceName) : 
										new DeviceTcpClientConnect(this.deviceId, this.deviceName);
				
		tcpConnect.setTcpIp(ip);
		tcpConnect.setTcpPort(port);
		
		synchronized( this.deviceConnectLock ){
			this.close();
			if( this.deviceConnect != null )		this.deviceConnect.close();
			this.deviceConnect = tcpConnect;
		}
	}
	
	/**		�ø��� ���� ����� ��� �ø��� �Ķ���� ����		*/
	public void setSerialParameters(int serialPort, int baudRate,
			int flowControl, int dataBits, int stopBits, int parity) {
		
		DeviceSerialConnect serialConnect = new DeviceSerialConnect(this.deviceId, this.deviceName);
		serialConnect.setPort(serialPort);
		serialConnect.setBaudRate(baudRate);
		serialConnect.setDataBits(dataBits);
		serialConnect.setFlowControl(flowControl);
		serialConnect.setParity(parity);
		serialConnect.setStopBits(stopBits);
		
		synchronized( this.deviceConnectLock ){
			this.close();
			if( this.deviceConnect != null )		this.deviceConnect.close();
			this.deviceConnect = serialConnect;
		}
	}
	
	/**		�����׷� ť ����		*/
	public Map<Integer, List<Queue<Tag>>> getLogicalGroupQueue(){
		return this.queueMap;
	}
	
	/**		�����׷� ť ����		*/
	public void setLogicalGroupQueue(Map<Integer, List<Queue<Tag>>> queueMap){
		synchronized( this.queueLock ){
			this.queueMap = queueMap;
		}
	}
	
	/**		���� �׷� ť ���	*/
	public final void addLogicalGroupQueue(int antenna, Queue<Tag> queue){
		if( queue == null )		return;
		
		synchronized( this.queueLock ){
			if( this.queueMap == null )		this.queueMap = new HashMap<Integer, List<Queue<Tag>>>(BomzMiddleware.MAX_DEVICE_ANTENNA_LENGTH);
			
			List<Queue<Tag>> queueList = this.queueMap.get(antenna);
			
			if( queueList == null ){
				queueList = new ArrayList<Queue<Tag>>();
				this.queueMap.put(antenna, queueList);
			}
			
			queueList.add(queue);
		}
		
	}
	
	/**		���� �׷� ť ����	*/
	public final void delLogicalGroupQueue(int antenna, Queue<Tag> queue){
		if( queue == null )		return;
		
		synchronized( this.queueLock ){
			if( this.queueMap == null )		return;
			List<Queue<Tag>> queueList = this.queueMap.get(antenna);
			
			if( queueList != null )		queueList.remove(queue);
		}
		
	}
	
	/**		��� ���׳��� ���� �׷� ť ����		*/
	public final void delLogicalGroupQueue(Queue<Tag> queue){
		if( queue == null )		return;
		
		synchronized( this.queueLock ){
			if( this.queueMap == null )	return;
			
			for(List<Queue<Tag>> list : this.queueMap.values())
				if( list != null )		list.remove(queue);
		}
		
	}
	
	/**		��ġ ��� ����		*/
	@Override
	public final void exit(){
		this.running = false;
		
		this.close();
		
		if( this.deviceConnect != null )	{
			synchronized( this.deviceConnectLock ){
				this.deviceConnect.close();
				this.deviceConnect = null;
			}
		}
		
		this.threadObj = null;
		this.queueMap = null;
	}
	
	/**		��ġ�� ���� ���� ó��		*/
	public final void disconnect(){
		this.close();
		if( this.deviceConnect == null )		return;
		
		this.deviceConnect.close();
	}
	
	/**
	 * 	��ġ ��� ���� �� ������ ó���� �ʿ��ϴٸ� �������̵��Ͽ� ����
	 */
	abstract public void close();
	

	/**		����� ���� InputStream		*/
	public InputStream getInputStream(){
		if( this.deviceConnect == null )		return null;
		else			return this.deviceConnect.getInputStream();
	}
	
	/**		����� ���� OutputStream		*/
	public OutputStream getOutputStream(){
		if( this.deviceConnect == null )		return null;
		else			return this.deviceConnect.getOutputStream();
	}
	
	public int getDeviceId() {
		return deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	/**		���ڿ� ������ �޼��� ����. ���� ������ true		*/
	public boolean writeMessage(String message){
		if( message == null )		return true;
		return this.writeMessage(message.getBytes());
	}
	
	/**		����Ʈ ������ �޼��� ����. ���� ������ true		*/
	public boolean writeMessage(byte[] message){
		if( message == null )		return true;
		
		if( this.deviceConnect == null )		return false;
		
		OutputStream os = this.deviceConnect.getOutputStream();
		if( os == null )		return false;
		
		try{
			os.write(message);
			os.flush();
			if( logger.isInfoEnabled() )
				logger.info("���ɾ� ���� ���� [��ġID:{}, ��ġ��:{}]", this.deviceId, this.deviceName);
			
			return true;
		}catch(Exception e){
			if( logger.isWarnEnabled() )
				logger.warn("���ɾ� ���� ���� [��ġID:{}, ��ġ��:{}]", this.deviceId, this.deviceName);
			
			this.deviceConnect.close();
			return false;
		}
	}
}
