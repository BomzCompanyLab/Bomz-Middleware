package kr.co.bomz.mw.ui;

/**
 * 	javafx.scene.control.ComboBox ������ �� �� ���� ���� Integer �� ��� ���
 * @author Bomz
 *
 */
public class ComboBoxIntegerItem {

	/**		ȭ�� ǥ�� �̸�		*/
	private String name;
	
	/**		���� ó�� ��			*/
	private int value;
	
	public ComboBoxIntegerItem(){}

	public ComboBoxIntegerItem(String name, int value){
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
}
