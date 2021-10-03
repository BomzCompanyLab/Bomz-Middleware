package kr.co.bomz.mw.soap;

/**
 * 	������ ó���� ������ ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class ReporterWs {

	/**		������ ���̵�		*/
	private int reporterId;
	
	/**		������ �̸�		*/
	private String reporterName;
	
	/**
	 * 		Ÿ�̸� �ð� (�ʴ���)
	 * 		0���� ���� ��� ��� ����
	 */
	private int timerRepeatTime;
	
	/**		������ ������ ���� ������		*/
	private String reporterIp;
	
	/**		������ ������ ���� ��Ʈ		*/
	private int reporterPort;
	
	/**		������ Ư���� ������		*/
	private String reporterParam;
	
	/**		�����Ϳ� ����� �����׷� ���̵� ���		*/
	private ReporterLogicalGroupMapWs logicalGroupMap;
	
	/**		������ ����̹� ���̵�		*/
	private int driverId;
	
	public ReporterWs(){}

	public int getReporterId() {
		return reporterId;
	}

	public void setReporterId(int reporterId) {
		this.reporterId = reporterId;
	}

	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	public int getTimerRepeatTime() {
		return timerRepeatTime;
	}

	public void setTimerRepeatTime(int timerRepeatTime) {
		this.timerRepeatTime = timerRepeatTime;
	}

	public String getReporterIp() {
		return reporterIp;
	}

	public void setReporterIp(String reporterIp) {
		this.reporterIp = reporterIp;
	}

	public int getReporterPort() {
		return reporterPort;
	}

	public void setReporterPort(int reporterPort) {
		this.reporterPort = reporterPort;
	}

	public String getReporterParam() {
		return reporterParam;
	}

	public void setReporterParam(String reporterParam) {
		this.reporterParam = reporterParam;
	}

	public ReporterLogicalGroupMapWs getLogicalGroupMap() {
		return logicalGroupMap;
	}

	public void setLogicalGroupMap(ReporterLogicalGroupMapWs logicalGroupMap) {
		this.logicalGroupMap = logicalGroupMap;
	}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	
}
