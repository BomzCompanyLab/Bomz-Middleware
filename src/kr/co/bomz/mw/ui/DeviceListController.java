package kr.co.bomz.mw.ui;

import java.net.URL;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.SelectTerms;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.SettingInfoService;
import kr.co.bomz.util.resource.ResourceBundle;;

/**
 * 	��ġ ��� UI ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DeviceListController extends AbstractListController<Device>{

	public static final URL fxml = DeviceListController.class.getResource("DeviceList.fxml");
	
	/**		��Ź�� �÷�		*/
	@FXML
	private TableColumn<Device, String> commTc;
	
	/**		����̹� �̸� �÷�		*/
	@FXML
	private TableColumn<Device, String> driverTc;
	
	/**		��ġ ���� ���� �÷�		*/
	@FXML
	private TableColumn<Device, String> commStateTc;
	
	
	public DeviceListController(){
		super("NM_DEV");
	}
	
	@Override
	List<Device> getItemList(SelectTerms terms) throws Exception{
		return DatabaseService.getInstance().selectDeviceListToTerm(terms);
	}
	
	@Override
	URL getRegPageUrl(){
		return DeviceRegController.fxml;
	}

	@Override
	void delItemList(List<Device> itemList, BomzProgressBar progressBar, EventHandler<ActionEvent> handler) {
		ControllerService.getInstance().delDevice(handler, itemList, progressBar);
	}

	@Override
	void initListColumnWidth() {
		super.nameTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.4));
		this.commTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.3));
		this.driverTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.2));
		this.commStateTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.1));
	}

	@Override
	void initLanguage() {
		kr.co.bomz.util.resource.ResourceBundle rb = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_dev.conf");
		
		this.commTc.setText(rb.getResourceValue("TC_COMM"));			// �������
		this.driverTc.setText(rb.getResourceValue("TC_DRIV"));				// ����̹�
		this.commStateTc.setText(rb.getResourceValue("TC_CONN"));	// ��Ż���
	}
}
