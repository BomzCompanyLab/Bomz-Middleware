package kr.co.bomz.mw.db;

import java.util.List;

import org.apache.ibatis.type.Alias;

/**
 * 	������ �±� ���� ���н� ���Ǵ� �±׸�� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("tagList")
public class TagList {

	private int reporterId;
	
	private String reporterName;
	
	private List<Tag> tagList;

	public TagList(){}

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

	public List<Tag> getTagList() {
		return tagList;
	}

	public void setTagList(List<Tag> tagList) {
		this.tagList = tagList;
	}
	
}
