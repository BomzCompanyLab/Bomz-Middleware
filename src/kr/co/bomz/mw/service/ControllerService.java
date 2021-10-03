package kr.co.bomz.mw.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.RXTXPort;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.db.ControlResponse;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.DeviceLogicalGroupMap;
import kr.co.bomz.mw.db.Driver;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.LogicalGroupDeviceMap;
import kr.co.bomz.mw.db.LogicalGroupReporterMap;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.db.ReporterLogicalGroupMap;
import kr.co.bomz.mw.driver.DriverLoader;
import kr.co.bomz.mw.ui.BomzProgressBar;
import kr.co.bomz.util.resource.ResourceBundle;

/**
 * 	�̵���� ���� ��� ���� ��û�� ó���ϴ� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class ControllerService {

	private final Logger logger = LoggerFactory.getLogger("Controller");
	
	private static final ControllerService _this = new ControllerService();
	
	/**	��� ��û�� ���������� ó���ǹǷ� �� ó����		*/
	private final Object lock = new Object();
	
	private ControllerService(){}
	
	public static final ControllerService getInstance(){
		return _this;
	}
	
	/**		��ġ ��� ��û		*/
	public void addDevice(Device device, BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.DEVICE_ADD, eventHandler, progressBar, device);
	}
	
	/**		��ġ ��� ��û (������ ȣ���)		*/
	public ControlResponse addDevice(Device device){
		if( device == null )		new ControlResponse(ControlResponse.TARGET_DEVICE, ControlResponse.TYPE_ADD, "Parameter is null");
		
		return this.addOrUpdDevice(device, true, null);
	}
	
	/**		��ġ ���� ��û		*/
	public void updDevice(Device device, BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.DEVICE_UPD, eventHandler, progressBar, device);
	}
	
	/**		��ġ ��� ��û (������ ȣ���)		*/
	public ControlResponse updDevice(Device device){
		if( device == null )		new ControlResponse(ControlResponse.TARGET_DEVICE, ControlResponse.TYPE_UPD, "Parameter is null");
		
		return this.addOrUpdDevice(device, false, null);
	}
	
	/**		�ټ��� ��ġ ���� ��û		*/
	public void delDevice(EventHandler<ActionEvent> eventHandler, List<Device> deviceList, BomzProgressBar progressBar){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.DEVICE_DEL, eventHandler, progressBar, deviceList);
	}
	
	/**		�ټ��� ��ġ ���� ��û		*/
	private ControlResponse delDevice(List<Device> deviceList, BomzProgressBar progressBar){
		if( progressBar != null )		progressBar.nextProgressStep("Wait");
		
		if( deviceList == null || deviceList.isEmpty() )		
			return new ControlResponse(ControlResponse.TARGET_DEVICE, ControlResponse.TYPE_DEL);	// ���� ���� ����
		
		if( progressBar != null )		progressBar.nextProgressStep();
		
		final int size = deviceList.size();
		List<Integer> failIdList = new ArrayList<Integer>();
		for(int i=0; i < size; i++){
			if( progressBar != null )		progressBar.updateProgressMessage("Delete (" + (i+1) + "/" + size + ")");
			if( !this.delDevice(deviceList.get(i).getDeviceId()).isSuccess() )		failIdList.add(deviceList.get(i).getDeviceId());
		}

		if( progressBar != null )		progressBar.nextProgressStep("Success");

		return multiDelControlResponse(ControlResponse.TARGET_DEVICE, failIdList);
	}
	
	/**		��ġ ���� ��û		*/
	public ControlResponse delDevice(int deviceId){
		ControlResponse controlResponse = null;
		
		try{
			
			if( this.logger.isDebugEnabled() )		this.logger.debug("��ġ ���� ��û [��ġ���̵�={}]", deviceId);
			
			synchronized( this.lock ){
				SqlSession session = null;
				try {
					session = DatabaseService.getInstance().openSession();			// ��� ���� ����
					
					DatabaseService.getInstance().delDevice(session, deviceId);	// ��� ����
					DeviceService.getInstance().delDevice(deviceId);						// �޸� ����
					QueueManagerService.getInstance().updateQueues();			// ť ����
					
					session.commit();
					controlResponse =  new ControlResponse(ControlResponse.TARGET_DEVICE, ControlResponse.TYPE_DEL);	// ���� ���� ����
					return controlResponse;
				} catch (Exception e) {
					// ��� ���� �� ���� �߻�
					if( session != null )		session.rollback();
					
					this.logger.error("��ġ ���� ���� [ID={}]", deviceId, e);
					
					controlResponse = new ControlResponse(ControlResponse.TARGET_DEVICE, ControlResponse.TYPE_DEL, e.getMessage());	// ���� ���� ����
					return controlResponse;
				}finally{
					if( session != null )		session.close();
				}
			}
			
		}finally{
			this.log(controlResponse, deviceId, null);
		}
	}
	
	/**		ó�� ��� �α� ó��		*/
	private void log(ControlResponse controlResponse, int id, String name){
		if( controlResponse.isSuccess() ){
			// ó�� ���� �α� ���
			if( this.logger.isInfoEnabled() )
				this.logger.info("{} {} ���� [ID={}, �̸�={}]", this.getLogTargetName(controlResponse), this.getLogTypeName(controlResponse), id, name);
			
		}else{
			// ó�� ���� �α� ���
			if( this.logger.isErrorEnabled() )
				this.logger.error("{} {} ���� [ID={}, �̸�={}, ����={}]", this.getLogTargetName(controlResponse), this.getLogTypeName(controlResponse), id, name, controlResponse.getErrMessage());
			
		}
	}
	
	/**		��ġ �߰�/���� ��û �� ��û �Ķ���� �α�		*/
	private void logDevice(Device device, boolean isAdd){
		if( !this.logger.isDebugEnabled() )		return;		// ����� ���� �̻� ó��
		
		this.logger.debug("��ġ {} ��û", isAdd?"���":"����");
		this.logger.debug("��û �Ķ���� ����");
		
		if( !isAdd )		this.logger.debug("��ġ ���̵� : {}", device.getDeviceId());		// ������ ��츸 ó��
		this.logger.debug("��ġ�� : {}", device.getDeviceName());
		this.logger.debug("��Ź�� {}: ", device.getCommType());
		this.logger.debug("����̹� : {}", device.getDriverId());
		this.logger.debug("TCP IP : {}", device.getTcpIp());
		this.logger.debug("Port : {}", device.getPort());
		this.logger.debug("SERIAL BaudRate : {}", device.getBaudRate());
		this.logger.debug("SERIAL FlowContorl : {}", device.getFlowControl());
		this.logger.debug("SERIAL DataBits : {}", device.getDataBits());
		this.logger.debug("SERIAL StopBits : {}", device.getStopBits());
		this.logger.debug("SERIAL Parity : {}", device.getParity());
		
		List<DeviceLogicalGroupMap> list = device.getLogicalGroupMapList();
		if( list == null || list.isEmpty() ){
			this.logger.debug("�����׷� ���� ���� ����");
		}else{
			this.logger.debug("�����׷� ���� ���� [���׳����̵�] [�����׷���̵�]");
			for(DeviceLogicalGroupMap dlg : list){
				this.logger.debug("���׳�ID : {}, �����׷�ID : {}", dlg.getAntennaId(), dlg.getLogicalGroupId());
			}
		}
		
	}
	
	/**		�α� ��� �� ���� ��ĸ� ����		*/
	private String getLogTypeName(ControlResponse controlResponse){
		switch( controlResponse.getControlType() ){
		case ControlResponse.TYPE_ADD :		return "���";
		case ControlResponse.TYPE_UPD :		return "����";
		case ControlResponse.TYPE_DEL :		return "����";
		default :		return "?";
		}
	}
	
	/**		�α� ��� �� ���� ���� ����		*/
	private String getLogTargetName(ControlResponse controlResponse){
		switch( controlResponse.getControlTarget() ){
		case ControlResponse.TARGET_DEVICE :						return "��ġ";
		case ControlResponse.TARGET_LOGICALGROUP :		return "�����׷�";
		case ControlResponse.TARGET_REPORTER :				return "������";
		case ControlResponse.TARGET_DEVICE_DRIVER :		return "��ġ����̹�";
		case ControlResponse.TARGET_REPORTER_DRIVER :	return "�����͵���̹�";
		default :		return "?";
		}
	}
	
	/**		��ġ ó�� ��û �Ķ���� ��ȿ�� �˻�		*/
	private ControlResponse validationDevice(Device device, boolean isAdd){
		// ��ġ�� �˻�
		ControlResponse controlResponse = this.validationString(ControlResponse.TARGET_DEVICE, isAdd, this.makeResourceBundle().getResourceValue("NM_DEV"), device.getDeviceName());
		if( controlResponse != null )		return controlResponse;
		device.setDeviceName(device.getDeviceName().trim());		// ���� ����
		
		// ��Ź�Ŀ� ���� �˻�
		switch(device.getCommType()){
		case Device.COMM_TCP_TYPE :			return this.validationDeviceTcp(device, isAdd);
		case Device.COMM_SERIAL_TYPE :		return this.validationDeviceSerial(device, isAdd);
		default :
			// ���ǵ��� ���� ��� ��� �ڵ� �� [��û��:XX] [��ȿ��:YY]
			ResourceBundle rb = this.makeResourceBundle();
			return new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd), rb.getResourceValue("ERR_1_1") + device.getCommType() + rb.getResourceValue("ERR_1_2") + Device.COMM_SERIAL_TYPE + " , " + Device.COMM_TCP_TYPE + "]");
		}
	}
	
	/**		��ġ ó�� ��û �Ķ���� ��ȿ�� �˻�. TCP ��� Ÿ��		*/
	private ControlResponse validationDeviceTcp(Device device, boolean isAdd){
		ControlResponse controlResponse;
		
		// IP �˻�
		if( device.getTcpMode() == Device.TCP_MODE_CLIENT ){
			// Ŭ���̾�Ʈ ����� ��� IP �˻�
			controlResponse = this.validationIPAddress(ControlResponse.TARGET_DEVICE, isAdd, device.getTcpIp());
			if( controlResponse != null )		return controlResponse;
			device.setTcpIp(device.getTcpIp().trim());		// ���� ����
			
		}else{
			device.setTcpIp(null);		// ���� ����� ��� IP �� �ʿ� ����
		}
		
		
		// PORT �˻�
		controlResponse = this.validationTCPPort(ControlResponse.TARGET_DEVICE, isAdd, device.getPort());
		if( controlResponse != null )		return controlResponse;
		
		// ��ȿ�� �˻� ���
		return null;
	}
	
	/**		������ ó�� ��û �Ķ���� ��ȿ�� �˻�. TCP ��� Ÿ��		*/
	private ControlResponse validationReporterTcp(Reporter reporter, boolean isAdd){
		// IP �˻�
		ControlResponse controlResponse = this.validationIPAddress(ControlResponse.TARGET_REPORTER, isAdd, reporter.getReporterIp());
		if( controlResponse != null )		return controlResponse;
		reporter.setReporterIp(reporter.getReporterIp().trim());		// ���� ����
		
		// PORT �˻�
		controlResponse = this.validationTCPPort(ControlResponse.TARGET_REPORTER, isAdd, reporter.getReporterPort());
		if( controlResponse != null )		return controlResponse;
		
		// ��ȿ�� �˻� ���
		return null;
	}
	
	
	/**		��ġ ó�� ��û �Ķ���� ��ȿ�� �˻�. TCP ��� Ÿ��		*/
	private ControlResponse validationDeviceSerial(Device device, boolean isAdd){
		// PORT �˻�
		int intValue = device.getPort();
		if( intValue <= 0 ){
			// SERIAL PORT �� ���� ���� [��û��:XX] [��밪=1�̻�]
			ResourceBundle rb = this.makeResourceBundle();
			return new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd), rb.getResourceValue("ERR_3_1") + intValue + rb.getResourceValue("ERR_3_2"));
		}
		
		// BaudRate �˻�
		ControlResponse controlResponse = this.validationDeviceSerialBaudRate(isAdd, device.getBaudRate());
		if( controlResponse != null )		return controlResponse;
				
		// FlowControl �˻�
		controlResponse = this.validationDeviceSerialFlowControl(isAdd, device.getFlowControl());
		if( controlResponse != null )		return controlResponse;
		
		// DataBits �˻�
		controlResponse = this.validationDeviceSerialDataBits(isAdd, device.getDataBits());
		if( controlResponse != null )		return controlResponse;
		
		// StopBits �˻�
		controlResponse = this.validationDeviceSerialStopBits(isAdd, device.getStopBits());
		if( controlResponse != null )		return controlResponse;
		
		// Parity �˻�
		controlResponse = this.validationDeviceSerialParity(isAdd, device.getParity());
		if( controlResponse != null )		return controlResponse;
		
		return null;
	}
	
	/**		�ø������ DataBits �� ��ȿ�� �˻�		*/
	private ControlResponse validationDeviceSerialParity(boolean isAdd, int value){
		switch(value){
		case RXTXPort.PARITY_EVEN :		break;
		case RXTXPort.PARITY_MARK :		break;
		case RXTXPort.PARITY_NONE :		break;
		case RXTXPort.PARITY_ODD :			break;
		case RXTXPort.PARITY_SPACE :		break;
		default : 	
			// SERIAL Parity �� ���� [��û��:XX]
			return new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd), "SERIAL Port " + this.makeResourceBundle().getResourceValue("ERR_4") + value + "]");
		}
		
		return null;
	}
	
	/**		�ø������ StopBits �� ��ȿ�� �˻�		*/
	private ControlResponse validationDeviceSerialStopBits(boolean isAdd, int value){
		switch(value){
		case RXTXPort.STOPBITS_1 :		break;
		case RXTXPort.STOPBITS_1_5 :	break;
		case RXTXPort.STOPBITS_2 :		break;
		default : 	
			// SERIAL StopBits �� ���� [��û��:XX]
			return new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd), "SERIAL StopBits " + this.makeResourceBundle().getResourceValue("ERR_4") + value + "]");
		}
		
		return null;
	}
	
	/**		�ø������ DataBits �� ��ȿ�� �˻�		*/
	private ControlResponse validationDeviceSerialDataBits(boolean isAdd, int value){
		switch(value){
		case RXTXPort.DATABITS_5 :		break;
		case RXTXPort.DATABITS_6 :		break;
		case RXTXPort.DATABITS_7 :		break;
		case RXTXPort.DATABITS_8 :		break;
		default : 	
			// SERIAL DataBits �� ���� [��û��:XX]
			return new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd), "SERIAL DataBits " + this.makeResourceBundle().getResourceValue("ERR_4") + value + "]");
		}
		
		return null;
	}
	
	/**		�ø������ FlowControl �� ��ȿ�� �˻�		*/
	private ControlResponse validationDeviceSerialFlowControl(boolean isAdd, int value){
		switch(value){
		case RXTXPort.FLOWCONTROL_NONE :				break;
		case RXTXPort.FLOWCONTROL_RTSCTS_IN :			break;
		case RXTXPort.FLOWCONTROL_RTSCTS_OUT :		break;
		case RXTXPort.FLOWCONTROL_XONXOFF_IN :		break;
		case RXTXPort.FLOWCONTROL_XONXOFF_OUT	 :	break;
		default : 	
			// SERIAL FlowControl �� ���� [��û��:XX]
			return new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd), "SERIAL FlowControl " + this.makeResourceBundle().getResourceValue("ERR_4") + value + "]");
		}
		
		return null;
	}
	
	/**		�ø������ BaudRate �� ��ȿ�� �˻�		*/
	private ControlResponse validationDeviceSerialBaudRate(boolean isAdd, int value){
		switch( value ){
		case 110:			break;
		case 300:			break;
		case 1200:		break;
		case 2400:		break;
		case 4800:		break;
		case 9600:		break;
		case 19200:		break;
		case 38400:		break;
		case 57600:		break;
		case 115200:	break;
		case 230400:	break;
		case 460800:	break;
		case 921600:	break;
		default:	
			// SERIAL BaudRate �� ���� [��û��:XX]
			return new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd), "SERIAL BaudRate " + this.makeResourceBundle().getResourceValue("ERR_4") + value + "]"); 
		}
		
		return null;
	}
	
	/**
	 * 	������ �� ��ȿ�� �˻�
	 * @param control		���� ���
	 * @param isAdd			true �� ��� ���, false �� ��� ����
	 * @param value			�˻� ��
	 * @return					��ȿ�� �˻� ��� �̻��� ���� ��� null
	 */
	private ControlResponse validationIPAddress(char control, boolean isAdd, String value){
		if( value == null )		return new ControlResponse(control, this.getControlType(isAdd), "IP Value NULL");
		value = value.trim();

		String[] ips = value.split("\\.");
		if( ips.length != 4 && ips.length != 6 )	// IP4 �Ǵ� IP6 �� �ϳ����� �Ѵ�
			return new ControlResponse(control, this.getControlType(isAdd), "IP " + this.makeResourceBundle().getResourceValue("ERR_4") + value + "]");		// IP �� ���� [��û��:XX]
		
		int intValue;
		for(String ip : ips){
			try{
				intValue = Integer.parseInt(ip);
				if( intValue < 0 || intValue > 255 )		// �������� ������ 0 ~ 255 ���̿��� �Ѵ�
					return new ControlResponse(control, this.getControlType(isAdd), "IP " + this.makeResourceBundle().getResourceValue("ERR_4") + value + "]");	// IP �� ���� [��û��:XX]
			}catch(Exception e){
				return new ControlResponse(control, this.getControlType(isAdd), "IP " + this.makeResourceBundle().getResourceValue("ERR_4") + value + "]");	// IP �� ���� [��û��:XX]
			}
		}
		
		return null;
	}
	
	/**
	 * 	TCP ��Ʈ ��ȿ�� �˻�
	 * @param control	���� ���
	 * @param isAdd		true �� ��� ���, false �� ��� ����
	 * @param port		�˻� ��
	 * @return				��ȿ�� �˻� ��� �̻��� ���� ��� null
	 */
	private ControlResponse validationTCPPort(char control, boolean isAdd, int port){
		if( port < 50 || port > 65535 ){
			// TCP PORT �� ���� ���� [��û��:XX] [��밪:50~65535]
			ResourceBundle rb = this.makeResourceBundle();
			return new ControlResponse(control, this.getControlType(isAdd), rb.getResourceValue("ERR_2_1") + port + rb.getResourceValue("ERR_2_2"));
		}else{
			return null;
		}
	}
	
	/**		
	 * 	���ڿ� �� ��ȿ�� �˻�
	 * 
	 * @param control			���� ���
	 * @param isAdd				true �� ��� ���, false �� ��� ����
	 * @param fieldName		��ȿ�� �˻� ���
	 * @param value				�˻� ��
	 * @return						��ȿ�� �˻� ��� �̻��� ���� ��� null
	 */
	private ControlResponse validationString(char control, boolean isAdd, String fieldName, String value){
		if( value == null )		return new ControlResponse(control, this.getControlType(isAdd), fieldName + "Name NULL");
		value = value.trim();
		
		int length = value.length();
		if( length < BomzMiddleware.MIN_NAME_LENGTH || length > BomzMiddleware.MAX_NAME_LENGTH ){
			// fieldName �� �ڸ��� ���� [��û��:XX] [������:YY~ZZ]
			ResourceBundle rb = this.makeResourceBundle();
			return new ControlResponse(control, this.getControlType(isAdd), fieldName + " " + rb.getResourceValue("ERR_5_1") + value + rb.getResourceValue("ERR_5_2") + BomzMiddleware.MIN_NAME_LENGTH + "~" + BomzMiddleware.MAX_NAME_LENGTH + "]");
		}
		
		return null;
	}
	
	/**
	 * 	��� �Ǵ� ���� �ڵ� �� ����
	 * @param isAdd		true �� ��� ���
	 * @return				�ڵ尪
	 */
	private char getControlType(boolean isAdd){
		return isAdd?ControlResponse.TYPE_ADD:ControlResponse.TYPE_UPD;
	}
	
	/**		��ġ ��� �Ǵ� ���� ó��		*/
	private ControlResponse addOrUpdDevice(Device device, boolean isAdd, BomzProgressBar progressBar){
		ControlResponse controlResponse = null;
		if( progressBar != null )		progressBar.nextProgressStep("Wait");
		
		try{
			
			this.logDevice(device, isAdd);												// ��û �Ķ���� �α� ó��
			if( progressBar != null )		progressBar.nextProgressStep("Parameter Check");

			controlResponse = this.validationDevice(device, isAdd);		// �� ��ȿ�� �˻�
			if( controlResponse != null )			return controlResponse;	// ��ȿ�� ���� �� ��� ����
			
			synchronized( this.lock ){
				SqlSession session = null;
				try {
					if( progressBar != null )		progressBar.nextProgressStep("Database Connect");
					session = DatabaseService.getInstance().openSession();				// ��� ���� ����
					
					Driver driver = DatabaseService.getInstance().selectDeviceDriver(session, device);
					if( driver == null )		throw new Exception("Device Driver Loading Error [Driver ID:" + device.getDriverId() + "]");
					
					if( isAdd ){	// ��� ó��
						if( progressBar != null )		progressBar.nextProgressStep("Database Insert");
						DatabaseService.getInstance().addDevice(session, device);		// ��� �߰�
						
						if( progressBar != null )		progressBar.nextProgressStep("Device Insert");
						DeviceService.getInstance().addDevice(device, driver.getDriverJarFile(), true);				// �޸� �߰�
					}else{			// ���� ó��
						if( progressBar != null )		progressBar.nextProgressStep("Database Update");
						DatabaseService.getInstance().updDevice(session, device);		// ��� ����
						
						if( progressBar != null )		progressBar.nextProgressStep("Device Update");
						DeviceService.getInstance().updDevice(device, driver.getDriverJarFile());						// �޸� ����
					}
					
					if( progressBar != null )		progressBar.nextProgressStep("Queue Init");
					QueueManagerService.getInstance().updateQueues();			// ť ����
					
					if( progressBar != null )		progressBar.nextProgressStep("Database Commit");
					session.commit();
					
					if( progressBar != null )		progressBar.nextProgressStep("Success");
					controlResponse = new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd));	// ���� ���� ����
					return controlResponse;
				} catch (Exception e) {
					// ��� ���� �� ���� �߻�
					if( session != null )		session.rollback();
					
					this.logger.error("��ġ {} ���� [ID={}]", isAdd?"���":"����", device.getDeviceId(), e);
					
					if( progressBar != null )		progressBar.nextProgressStep("Fail");
					
					controlResponse = new ControlResponse(ControlResponse.TARGET_DEVICE, this.getControlType(isAdd), e.getMessage());	// ���� ���� ����
					return controlResponse;
				}finally{
					if( session != null )		session.close();
				}
			}
			
		}finally{
			// ó�� ��� �α� ó��
			this.log(controlResponse, device.getDeviceId(), device.getDeviceName());
		}
	}
	
	/**		�����׷� ��� ��û		*/
	public void addLogicalGroup(LogicalGroup logicalGroup, BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.LOGICALGROUP_ADD, eventHandler, progressBar, logicalGroup);
	}
	
	/**		�����׷� ��� ��û (������ ȣ���)		*/
	public ControlResponse addLogicalGroup(LogicalGroup logicalGroup){
		if( logicalGroup == null )		new ControlResponse(ControlResponse.TARGET_LOGICALGROUP, ControlResponse.TYPE_ADD, "Parameter is null");
		
		return this.addOrUpdLogicalGroup(logicalGroup, true, null);
	}
	
	/**		�����׷� ���� ��û		*/
	public void updLogicalGroup(LogicalGroup logicalGroup, BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.LOGICALGROUP_UPD, eventHandler, progressBar, logicalGroup);
	}
	
	/**		�����׷� ���� ��û (������ ȣ���)		*/
	public ControlResponse updLogicalGroup(LogicalGroup logicalGroup){
		if( logicalGroup == null )		new ControlResponse(ControlResponse.TARGET_LOGICALGROUP, ControlResponse.TYPE_UPD, "Parameter is null");
		
		return this.addOrUpdLogicalGroup(logicalGroup, false, null);
	}
	
	/**		�ټ��� �����׷� ���� ��û		*/
	public void delLogicalGroup(EventHandler<ActionEvent> eventHandler, List<LogicalGroup> logicalGroupList, BomzProgressBar progressBar){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.LOGICALGROUP_DEL, eventHandler, progressBar, logicalGroupList);
	}
	
	/**		�ټ��� �����׷� ���� ��û		*/
	private ControlResponse delLogicalGroup(List<LogicalGroup> logicalGroupList, BomzProgressBar progressBar){
		if( progressBar != null )		progressBar.nextProgressStep("Wait");
		
		if( logicalGroupList == null || logicalGroupList.isEmpty() )		
			return new ControlResponse(ControlResponse.TARGET_LOGICALGROUP, ControlResponse.TYPE_DEL);	// ���� ���� ����
		
		if( progressBar != null )		progressBar.nextProgressStep();
		
		int size = logicalGroupList.size();
		List<Integer> failIdList = new ArrayList<Integer>();
		for(int i=0; i < size; i++){
			if( progressBar != null )		progressBar.updateProgressMessage("Delete (" + (i+1) + "/" + size + ")");
			if( !this.delLogicalGroup(logicalGroupList.get(i).getLogicalGroupId()).isSuccess() )		failIdList.add(logicalGroupList.get(i).getLogicalGroupId());
		}
		
		if( progressBar != null )		progressBar.nextProgressStep("Success");
		
		return this.multiDelControlResponse(ControlResponse.TARGET_LOGICALGROUP, failIdList);
	}
	
	/**		�����׷� ���� ��û		*/
	public ControlResponse delLogicalGroup(int logicalGroupId){
		ControlResponse controlResponse = null;
		
		try{
			
			if( this.logger.isDebugEnabled() )		this.logger.debug("�����׷� ���� ��û [�����׷���̵�={}]", logicalGroupId);
			
			synchronized( this.lock ){
				SqlSession session = null;
				try {
					session = DatabaseService.getInstance().openSession();			// ��� ���� ����
					
					DatabaseService.getInstance().delLogicalGroup(session, logicalGroupId);	// ��� ����
					LogicalGroupService.getInstance().delLogicalGroup(logicalGroupId);			// �޸� ����
					QueueManagerService.getInstance().updateQueues();			// ť ����
					
					session.commit();
					controlResponse =  new ControlResponse(ControlResponse.TARGET_LOGICALGROUP, ControlResponse.TYPE_DEL);	// ���� ���� ����
					return controlResponse;
				} catch (Exception e) {
					// ��� ���� �� ���� �߻�
					if( session != null )		session.rollback();
					
					this.logger.error("�����׷� ���� ���� [ID={}]", logicalGroupId, e);
					
					controlResponse = new ControlResponse(ControlResponse.TARGET_LOGICALGROUP, ControlResponse.TYPE_DEL, e.getMessage());	// ���� ���� ����
					return controlResponse;
				}finally{
					if( session != null )		session.close();
				}
			}
			
		}finally{
			this.log(controlResponse, logicalGroupId, null);
		}
	}
	
	/**		�����׷� ��� �Ǵ� ���� ó��		*/
	private ControlResponse addOrUpdLogicalGroup(LogicalGroup logicalGroup, boolean isAdd, BomzProgressBar progressBar){
		ControlResponse controlResponse = null;
		
		if( progressBar != null )		progressBar.nextProgressStep("Wait");
		
		try{
			this.logLogicalGroup(logicalGroup, isAdd);											// ��û �Ķ���� �α� ó��
			
			if( progressBar != null )		progressBar.nextProgressStep("Parameter Check");
			controlResponse = this.validationString(ControlResponse.TARGET_LOGICALGROUP, isAdd, this.makeResourceBundle().getResourceValue("NM_LOGI"), logicalGroup.getLogicalGroupName());
			if( controlResponse != null )		return controlResponse;
			
			synchronized( this.lock ){
				SqlSession session = null;
				try {
					if( progressBar != null )		progressBar.nextProgressStep("Database Connect");
					session = DatabaseService.getInstance().openSession();				// ��� ���� ����
					
					if( isAdd ){	// ��� ó��
						if( progressBar != null )		progressBar.nextProgressStep("Database Insert");
						DatabaseService.getInstance().addLogicalGroup(session, logicalGroup);		// ��� �߰�
						
						if( progressBar != null )		progressBar.nextProgressStep("LogicalGroup Insert");
						LogicalGroupService.getInstance().addLogicalGroup(logicalGroup, true);			// �޸� �߰�
					}else{			// ���� ó��
						if( progressBar != null )		progressBar.nextProgressStep("Database Update");
						DatabaseService.getInstance().updLogicalGroup(session, logicalGroup);		// ��� ����
						
						if( progressBar != null )		progressBar.nextProgressStep("LogicalGroup Update");
						LogicalGroupService.getInstance().updLogicalGroup(logicalGroup);			// �޸� ����
					}
					
					if( progressBar != null )		progressBar.nextProgressStep("Queue Init");
					QueueManagerService.getInstance().updateQueues();			// ť ����
					
					if( progressBar != null )		progressBar.nextProgressStep("Database Commit");
					session.commit();
					
					if( progressBar != null )		progressBar.nextProgressStep("Success");
					controlResponse = new ControlResponse(ControlResponse.TARGET_LOGICALGROUP, this.getControlType(isAdd));		// ���� ���� ����
					return controlResponse;
				} catch (Exception e) {
					// ��� ���� �� ���� �߻�
					if( session != null )		session.rollback();
					
					this.logger.error("�����׷� {} ���� [ID={}]", isAdd?"���":"����", logicalGroup.getLogicalGroupDeviceMapList(), e);
					
					if( progressBar != null )		progressBar.nextProgressStep("Fail");
					controlResponse = new ControlResponse(ControlResponse.TARGET_LOGICALGROUP, this.getControlType(isAdd), e.getMessage());	// ���� ���� ����
					return controlResponse;
				}finally{
					if( session != null )		session.close();
				}
			}
			
		}finally{
			// ó�� ��� �α� ó��
			this.log(controlResponse, logicalGroup.getLogicalGroupId(), logicalGroup.getLogicalGroupName());
		}
	}
	
	/**		�����׷� �߰�/���� ��û �� ��û �Ķ���� �α�		*/
	private void logLogicalGroup(LogicalGroup logicalGroup, boolean isAdd){
		if( !this.logger.isDebugEnabled() )		return;		// ����� ���� �̻� ó��
		
		this.logger.debug("�����׷� {} ��û", isAdd?"���":"����");
		this.logger.debug("��û �Ķ���� ����");
		
		if( !isAdd )		this.logger.debug("�����׷� ���̵� : {}", logicalGroup.getLogicalGroupId());		// ������ ��츸 ó��
		this.logger.debug("�����׷�� : {}", logicalGroup.getLogicalGroupName());
		
		List<LogicalGroupDeviceMap> lgdList = logicalGroup.getLogicalGroupDeviceMapList();
		if( lgdList == null || lgdList.isEmpty() ){
			this.logger.debug("�����׷�� ��ġ ���� ���� ����");
		}else{
			this.logger.debug("�����׷�� ��ġ ���� ���� [��ġ���̵�] [���׳����̵�]");
			for(LogicalGroupDeviceMap lgd : lgdList)
				this.logger.debug("��ġID : {}, ���׳�ID : {}", lgd.getDeviceId(), lgd.getAntennaId());
		}
		
		List<LogicalGroupReporterMap> rList = logicalGroup.getLogicalGroupReporterMapList();
		if( rList == null || rList.isEmpty() ){
			this.logger.debug("�����׷�� ������ ���� ���� ����");
		}else{
			this.logger.debug("�����׷�� ������ ���� ���� [�����;��̵�]");
			for(LogicalGroupReporterMap rId : rList)				this.logger.debug("������ID : {}", rId.getReporterId());
		}
		
	}
	
	/**		������ ��� ��û		*/
	public void addReporter(Reporter reporter, BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.REPORTER_ADD, eventHandler, progressBar, reporter);
	}
	
	/**		������ ��� ó�� (������ ȣ���)		*/
	public ControlResponse addReporter(Reporter reporter){
		if( reporter == null )		new ControlResponse(ControlResponse.TARGET_REPORTER, ControlResponse.TYPE_ADD, "Parameter is null");
		
		return this.addOrUpdReporter(reporter, true, null);
	}
	
	/**		������ ���� ��û		*/
	public void updReporter(Reporter reporter, BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.REPORTER_UPD, eventHandler, progressBar, reporter);
	}
	
	/**		������ ���� ó�� (������ ȣ���)		*/
	public ControlResponse updReporter(Reporter reporter){
		if( reporter == null )		new ControlResponse(ControlResponse.TARGET_REPORTER, ControlResponse.TYPE_UPD, "Parameter is null");
		
		return this.addOrUpdReporter(reporter, false, null);
	}
	
	/**		�ټ��� ������ ���� ��û		*/
	public void delReporter(EventHandler<ActionEvent> eventHandler, List<Reporter> reporterList, BomzProgressBar progressBar){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.REPORTER_DEL, eventHandler, progressBar, reporterList);
	}
	
	/**		�ټ��� ������ ���� ��û		*/
	private ControlResponse delReporter(List<Reporter> reporterList, BomzProgressBar progressBar){
		
		if( progressBar != null )		progressBar.nextProgressStep("Wait");
		
		if( reporterList == null || reporterList.isEmpty() )		
			return new ControlResponse(ControlResponse.TARGET_REPORTER, ControlResponse.TYPE_DEL);	// ���� ���� ����
		
		if( progressBar != null )		progressBar.nextProgressStep();
		
		int size = reporterList.size();
		List<Integer> failIdList = new ArrayList<Integer>();
		for(int i=0; i < size; i++){
			if( progressBar != null )		progressBar.updateProgressMessage("Delete (" + (i+1) + "/" + size + ")");
			if( !this.delReporter(reporterList.get(i).getReporterId()).isSuccess() )		failIdList.add(reporterList.get(i).getReporterId());
		}
		
		if( progressBar != null )		progressBar.nextProgressStep("Success");
		
		return this.multiDelControlResponse(ControlResponse.TARGET_REPORTER, failIdList);
	}
	
	/**		������ ���� ��û		*/
	public ControlResponse delReporter(int reporterId){
		ControlResponse controlResponse = null;
		
		try{
			
			if( this.logger.isDebugEnabled() )		this.logger.debug("������ ���� ��û [�����;��̵�={}]", reporterId);
			
			synchronized( this.lock ){
				SqlSession session = null;
				try {
					session = DatabaseService.getInstance().openSession();				// ��� ���� ����
					
					DatabaseService.getInstance().delReporter(session, reporterId);	// ��� ����
					ReporterService.getInstance().delReporter(reporterId);				// �޸� ����
					QueueManagerService.getInstance().updateQueues();				// ť ����
					
					session.commit();
					controlResponse =  new ControlResponse(ControlResponse.TARGET_REPORTER, ControlResponse.TYPE_DEL);	// ���� ���� ����
					return controlResponse;
				} catch (Exception e) {
					// ��� ���� �� ���� �߻�
					if( session != null )		session.rollback();
					
					this.logger.error("������ ���� ���� [ID={}]", reporterId, e);
					
					controlResponse = new ControlResponse(ControlResponse.TARGET_REPORTER, ControlResponse.TYPE_DEL, e.getMessage());	// ���� ���� ����
					return controlResponse;
				}finally{
					if( session != null )		session.close();
				}
			}
			
		}finally{
			this.log(controlResponse, reporterId, null);
		}
	}
	
	/**		������ ��� �Ǵ� ���� ó��		*/
	private ControlResponse addOrUpdReporter(Reporter reporter, boolean isAdd, BomzProgressBar progressBar){
		ControlResponse controlResponse = null;
		
		if( progressBar != null )		progressBar.nextProgressStep("Wait");
		
		try{
			this.logReporter(reporter, isAdd);											// ��û �Ķ���� �α� ó��
			
			if( progressBar != null )		progressBar.nextProgressStep("Parameter Check");
			controlResponse = this.validationReporter(reporter, isAdd);	// �� ��ȿ�� �˻�
			if( controlResponse != null )			return controlResponse;		// ��ȿ�� ���� �� ��� ����
			
			synchronized( this.lock ){
				SqlSession session = null;
				try {
					if( progressBar != null )		progressBar.nextProgressStep("Database Connect");
					session = DatabaseService.getInstance().openSession();				// ��� ���� ����
					
					Driver driver = DatabaseService.getInstance().selectReporterDriver(session, reporter);
					if( driver == null )		throw new Exception("Device Driver Loading Error [Driver ID:" + reporter.getDriverId() + "]");
					
					
					if( isAdd ){	// ��� ó��
						if( progressBar != null )		progressBar.nextProgressStep("Database Insert");
						DatabaseService.getInstance().addReporter(session, reporter);		// ��� �߰�
						
						if( progressBar != null )		progressBar.nextProgressStep("Reporter Insert");
						ReporterService.getInstance().addReporter(reporter, driver.getDriverJarFile(), true);				// �޸� �߰�
					}else{			// ���� ó��
						if( progressBar != null )		progressBar.nextProgressStep("Database Update");
						DatabaseService.getInstance().updReporter(session, reporter);		// ��� ����
						
						if( progressBar != null )		progressBar.nextProgressStep("Reporter Update");
						ReporterService.getInstance().updReporter(reporter, driver.getDriverJarFile());						// �޸� ����
					}
					
					if( progressBar != null )		progressBar.nextProgressStep("Queue Init");
					QueueManagerService.getInstance().updateQueues();			// ť ����
					
					if( progressBar != null )		progressBar.nextProgressStep("Database Commit");
					session.commit();
					
					if( progressBar != null )		progressBar.nextProgressStep("Success");
					controlResponse = new ControlResponse(ControlResponse.TARGET_REPORTER, this.getControlType(isAdd));		// ���� ���� ����
					return controlResponse;
				} catch (Exception e) {
					// ��� ���� �� ���� �߻�
					if( session != null )		session.rollback();
					
					this.logger.error("������ {} ���� [ID={}]", isAdd?"���":"����", reporter.getReporterId(), e);
					
					if( progressBar != null )		progressBar.nextProgressStep("Fail");
					controlResponse = new ControlResponse(ControlResponse.TARGET_REPORTER, this.getControlType(isAdd), e.getMessage());	// ���� ���� ����
					return controlResponse;
				}finally{
					if( session != null )		session.close();
				}
			}
			
		}finally{
			// ó�� ��� �α� ó��
			this.log(controlResponse, reporter.getReporterId(), reporter.getReporterName());
		}
	}
	
	/**		��ġ ó�� ��û �Ķ���� ��ȿ�� �˻�		*/
	private ControlResponse validationReporter(Reporter reporter, boolean isAdd){
		// �����͸� �˻�
		ControlResponse controlResponse = this.validationString(ControlResponse.TARGET_REPORTER, isAdd, this.makeResourceBundle().getResourceValue("NM_REP"), reporter.getReporterName());
		if( controlResponse != null )		return controlResponse;
		reporter.setReporterName(reporter.getReporterName().trim());		// ���� ����
		
		controlResponse = this.validationReporterTcp(reporter, isAdd);
		if( controlResponse != null )		return controlResponse;
		
		return null;
	}
	
	/**		������ �߰�/���� ��û �� ��û �Ķ���� �α�		*/
	private void logReporter(Reporter reporter, boolean isAdd){
		if( !this.logger.isDebugEnabled() )		return;		// ����� ���� �̻� ó��
		
		this.logger.debug("������ {} ��û", isAdd?"���":"����");
		this.logger.debug("��û �Ķ���� ����");
		
		if( !isAdd )		this.logger.debug("������ ���̵� : {}", reporter.getReporterId());		// ������ ��츸 ó��
		this.logger.debug("�����͸� : {}", reporter.getReporterName());
		this.logger.debug("����̹� : {}", reporter.getDriverId());
		this.logger.debug("Ÿ�̸ӽð� : {}", reporter.getTimerRepeatTime());
		this.logger.debug("������ : {}", reporter.getReporterIp());
		this.logger.debug("��Ʈ : {}", reporter.getReporterPort());
		
		List<ReporterLogicalGroupMap> rList = reporter.getLogicalGroupMapList();
		if( rList == null || rList.isEmpty() ){
			this.logger.debug("�����Ϳ� �����׷� ���� ���� ����");
		}else{
			this.logger.debug("�����Ϳ� �����׷� ���� ���� [�����׷���̵�]");
			for(ReporterLogicalGroupMap rId : rList)				this.logger.debug("�����׷�ID : {}", rId.getLogicalGroupId());
		}
		
	}
	
	/**		�ټ��� ������ ���� ��û �� �Ϻκи� ������ ��� ������ ���̵� ����� ������ ControlResponse ����		*/
	private ControlResponse multiDelControlResponse(char target, List<Integer> failIdList){
		// ��� ��ġ ���� ���� ��
		if( failIdList.isEmpty() )		
			return new ControlResponse(target, ControlResponse.TYPE_DEL);	// ���� ���� ����
		
		// ���� ��û ��ġ �� ������ �׸��� ���� ���
		int size = failIdList.size();
		StringBuilder errMsgBuffer = new StringBuilder();
		for(int i=0; i < size; i++){
			if( i != 0 )		errMsgBuffer.append(", ");
			errMsgBuffer.append(failIdList.get(i));
		}
		
		ControlResponse response = new ControlResponse(target, ControlResponse.TYPE_DEL, null);	// ���� ���� ����
		response.setErrMessage( this.getLogTargetName(response) + " " + this.makeResourceBundle().getResourceValue("ERR_6") + " [ID:" + errMsgBuffer.toString() + "]");	// �������� [ID:XX]

		return response;
	}

	/**		�ټ��� ��ġ ���� ��û		*/
	public void delDriver(EventHandler<ActionEvent> eventHandler, char target, List<Driver> driverList, BomzProgressBar progressBar){
		if( eventHandler == null )		return;
		
		this.makeTask(
				target == ControlResponse.TARGET_DEVICE_DRIVER ? ControllerState.DRIVER_DEVICE_DEL : ControllerState.DRIVER_REPORTER_DEL, 
				eventHandler, progressBar, driverList
			);
	}
		
	/**		�ټ��� ����̹� ���� ��û		*/
	public ControlResponse delDriver(char target, List<Driver> driverList, BomzProgressBar progressBar){
		if( progressBar != null )		progressBar.nextProgressStep("Wait");
		
		if( driverList == null || driverList.isEmpty() )		
			return new ControlResponse(target, ControlResponse.TYPE_DEL);	// ���� ���� ����
		
		if( progressBar != null )		progressBar.nextProgressStep();
		
		int size = driverList.size();
		List<Integer> failIdList = new ArrayList<Integer>();
		for(int i=0; i < size; i++){
			if( progressBar != null )		progressBar.updateProgressMessage("Delete (" + (i+1) + "/" + size + ")");
			if( !this.delDriver(target, driverList.get(i)).isSuccess() )		failIdList.add(driverList.get(i).getDriverId());
		}
		
		return this.multiDelControlResponse(target, failIdList);
	}
	
	/**		����̹� ���� ��û		*/
	public ControlResponse delDriver(char target, Driver driver){
		ControlResponse controlResponse = null;
		
		try{
			
			if( this.logger.isDebugEnabled() )		this.logger.debug("{} ����̹� ���� ��û [����̹����̵�={}]", target==ControlResponse.TARGET_DEVICE_DRIVER?"��ġ":"������", driver.getDriverId());
			
			synchronized( this.lock ){
				SqlSession session = null;
				try {
					session = DatabaseService.getInstance().openSession();			// ��� ���� ����
					
					driver.setDriverTarget(target);
					DatabaseService.getInstance().delDriver(session, driver);	// ��� ����
					
					session.commit();
					controlResponse =  new ControlResponse(target, ControlResponse.TYPE_DEL);	// ���� ���� ����
					return controlResponse;
					
				} catch (Exception e) {
					// ��� ���� �� ���� �߻�
					if( session != null )		session.rollback();
					
					this.logger.error("{} ����̹� ���� ���� [ID={}]", target==ControlResponse.TARGET_DEVICE_DRIVER?"��ġ":"������", driver.getDriverId(), e);
					
					controlResponse = new ControlResponse(target, ControlResponse.TYPE_DEL, e.getMessage());	// ���� ���� ����
					return controlResponse;
				}finally{
					if( session != null )		session.close();
				}
			}
			
		}finally{
			this.log(controlResponse, driver.getDriverId(), null);
		}
	}
	
	/**		����̹� ��� ��û		*/
	public void addDriver(Driver driver, BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.DRIVER_ADD, eventHandler, progressBar, driver);
	}
	
	/**		��ġ ����̹� ��� (������ ȣ���)		*/
	public ControlResponse addDeviceDriver(Driver driver){
		if( driver == null )		new ControlResponse(ControlResponse.TARGET_DEVICE_DRIVER, ControlResponse.TYPE_ADD, "Parameter is null");
		return this.addOrUpdDriver(driver, true, null);
	}
	
	/**		������ ����̹� ��� (������ ȣ���)		*/
	public ControlResponse addReporterDriver(Driver driver){
		if( driver == null )		new ControlResponse(ControlResponse.TARGET_REPORTER_DRIVER, ControlResponse.TYPE_ADD, "Parameter is null");
		return this.addOrUpdDriver(driver, true, null);
	}
	
	/**		��ġ ����̹� ���� (������ ȣ���)		*/
	public ControlResponse updDeviceDriver(Driver driver){
		if( driver == null )		new ControlResponse(ControlResponse.TARGET_DEVICE_DRIVER, ControlResponse.TYPE_UPD, "Parameter is null");
		return this.addOrUpdDriver(driver, false, null);
	}
	
	/**		������ ����̹� ���� (������ ȣ���)		*/
	public ControlResponse updReporterDriver(Driver driver){
		if( driver == null )		new ControlResponse(ControlResponse.TARGET_REPORTER_DRIVER, ControlResponse.TYPE_UPD, "Parameter is null");
		return this.addOrUpdDriver(driver, false, null);
	}
	
	/**		����̹� ���� ��û		*/
	public void updDriver(Driver driver, BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		if( eventHandler == null )		return;
		
		this.makeTask(ControllerState.DRIVER_UPD, eventHandler, progressBar, driver);
	}
	
	/**		����̹� ��� �Ǵ� ���� ó��		*/
	private ControlResponse addOrUpdDriver(Driver driver, boolean isAdd, BomzProgressBar progressBar){
		ControlResponse controlResponse = null;
		if( progressBar != null )		progressBar.nextProgressStep("Wait");
		
		try{
			this.logDriver(driver, isAdd);											// ��û �Ķ���� �α� ó��
			
			if( progressBar != null )		progressBar.nextProgressStep("Parameter Check");
			controlResponse = this.validationDriver(driver, isAdd);		// �� ��ȿ�� �˻�
			if( controlResponse != null )			return controlResponse;	// ��ȿ�� ���� �� ��� ����

			// ����̹� ��ȿ�� �˻�
			DriverLoader loader = new DriverLoader();
			if( driver.getDriverJarFile() != null && !loader.validationDriver(driver.getDriverJarFile(), driver.getDriverTarget()==ControlResponse.TARGET_DEVICE_DRIVER) ){
				controlResponse = new ControlResponse(driver.getDriverTarget(), this.getControlType(isAdd), this.makeResourceBundle().getResourceValue("ERR_7"));	// ���� ���� ����
				return controlResponse;
			}
			
			synchronized( this.lock ){
				SqlSession session = null;
				try {
					if( progressBar != null )		progressBar.nextProgressStep("Database Connect");
					session = DatabaseService.getInstance().openSession();				// ��� ���� ����
					
					if( isAdd ){	// ��� ó��
						if( progressBar != null )		progressBar.nextProgressStep("Database Insert");
						DatabaseService.getInstance().addDriver(session, driver);		// ��� �߰�
						if( progressBar != null )		progressBar.nextProgressStep("Database Insert Ok");
					}else{			// ���� ó��
						if( progressBar != null )		progressBar.nextProgressStep("Database Update");
						DatabaseService.getInstance().updDriver(session, driver);		// ��� ����
						
						if( progressBar != null )		progressBar.nextProgressStep("Driver Reload");
						// ������ ��� �ش� ����̹��� ����ϰ��ִ� ��ġ/����Ʈ�� �ٽ� �����Ѵ�
						this.reload(driver.getDriverTarget()==ControlResponse.TARGET_DEVICE_DRIVER, driver.getDriverId(), driver.getDriverJarFile());
					}
					
					if( progressBar != null )		progressBar.nextProgressStep("Database Commit");
					session.commit();
					
					if( progressBar != null )		progressBar.nextProgressStep("Success");
					controlResponse = new ControlResponse(driver.getDriverTarget(), this.getControlType(isAdd));		// ���� ���� ����
					return controlResponse;
				} catch (Exception e) {
					// ��� ���� �� ���� �߻�
					if( session != null )		session.rollback();
					
					this.logger.error("{} ����̹� {} ���� [ID={}]", driver.getDriverTarget() == ControlResponse.TARGET_DEVICE_DRIVER?"��ġ":"������",  isAdd?"���":"����", driver.getDriverId(), e);
					
					if( progressBar != null )		progressBar.nextProgressStep("Fail");
					controlResponse = new ControlResponse(driver.getDriverTarget(), this.getControlType(isAdd), e.getMessage());	// ���� ���� ����
					return controlResponse;
				}finally{
					if( session != null )		session.close();
				}
			}
			
		}finally{
			// ó�� ��� �α� ó��
			this.log(controlResponse, driver.getDriverId(), driver.getDriverName());
		}
	}
	
	/**		����̹� ���� �� �ش� ����̹��� ����ϴ� ��ġ/�����͸� ���� �� �ٽ� �����Ѵ�		*/
	private void reload(boolean isDevice, int driverId, byte[] driverFile) throws Exception{
		if( driverFile == null )		return;		// JAR ������ �������� �ʾ��� ��� ���� �ε��� �ʿ䰡 ����
		
		if( isDevice )		DeviceService.getInstance().reloadDriverUpdate(driverId, driverFile);
		else					ReporterService.getInstance().reloadDriverUpdate(driverId, driverFile);
	}
	
	/**		����̹� ó�� ��û �Ķ���� ��ȿ�� �˻�		*/
	private ControlResponse validationDriver(Driver driver, boolean isAdd){
		// ����̹��� �˻�
		ControlResponse controlResponse = this.validationString(driver.getDriverTarget(), isAdd, this.makeResourceBundle().getResourceValue("NM_DR"), driver.getDriverName());
		if( controlResponse != null )		return controlResponse;
		driver.setDriverName(driver.getDriverName().trim());		// ���� ����
	
		// ����̹� Ÿ�� �˻�
		char target = driver.getDriverTarget();
		if( target != ControlResponse.TARGET_DEVICE_DRIVER && target != ControlResponse.TARGET_REPORTER_DRIVER )
			return new ControlResponse(driver.getDriverTarget(), isAdd?ControlResponse.TYPE_ADD:ControlResponse.TYPE_UPD, this.makeResourceBundle().getResourceValue("ERR_8"));
		
		// ����̹� ���� �˻�
		if( isAdd ){
			// ����� ��� ����̹����ϰ� ����̹����ϸ��� �ʼ�
			if( driver.isNullDriverFileName() || driver.isNullDriverJarFile() )		
				return new ControlResponse(driver.getDriverTarget(), isAdd?ControlResponse.TYPE_ADD:ControlResponse.TYPE_UPD, this.makeResourceBundle().getResourceValue("ERR_7"));

		}else{
			// ������ ��� ����̹����ϰ� ����̹����ϸ��� ���� ���� �ִ�
			if( driver.isNullDriverFileName() != driver.isNullDriverJarFile() )		
				return new ControlResponse(driver.getDriverTarget(), isAdd?ControlResponse.TYPE_ADD:ControlResponse.TYPE_UPD, this.makeResourceBundle().getResourceValue("ERR_7"));
			
		}
		
		return null;
	}
	
	/**		����̹� �߰�/���� ��û �� ��û �Ķ���� �α�		*/
	private void logDriver(Driver driver, boolean isAdd){
		if( !this.logger.isDebugEnabled() )		return;		// ����� ���� �̻� ó��
		
		this.logger.debug("{} ����̹� {} ��û", driver.getDriverTarget() == ControlResponse.TARGET_DEVICE_DRIVER?"��ġ":"������", isAdd?"���":"����");
		this.logger.debug("��û �Ķ���� ����");
		
		if( !isAdd )		this.logger.debug("����̹� ���̵� : {}", driver.getDriverId());		// ������ ��츸 ó��
		this.logger.debug("����̹��� : {}", driver.getDriverName());
		this.logger.debug("����̹����ϸ� : {}", driver.getDriverFileName());
	}
	
	/**		�ٱ��� ǥ���� ���� ���ҽ����� ����		*/
	private ResourceBundle makeResourceBundle(){
		return new ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_ctr.conf");
	}
	
	/**		UI ��û ó�� �� ������ ����	 (UI���� ������� ������)		*/
	private void makeTask(ControllerState controllerState, EventHandler<ActionEvent> eventHandler, BomzProgressBar progressBar, Object ... param){
		Task<ControlResponse> task = new Task<ControlResponse>(){
			@SuppressWarnings("unchecked")
			@Override
			protected ControlResponse call() throws Exception {
				
				switch( controllerState ){
				case DEVICE_ADD :						return addOrUpdDevice((Device)param[0], true, progressBar);
				case DEVICE_UPD :						return addOrUpdDevice((Device)param[0], false, progressBar);
				case DEVICE_DEL :						return delDevice((List<Device>)param[0], progressBar);
				
				case LOGICALGROUP_ADD :		return addOrUpdLogicalGroup((LogicalGroup)param[0], true, progressBar);
				case LOGICALGROUP_UPD :		return addOrUpdLogicalGroup((LogicalGroup)param[0], false, progressBar);
				case LOGICALGROUP_DEL :		return delLogicalGroup((List<LogicalGroup>)param[0], progressBar);
				
				case REPORTER_ADD :				return addOrUpdReporter((Reporter)param[0], true, progressBar);
				case REPORTER_UPD :					return addOrUpdReporter((Reporter)param[0], false, progressBar);
				case REPORTER_DEL :					return delReporter((List<Reporter>)param[0], progressBar);
				
				case DRIVER_ADD :						return addOrUpdDriver((Driver)param[0], true, progressBar);
				case DRIVER_UPD :						return addOrUpdDriver((Driver)param[0], false, progressBar);
				case DRIVER_DEVICE_DEL :			return delDriver(ControlResponse.TARGET_DEVICE_DRIVER, (List<Driver>)param[0], progressBar);
				case DRIVER_REPORTER_DEL :	return delDriver(ControlResponse.TARGET_REPORTER_DRIVER, (List<Driver>)param[0], progressBar);
				default : 					return null;
				}
				
			}
			
			@Override
			public void succeeded(){
				eventHandler.handle(new ActionEvent(getValue(), this));
			}
			
			@Override
			public void failed(){
				eventHandler.handle(new ActionEvent(null, this));
			}
		};
		
		new Thread(task).start();
	}
}
