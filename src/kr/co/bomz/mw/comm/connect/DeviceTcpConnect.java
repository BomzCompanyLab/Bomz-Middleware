package kr.co.bomz.mw.comm.connect;

/**
 * 	TCP ������� ��ġ ������ ���� �������̽�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public interface DeviceTcpConnect extends DeviceConnect{

	/**
	 * 	TCP Ŭ���̾�Ʈ ����� ��쿡�� ���
	 * @param ip	������ IP ����
	 */
	public void setTcpIp(String ip);
	
	/**	TCP ��Ʈ		*/
	public void setTcpPort(int port);
}
