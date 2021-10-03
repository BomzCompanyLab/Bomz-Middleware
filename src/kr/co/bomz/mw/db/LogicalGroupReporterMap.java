package kr.co.bomz.mw.db;

import org.apache.ibatis.type.Alias;

import kr.co.bomz.cmn.ui.transfer.TransferSelectItem;

/**
 * �����׷쿡 ����� ������ ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("logicalGroupReporterMap")
public class LogicalGroupReporterMap implements TransferSelectItem<Integer>{

	private int reporterId;
	
	private String reporterName;
	
	public LogicalGroupReporterMap(){}
	
	public LogicalGroupReporterMap(int reporterId, String reporterName){
		this.reporterId = reporterId;
		this.reporterName = reporterName;
	}

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

	@Override
	public Integer getItemId() {
		return this.reporterId;
	}

	@Override
	public String getItemName() {
		return this.reporterName;
	}
	
}
