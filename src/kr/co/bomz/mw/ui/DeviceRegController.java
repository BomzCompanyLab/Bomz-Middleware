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
import kr.co.bomz.mw.comm.AbstractDevice;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.DeviceLogicalGroupMap;
import kr.co.bomz.mw.db.Driver;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.SettingInfoService;

/**
 * 	��ġ ��� UI ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DeviceRegController extends AbstractRegController{

	public static final URL fxml = DeviceListController.class.getResource("DeviceReg.fxml");
	
	/**		��Ź�� ���̺� 		*/
	@FXML
	private Label commLb;
	
	/**		TCP ��� ���̺� 		*/
	@FXML
	private Label tcpModeLb;
	
	/**		�׷�/���׳� 1			*/
	@FXML
	private Label gpAnt1Lb;
	
	/**		�׷�/���׳� 2			*/
	@FXML
	private Label gpAnt2Lb;
	
	/**		�׷�/���׳� 3			*/
	@FXML
	private Label gpAnt3Lb;
	
	/**		�׷�/���׳� 4			*/
	@FXML
	private Label gpAnt4Lb;
	
	@FXML
	private ComboBox<Driver> deviceDriverCb;
	
	@FXML
	private BomzToggleButton deviceCommTb;
	
	@FXML
	private BomzToggleButton tcpModeTb;
	
	@FXML
	private TextField tcpIpTf;
	
	@FXML
	private TextField tcpPortTf;
	
	@FXML
	private ComboBox<ComboBoxIntegerItem> serialPortCb;
	
	@FXML
	private ComboBox<Integer> serialBaudRateCb;
	
	@FXML
	private ComboBox<ComboBoxIntegerItem> serialFlowControlCb;
	
	@FXML
	private ComboBox<ComboBoxIntegerItem> serialDataBitsCb;
	
	@FXML
	private ComboBox<ComboBoxIntegerItem> serialStopBitsCb;
	
	@FXML
	private ComboBox<ComboBoxIntegerItem> serialParityCb;
	
	@FXML
	private TransferSelect<Integer> logicalGroupTransferSelect1;
	@FXML
	private TransferSelect<Integer> logicalGroupTransferSelect2;
	@FXML
	private TransferSelect<Integer> logicalGroupTransferSelect3;
	@FXML
	private TransferSelect<Integer> logicalGroupTransferSelect4;
	
	public DeviceRegController(){
		super("NM_DEV");
	}
	
	@Override
	void initialize(ResourceBundle resourceBundle) throws Exception{
		this.initDeviceCommToggleButton();		// ��Ź�� ��۹�ư �ʱ�ȭ
		this.initTcpModeToggleButton();				// TCP ��� ��� ��۹�ư �ʱ�ȭ
		this.initDeviceDriverList();							// ��ġ ����̹� ��� �ʱ�ȭ
	}
	
	/**		��ġ ��Ź�� ��۹�ư �ʱ�ȭ		*/
	private void initDeviceCommToggleButton(){
		this.deviceCommTb.setOnButtonText("TCP/IP");
		this.deviceCommTb.setOffButtonText("SERIAL");
		this.deviceCommTb.setButtonWidth(80);
		this.deviceCommTb.setButtonHeight(25);
		this.deviceCommTb.addEventHandler( e -> this.handleDeviceCommChangeEvent() );
	}
	
	/**		TCP ��Ÿ�� ��۹�ư �ʱ�ȭ		*/
	private void initTcpModeToggleButton(){
		this.tcpModeTb.setOnButtonText("SERVER");
		this.tcpModeTb.setOffButtonText("CLIENT");
		this.tcpModeTb.setButtonWidth(80);
		this.tcpModeTb.setButtonHeight(25);
		this.tcpModeTb.addEventHandler( e -> this.handleTcpModeChangeEvent() );
	}
	
	/**	
	 * 	��ġ ����̹� ��� �ʱ�ȭ
	 * @return	�����ͺ��̽� ���� �� false
	 */
	private void initDeviceDriverList() throws Exception{
		try {
			List<Driver> list = DatabaseService.getInstance().selectDeviceDriverAllList();
			if( list != null && !list.isEmpty() ){
				this.deviceDriverCb.getItems().addAll(list);
				this.deviceDriverCb.getSelectionModel().selectFirst();
			}else{
				// ��ϵ� ����̹��� ���ٴ� �˾� ����
				new BomzPopup().showOkPopup(super.languageResourceBundle.getResourceValue("ERR_DEV_2"));		// �˾� ǥ��
			}
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("��ġ ����̹� �ʱ�ȭ �����ͺ��̽� ����", e);
			throw new Exception(super.languageResourceBundle.getResourceValue("ERR_DEV_1"));
		}
		
	}
	
	/**		��Ź�� ��۹�ư Ŭ�� �� ȣ��		*/
	private void handleDeviceCommChangeEvent(){
		this.changeDeviceComm( this.deviceCommTb.isOn() );
	}
	
	/**		TCP ��� ��۹�ư Ŭ�� �� ȣ��		*/
	private void handleTcpModeChangeEvent(){
		this.tcpIpTf.setDisable(this.tcpModeTb.isOn());
	}
	
	
	/**	���ȭ�� �ʱ�ȭ		*/
	@Override
	void initRegPage() throws Exception{
		this.initLogicalGroupRegPageList();		// �����׷� ��� �ʱ�ȭ
		this.deviceCommTb.setOn();				// �⺻���� ON : TCP/IP �� ����Ѵ�
		this.initDefaultCommSerialValue();		// �ø�������� �⺻ ������ ��Ȱ��ȭ
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
				
				this.logicalGroupTransferSelect1.setNotUseItemList(list);
				this.logicalGroupTransferSelect2.setNotUseItemList(list);
				this.logicalGroupTransferSelect3.setNotUseItemList(list);
				this.logicalGroupTransferSelect4.setNotUseItemList(list);
			}
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("���ȭ�� �����׷� �ʱ�ȭ �����ͺ��̽� ����", e);
			throw new Exception(super.languageResourceBundle.getResourceValue("ERR_DEV_3"));
		}
		
	}
	
	/**	����ȭ�� �ʱ�ȭ		*/
	@Override
	void initUpdPage(int deviceId) throws Exception{
		Device device = DatabaseService.getInstance().selectDeviceInfo(deviceId);
		
		super.nameTf.setText(device.getDeviceName());				// ���� �̸� ����
		this.selectedDriverComboBox(device.getDriverId());		// ������� ����̹� ����
		
		// TCP ��忡 ���� ó��
		if( device.getTcpMode() == Device.TCP_MODE_CLIENT )	this.tcpModeTb.setOff();
		else		this.tcpModeTb.setOn();
		
		this.handleTcpModeChangeEvent();		// IP �Է� �ʵ� Ȱ��ȭ, ��Ȱ��ȭ ó��
		
		
		// ��Ź�Ŀ� ���� ó��
		if( device.getCommType() == Device.COMM_TCP_TYPE ){
			// TCP ���
			this.deviceCommTb.setOn();		// TCP/IP ��Ź�� ����
			if( !device.isNullTcpIp() )		this.tcpIpTf.setText(device.getTcpIp());
			this.tcpPortTf.setText(device.getPort() + "");
			this.initDefaultCommSerialValue();		// �ø�������� �⺻ ������ ��Ȱ��ȭ
		}else{
			// SERIAL ���
			this.deviceCommTb.setOff();		// �ø��� ��Ź�� ����
			this.tcpModeTb.setDisable(true);		// �ø�������� ��� TCP ��� ��Ȱ��ȭ
			this.selectedComboBoxItem(this.serialPortCb, device.getPort());
			this.serialBaudRateCb.getSelectionModel().select(new Integer(device.getBaudRate()));
			this.selectedComboBoxItem(this.serialFlowControlCb, device.getFlowControl());
			this.selectedComboBoxItem(this.serialDataBitsCb, device.getDataBits());
			this.selectedComboBoxItem(this.serialStopBitsCb, device.getStopBits());
			this.selectedComboBoxItem(this.serialParityCb, device.getParity());
		}
		
		// ��ġ�� �����׷� ���� ���� ����
		this.initUpdLogicalGroup(device);
		
		this.changeDeviceComm( this.deviceCommTb.isOn() );		
	}
	
	/**		������ �� �����׷� ���� ���� �ʱ�ȭ		*/
	private void initUpdLogicalGroup(Device device) throws Exception{
		this.initLogicalGroupRegPageList();		// ������ ��Ͽ� ��ü �����׷� �߰�
		
		List<DeviceLogicalGroupMap> list = device.getLogicalGroupMapList();
		if( list == null || list.isEmpty() )		return;
		
		TransferSelect<Integer> select;
		TransferSelectItem<Integer> item;
		for(DeviceLogicalGroupMap dlg : list){
			select = this.getTransferSelect(dlg.getAntennaId());
			item = select.removeNotUseItem(dlg.getLogicalGroupId());
			
			if( item == null ){
				Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
				logger.warn("�� �� ���� �����׷� ���̵�� ���� ȭ�� �ʱ�ȭ [�����׷���̵�={}]", dlg.getLogicalGroupId());
			}else{
				select.addUseItem(item);
			}
		}
		
	}
	
	/**		���׳� ��ȣ�� �´� �����׷� ���ùڽ� ����		*/
	private TransferSelect<Integer> getTransferSelect(int antennaId) throws Exception{
		switch( antennaId ){
		case AbstractDevice.ANTENNA_1_ID :		return this.logicalGroupTransferSelect1;
		case AbstractDevice.ANTENNA_2_ID :		return this.logicalGroupTransferSelect2;
		case AbstractDevice.ANTENNA_3_ID :		return this.logicalGroupTransferSelect3;
		case AbstractDevice.ANTENNA_4_ID :		return this.logicalGroupTransferSelect4;
		default :		throw new Exception(super.languageResourceBundle.getResourceValue("ERR_DEV_4") + " [ID=" + antennaId + "]");
		}
	}
	
	/**		����ȭ���� �� ComboBox �� ������� ������ ����		*/
	private void selectedComboBoxItem(ComboBox<ComboBoxIntegerItem> comboBox, int id){
		for(ComboBoxIntegerItem item : comboBox.getItems()){
			if( item.getValue() != id )		continue;
			
			comboBox.getSelectionModel().select(item);
			break;
		}
	}
	
	
	/**		����ȭ���� �� ������� ����̹� ����		*/
	private void selectedDriverComboBox(int driverId){
		for(Driver driver : this.deviceDriverCb.getItems()){
			if( driver.getDriverId() == driverId ){
				this.deviceDriverCb.getSelectionModel().select(driver);
				break;
			}
		}
	}
	
	/**		��Ź�� ���濡 ���� ��ż��� �� Ȱ��ȭ/��Ȱ��ȭ ó��		*/
	private void changeDeviceComm(boolean isTcp){
		this.disableDeviceCommTcp(!isTcp);
		this.disableDeviceCommSerial(isTcp);
	}
	
	/**		TCP/IP ��ż��� �� Ȱ��ȭ/��Ȱ��ȭ ó��		*/
	private void disableDeviceCommTcp(boolean disable){
		this.tcpIpTf.setDisable(disable || this.tcpModeTb.isOn());
		this.tcpPortTf.setDisable(disable);
		this.tcpModeTb.setDisable(disable);		// �ø�������� ��� TCP ��� ��Ȱ��ȭ
	}
	
	/**		SERIAL ��ż��� �� Ȱ��ȭ/��Ȱ��ȭ ó��		*/
	private void disableDeviceCommSerial(boolean disable){
		this.serialBaudRateCb.setDisable(disable);
		this.serialDataBitsCb.setDisable(disable);
		this.serialFlowControlCb.setDisable(disable);
		this.serialParityCb.setDisable(disable);
		this.serialPortCb.setDisable(disable);
		this.serialStopBitsCb.setDisable(disable);
	}
	
	/**		���ȭ���� ��� �ø�����Ű� �⺻ ������ ����		*/
	private void initDefaultCommSerialValue(){
		this.serialBaudRateCb.getSelectionModel().select(new Integer(19200));
		this.serialDataBitsCb.getSelectionModel().select(3);
		this.serialFlowControlCb.getSelectionModel().select(0);
		this.serialParityCb.getSelectionModel().select(0);
		this.serialPortCb.getSelectionModel().select(0);
		this.serialStopBitsCb.getSelectionModel().select(0);
	}
	
	@Override
	void handleSubmit(BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		Device device = this.toInputData();
		if( device == null ){
			eventHandler.handle(new ActionEvent());
		}else{
			if( super.isReg() )		ControllerService.getInstance().addDevice(device, progressBar, eventHandler);
			else							ControllerService.getInstance().updDevice(device, progressBar, eventHandler);
		}
	}
	
	/**		���/���� �� �Է� ���� Device ���·� ��ȯ		*/ 
	private Device toInputData(){
		Device device = new Device();
		if( !super.isReg() )		device.setDeviceId(super.getUpdateItemId());		// ����ó���� ��� ������ ��ġ ���̵�
		
		try{
			device.setDriverId(this.deviceDriverCb.getSelectionModel().getSelectedItem().getDriverId());		// ����̹� ���̵�
		}catch(Exception e){
			// �������� ������ �� �� �ִ�
			new BomzPopup().showOkPopup(super.languageResourceBundle.getResourceValue("ERR_DEV_5"));		// �˾� ǥ��
			return  null;
		}
		
		device.setDeviceName(super.nameTf.getText());		// ��ġ�� 

		device.setTcpMode(this.tcpModeTb.isOn() ? Device.TCP_MODE_SERVER : Device.TCP_MODE_CLIENT);	// TCP ���
		
		// TCP �ϰ�� �����ǿ� ��Ʈ �˻�
		if( this.deviceCommTb.isOn() ){
			device.setCommType(Device.COMM_TCP_TYPE);
			device.setTcpIp(this.tcpIpTf.getText());		// TCP ������
			try{
				device.setPort(Integer.parseInt(this.tcpPortTf.getText().trim()));				// TCP ��Ʈ �˻�
			}catch(Exception e){
				new BomzPopup().showOkPopup(super.languageResourceBundle.getResourceValue("ERR_DEV_6"));		// �˾� ǥ��
				return  null;
			}
		}else{
			device.setCommType(Device.COMM_SERIAL_TYPE);
			device.setBaudRate(this.serialBaudRateCb.getSelectionModel().getSelectedItem());
			device.setPort( this.serialPortCb.getSelectionModel().getSelectedItem().getValue());
			device.setDataBits(this.serialDataBitsCb.getSelectionModel().getSelectedItem().getValue());
			device.setFlowControl(this.serialFlowControlCb.getSelectionModel().getSelectedItem().getValue());
			device.setParity(this.serialParityCb.getSelectionModel().getSelectedItem().getValue());
			device.setStopBits(this.serialStopBitsCb.getSelectionModel().getSelectedItem().getValue());
		}
		
		// ���׳��� �����׷� ��� ����
		List<DeviceLogicalGroupMap> lgList = new ArrayList<DeviceLogicalGroupMap>();
		this.valdationLogicalGroupList(this.logicalGroupTransferSelect1.getUseItemList(), AbstractDevice.ANTENNA_1_ID, lgList);
		this.valdationLogicalGroupList(this.logicalGroupTransferSelect2.getUseItemList(), AbstractDevice.ANTENNA_2_ID, lgList);
		this.valdationLogicalGroupList(this.logicalGroupTransferSelect3.getUseItemList(), AbstractDevice.ANTENNA_3_ID, lgList);
		this.valdationLogicalGroupList(this.logicalGroupTransferSelect4.getUseItemList(), AbstractDevice.ANTENNA_4_ID, lgList);
		device.setLogicalGroupMapList(lgList);
		
		return device;
	}
	
	/**		���׳��� �����׷� ������ ��ġ��		*/
	private void valdationLogicalGroupList(List<TransferSelectItem<Integer>> itemList, int antennaId, List<DeviceLogicalGroupMap> lgList){
		for(TransferSelectItem<Integer> item : itemList )
			lgList.add( new DeviceLogicalGroupMap(antennaId, item.getItemId()) );
	}

	@Override
	URL getListPageURL(){
		return DeviceListController.fxml;
	}

	@Override
	void initLanguage() {
		kr.co.bomz.util.resource.ResourceBundle rb = new kr.co.bomz.util.resource.ResourceBundle(SettingInfoService.getInstance().getLanguagePath(), "language_dev.conf");
		
		this.commLb.setText( rb.getResourceValue("LB_COMM") );
		this.tcpModeLb.setText( rb.getResourceValue("LB_TCP_MODE") );
		this.gpAnt1Lb.setText( rb.getResourceValue("LB_GP_ANT") + 1 );
		this.gpAnt2Lb.setText( rb.getResourceValue("LB_GP_ANT") + 2 );
		this.gpAnt3Lb.setText( rb.getResourceValue("LB_GP_ANT") + 3 );
		this.gpAnt4Lb.setText( rb.getResourceValue("LB_GP_ANT") + 4 );
		
		this.logicalGroupTransferSelect1.setTitleName(
				super.languageResourceBundle.getResourceValue("LB_USE"), 
				super.languageResourceBundle.getResourceValue("LB_NON_USE")
			);
	}
	
}
