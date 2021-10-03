package kr.co.bomz.mw.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import kr.co.bomz.cmn.ui.button.BomzToggleButton;
import kr.co.bomz.cmn.ui.transfer.TransferSelect;
import kr.co.bomz.cmn.ui.transfer.TransferSelectItem;
import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.db.Driver;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.db.ReporterLogicalGroupMap;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.SettingInfoService;

/**
 * 	������ ��� UI ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class ReporterRegController extends AbstractRegController{

	public static final URL fxml = ReporterRegController.class.getResource("ReporterReg.fxml");
	
	/**		�����׷� ���̺�			*/
	@FXML
	private Label logicalGroupLb;
	
	/**		Ÿ�̸� ��뿩�� ���̺�		*/
	@FXML
	private Label timerUseLb;
	
	/**		Ÿ�̸� �����ֱ� ���̺�		*/
	@FXML
	private Label timerValueLb;
	
	/**		������ �Ķ���� ���̺�		*/
	@FXML
	private Label parameterLb;
	
	@FXML
	private ComboBox<Driver> reporterDriverCb;
	
	@FXML
	private TextField tcpIpTf;
	
	@FXML
	private TextField tcpPortTf;
	
	@FXML
	private TransferSelect<Integer> logicalGroupTransferSelect;
	
	@FXML
	private BomzToggleButton timerUseTb;
	
	@FXML
	private TextField timerRepeatPeriodTf;
	
	@FXML
	private TextField reporterParameterTf;
	
	public ReporterRegController(){
		super("NM_REP");
	}
	
	@Override
	void initialize(ResourceBundle resourceBundle) throws Exception{
		this.initReporterDriverList();						// ������ ����̹� ��� �ʱ�ȭ
		this.initTimerTypeToggleButton();			// Ÿ�̸� ��뿩�� ��۹�ư �ʱ�ȭ
	}
	
	/**		Ÿ�̸� ����� ��۹�ư �ʱ�ȭ		*/
	private void initTimerTypeToggleButton(){
		this.timerUseTb.setButtonWidth(80);
		this.timerUseTb.setButtonHeight(25);
		this.timerUseTb.addEventHandler( e -> this.handleTimerUseTypeChangeEvent() );
	}
	
	/**		Ÿ�̸� ����� ��۹�ư Ŭ�� �̺�Ʈ		*/
	private void handleTimerUseTypeChangeEvent(){
		this.timerRepeatPeriodTf.setDisable( this.timerUseTb.isOn() );
	}
	
	/**	
	 * 	������ ����̹� ��� �ʱ�ȭ
	 * @return	�����ͺ��̽� ���� �� false
	 */
	private void initReporterDriverList() throws Exception{
		try {
			List<Driver> list = DatabaseService.getInstance().selectReporterDriverAllList();
			if( list != null && !list.isEmpty() ){
				this.reporterDriverCb.getItems().addAll(list);
				this.reporterDriverCb.getSelectionModel().selectFirst();
			}else{
				// ��ϵ� ����̹��� ���ٴ� �˾� ����
				new BomzPopup().showOkPopup(super.languageResourceBundle.getResourceValue("ERR_DEV_2"));		// �˾� ǥ��
			}
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("������ ����̹� �ʱ�ȭ �����ͺ��̽� ����", e);
			throw new Exception(super.languageResourceBundle.getResourceValue("ERR_DEV_1"));
		}
		
	}
	
	/**	���ȭ�� �ʱ�ȭ		*/
	@Override
	void initRegPage() throws Exception{
		this.initLogicalGroupRegPageList();		// �����׷� ��� �ʱ�ȭ
	}
	
	/**	���ȭ���� �� �����׷� ��� �ʱ�ȭ		*/
	private void initLogicalGroupRegPageList() throws Exception{
		try{
			List<LogicalGroup> lgList = DatabaseService.getInstance().selectAllLogicalGroupList();
			if( lgList != null && !lgList.isEmpty() ){
				// ��� ����ȯ ����
				int size = lgList.size();
				List<TransferSelectItem<Integer>> list = new ArrayList<TransferSelectItem<Integer>>(size);
				for(int i=0; i < size; i++)		list.add( lgList.get(i) );
				
				this.logicalGroupTransferSelect.setNotUseItemList(list);
			}
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("���ȭ�� �����׷� �ʱ�ȭ �����ͺ��̽� ����", e);
			throw new Exception(super.languageResourceBundle.getResourceValue("ERR_DEV_3"));
		}
		
	}
	
	/**	����ȭ�� �ʱ�ȭ		*/
	@Override
	void initUpdPage(int reporterId) throws Exception{
		Reporter reporter = DatabaseService.getInstance().selectReporterInfo(reporterId);
		
		super.nameTf.setText(reporter.getReporterName());		// ���� �̸� ����
		this.selectedDriverComboBox(reporter.getDriverId());		// ������� ����̹� ����
		
		int timerRepeatTime = reporter.getTimerRepeatTime();
		if( timerRepeatTime > 0 ){
			// Ÿ�̸� �ð������� ���
			this.timerUseTb.setOff();
			this.timerRepeatPeriodTf.setText( String.valueOf(timerRepeatTime) );
		}else{
			// ��� ������ ���
			this.timerUseTb.setOn();
		}
		
		// �Ķ���� ó��
		if( !reporter.isNullReporterParam() )		this.reporterParameterTf.setText(reporter.getReporterParam());
		
		this.tcpIpTf.setText(reporter.getReporterIp());
		this.tcpPortTf.setText(reporter.getReporterPort() + "");
		
		// �����Ϳ� �����׷� ���� ���� ����
		this.initUpdLogicalGroup(reporter);
	}
	
	/**		������ �� �����׷� ���� ���� �ʱ�ȭ		*/
	private void initUpdLogicalGroup(Reporter reporter) throws Exception{
		this.initLogicalGroupRegPageList();		// ������ ��Ͽ� ��ü �����׷� �߰�
		
		List<ReporterLogicalGroupMap> list = reporter.getLogicalGroupMapList();
		if( list == null || list.isEmpty() )		return;
		
		TransferSelectItem<Integer> item;
		for(ReporterLogicalGroupMap rlg : list){
			item = this.logicalGroupTransferSelect.removeNotUseItem(rlg.getLogicalGroupId());
			
			if( item == null ){
				Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
				logger.warn("�� �� ���� �����׷� ���̵�� ���� ȭ�� �ʱ�ȭ [�����׷���̵�={}]", rlg.getLogicalGroupId());
			}else{
				this.logicalGroupTransferSelect.addUseItem(item);
			}
		}
		
	}
	
	/**		����ȭ���� �� ������� ����̹� ����		*/
	private void selectedDriverComboBox(int driverId){
		for(Driver driver : this.reporterDriverCb.getItems()){
			if( driver.getDriverId() == driverId ){
				this.reporterDriverCb.getSelectionModel().select(driver);
				break;
			}
		}
	}
	
	@Override
	void handleSubmit(BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		Reporter reporter = this.toInputData();
		if( reporter == null ){
			eventHandler.handle(new ActionEvent());
		}else{
			if( super.isReg() )		ControllerService.getInstance().addReporter(reporter, progressBar, eventHandler);
			else							ControllerService.getInstance().updReporter(reporter, progressBar, eventHandler);
		}
	}
	
	/**		���/���� �� �Է� ���� Device ���·� ��ȯ		*/ 
	private Reporter toInputData(){
		Reporter reporter = new Reporter();
		if( !super.isReg() )		reporter.setReporterId(super.getUpdateItemId());		// ����ó���� ��� ������ ������ ���̵�
		
		try{
			reporter.setDriverId(this.reporterDriverCb.getSelectionModel().getSelectedItem().getDriverId());		// ����̹� ���̵�
		}catch(Exception e){
			// �������� ������ �� �� �ִ�
			new BomzPopup().showOkPopup(super.languageResourceBundle.getResourceValue("ERR_DEV_5"));		// �˾� ǥ��
			return  null;
		}
		
		try{
			if( this.timerUseTb.isOn() ){		
				// ��� ������ ���
				reporter.setTimerRepeatTime(0);		// 0 ���� ��� ����
			}else{
				reporter.setTimerRepeatTime( this.parsingReatPeriodValue() );		// Ÿ�̸� ��� ��
			}
		}catch(Exception e){
			// Ÿ�̸� �����ֱ� �ð� �Է°� ������ ���
			kr.co.bomz.util.resource.ResourceBundle rb = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_rep.conf");
			new BomzPopup().showOkPopup(rb.getResourceValue("ERR_4"));		// �˾� ǥ��
			return  null;
		}
		
		reporter.setReporterParam(this.reporterParameterTf.getText());		// �Ķ���� ��
		
		reporter.setReporterName(super.nameTf.getText());		// ��ġ�� 
		
		reporter.setReporterIp(this.tcpIpTf.getText());		// TCP ������
		try{
			reporter.setReporterPort(Integer.parseInt(this.tcpPortTf.getText().trim()));				// TCP ��Ʈ �˻�
		}catch(Exception e){
			new BomzPopup().showOkPopup(super.languageResourceBundle.getResourceValue("ERR_DEV_6"));		// �˾� ǥ��
			return  null;
		}
		
		// �����׷� ��� ����
		List<ReporterLogicalGroupMap> lgList = new ArrayList<ReporterLogicalGroupMap>();
		for(TransferSelectItem<Integer> item : this.logicalGroupTransferSelect.getUseItemList() )
			lgList.add( new ReporterLogicalGroupMap(item.getItemId()) );
		
		reporter.setLogicalGroupMapList(lgList);
		
		return reporter;
	}

	/**		Ÿ�̸� �ð��ֱ� ���� ����� ��� �ð� �� �м�		*/
	private int parsingReatPeriodValue() throws Exception{
		int repeatPeriod = Integer.parseInt(this.timerRepeatPeriodTf.getText().trim());
		if( repeatPeriod <= 0 )		throw new Exception();
		return repeatPeriod;
	}
	
	@Override
	URL getListPageURL(){
		return ReporterListController.fxml;
	}

	@Override
	void initLanguage() {
		kr.co.bomz.util.resource.ResourceBundle rb = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_rep.conf");
		
		this.timerUseLb.setText( rb.getResourceValue("LB_TIME_USE") );
		this.timerValueLb.setText( rb.getResourceValue("LB_TIME_VAL") );
		this.parameterLb.setText( rb.getResourceValue("LB_PARAM") );
		this.logicalGroupLb.setText( rb.getResourceValue("LB_LOGI") );
		
		this.timerUseTb.setOnButtonText( rb.getResourceValue("TB_USE") );
		this.timerUseTb.setOffButtonText( rb.getResourceValue("TB_N_USE") );
		
		this.logicalGroupTransferSelect.setTitleName(
				super.languageResourceBundle.getResourceValue("LB_USE"), 
				super.languageResourceBundle.getResourceValue("LB_NON_USE")
			);
	}
	
}
