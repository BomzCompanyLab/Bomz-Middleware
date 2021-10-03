package kr.co.bomz.mw.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import kr.co.bomz.cmn.module.BomzResource;
import kr.co.bomz.mw.service.SceneLoaderService;
import kr.co.bomz.mw.service.SettingInfoService;

/**
 * 	UI ù ȭ��. ��ü �޴� ������ ǥ��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class IndexController implements Initializable{

	/**		FXML URL		*/
	public static final URL fxml = IndexController.class.getResource("Index.fxml");
	
	/**		��ġ ��ư		*/
	@FXML
	private Button deviceBt;
	/**		�����׷� ��ư		*/
	@FXML
	private Button logicalGroupBt;
	/**		������ ��ư		*/
	@FXML
	private Button reporterBt;
	/**		��ġ ����̹� ��ư		*/
	@FXML
	private Button deviceDriverBt;
	/**		������ ����̹� ��ư		*/
	@FXML
	private Button reporterDriverBt;
	/**		���� ��ư		*/
	@FXML
	private Button settingBt;
	
	public IndexController(){}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// ��� ����
		kr.co.bomz.util.resource.ResourceBundle languageResourceBundle = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_idx.conf");
		this.deviceBt.setText(languageResourceBundle.getResourceStringValue("NM_DEV"));
		this.logicalGroupBt.setText(languageResourceBundle.getResourceStringValue("NM_LOGI"));
		this.reporterBt.setText(languageResourceBundle.getResourceStringValue("NM_REP"));
		this.deviceDriverBt.setText(languageResourceBundle.getResourceStringValue("NM_DDR"));
		this.reporterDriverBt.setText(languageResourceBundle.getResourceStringValue("NM_RDR"));
		this.settingBt.setText(languageResourceBundle.getResourceStringValue("NM_SET"));
	}
	
	/**		��ġ ������ Ŭ�� �̺�Ʈ		*/
	@FXML
	void handleDeviceListAction(){
		SceneLoaderService.getInstance().load(DeviceListController.fxml);
	}
	
	/**		�����׷� ������ Ŭ�� �̺�Ʈ		*/
	@FXML
	void handleLogicalGroupListAction(){
		SceneLoaderService.getInstance().load(LogicalGroupListController.fxml);
	}
	
	/**		������ ������ Ŭ�� �̺�Ʈ		*/
	@FXML
	void handleReporterListAction(){
		SceneLoaderService.getInstance().load(ReporterListController.fxml);
	}
	
	@FXML
	void handleDeviceDriverListAction(){
		this.handleDriverListAction(true);
	}
	
	@FXML
	void handleReporterDriverListAction(){
		this.handleDriverListAction(false);
	}
	
	@FXML
	void handleSettingAction(){
		SceneLoaderService.getInstance().load(SettingController.fxml);
	}
	
	private void handleDriverListAction(boolean isDeviceDriver){
		BomzResource resource = new BomzResource();
		resource.addResource(CommonResource.CONTROL_DRIVER_TYPE, isDeviceDriver);
		SceneLoaderService.getInstance().load(DriverListController.fxml,resource);
	}

}
