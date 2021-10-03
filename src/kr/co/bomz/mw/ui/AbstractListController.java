package kr.co.bomz.mw.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import kr.co.bomz.cmn.module.BomzResource;
import kr.co.bomz.cmn.ui.popup.ConfirmPopup;
import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.db.ControlResponse;
import kr.co.bomz.mw.db.Item;
import kr.co.bomz.mw.db.ListItem;
import kr.co.bomz.mw.db.SelectTerms;
import kr.co.bomz.mw.service.SceneLoaderService;
import kr.co.bomz.mw.service.SettingInfoService;

/**
 * 	���ȭ�� �ֻ��� ��Ʈ�ѷ�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractListController<T extends Item> implements Initializable{

	/**		�� ȭ�鿡 ǥ�õǴ� �ѹ��� ��ư ��		*/
	public static final int NAVIGATOR_BUTTON_LENGTH = 4;
	
	/**		���ȭ�� ��ũ�ѹ� ����		*/
	protected static final int LIST_SCROLLBAR_WIDTH = 15;
	
	/**		�޴� �̸�		*/
	protected String title;
	
	/**		���� ���ȭ�� ��ȣ		*/
	private int pageNo;
	
	/**		��ü ������ ��		*/
	private int itemTotalLength;
	
	/**		��ü ������ �� ���̺�		*/
	@FXML
	protected Label itemTotalLengthLb;
	
	/**		������� ���̺���		*/
	@FXML
	protected TableView<T> listTv;

	/**		�̸� �÷�		*/
	@FXML
	protected TableColumn<T, String> nameTc;
	
	/**		�޴� ����		*/
	@FXML
	private Label menuTitleLb;
	
	/**		�űԵ�� ��ư		*/
	@FXML
	private Button newBt;
	
	@FXML
	private VBox rightContent;

	@FXML
	private FlowPane navigatorPn;
	
	/**		������ �����ʹ�ư �޴�		*/
	private ItemContextMenu<T> itemContextMenu = new ItemContextMenu<T>();
	
	/**		�ٱ�� ���� ���ҽ�����	*/
	protected final kr.co.bomz.util.resource.ResourceBundle languageResourceBundle;
	
	public AbstractListController(String titleResourceId){
		this.languageResourceBundle = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_list.conf");
		this.title = this.languageResourceBundle.getResourceStringValue(titleResourceId);
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.listTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);		// ���� ������ ���� ���� ��ü ���� ����
		this.listTv.addEventFilter(MouseEvent.MOUSE_PRESSED, (e)->this.handleItemClickAction(e));
		this.itemContextMenu.addMenu(this.languageResourceBundle.getResourceStringValue("DEL"), (e)->this.delItem(this.itemContextMenu.getItemIdList()));
		
		this.initialize(resourceBundle);	// �ʿ��� ��� ����
		this.initListColumnWidth();		// �÷� ���� ����
		this.initDefaultLanguage();		// �ٱ��� ����
		
		try{
			this.refreshList( (Integer)resourceBundle.getObject(CommonResource.CONTROL_PAGE_ID) );
		}catch(Exception e){
			this.refreshList(1);
		}
	}

	/**		�ٱ�� ���� ��� ����		*/
	private void initDefaultLanguage(){
		this.nameTc.setText(this.languageResourceBundle.getResourceValue("TC_NM"));		// �̸�
		this.menuTitleLb.setText(this.title + " " + this.languageResourceBundle.getResourceValue("NM_MENU"));	// �޴� Ÿ�̺�
		this.newBt.setText(this.languageResourceBundle.getResourceValue("BT_NEW"));	// �űԵ�� ��ư
		
		this.initLanguage();		// ��ӹ��� Ŭ�������� �� �� �ٱ��� ����
	}
	
	/**		���ȭ�� ���ΰ�ħ		*/
	private void refreshList(int pageNo){
		
		try {
			SelectTerms terms = new SelectTerms();
			terms.setPageNo(pageNo);
			
			List<T> deviceList = this.getItemList(terms);
			ObservableList<T> oList = this.listTv.getItems();
			oList.clear();
			oList.addAll(deviceList);
			
			if( deviceList.isEmpty() ){
				// ��ϵ� �������� ���� ��Ͽ� ǥ���� �� ���� ���
				this.pageNo = 1;
				this.initItemTotalLength(0);
			}else{
				// ��ϵ� �������� �Ѱ� �̻� ���� ���
				ListItem listItem = (ListItem)oList.get(0);
				this.pageNo = listItem.getPageNo();
				this.initItemTotalLength(listItem.getItemTotalCount());
			}
			
			this.initNavigator(terms);		// �ѹ��� �ʱ�ȭ
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("{} ���ȭ�� ��� �˻� �� ����", this.title, e);
		}
	}
	
	/**		�ѹ��� �ʱ�ȭ		*/
	private void initNavigator(SelectTerms terms){
		ObservableList<Node> list = this.navigatorPn.getChildren();
		list.clear();		// ���� ���� ����
		
		// ��ü ������ ���� �� ������ ���
		int allPageLength = this.getNavigatorAllPageLength(terms);
		int startNo = this.getNavigatorStartPageNumber();
		int endNo = this.getNavigatorEndPageNumber(startNo, allPageLength);
		
		list.add( this.makeNavigatorButton("��", 1, false, false, false) );							// �� ������ ��ư �߰�
		list.add( this.makeNavigatorButton("��", this.getNavigatorBeforePageNumber(startNo), false, false, true) );		// ���� ��ư �߰�
		
		for(int no=startNo; no <= endNo; no++)
			list.add( this.makeNavigatorButton( String.valueOf(no), no, no == this.pageNo, false, false) );
		
		list.add( this.makeNavigatorButton("��", this.getNavigatorAfterPageNumber(startNo, endNo, allPageLength), false, true, false) );	// ���� ��ư �߰�
		list.add( this.makeNavigatorButton("��", allPageLength, false, false, false) );		// �� �ڷ� ��ư �߰�
	}
	
	/**	�ѹ��� ���� ��ȣ ��� ���� ��ȣ		*/
	private int getNavigatorBeforePageNumber(int startNo){
		int beforeNo = startNo - NAVIGATOR_BUTTON_LENGTH;
		return beforeNo <= 0 ? 1 : beforeNo;
	}
	
	/**	�ѹ��� ���� ��ȣ ��� ���� ��ȣ		*/
	private int getNavigatorAfterPageNumber(int startNo, int endNo, int allPageLength){
		return ++endNo > allPageLength ? startNo : endNo;
	}
	
	/**	�ѹ��� ���� ��ȣ		*/
	private int getNavigatorEndPageNumber(int startNo, int allPageLength){
		int endNo = (startNo - 1) + NAVIGATOR_BUTTON_LENGTH;
		return endNo > allPageLength ? allPageLength : endNo;
	}
	
	/**	�ѹ��� ���� ��ȣ		*/
	private int getNavigatorStartPageNumber(){
		return ( ((this.pageNo - 1) / NAVIGATOR_BUTTON_LENGTH) * NAVIGATOR_BUTTON_LENGTH) + 1;
	}
	
	/**		��ü ��� ������ �� ���		*/
	private int getNavigatorAllPageLength(SelectTerms terms){
		int pageLength = this.itemTotalLength / terms.getPageItemLength();
		pageLength += this.itemTotalLength % terms.getPageItemLength() > 0 ? 1 : 0;
		return pageLength <= 0 ? 1 : pageLength;		// ������ ���� ó��
	}
	
	/**		�ѹ��� ��ư ����		*/
	private Button makeNavigatorButton(String name, int linkPageNo, boolean isSelect, boolean leftMargin, boolean rightMargin){
		Button bt = new Button(name);
		
		bt.getStyleClass().add("navigatorBt");
		if( isSelect )	bt.getStyleClass().add("navigatorSelectBt");
		
		if( leftMargin )		FlowPane.setMargin(bt, new Insets(0, 0, 0, 30));
		if( rightMargin )		FlowPane.setMargin(bt, new Insets(0, 30, 0, 0));
		
		bt.setOnAction( e -> this.refreshList( linkPageNo > 1 ? linkPageNo : 1 ) );
		return bt;
	}
	
	/**		��ü ������ ��		*/
	private void initItemTotalLength(int itemTotalLength){
		this.itemTotalLength = itemTotalLength;
		this.itemTotalLengthLb.setText("total : " + this.itemTotalLength);
	}
	
	/**		BACK ��ư Ŭ�� �̺�Ʈ		*/
	@FXML
	void handleBackAction(){
		SceneLoaderService.getInstance().load(IndexController.fxml);
	}
	
	/**		�űԵ�� ��ư Ŭ�� �̺�Ʈ		*/
	@FXML
	void handleNewItemAction(){
		this.updateRightContent(-1);		// ���ȭ�� ���� ��û
	}
	
	/**		
	 * ������ ȭ�� ���� ó��
	 * 
	 * @param itemId		-1�� ��� ���ȭ�� ��û. -1�� �ƴ� ��� ������ ���̵��̹Ƿ� ����ȭ�� ��û
	 */
	private void updateRightContent(int itemId){
		try {
			BomzResource resource = new BomzResource();
			resource.addResource(CommonResource.CONTROL_PAGE_ID, this.pageNo);		// ���� �ѹ��� ��ȣ
			
			if( itemId == -1 ){		// ���ȭ�� ��û
				resource.addResource(CommonResource.CONTROL_TYPE, Boolean.TRUE);
			}else{						// ����ȭ�� ��û
				resource.addResource(CommonResource.CONTROL_TYPE, Boolean.FALSE);
				resource.addResource(CommonResource.CONTROL_ITEM_ID, itemId);
			}
			
			// ���ȭ�鿡�� �����Ϸ����ϴ� ���ҽ����� Ư���� ���� ��� ó��
			java.util.Map<String, Object> resourceMapFromListPage = this.getRegPageResource();
			if( resourceMapFromListPage != null && !resourceMapFromListPage.isEmpty()){
				Object value;
				for(String key : resourceMapFromListPage.keySet()){
					if( key == null )		continue;		// ������ ���� ó��
					value = resourceMapFromListPage.get(key);
					if( value == null )		continue;		// ������ ���� ó��
					
					resource.addResource(key, value);		// ���ҽ� �߰�
				}
			}
			
			Pane pane = FXMLLoader.load(this.getRegPageUrl(), resource);
			
			this.rightContent.getChildren().clear();
			this.rightContent.getChildren().add(pane);
		} catch (IOException e) {
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("{} {} �̺�Ʈ ó�� �� ����", this.title, (itemId == -1 ? "�űԵ�� ��ư":"����ȭ�� ����"), e);
		}
	}
	
	/**		
	 * 	��� Ŭ�� �� �̺�Ʈ
	 * 
	 * 	�����ʹ�ư Ŭ�� �� ���� �޴� ����
	 * 	���ʹ�ư ���� Ŭ�� �� �ش� �׸��� �󼼺��� ȭ�� ����
	 * 
	 * @param event		���콺 �̺�Ʈ
	 */
	void handleItemClickAction(MouseEvent event){
		this.itemContextMenu.hide();		// ���콺 �����ʸ޴��� �������� ���� ��� �����
		final int ordinal = event.getButton().ordinal();
		
		if( ordinal == MouseButton.SECONDARY.ordinal() )	
			this.showMouseRightButtonMenu(event);		// ���콺 ������ ��ư Ŭ��
		else if( ordinal == MouseButton.PRIMARY.ordinal() && event.getClickCount() == 2 )	
			this.showItemUpdPage();		// ���콺 ���� ��ư Ŭ��. ����Ŭ���� ó��
	}

	/**		������ ����ȭ�� ���̱�		*/
	private void showItemUpdPage(){
		T item = this.listTv.getSelectionModel().getSelectedItem();
		if( item == null )		return;		// ������ ���� ó��. ���õ� �������� ���� ���
		
		this.updateRightContent(item.getItemId());		// ����ȭ�� ǥ�� ��û ó��
	}
	
	/**		������ ���� ó��		*/
	private void delItem(List<T> itemList){
		this.itemContextMenu.hide();
		
		if( itemList == null || itemList.isEmpty() )	return;
		
		// ���� �� �ѹ� �� Ȯ�� ó��
		ConfirmPopup confirm = new ConfirmPopup(SceneLoaderService.getInstance().getStage(), this.languageResourceBundle.getResourceStringValue("DEL_CNF"));
		confirm.show(
				// Ȯ�� ��ư Ŭ�� �� ó��
				(e)->delEventHandle(itemList), 
				null		// ��� ��ư Ŭ�� �� ������ ó�� ����
			);
	}
	
	/**		���� �̺�Ʈ ó��		*/
	private void delEventHandle(List<T> itemList){
		BomzProgressBar progressBar = new BomzProgressBar(3);
		progressBar.showProgress();
		
		this.delItemList(itemList, progressBar, 
			(e)->{
				progressBar.hideProgress();
				
				Object obj = e.getSource();
				if( obj != null && obj instanceof ControlResponse){
					ControlResponse response =  (ControlResponse)e.getSource();
					
					if( response.isSuccess() )	new BomzPopup().showOkPopup( this.languageResourceBundle.getResourceStringValue("DEL_SUCC") );
					else									new BomzPopup().showOkPopup(response.getErrMessage());
					
					this.refreshList(this.pageNo);
				}
				
			}
		);		// ���� ó��
		
	}
	
	/**		���콺 �����ʹ�ư �޴� ���̱�		*/
	private void showMouseRightButtonMenu(MouseEvent event){
		this.itemContextMenu.show(this.listTv.getSelectionModel().getSelectedItems(), event.getScreenX(), event.getScreenY());
		event.consume();
	}
	
	/**		������ ��� ����		*/
	abstract List<T> getItemList(SelectTerms terms) throws Exception;
	
	/**		���ȭ�� URL ����		*/
	abstract URL getRegPageUrl();
	
	/**		������ ��� ����		*/
	abstract void delItemList(List<T> itemIdList, BomzProgressBar progressBar, EventHandler<ActionEvent> handler);
	
	/**		���̺� �÷� ���� ����		*/
	abstract void initListColumnWidth();
	
	/**		�ٱ��� ����		*/
	abstract void initLanguage();
	
	/**		�ʿ��� ��� ��ӹ��� Ŭ�������� ����		*/
	protected void initialize(ResourceBundle resourceBundle){}
	
	/**		���/����ȭ�� �� ���ȭ�鿡�� Ư���� ������ ���ҽ� ���� ���� ��� ó��	*/
	protected java.util.Map<String, Object> getRegPageResource(){
		return null;
	}

}
