package kr.co.bomz.mw.ui;

import java.net.URL;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.db.SelectTerms;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.SettingInfoService;
import kr.co.bomz.util.resource.ResourceBundle;;

/**
 * 	������ ��� UI ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class ReporterListController extends AbstractListController<Reporter>{

	public static final URL fxml = ReporterListController.class.getResource("ReporterList.fxml");
	
	/**		��Ź�� �÷�		*/
	@FXML
	private TableColumn<Reporter, String> commTc;
	
	/**		����̹� �̸� �÷�		*/
	@FXML
	private TableColumn<Reporter, String> driverTc;
	
	public ReporterListController(){
		super("NM_REP");
	}
	
	@Override
	List<Reporter> getItemList(SelectTerms terms) throws Exception{
		return DatabaseService.getInstance().selectReporterListToTerm(terms);
	}
	
	@Override
	URL getRegPageUrl(){
		return ReporterRegController.fxml;
	}

	@Override
	void delItemList(List<Reporter> itemList, BomzProgressBar progressBar, EventHandler<ActionEvent> handler) {
		ControllerService.getInstance().delReporter(handler, itemList, progressBar);
	}

	@Override
	void initListColumnWidth() {
		super.nameTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.4));
		this.commTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.4));
		this.driverTc.prefWidthProperty().bind(super.listTv.widthProperty().subtract(AbstractListController.LIST_SCROLLBAR_WIDTH).multiply(0.2));
	}

	@Override
	void initLanguage() {
		kr.co.bomz.util.resource.ResourceBundle rb = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_rep.conf");
		
		this.commTc.setText(rb.getResourceValue("TC_COMM"));			// �������
		this.driverTc.setText(rb.getResourceValue("TC_DRIV"));				// ����̹�
	}
}
