package kr.co.bomz.mw.db;

import org.apache.ibatis.type.Alias;

/**
 * 	��ġ �Ǵ� ������ ����̹�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("driver")
public class Driver extends ListItem{

	/**		����̹� ���̵�		*/
	private int driverId;
	
	/**		����̹� �̸�		*/
	private String driverName;
	
	/**		����̹� ���� ���� �̸�		*/
	private String driverFileName;
	
	/**		����̹��� ������� ��ġ/������ ��		*/
	private int driverMappingLength;
	
	/**		����̹� JAR ���� ������		*/
	private byte[] driverJarFile;
	
	/**
	 * 	����̹� ����(��ġ����̹�/�����͵���̹�)
	 * 
	 * 	@see	ControlResponse.TARGET_DEVICE_DRIVER
	 * 	@see	ControlResponse.TARGET_REPORTER_DRIVER
	 */
	private char driverTarget;
	
	/**	��ġ ����̹��� ��� ���̺���		*/
	public static final String DRIVER_TABLE_DEVICE = "DEVICE";
	
	/**	������ ����̹��� ��� ���̺���		*/
	public static final String DRIVER_TABLE_REPORTER = "REPORTER";
	
	public Driver(){}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public char getDriverTarget() {
		return driverTarget;
	}

	public void setDriverTarget(char driverTarget) {
		this.driverTarget = driverTarget;
	}

	public String getDriverTableName(){
		return this.driverTarget == ControlResponse.TARGET_DEVICE_DRIVER ? DRIVER_TABLE_DEVICE : DRIVER_TABLE_REPORTER;
	}
	
	public String getDriverFileName() {
		return driverFileName;
	}

	public void setDriverFileName(String driverFileName) {
		this.driverFileName = driverFileName;
	}
	
	public boolean isNullDriverFileName(){
		return this.driverFileName == null;
	}

	public int getDriverMappingLength() {
		return driverMappingLength;
	}

	public void setDriverMappingLength(int driverMappingLength) {
		this.driverMappingLength = driverMappingLength;
	}

	public byte[] getDriverJarFile() {
		return driverJarFile;
	}

	public void setDriverJarFile(byte[] driverJarFile) {
		this.driverJarFile = driverJarFile;
	}
	
	public boolean isNullDriverJarFile(){
		return this.driverJarFile == null;
	}

	@Override
	public String toString(){
		return this.driverName;
	}

	@Override
	public Integer getItemId() {
		return this.driverId;
	}

	@Override
	public String getItemName() {
		return this.driverName;
	}
}
