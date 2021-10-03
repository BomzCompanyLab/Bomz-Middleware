package kr.co.bomz.mw.ui;

/**
 * 	javafx.scene.control.ComboBox ������ �� �� ���� ���� String �� ��� ���
 * @author Bomz
 *
 */
public class ComboBoxStringItem {

	/**		ȭ�� ǥ�� �̸�		*/
	private String name;
	
	/**		���� ó�� ��			*/
	private String value;
	
	public ComboBoxStringItem(){}

	public ComboBoxStringItem(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
}
