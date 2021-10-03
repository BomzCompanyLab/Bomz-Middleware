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
import javafx.scene.control.Label;
import kr.co.bomz.cmn.ui.transfer.TransferSelect;
import kr.co.bomz.cmn.ui.transfer.TransferSelectItem;
import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.comm.AbstractDevice;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.LogicalGroupDeviceMap;
import kr.co.bomz.mw.db.LogicalGroupReporterMap;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.SettingInfoService;

/**
 * 	�����׷� ��� UI ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class LogicalGroupRegController extends AbstractRegController{

	public static final URL fxml = LogicalGroupRegController.class.getResource("LogicalGroupReg.fxml");
	
	/**		��ġ ���׳� ���̺�		*/
	@FXML
	private Label devAntennaLb;
	
	/**		������ ���̺�		*/
	@FXML
	private Label reporterLb;
	
	@FXML
	private TransferSelect<Integer> deviceAntennaTransferSelect;
	
	@FXML
	private TransferSelect<Integer> reporterTransferSelect;
	
	public LogicalGroupRegController(){
		super("NM_LOGI");
	}
	
	@Override
	void initialize(ResourceBundle resourceBundle) throws Exception{}
	
	/**	���ȭ�� �ʱ�ȭ		*/
	@Override
	void initRegPage() throws Exception{
		this.initDeviceAntennaRegPageList();	// ��ġ ���׳� ��� �ʱ�ȭ
		this.initReporterRegPageList();				// ������ ��� �ʱ�ȭ
	}
	
	/**	���ȭ���� �� ��ġ ���׳� ��� �ʱ�ȭ		*/
	private void initDeviceAntennaRegPageList() throws Exception{
		try{
			List<Device> devList = DatabaseService.getInstance().selectAllDeviceList();
			if( devList != null && !devList.isEmpty() ){
				// ��� ����ȯ ����
				Device bDev;
				int size = devList.size();
				List<TransferSelectItem<Integer>> list = new ArrayList<TransferSelectItem<Integer>>(size * BomzMiddleware.MAX_DEVICE_ANTENNA_LENGTH);
				for(int i=0; i < size; i++){
					for(int ant=AbstractDevice.ANTENNA_1_ID; ant < BomzMiddleware.MAX_DEVICE_ANTENNA_LENGTH; ant++){
						bDev = devList.get(i);
						list.add( new LogicalGroupDeviceMap(bDev.getDeviceId(), ant, bDev.getDeviceName()) );
					}
				}
				
				this.deviceAntennaTransferSelect.setNotUseItemList(list);
			}
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("���ȭ�� ���� ��ġ���׳� �ʱ�ȭ �����ͺ��̽� ����", e);
			throw new Exception(super.languageResourceBundle.getResourceValue("ERR_LG_1"));
		}
		
	}
	
	/**	���ȭ���� �� ������ ��� �ʱ�ȭ		*/
	private void initReporterRegPageList() throws Exception{
		try{
			List<Reporter> reporterList = DatabaseService.getInstance().selectAllReporterList();
			if( reporterList != null && !reporterList.isEmpty() ){
				// ��� ����ȯ ����
				Reporter bRep;
				int size = reporterList.size();
				List<TransferSelectItem<Integer>> list = new ArrayList<TransferSelectItem<Integer>>(size);
				for(int i=0; i < size; i++){
					bRep = reporterList.get(i);
					list.add( new LogicalGroupReporterMap(bRep.getReporterId(), bRep.getReporterName()) );
				}
				
				this.reporterTransferSelect.setNotUseItemList(list);
			}
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("���ȭ�� ���� ������ �ʱ�ȭ �����ͺ��̽� ����", e);
			throw new Exception(super.languageResourceBundle.getResourceValue("ERR_LG_2"));
		}
		
	}
	
	/**	����ȭ�� �ʱ�ȭ		*/
	@Override
	void initUpdPage(int logicalGroupId) throws Exception{
		LogicalGroup logicalGroup = DatabaseService.getInstance().selectLogicalGroupInfo(logicalGroupId);
		
		super.nameTf.setText(logicalGroup.getLogicalGroupName());				// ���� �̸� ����
		
		// �����׷�� ��ġ ���� ���� ����
		this.initUpdDeviceAntenna(logicalGroup);
		// �����׷�� ������ ���� ���� ����
		this.initUpdReporter(logicalGroup);
	}
	
	/**		������ �� ��ġ ���׳� ���� ���� �ʱ�ȭ		*/
	private void initUpdDeviceAntenna(LogicalGroup logicalGroup) throws Exception{
		this.initDeviceAntennaRegPageList();		// ������ ��Ͽ� ��ü ��ġ ���׳� �߰�
		
		List<LogicalGroupDeviceMap> list = logicalGroup.getLogicalGroupDeviceMapList();
		if( list == null || list.isEmpty() )		return;
		
		TransferSelectItem<Integer> item;
		for(LogicalGroupDeviceMap lgd : list){
			item = this.deviceAntennaTransferSelect.removeNotUseItem(lgd.getItemId());
			
			if( item == null ){
				Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
				logger.warn("�� �� ���� ��ġ���׳� ���̵�� ���� ȭ�� �ʱ�ȭ [��ġ���̵�={}, ���׳����̵�={}]", lgd.getDeviceId(), lgd.getAntennaId());
			}else{
				this.deviceAntennaTransferSelect.addUseItem(item);
			}
		}
		
	}
	
	/**		������ �� ������ ���� ���� �ʱ�ȭ		*/
	private void initUpdReporter(LogicalGroup logicalGroup) throws Exception{
		this.initReporterRegPageList();		// ������ ��Ͽ� ��ü ������ �߰�
		
		List<LogicalGroupReporterMap> list = logicalGroup.getLogicalGroupReporterMapList();
		if( list == null || list.isEmpty() )		return;
		
		TransferSelectItem<Integer> item;
		for(LogicalGroupReporterMap rep : list){
			item = this.reporterTransferSelect.removeNotUseItem(rep.getItemId());
			
			if( item == null ){
				Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
				logger.warn("�� �� ���� ������ ���̵�� ���� ȭ�� �ʱ�ȭ [�����;��̵�={}, �����͸�={}]", rep.getReporterId(), rep.getReporterName());
			}else{
				this.reporterTransferSelect.addUseItem(item);
			}
		}
		
	}
	
	@Override
	void handleSubmit(BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		LogicalGroup lg = this.toInputData();
		if( lg == null ){
			eventHandler.handle(new ActionEvent());
		}else{
			if( super.isReg() )		ControllerService.getInstance().addLogicalGroup(lg, progressBar, eventHandler);
			else							ControllerService.getInstance().updLogicalGroup(lg, progressBar, eventHandler);
		}
	}
	
	/**		���/���� �� �Է� ���� LogicalGroup ���·� ��ȯ		*/ 
	private LogicalGroup toInputData(){
		LogicalGroup lg = new LogicalGroup();
		if( !super.isReg() )		lg.setLogicalGroupId(super.getUpdateItemId());		// ����ó���� ��� ������ �����׷� ���̵�
		
		lg.setLogicalGroupName(super.nameTf.getText());		// �����׷�� 
		
		// ���׳��� �����׷� ��� ����
		lg.setLogicalGroupDeviceMapList(this.valdationDeviceAntennaList());
		// �����ͺ� �����׷� ��� ����
		lg.setLogicalGroupReporterMapList(this.valdationReporterList());
		
		return lg;
	}
	
	/**		���׳��� �����׷� ������ ��ġ��		*/
	private List<LogicalGroupDeviceMap> valdationDeviceAntennaList(){
		List<LogicalGroupDeviceMap> devAntennaList = new ArrayList<LogicalGroupDeviceMap>();
		for(TransferSelectItem<Integer> item : this.deviceAntennaTransferSelect.getUseItemList() ){
			devAntennaList.add( (LogicalGroupDeviceMap)item );
		}
			
		return devAntennaList;
	}
	
	/**		������ �����׷� ������ ��ġ��		*/
	private List<LogicalGroupReporterMap> valdationReporterList(){
		List<LogicalGroupReporterMap> reporterList = new ArrayList<LogicalGroupReporterMap>();
		for(TransferSelectItem<Integer> item : this.reporterTransferSelect.getUseItemList() ){
			reporterList.add( (LogicalGroupReporterMap)item );
		}
			
		return reporterList;
	}

	@Override
	URL getListPageURL(){
		return LogicalGroupListController.fxml;
	}

	@Override
	void initLanguage() {
		kr.co.bomz.util.resource.ResourceBundle rb = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_lg.conf");
		
		this.devAntennaLb.setText( rb.getResourceValue("LB_DEV_ANT") );
		this.reporterLb.setText( rb.getResourceValue("LB_REP") );
		
		this.deviceAntennaTransferSelect.setTitleName(
				super.languageResourceBundle.getResourceValue("LB_USE"), 
				super.languageResourceBundle.getResourceValue("LB_NON_USE")
			);
		
		this.reporterTransferSelect.setTitleName(
				super.languageResourceBundle.getResourceValue("LB_USE"), 
				super.languageResourceBundle.getResourceValue("LB_NON_USE")
			);
	}
	
}

