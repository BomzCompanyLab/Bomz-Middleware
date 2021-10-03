package kr.co.bomz.mw.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.db.ControlResponse;
import kr.co.bomz.mw.db.Driver;
import kr.co.bomz.mw.db.SelectTerms;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.SettingInfoService;;

/**
 * 	����̹�(��ġ/������) ��� UI ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DriverListController extends AbstractListController<Driver>{

	public static final URL fxml = DriverListController.class.getResource("DriverList.fxml");
	
	/**		��ġ ����� ��� true, ������ ����� ��� false		*/
	private boolean isDeviceDriver;
	
	/**		����̹� �̸� �÷�		*/
	@FXML
	private TableColumn<Driver, Integer> driverFileNameTc;
	
	/**		����̹��� ������� ��ġ �Ǵ� ������ ��  �÷�		*/
	@FXML
	private TableColumn<Driver, Integer> driverMappingTc;
	
	public DriverListController(){
		super("NM_DR");
	}
	
	@Override
	protected void initialize(ResourceBundle resourceBundle){
		try{
			// ���� ȭ���� ��ġ����̹� ������� �����͵���̹� ������� ���� ó��
			this.isDeviceDriver = (Boolean)resourceBundle.getObject(CommonResource.CONTROL_DRIVER_TYPE);
		}catch(Exception e){
			// ������ ���� ó��. ���� �߻� �� ��ġ ȭ������ ó��
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("����̹� ���ȭ�� Ÿ�� �� ������ ��ġ����̹� ȭ�� ó��", e);
			
			this.isDeviceDriver = true;
		}
		
		try{
			// �⺻ �̸� �տ� '��ġ' �Ǵ� '������' �̸� �߰�
			super.title = super.languageResourceBundle.getResourceStringValue( this.isDeviceDriver ? "NM_DEV" : "NM_REP") + " " + super.title;
		}catch(Exception e){}		// �߿��� ������ �ƴϹǷ� �������� �����Ѵ�
	}
	
	@Override
	List<Driver> getItemList(SelectTerms terms) throws Exception{
		terms.setDriverTableName(this.isDeviceDriver ? Driver.DRIVER_TABLE_DEVICE : Driver.DRIVER_TABLE_REPORTER);
		return DatabaseService.getInstance().selectDriverListToTerm(terms);
	}
	
	@Override
	URL getRegPageUrl(){
		return DriverRegController.fxml;
	}

	@Override
	void delItemList(List<Driver> itemList, BomzProgressBar progressBar, EventHandler<ActionEvent> handler) {
		ControllerService.getInstance().delDriver(handler, this.isDeviceDriver ? ControlResponse.TARGET_DEVICE_DRIVER : ControlResponse.TARGET_REPORTER_DRIVER, itemList, progressBar);
	}

	@Override
	void initListColumnWidth() {
		super.nameTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.4));
		this.driverFileNameTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.4));
		this.driverMappingTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.2));
	}

	@Override
	void initLanguage() {
		kr.co.bomz.util.resource.ResourceBundle rb = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_dr.conf");
		
		this.driverFileNameTc.setText(rb.getResourceValue("TC_FR_NM"));	// ���ϸ�
		this.driverMappingTc.setText(rb.getResourceValue("TC_MP_LN"));		// ����̹� ���� ��
	}

	@Override
	protected Map<String, Object> getRegPageResource(){
		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put(CommonResource.CONTROL_DRIVER_TYPE, this.isDeviceDriver);
		
		return map;
	}
}
