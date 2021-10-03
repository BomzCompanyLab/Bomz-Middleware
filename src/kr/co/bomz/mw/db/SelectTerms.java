package kr.co.bomz.mw.db;

import org.apache.ibatis.type.Alias;

import kr.co.bomz.mw.service.SettingInfoService;

/**
 * 	���ȭ�� �˻� �� �˻� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("selectTerms")
public class SelectTerms {

	/**		�˻� ���� : ����		*/
	public static final int TERM_TYPE_TITLE = 1001;
	
	/**		�˻� ���� : ����		*/
	public static final int TERM_TYPE_CONTENT = 1002;
	
	/**		�˻� ���� : ���� + ����		*/
	public static final int TERM_TYPE_ALL = 1003;
	
	/**		���� ȭ�� ��ȣ. 1���� ����		*/
	private int pageNo;
	
	/**		�� ȭ�鿡 ǥ�õǴ� ������ ��		*/
	private int pageItemLength = SettingInfoService.getInstance().getPageItemLength();
	
	/**		
	 * 		�˻� ����
	 * @see		SelectTerms.TERM_TYPE_TITLE
	 * @see		SelectTerms.TERM_TYPE_CONTENT
	 * @see		SelectTerms.TERM_TYPE_ALL	
	 **/
	private int termType;
	
	/**		�˻���		*/
	private String termValue;
	
	/**		����̹� �˻��� ��� ���Ǵ� ����̹� ���̺���		*/
	private String driverTableName;
	
	public SelectTerms(){}
	
	public int getPageNo(){
		return this.pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	public int getTermType() {
		return termType;
	}

	public void setTermType(int termType) {
		this.termType = termType;
	}

	public String getTermValue() {
		return termValue;
	}

	public void setTermValue(String termValue) {
		this.termValue = termValue;
	}

	public int getPageItemLength() {
		return pageItemLength;
	}

	public void setPageItemLength(int pageItemLength) {
		this.pageItemLength = pageItemLength;
	}

	public String getDriverTableName() {
		return driverTableName;
	}

	public void setDriverTableName(String driverTableName) {
		this.driverTableName = driverTableName;
	}
	
}
