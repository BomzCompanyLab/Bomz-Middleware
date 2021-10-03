package kr.co.bomz.mw.ui;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import kr.co.bomz.mw.service.SceneLoaderService;

/**
 * 	������ �����ʹ�ư �޴�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class ItemContextMenu<T> {

	private final ContextMenu menu = new ContextMenu();
	
	private List<T> itemIdList;
	
	public ItemContextMenu(){}
	
	/**		�޴� �߰�		*/
	public void addMenu(String menuName, EventHandler<ActionEvent> handler){
		MenuItem menuItem = new MenuItem(menuName == null ? "" : menuName);
		if( handler != null )			menuItem.setOnAction(handler);
		
		this.menu.getItems().add(menuItem);
	}
	
	/**		�޴� ���̱�		*/
	public void show(List<T> itemIdList, double x, double y){
		if( this.isShow() )		this.hide();
		
		if( itemIdList == null || itemIdList.isEmpty() )		return;
		
		this.itemIdList = itemIdList;
		this.menu.show(SceneLoaderService.getInstance().getStage(), x, y);
	}
	
	/**		���� �޴��� �������� �ִ��� ����		*/
	public boolean isShow(){
		return this.menu.isShowing();
	}
	
	/**		�޴� �����		*/
	public void hide(){
		if( !this.isShow() )		return;
		
		this.itemIdList = null;
		this.menu.hide();
	}
	
	/**		ȭ���� ������ ��� ������ ������ ��� ����		*/
	public List<T> getItemIdList(){
		return this.itemIdList;
	}
}
