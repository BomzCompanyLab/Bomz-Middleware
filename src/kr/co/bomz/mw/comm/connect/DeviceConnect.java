package kr.co.bomz.mw.comm.connect;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 	��ġ ������ ���� �������̽�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public interface DeviceConnect {

	/**	��ġ ����		*/
	public boolean connect();
	
	/**	��ġ ���� ����	*/
	public void close();
	
	/**	InputStream ����		*/
	public InputStream getInputStream();
	
	/**	OutputStream ����	*/
	public OutputStream getOutputStream();
	
	/**	TCP ������ ��� true		*/
	public boolean isTcp();
}
