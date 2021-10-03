package kr.co.bomz.mw.db;

/**
 * 	��� ȭ���� ���� ��� �ش� Ŭ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public abstract class ListItem implements Item{

	/**		���� ��� ȭ�� ��ȣ. 1���� ����		*/
	private int pageNo;
	
	/**		��ü ������ ��		*/
	private int itemTotalCount;
	
	public ListItem(){}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getItemTotalCount() {
		return itemTotalCount;
	}

	public void setItemTotalCount(int itemTotalCount) {
		this.itemTotalCount = itemTotalCount;
	}
	
}
