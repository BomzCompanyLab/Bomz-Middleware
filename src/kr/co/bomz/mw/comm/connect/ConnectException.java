package kr.co.bomz.mw.comm.connect;

/**
 * 	��ġ ���� ���� �� �߻�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class ConnectException extends Exception{

	private static final long serialVersionUID = 4152395707633787717L;

	public ConnectException(){}
	
	public ConnectException(Throwable err){
		super(err);
	}
}
