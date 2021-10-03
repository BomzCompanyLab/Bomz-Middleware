package kr.co.bomz.mw.db;

import org.apache.ibatis.type.Alias;

import kr.co.bomz.cmn.ui.transfer.TransferSelectItem;

/**
 * �����Ϳ� ����� �����׷� ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("reporterLogicalGroupMap")
public class ReporterLogicalGroupMap implements TransferSelectItem<Integer>{

	private int logicalGroupId;
	
	private String logicalGroupName;
	
	public ReporterLogicalGroupMap(){}
	
	public ReporterLogicalGroupMap(int logicalGroupId){
		this.logicalGroupId = logicalGroupId;
	}
	
	public ReporterLogicalGroupMap(int logicalGroupId, String logicalGroupName){
		this.logicalGroupId = logicalGroupId;
		this.logicalGroupName = logicalGroupName;
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

	@Override
	public Integer getItemId() {
		return this.logicalGroupId;
	}

	@Override
	public String getItemName() {
		return this.logicalGroupName;
	}
	
}
