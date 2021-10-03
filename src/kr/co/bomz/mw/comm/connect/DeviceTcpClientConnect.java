package kr.co.bomz.mw.comm.connect;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TCP Client Ÿ���� ��ġ ���� ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DeviceTcpClientConnect implements DeviceTcpConnect{
	
	/**		��ġ �α�		*/
	private static final Logger logger = LoggerFactory.getLogger("Device");

	private Socket socket;
	
	private String ip;
	private int port;
	
	private final int deviceId;
	private final String deviceName;
	
	private InputStream is;
	private OutputStream os;
	
	public DeviceTcpClientConnect(int deviceId, String deviceName){
		this.deviceId = deviceId;
		this.deviceName = deviceName;
	}
	
	@Override
	public boolean connect() {
		
		if( this.is != null )		return true;
		
		try{
			this.socket = new Socket(this.ip, this.port);
			this.socket.setSoTimeout(10000);
			
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
			
			if( logger.isInfoEnabled() )		
				logger.info("��ġ ���� ���� [devId={}, name={}, ip={}, port={}]", 
						this.deviceId, this.deviceName, this.ip, this.port);
			
			return true;
		}catch(Exception e){
			if( logger.isWarnEnabled() )		
				logger.warn("��ġ ���� ���� [devId={}, name={}, ip={}, port={}]", 
						this.deviceId, this.deviceName, this.ip, this.port);
			
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
		
		if( this.socket != null )			try{		this.socket.close();		}catch(Exception e){}
		this.socket = null;
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
		return true;
	}

	@Override
	public void setTcpIp(String ip) {
		this.ip = ip;
	}

	@Override
	public void setTcpPort(int port) {
		this.port = port;
	}
	
}
