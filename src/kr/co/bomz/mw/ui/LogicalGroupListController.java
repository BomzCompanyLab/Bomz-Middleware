package kr.co.bomz.mw.ui;

import java.net.URL;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.SelectTerms;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.SettingInfoService;
import kr.co.bomz.util.resource.ResourceBundle;;

/**
 * 	�����׷� ��� UI ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class LogicalGroupListController extends AbstractListController<LogicalGroup>{

	public static final URL fxml = LogicalGroupListController.class.getResource("LogicalGroupList.fxml");
	
	/**		�����׷쿡 ����� ��ġ �� �÷�		*/
	@FXML
	private TableColumn<LogicalGroup, Integer> mappingDeviceTc;
	
	/**		�����׷쿡 ����� ������ �� �÷�		*/
	@FXML
	private TableColumn<LogicalGroup, Integer> mappingReporterTc;
	
	public LogicalGroupListController(){
		super("NM_LG");
	}
	
	@Override
	List<LogicalGroup> getItemList(SelectTerms terms) throws Exception{
		return DatabaseService.getInstance().selectLogicalGroupListToTerm(terms);
	}
	
	@Override
	URL getRegPageUrl(){
		return LogicalGroupRegController.fxml;
	}

	@Override
	void delItemList(List<LogicalGroup> itemList, BomzProgressBar progressBar, EventHandler<ActionEvent> handler) {
		 ControllerService.getInstance().delLogicalGroup(handler, itemList, progressBar);
	}

	@Override
	void initListColumnWidth() {
		super.nameTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.6));
		this.mappingDeviceTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.2));
		this.mappingReporterTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.2));
	}

	@Override
	void initLanguage() {
		kr.co.bomz.util.resource.ResourceBundle rb = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_lg.conf");
		
		this.mappingDeviceTc.setText(rb.getResourceValue("TC_DEV_MP"));			// ��ġ���׳� ���� ��
		this.mappingReporterTc.setText(rb.getResourceValue("TC_REP_MP"));		// ������ ���� ��
	}

}
