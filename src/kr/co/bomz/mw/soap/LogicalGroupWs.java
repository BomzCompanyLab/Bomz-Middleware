package kr.co.bomz.mw.soap;

/**
 * 	������ ó���� �����׷� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class LogicalGroupWs {

	/**	�����׷� ���̵�		*/
	private int logicalGroupId;
	
	/**	�����׷��		*/
	private String logicalGroupName;

	/**	��ġ�� �����׷� ���� ���		*/
	private LogicalGroupDeviceMapWs deviceMap;
	
	/**	��ġ�� �����׷� ���� ���		*/
	private LogicalGroupReporterMapWs reporterMap;
	
	public LogicalGroupWs(){}

	public int getLogicalGroupId() {
		return logicalGroupId;
	}

	public void setLogicalGroupId(int logicalGroupId) {
		this.logicalGroupId = logicalGroupId;
	}

	public String getLogicalGroupName() {
		return logicalGroupName;
	}

	public void setLogicalGroupName(String logicalGroupName) {
		this.logicalGroupName = logicalGroupName;
	}

	public LogicalGroupDeviceMapWs getDeviceMap() {
		return deviceMap;
	}

	public void setDeviceMap(LogicalGroupDeviceMapWs deviceMap) {
		this.deviceMap = deviceMap;
	}

	public LogicalGroupReporterMapWs getReporterMap() {
		return reporterMap;
	}

	public void setReporterMap(LogicalGroupReporterMapWs reporterMap) {
		this.reporterMap = reporterMap;
	}
	
}
