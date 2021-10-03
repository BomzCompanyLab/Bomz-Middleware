package kr.co.bomz.mw.db;

import java.util.List;

import org.apache.ibatis.type.Alias;

import gnu.io.RXTXPort;
import kr.co.bomz.cmn.ui.transfer.TransferSelectItem;
import kr.co.bomz.mw.comm.AbstractDevice;

/**
 * 	��ġ ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("device")
public class Device extends ListItem implements TransferSelectItem<Integer>{

	/**		��Ź�� : TCP/IP ���		*/
	public static final char COMM_TCP_TYPE = 'T';
	
	/**		��Ź�� : SERIAL (RS-485 or RS-232) ���		*/
	public static final char COMM_SERIAL_TYPE = 'S';
	
	
	/**		TCP ��Ÿ�� : SERVER ���		*/
	public static final char TCP_MODE_SERVER = 'S';
	
	/**		TCP ��Ÿ�� : CLIENT ���		*/
	public static final char TCP_MODE_CLIENT = 'C';
	
	
	/**		��� ���� ���� : �����		*/
	public static final char COMM_STATE_YES = 'S';
	
	/**		��� ���� ���� : ��������		*/
	public static final char COMM_STATE_NO = 'D';
	
	/**		��ġ ���̵�		*/
	private int deviceId;
	
	/**		��ġ ���׳� ��ȣ		*/
	private Integer deviceAntennaId;
	
	/**		��ġ �̸�			*/
	private String deviceName;
	
	/**
	 * 	��� ���
	 * 	@see	Device.COMM_TCP_TYPE
	 * 	@see	Device.COMM_SERIAL_TYPE
	 */
	private char commType;
	
	/**
	 * 	TCP ��� ���
	 * 	@see	Device.TCP_MODE_SERVER
	 * 	@see	Device.TCP_MODE_CLIENT
	 */
	private char tcpMode;
	
	/**		TCP ������		*/
	private String tcpIp;
	
	/**		TCP �Ǵ� SERIAL ��Ʈ		*/
	private int port;

	/**		
	 * 		SERIAL BaudRate		
	 * 
	 * 	��밪
	 * 		110, 300, 1200, 2400, 4800, 9600
	 * 		19200, 38400, 57600, 115200
	 * 		230400, 460800, 921600	
	 * */
	private int baudRate;
	
	/**		
	 * 		SERIAL FlowControl
	 * 
	 *		@see	RXTXPort.FLOWCONTROL_NONE
	 *		@see	RXTXPort.FLOWCONTROL_RTSCTS_IN
	 *		@see	RXTXPort.FLOWCONTROL_RTSCTS_OUT
	 *		@see	RXTXPort.FLOWCONTROL_XONXOFF_IN
	 *		@see	RXTXPort.FLOWCONTROL_XONXOFF_OUT
	 **/
	private int flowControl;
	
	/**		
	 * 		SERIAL DataBits
	 * 
	 * 	@see	RXTXPort.DATABITS_5
	 * 	@see	RXTXPort.DATABITS_6
	 * 	@see	RXTXPort.DATABITS_7
	 * 	@see	RXTXPort.DATABITS_8
	 **/
	private int dataBits;
	
	/**		
	 * 		SERIAL StopBits
	 * 
	 * 	@see	RXTXPort.STOPBITS_1
	 * 	@see	RXTXPort.STOPBITS_1_5
	 * 	@see	RXTXPort.STOPBITS_2
	 **/
	private int stopBits;
	
	/**		
	 * 		SERIAL Parity
	 * 
	 * 	@see	RXTXPort.PARITY_EVEN
	 * 	@see	RXTXPort.PARITY_MARK
	 * 	@see	RXTXPort.PARITY_NONE
	 * 	@see	RXTXPort.PARITY_ODD
	 * 	@see	RXTXPort.PARITY_SPACE
	 **/
	private int parity;
	
	/**
	 * 		��ġ ���� ����
	 * 
	 * 	@see	Device.COMM_STATE_YES
	 * 	@see	Device.COMM_STATE_NO
	 */
	private char commState;
	
	/**		����ϴ� �����׷� �������� ���	*/
	private List<DeviceLogicalGroupMap> logicalGroupMapList;
	
	/**		��ġ ����̹� ���̵�		*/
	private int driverId;
	
	/**		��ġ ����̹� �̸�		*/
	private String driverName;
	
	private AbstractDevice device;
	
	public Device(){}
	
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public AbstractDevice getDevice() {
		return device;
	}

	public void setDevice(AbstractDevice device) {
		this.device = device;
	}

	public char getCommType() {
		return commType;
	}

	public void setCommType(char commType) {
		this.commType = commType;
	}

	public String getTcpIp() {
		return tcpIp;
	}

	public void setTcpIp(String tcpIp) {
		this.tcpIp = tcpIp;
	}
	
	public boolean isNullTcpIp(){
		return this.tcpIp == null;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public int getFlowControl() {
		return flowControl;
	}

	public void setFlowControl(int flowControl) {
		this.flowControl = flowControl;
	}

	public int getDataBits() {
		return dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public int getParity() {
		return parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public List<DeviceLogicalGroupMap> getLogicalGroupMapList() {
		return logicalGroupMapList;
	}

	public void setLogicalGroupMapList(List<DeviceLogicalGroupMap> logicalGroupMapList) {
		this.logicalGroupMapList = logicalGroupMapList;
	}

	public boolean isNullLogicalGroupMapList(){
		return this.logicalGroupMapList == null;
	}
	
	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public char getCommState() {
		return commState;
	}

	public void setCommState(char commState) {
		this.commState = commState;
	}
	
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public char getTcpMode() {
		return tcpMode;
	}

	public void setTcpMode(char tcpMode) {
		this.tcpMode = tcpMode;
	}

	/**		List UI ȭ�� ǥ�ÿ�. ��Ź�Ŀ� ���� ��� ���� ����		*/
	public String getComm(){
		if( this.commType == COMM_TCP_TYPE ){
			return ( this.tcpMode == TCP_MODE_SERVER ? "" : this.tcpIp + ":") + this.port;
		}else{
			return "COM" + this.port;
		}
	}

	/**		List UI ȭ�� ǥ�ÿ�. ��� ���� ���� ����		*/
	public String getCommStateValue(){
		return this.commState == Device.COMM_STATE_YES ? "O" : "X";
	}
	
	@Override
	public Integer getItemId() {
		return this.deviceId;
	}

	@Override
	public String getItemName() {
		if( this.deviceAntennaId == null )		return this.deviceName;
		else			return this.deviceName + " [Ant:" + this.deviceAntennaId + "]";
	}
}
