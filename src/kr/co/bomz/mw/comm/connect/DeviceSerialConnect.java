package kr.co.bomz.mw.comm.connect;

import gnu.io.CommPortIdentifier;
import gnu.io.RXTXPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * �ø��� Ÿ���� ��ġ ���� ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DeviceSerialConnect implements DeviceConnect{

	/**		��ġ �α�		*/
	private static final Logger logger = LoggerFactory.getLogger("Device");
	
	private RXTXPort serialPort;
	private String port;
	private int baudRate;
	private int flowControl;
	private int dataBits;
	private int stopBits;
	private int parity;	
	
	private final int deviceId;
	private final String deviceName;
	
	private InputStream is;
	
	private OutputStream os;
	
	public DeviceSerialConnect(int deviceId, String deviceName){
		this.deviceId = deviceId;
		this.deviceName = deviceName;
	}

	/**		��ġ���� ����� �� �ִ� �ø�����Ʈ���� �˻�		*/
	private boolean checkSerialPortId(){
		
		if( this.port == null ){
			if( logger.isWarnEnabled() )		logger.warn("��Ʈ ��ȣ�� �������� ���� ���·� ��ġ ���� �õ� [devId={}, name={}]", this.deviceId, this.deviceName);
			return false;
		}
				
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portList = gnu.io.CommPortIdentifier.getPortIdentifiers();
		while( portList.hasMoreElements() ){
			if( this.port.equals( ((CommPortIdentifier)portList.nextElement()).getName()) )	return true;
		}
		
		if( logger.isWarnEnabled() )
			logger.warn("����� �� ���� ��Ʈ�� �ø������� ��û [devId={}, name={}, port={}]", this.deviceId, this.deviceName, this.port);
		
		return false;
	}
	
	@Override
	public boolean connect() {
		
		if( this.is != null )		return true;
		
		if( !this.checkSerialPortId() )		return false;
		
		try{
			this.serialPort = new RXTXPort(this.port);
			
			this.serialPort.setFlowControlMode( this.flowControl );
			this.serialPort.setSerialPortParams(this.baudRate, this.dataBits, this.stopBits, this.parity);
						
			this.is = this.serialPort.getInputStream();
			this.os = this.serialPort.getOutputStream();
			
			if( logger.isInfoEnabled() )
				logger.info("��ġ ���� ���� [devId={}, name={}, port={}, flowControl={}, baudRate={}, dataBits={}, stopBits={}, parity={}]", 
						this.deviceId, this.deviceName, this.port, this.flowControl, this.baudRate, 
						this.dataBits, this.stopBits, this.parity);
			
			return true;
		}catch(Exception e){
			if( logger.isWarnEnabled() )
				logger.warn("��ġ ���� ���� [devId={}, name={}, port={}, flowControl={}, baudRate={}, dataBits={}, stopBits={}, parity={}]", 
						this.deviceId, this.deviceName, this.port, this.flowControl, this.baudRate, 
						this.dataBits, this.stopBits, this.parity);
			
			this.close();
			return false;
		}
		
	}
	
	@Override
	public void close() {
		if( this.is != null )			try{		this.is.close();		}catch(Exception e){}
		this.is = null;
		
		if( this.os != null )			try{		this.os.close();		}catch(Exception e){}
		this.os = null;
		
		if( this.serialPort != null ){
			try{		this.serialPort.clearCommInput();		}catch(Exception e){}
			try{		this.serialPort.close();		}catch(Exception e){}
			this.serialPort = null;
		}

	}

	public void setPort(int port) {
		this.port = "COM" + port;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public void setFlowControl(int flowControl) {
		this.flowControl = flowControl;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	@Override
	public InputStream getInputStream() {
		return this.is;
	}

	@Override
	public OutputStream getOutputStream() {
		return this.os;
	}
	
	@Override
	public boolean isTcp(){
		return false;
	}
}
