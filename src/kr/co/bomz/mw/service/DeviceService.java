package kr.co.bomz.mw.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import kr.co.bomz.mw.comm.AbstractDevice;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.DeviceMapper;
import kr.co.bomz.mw.driver.DriverLoader;
import kr.co.bomz.mw.util.DeviceOrReporterExitUtil;
import kr.co.bomz.util.resource.ResourceBundle;

/**
 * 	��ġ ���� ����
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DeviceService {

	private static final DeviceService _this = new DeviceService();
	
	/**		
	 * 		��ġ ���� ��
	 * 	Key	:	��ġ ���̵�
	 */
	private final Map<Integer, Device> deviceMap = new HashMap<Integer, Device>();
	
	private DeviceService(){}
	
	public static final DeviceService getInstance(){
		return _this;
	}
	
	/**		��ġ ���� �˻�		*/
	public Device getDevice(int deviceId){
		return this.deviceMap.get(deviceId);
	}
	
	/**		��ü ��ġ ���� ��� �˻�		*/
	public Device[] getDeviceAll(){
		return deviceMap.values().toArray(new Device[deviceMap.size()]);
	}
	
	/**		��ϵ� ��ġ ���̵����� �˻�. true �� ��� ��ϵ� ��ġ���̵�		*/
	public boolean validationDeviceId(int deviceId){
		return this.deviceMap.containsKey(deviceId);
	}
	
	/**		�ý��� ���� �� ��ġ ���� �ʱ�ȭ		*/
	public void initDevice(SqlSession session, List<Device> deviceList) throws Exception{
		if( deviceList == null )		return;
		
		DeviceMapper mapper = session.getMapper(DeviceMapper.class);
		
		for(Device device : deviceList){
			device.setLogicalGroupMapList(mapper.selectDeviceLogicalGroupMap(device));
			this.addDevice(device, DatabaseService.getInstance().selectDeviceDriver(session, device).getDriverJarFile(), true);
		}
		
	}
	
	/**
	 * 	��ġ ���
	 * @param reporter			��ġ ����
	 * @param autoReporterStart	��ġ ���� �� �ڵ� ���� ����. true �� ��� �ڵ� ����
	 * @throws Exception		��ġ ����̹� ���� ���� �Ǵ� �߸��� �����׷� ���� ��û�� ��� �߻�
	 */
	void addDevice(Device device, byte[] driverFile, boolean autoReporterStart) throws Exception{
		if( this.deviceMap.containsKey(device.getDeviceId()) )		// �̹� ��ϵ� ��ġ ���̵�� ��� ��û
			throw new Exception(this.makeResourceBundle().getResourceValue("ERR_1") + " [ID=" + device.getDeviceId() + "]");
		
		device.setDevice( this.newInstanceDevice(device, driverFile) );		// ��ġ ����ü ����
		this.deviceMap.put(device.getDeviceId(), device);			// ��ġ ������ �̻��� �����Ƿ� �޸� ���
		
		if( autoReporterStart )			device.getDevice().deviceStart();		// ��ġ ����
	}
	
	/**	��ġ ����̹� ���� ����		*/
	private AbstractDevice newInstanceDevice(Device device, byte[] driverFile) throws Exception{
		DriverLoader driverLoader = new DriverLoader();
		try{
			AbstractDevice result = driverLoader.newInstanceDevice(device.getDriverId(), driverFile);
			result.setDeviceInfo(device.getDeviceId(), device.getDeviceName());
			
			if( device.getCommType() == Device.COMM_SERIAL_TYPE )
				result.setSerialParameters(device.getPort(), device.getBaudRate(), device.getFlowControl(), device.getDataBits(), device.getStopBits(), device.getParity());
			else
				result.setTcpParameters(device.getTcpIp(), device.getPort(), device.getTcpMode() == Device.TCP_MODE_SERVER);
			
			return result;
		}catch(Throwable e){
			throw new Exception(e.getMessage(), e);
		}
	}
	
	/**
	 * 	��ġ ����
	 * @param reporter		���ο� ��ġ ����
	 * @throws Exception	��ϵ��� ���� ��ġ ���̵�, �߸��� �����׷� ���̵� �Ǵ� �߸��� ��ġ ����̹� ���̵��� ��� �߻�
	 */
	void updDevice(Device device, byte[] driverFile) throws Exception{
		Device oDevice = this.deviceMap.remove(device.getDeviceId());
		if( oDevice == null )		// ��ϵ��� ���� ��ġ ���̵�� ���� ��û
			throw new Exception(this.makeResourceBundle().getResourceValue("ERR_3") + " [ID:" + device.getDeviceId() + "]");
		
		try{
			this.addDevice(device, driverFile, false);			// �ű� ������ ���
		}catch(Exception e){
			this.deviceMap.put(device.getDeviceId(), oDevice);
			throw e;
		}
		
		oDevice.getDevice().exit();				// ���� ��ġ ����
		device.getDevice().deviceStart();		// �ű� ��ġ ����
	}
	
	/**
	 * 	��ġ ����
	 * @param reporterId		������ ��ġ ���̵�
	 * @throws Exception		��ϵ��� ���� ��ġ ���̵��� ��� �߻�
	 */
	void delDevice(int deviceId) throws Exception{
		Device device = this.deviceMap.remove(deviceId);
		if( device == null )		// ��ϵ��� ���� ��ġ ���̵�� ���� ��û
			throw new Exception(this.makeResourceBundle().getResourceValue("ERR_2") + " [ID:" + deviceId + "]");
	
		device.getDevice().exit();
	}
	
	/**		����̹��� ����Ǿ��� ��� �ش� ����̹��� ����ϴ� ��ġ ���� �� �ٽ� ����		*/
	void reloadDriverUpdate(int driverId, byte[] driverFile) throws Exception{
		List<Integer> exitCheckList = Collections.synchronizedList(new ArrayList<Integer>()); 
		List<DeviceOrReporterExitUtil> devList = new ArrayList<DeviceOrReporterExitUtil>();
		
		AbstractDevice beforeDevice, afterDevice;
		for(Device device : this.deviceMap.values() ){
			if( device.getDriverId() != driverId )		continue;		// �ٸ� ����̹��� ����� ��� ���ٸ� ó�� ����
			
			beforeDevice = device.getDevice();							// ���� ����̹� ����
			afterDevice = this.newInstanceDevice(device, driverFile);	// �ű� ����̹� ����
			
			afterDevice.setLogicalGroupQueue( beforeDevice.getLogicalGroupQueue() );	// ť �̵�
			
			device.setDevice(afterDevice);		// �ű� �ε� ���Ϸ� ����
			
			devList.add(new DeviceOrReporterExitUtil(exitCheckList, beforeDevice, afterDevice));	// exit() ȣ��ó���� ������� ���� 
		}
		
		for(DeviceOrReporterExitUtil util : devList)		util.exitCheckStart();		// ������� exit() ȣ��	
		
		// ��� exit() ȣ�� ó���� ���������� ���
		int size = devList.size();
		while( exitCheckList.size() < size )	try{		Thread.sleep(300);	}catch(Exception e){}
	}
	
	/**		�ٱ��� ���� ǥ�ÿ� ���ҽ����� ����		*/
	private ResourceBundle makeResourceBundle(){
		return new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_dev.conf");
	}
}
