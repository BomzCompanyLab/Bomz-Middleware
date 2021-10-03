package kr.co.bomz.mw.db;


/**
 * 	���� ���� ��û�� ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class ControlResponse{

	/**		���� ���. ��ġ		*/
	public static final char TARGET_DEVICE = 'D';
	
	/**		���� ���. �����׷�		*/
	public static final char TARGET_LOGICALGROUP = 'L';
	
	/**		���� ���. ������		*/
	public static final char TARGET_REPORTER = 'R';
	
	/**		���� ���. ��ġ����̹�		*/
	public static final char TARGET_DEVICE_DRIVER = 'E';
	
	/**		���� ���. �����͵���̹�		*/
	public static final char TARGET_REPORTER_DRIVER = 'P';
	
	/**		���� ���. ���		*/
	public static final char TYPE_ADD = 'A';
	
	/**		���� ���. ����		*/
	public static final char TYPE_UPD = 'U';
	
	/**		���� ���. ����		*/
	public static final char TYPE_DEL = 'D';
	
	/**		���� ��� �Ǵ� ��Ŀ��� ���ǵ��� ���� Ÿ��		*/
	public static final char FAIL = 'F';
	
	/**		���� ó�� ���� ����		*/
	private final boolean success;
	
	/**
	 * 		���� ���
	 * 	@see	ControlResponse.TARGET_DEVICE
	 * 	@see	ControlResponse.TARGET_LOGICALGROUP
	 * 	@see	ControlResponse.TARGET_REPORTER
	 */
	private final char controlTarget;
	
	/**
	 * 		���� ���
	 * 	@see	ControlResponse.TYPE_ADD
	 * 	@see	ControlResponse.TYPE_UPD
	 * 	@see	ControlResponse.TYPE_DEL
	 */
	private final char controlType;
	
	/**		���� �� ���� ����		*/
	private String errMessage;
	
	/**
	 * 	���� ��û ���� ����
	 * @param controlTarget		���� ���
	 * @param controlType		���� ���
	 */
	public ControlResponse(char controlTarget, char controlType){
		this.success = true;
		this.controlTarget = controlTarget;
		this.controlType = controlType;
	}
	
	/**
	 * 	���� ��û ���� ����
	 * @param controlTarget		���� ���
	 * @param controlType		���� ���
	 * @param errMessage		���� ����
	 */
	public ControlResponse(char controlTarget, char controlType, String errMessage){
		this.success = false;
		this.controlTarget = controlTarget;
		this.controlType = controlType;
		this.errMessage = errMessage;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public char getControlTarget() {
		return controlTarget;
	}

	public char getControlType() {
		return controlType;
	}
	
}
