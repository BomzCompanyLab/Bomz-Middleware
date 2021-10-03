package kr.co.bomz.mw.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import kr.co.bomz.cmn.ui.button.BomzToggleButton;
import kr.co.bomz.cmn.ui.field.BomzPasswordField;
import kr.co.bomz.cmn.ui.field.BomzTextField;
import kr.co.bomz.mw.service.SceneLoaderService;
import kr.co.bomz.mw.service.SettingInfoService;
import kr.co.bomz.mw.service.SoapService;

/**
 * 	����ȭ�� ��Ʈ�ѷ�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class SettingController implements Initializable{

	public static final URL fxml = SettingController.class.getResource("Setting.fxml");
	
	@FXML
	private Label menuTitleLb;
	
	@FXML
	private Label languageLb;
	
	@FXML
	private Label pageItemLengthLb;
	
	@FXML
	private Label webServiceLb;
	
	@FXML
	private Label webServicePortLb;
	
	@FXML
	private Label webServicePwLb;
	
	@FXML
	private Label webServiceWsdlLb;
	
	@FXML
	private ComboBox<ComboBoxStringItem> languageCb;
	
	@FXML
	private ComboBox<ComboBoxIntegerItem> pageItemLengthCb;
	
	@FXML 
	private BomzToggleButton webServiceTb;
	
	@FXML
	private BomzTextField webServicePortTf;
	
	@FXML
	private BomzPasswordField webServicePwTf;
	
	@FXML
	private Button submitBt;
	
	@FXML
	private Button cancelBt; 
	
	private kr.co.bomz.util.resource.ResourceBundle languageResourceBundle = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_st.conf");
	
	public SettingController(){}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.initDefaultLanguage();		// �ٱ��� ����
		this.initLanguage();					// ��� ���� �� �ʱ�ȭ
		this.initPageItemLength();		// ���ȭ�� �� ������ �� ���� �� �ʱ�ȭ
		this.initWebService();				// ������ ��뿩�� �ʱ�ȭ
		this.initWebServicePort();			// ������ ��Ʈ �� �ʱ�ȭ
		this.initWebServicePw();			// ������ ��ȣ �� �ʱ�ȭ
	}

	/**		������ ��ȣ �� �ʱ�ȭ		*/
	private void initWebServicePw(){
		this.webServicePwTf.setText(SettingInfoService.getInstance().getWebServicePw());
	}
	
	/**		������ ��Ʈ �� �ʱ�ȭ		*/
	private void initWebServicePort(){
		this.webServicePortTf.setText( String.valueOf(SettingInfoService.getInstance().getWebServicePort()) );
	}
	
	/**		������ ��뿩�� �� �ʱ�ȭ		*/
	private void initWebService(){
		this.webServiceTb.setOnButtonText( this.languageResourceBundle.getResourceValue("USE") );
		this.webServiceTb.setOffButtonText( this.languageResourceBundle.getResourceValue("NON_USE") );
		
		this.webServiceTb.setButtonWidth(120);
		if( SettingInfoService.getInstance().getWebService() )		this.webServiceTb.setOn();
		else																					this.webServiceTb.setOff();
	}
	
	/**		���ȭ�� �� ������ �� ���� �� �ʱ�ȭ		*/
	private void initPageItemLength(){
		final String unit = this.languageResourceBundle.getResourceValue("UNIT");
		
		ObservableList<ComboBoxIntegerItem> itemList = this.pageItemLengthCb.getItems();
		
		int settingValue = SettingInfoService.getInstance().getPageItemLength();
		ComboBoxIntegerItem item;
		for(int i=5; i <= 50; i+=5){
			item = new ComboBoxIntegerItem(i + unit, i);
			itemList.add(item);
			if( i == settingValue )		this.pageItemLengthCb.getSelectionModel().select(item);
		}
		
		// ���� ���õ� ������ ���� ��� �⺻������ ù��° ������ ����
		if( this.pageItemLengthCb.getSelectionModel().getSelectedIndex() == -1 )	
			this.pageItemLengthCb.getSelectionModel().select(0);		// �⺻���� 0��° ���õǰ� ��
	}
	
	/**		��� ���� �� �ʱ�ȭ		*/
	private void initLanguage(){
		int select = 0;
		String nowLanguage = SettingInfoService.getInstance().getLanguage();
		
		ObservableList<ComboBoxStringItem> itemList = this.languageCb.getItems();
		String[] languages = SettingInfoService.getInstance().getSuccessLanguages(); 
		for(int i=0; i < languages.length; i++){
			itemList.add(
					new ComboBoxStringItem(this.languageResourceBundle.getResourceValue("LANG_" + languages[i].toUpperCase()), languages[i])
				);
			
			if( nowLanguage.equals(languages[i]) )		select = i;
		}
		
		this.languageCb.getSelectionModel().select(select);
	}
	
	/**		�ٱ�� ���� ��� ����		*/
	private void initDefaultLanguage(){
		this.menuTitleLb.setText(this.languageResourceBundle.getResourceValue("LB_TITLE"));		// �޴���
		
		this.languageLb.setText(this.languageResourceBundle.getResourceValue("LB_LANG"));		// ���
		this.pageItemLengthLb.setText(this.languageResourceBundle.getResourceValue("LB_PG_ITEM_LN"));	// �� ȭ�� �� ������ ǥ�� ��
		this.webServiceLb.setText(this.languageResourceBundle.getResourceValue("LB_WEBSERVICE"));	// ������ ��뿩��
		this.webServicePortLb.setText(this.languageResourceBundle.getResourceValue("LB_WEBSERVICE_PORT"));		// ������ ��Ʈ��ȣ
		this.webServicePwLb.setText(this.languageResourceBundle.getResourceValue("LB_WEBSERVICE_PW"));			// ������ ��ȣ
		this.webServiceWsdlLb.setText(this.languageResourceBundle.getResourceValue("LB_WEBSERVICE_WSDL"));	// ������ WSDL
		
		this.submitBt.setText(this.languageResourceBundle.getResourceValue("BT_OK"));
		this.cancelBt.setText(this.languageResourceBundle.getResourceValue("BT_CANCEL"));
	}
	
	@FXML
	void handleSubmitAction(){
			
		try{
			SettingInfoService.getInstance().updateSettingInfo(
					this.languageResourceBundle, 
					this.languageCb.getSelectionModel().getSelectedItem().getValue(),
					this.pageItemLengthCb.getSelectionModel().getSelectedItem().getValue(),
					this.webServiceTb.isOn(),
					this.webServicePortTf.getText(),
					this.webServicePwTf.getText()
				);
		
			// ������ ���࿩�� ó�� (���࿩�� ���� �Ǵ� ��Ʈ ������ ���� ����)
			SoapService.getInstance().startOrStop();
			
			// ���� �� ù ȭ������ �̵�
			SceneLoaderService.getInstance().load(IndexController.fxml);
		}catch(Exception e){
			new BomzPopup().showOkPopup(e.getMessage());		// �˾� ǥ��
		}
		
	}
	
	@FXML
	void handleCancelAction(){
		SceneLoaderService.getInstance().load(IndexController.fxml);
	}
	
}
