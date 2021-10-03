package kr.co.bomz.mw.soap;

import gnu.io.RXTXPort;

/**
 * 	������ ó���� ��ġ ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class DeviceWs {

	/**		��ġ ���̵�		*/
	private int deviceId;
	
	/**		��ġ �̸�			*/
	private String deviceName;
	
	/**
	 * 	��� ���. 
	 * 	true �� ��� tcp ���
	 * 	false �� ��� serial ���
	 */
	private boolean commTcpType;
	
	/**
	 * 	TCP ��� ���
	 * 	true �� ��� tcp server ���
	 * 	false �� ��� tcp client ���
	 */
	private boolean tcpServerMode;
	
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
	
	/**		����ϴ� �����׷� �������� ���	*/
	private DeviceLogicalGroupMapWs logicalGroupMap;
	
	/**		��ġ ����̹� ���̵�		*/
	private int driverId;
	
	public DeviceWs(){}

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

	public boolean isCommTcpType() {
		return commTcpType;
	}

	public void setCommTcpType(boolean commTcpType) {
		this.commTcpType = commTcpType;
	}

	public boolean isTcpServerMode() {
		return tcpServerMode;
	}

	public void setTcpServerMode(boolean tcpServerMode) {
		this.tcpServerMode = tcpServerMode;
	}

	public String getTcpIp() {
		return tcpIp;
	}

	public void setTcpIp(String tcpIp) {
		this.tcpIp = tcpIp;
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

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public DeviceLogicalGroupMapWs getLogicalGroupMap() {
		return logicalGroupMap;
	}

	public void setLogicalGroupMap(DeviceLogicalGroupMapWs logicalGroupMap) {
		this.logicalGroupMap = logicalGroupMap;
	}
	
}
