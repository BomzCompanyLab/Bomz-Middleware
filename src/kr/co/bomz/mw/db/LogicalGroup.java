package kr.co.bomz.mw.db;

import java.util.List;

import org.apache.ibatis.type.Alias;

import kr.co.bomz.cmn.ui.transfer.TransferSelectItem;

/**
 * 	�����׷� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("logicalGroup")
public class LogicalGroup extends ListItem implements TransferSelectItem<Integer>{

	/**	�����׷� ���̵�		*/
	private int logicalGroupId;
	
	/**	�����׷��		*/
	private String logicalGroupName;

	/**	�����׷�� ����� ��ġ ��		*/
	private int deviceMappingLength;
	
	/**	�����׷�� ����� ������ ��	*/
	private int reporterMappingLength;
	
	/**	��ġ�� �����׷� ���� ���		*/
	private List<LogicalGroupDeviceMap> logicalGroupDeviceMapList;
	
	/**	�����׷� ��� ������ ���̵� ���		*/
	private List<LogicalGroupReporterMap> logicalGroupReporterMapList;
	
	public LogicalGroup(){}

	public int getLogicalGroupId() {
		return logicalGroupId;
	}

	public void setLogicalGroupId(int logicalGroupId) {
		this.logicalGroupId = logicalGroupId;
	}
	
	public List<LogicalGroupDeviceMap> getLogicalGroupDeviceMapList() {
		return logicalGroupDeviceMapList;
	}

	public void setLogicalGroupDeviceMapList(List<LogicalGroupDeviceMap> logicalGroupDeviceMapList) {
		this.logicalGroupDeviceMapList = logicalGroupDeviceMapList;
	}
	
	public List<LogicalGroupReporterMap> getLogicalGroupReporterMapList() {
		return logicalGroupReporterMapList;
	}

	public void setLogicalGroupReporterMapList(List<LogicalGroupReporterMap> logicalGroupReporterMapList) {
		this.logicalGroupReporterMapList = logicalGroupReporterMapList;
	}

	public String getLogicalGroupName() {
		return logicalGroupName;
	}

	public void setLogicalGroupName(String logicalGroupName) {
		this.logicalGroupName = logicalGroupName;
	}

		
	public int getDeviceMappingLength() {
		return deviceMappingLength;
	}

	public void setDeviceMappingLength(int deviceMappingLength) {
		this.deviceMappingLength = deviceMappingLength;
	}

	public int getReporterMappingLength() {
		return reporterMappingLength;
	}

	public void setReporterMappingLength(int reporterMappingLength) {
		this.reporterMappingLength = reporterMappingLength;
	}
	
	@Override
	public String toString(){
		return this.logicalGroupName;
	}

	@Override
	public Integer getItemId() {
		return this.logicalGroupId;
	}

	@Override
	public String getItemName() {
		return this.logicalGroupName;
	}
	
}
