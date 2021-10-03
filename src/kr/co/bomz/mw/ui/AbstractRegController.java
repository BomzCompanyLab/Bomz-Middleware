package kr.co.bomz.mw.ui;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import kr.co.bomz.cmn.module.BomzResource;
import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.db.ControlResponse;
import kr.co.bomz.mw.service.SceneLoaderService;
import kr.co.bomz.mw.service.SettingInfoService;

/**
 * 	��� �Ǵ� ����ȭ�� �ֻ��� ��Ʈ�ѷ�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractRegController implements Initializable{

	/**		�޴� �̸�		*/
	protected String title;
	
	/**		������ ǥ�����̴� ���ȭ�� �ѹ��� ������ ��ȣ		*/
	private int listPageNo;
	
	/**		�̸� ���̺�		*/
	@FXML
	private Label nameLb;
	
	/**		Ȯ�� ��ư		*/
	@FXML
	private Button submitBt;
	
	/**		��� ��ư		*/
	@FXML
	private Button cancelBt;
	
	/**		�޴� Ÿ��Ʋ ǥ�ÿ� Label		*/
	@FXML
	private Label menuTitleLb;
	
	/**		�� �׸��� �̸��Է� �ʵ�		*/
	@FXML
	protected TextField nameTf;
	
	/**		�߾� ���� �Է� ������ ǥ���Ǵ� ScrollPane		*/
	@FXML
	private ScrollPane centerScrollPn;
	
	/**		���ó���� ��� true, ������ ��� false		*/
	private boolean isReg;
	
	/**		����ó���� ��� ������ ������ ���̵�		*/
	private int updateItemId;
	
	/**		�ٱ�� ���� ���ҽ�����	*/
	protected final kr.co.bomz.util.resource.ResourceBundle languageResourceBundle;
	
	AbstractRegController(String titleResourceId){
		this.languageResourceBundle = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_reg.conf");
		this.title = this.languageResourceBundle.getResourceStringValue(titleResourceId);
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try{
			this.isReg = (Boolean)resourceBundle.getObject(CommonResource.CONTROL_TYPE);		// ��� �Ǵ� ����ȭ�� ����. true �� ��� ��� ȭ��
			this.listPageNo = (Integer)resourceBundle.getObject(CommonResource.CONTROL_PAGE_ID);	// ���� ���ȭ�� �ѹ��� ��ȣ
			
			this.initialize(resourceBundle);		// ���� ������ ������ �ʱ�ȭ �۾� ����
			this.initDefaultLanguage();		// �ٱ��� ����
			
			if( this.isReg )		this.initializeRegPage();
			else						this.initializeUpdPage((Integer)resourceBundle.getObject(CommonResource.CONTROL_ITEM_ID));
			
			this.initCenterMaxHeight();
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("{} ���/���� ȭ�� �ʱ�ȭ ����", this.title, e);
			
			new BomzPopup().showOkPopup(e.getMessage());		// �˾� ǥ��
		}
		
	}
	
	/**		�ٱ�� ���� ��� ����		*/
	private void initDefaultLanguage(){
		this.nameLb.setText( this.languageResourceBundle.getResourceValue("LB_NM") );
		
		this.submitBt.setText( this.languageResourceBundle.getResourceValue("BT_OK") );
		this.cancelBt.setText( this.languageResourceBundle.getResourceValue("BT_CANCEL") );
		
		this.initLanguage();
	}
	
	/**		�ٱ��� ����		*/
	abstract void initLanguage();
	
	/**		�ʱ�ȭ �۾� ����		*/
	abstract void initialize(ResourceBundle resourceBundle) throws Exception;
	
	/**		�߾� �����Է�ȭ�� �ִ� ���� ����		*/
	private void initCenterMaxHeight(){
		this.centerScrollPn.maxHeightProperty().bind(SceneLoaderService.getInstance().getStage().heightProperty().subtract(250));
	}
	/**		���ȭ�� �ʱ�ȭ		*/
	private void initializeRegPage() throws Exception{
		this.menuTitleLb.setText(this.title + " " + this.languageResourceBundle.getResourceStringValue("TITL_REG"));
		
		this.initRegPage();	// ���� ������ ���� ȣ��
	}
	
	/**		���ȭ�� �ʱ�ȭ		*/
	abstract void initRegPage() throws Exception;
	
	/**		����ȭ�� �ʱ�ȭ		*/
	private void initializeUpdPage(int itemId) throws Exception{
		this.menuTitleLb.setText(this.title + " " + this.languageResourceBundle.getResourceStringValue("TITL_UPD"));
		this.updateItemId = itemId;
		
		this.initUpdPage(itemId);		// ���� ������ ���� ȣ��
	}
	
	/**		����ȭ�� �ʱ�ȭ		*/
	abstract void initUpdPage(int itemId) throws Exception;
	
	/**		Ȯ�� ��ư Ŭ�� �� ȣ��		*/
	@FXML
	void handleSubmitAction(){
		BomzProgressBar progressBar = new BomzProgressBar(8);
		progressBar.showProgress();
		
		this.handleSubmit(progressBar, 
				(e)->{
					progressBar.hideProgress();
					
					Object obj = e.getSource();
					if( obj != null && obj instanceof ControlResponse){
						ControlResponse response =  (ControlResponse)e.getSource();
						
						if( response.isSuccess() ){
							// ���/���� ����
							new BomzPopup().showOkPopup( 
									this.title + " " + 
									this.languageResourceBundle.getResourceValue(this.isReg?"TITL_REG":"TITL_UPD") + " " + 
									this.languageResourceBundle.getResourceValue("SUCC") 
								);		// �˾� ǥ��	
							
							this.viewListPage();
						}else{
							Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
							logger.error("{} {} ���� [{}]", this.title, this.isReg?"���":"����", response.getErrMessage());
							new BomzPopup().showOkPopup(response.getErrMessage());		// �˾� ǥ��
						}
						
					}
					
				}
			);
			
	}
	
	/**		��� ��ư Ŭ�� �� ȣ��		*/
	@FXML
	void handleCancelAction(){
		this.viewListPage();
	}
	
	private void viewListPage(){
		BomzResource resource = new BomzResource();
		resource.addResource(CommonResource.CONTROL_PAGE_ID, this.listPageNo);		// ���� ���ȭ�� �ѹ��� ��ȣ
		
		java.util.Map<String, Object> resourceMapFromRegPage = this.getListPageResource();
		if( resourceMapFromRegPage != null && !resourceMapFromRegPage.isEmpty()){
			Object value;
			for(String key : resourceMapFromRegPage.keySet()){
				if( key == null )		continue;		// ������ ���� ó��
				value = resourceMapFromRegPage.get(key);
				if( value == null )		continue;		// ������ ���� ó��
				
				resource.addResource(key, value);		// ���ҽ� �߰�
			}
		}
		
		SceneLoaderService.getInstance().load(this.getListPageURL(), resource);
	}
	
	/**		���ȭ������ �̵� ó��		*/
	abstract URL getListPageURL();
	
	/**		Ȯ�� ��ư Ŭ�� �� �̺�Ʈ ó��. �Է� �� ��ȿ�� ������ ��� null ����		*/
	abstract void handleSubmit(BomzProgressBar progressBar, EventHandler<ActionEvent> handler);
	
	/**		��� �۾��� ��� true, ������ ��� false		*/
	protected boolean isReg(){
		return this.isReg;
	}
	
	/**		������ ��� ������ ������ ���̵�		*/
	protected int getUpdateItemId(){
		return this.updateItemId;
	}
	
	/**		���/����ȭ�鿡�� ���ȭ������ Ư���� ������ ���ҽ� ���� ���� ��� ó��	*/
	protected java.util.Map<String, Object> getListPageResource(){
		return null;
	}
}
