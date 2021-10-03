package kr.co.bomz.mw.db;

import org.apache.ibatis.type.Alias;

/**
 * 	��ġ > �����׷� > �����ͷ� ������ ��ȯ�� ���
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("tag")
public class Tag{

	/**			������ ���� ���з� �α� ����� ���Ǵ� ���̵�		*/
	private int logId;
	
	/**			��ġ ���̵�		*/
	private int deviceId;
	
	/**			��ġ �̸�			*/
	private String deviceName;
	
	/**			�����׷� ���̵�		*/
	private int logicalGroupId;
	
	/**			�����׷� �̸�		*/
	private String logicalGroupName;
		
	/**			�±� ���� �ð�			*/
	private String readTime;
	
	/**			���� �±� ���̵� 	*/
	private String tagId;
	
	/**			�±׸� ������ ���׳� ��ȣ		*/
	private int antenna;
	
	public Tag(){}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

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

	public String getReadTime() {
		return readTime;
	}

	public void setReadTime(String readTime) {
		this.readTime = readTime;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public int getAntenna() {
		return antenna;
	}

	public void setAntenna(int antenna) {
		this.antenna = antenna;
	}
	
}
