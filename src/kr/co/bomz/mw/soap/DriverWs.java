package kr.co.bomz.mw.soap;

public class DriverWs {

	/**		����̹� ���̵�		*/
	private int driverId;
	
	/**		����̹� �̸�		*/
	private String driverName;
	
	/**		����̹� ���� ���� �̸�		*/
	private String driverFileName;
	
	/**		����̹� JAR ���� ������		*/
	private byte[] driverJarFile;
	
	public DriverWs(){}

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

	public String getDriverFileName() {
		return driverFileName;
	}

	public void setDriverFileName(String driverFileName) {
		this.driverFileName = driverFileName;
	}

	public byte[] getDriverJarFile() {
		return driverJarFile;
	}

	public void setDriverJarFile(byte[] driverJarFile) {
		this.driverJarFile = driverJarFile;
	}
	
}
