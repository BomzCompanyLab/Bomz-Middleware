package kr.co.bomz.mw.comm.connect;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TCP Server Ÿ���� ��ġ ���� ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DeviceTcpServerConnect implements DeviceTcpConnect{
	
	/**		��ġ �α�		*/
	private static final Logger logger = LoggerFactory.getLogger("Device");

	private ServerSocket sSocket;
	private Socket socket;
	
	private int port;
	
	private final int deviceId;
	private final String deviceName;
	
	private InputStream is;
	private OutputStream os;
	
	public DeviceTcpServerConnect(int deviceId, String deviceName){
		this.deviceId = deviceId;
		this.deviceName = deviceName;
	}
	
	@Override
	public boolean connect() {
		// ���� ������ �Ǿ��ִ��� Ȯ��
		if( this.is != null )		return true;
		
		// ���� ���� �õ�
		if( !this.openServer() )		return false;
		
		return this.acceptClient();
	}
	
	/** Ŭ���̾�Ʈ ��ġ ���� ó��		*/ 
	private boolean acceptClient(){
		try{
			this.socket = this.sSocket.accept();
			
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
			
			if( logger.isInfoEnabled() )		
				logger.info("��ġ ���� ���� [devId={}, name={}, port={}]", 
						this.deviceId, this.deviceName, this.port);
			
			return true;
		}catch(SocketTimeoutException e){
			// Ÿ�Ӿƿ�
			return false;
		}catch(Exception e){
			if( logger.isWarnEnabled() )		
				logger.warn("��ġ ���� ���� [devId={}, name={}, port={}]", 
						this.deviceId, this.deviceName, this.port);
			
			this.close();
			return false;
		}
	}
	

	/**		��ġ ���ӿ� ���� ����		*/
	private boolean openServer(){
		if( this.sSocket != null )		return true;
		
		try{
			this.sSocket = new ServerSocket(this.port);
			this.sSocket.setSoTimeout(1000);
			if( logger.isInfoEnabled() )
				logger.info("��ġ ���� ���� ���� ���� [devId={}, name={}, port={}]", 
						this.deviceId, this.deviceName, this.port);
			
			return true;
		}catch(Exception e){
			if( logger.isWarnEnabled() )		
				logger.warn("��ġ ���� ���� ���� ���� [devId={}, name={}, port={}]", 
						this.deviceId, this.deviceName, this.port);
			
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
		
		if( this.sSocket != null )		try{		this.sSocket.close();		}catch(Exception e){}
		this.sSocket = null;
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
	public void setTcpIp(String ip) {}

	@Override
	public void setTcpPort(int port) {
		this.port = port;
	}
	
}
