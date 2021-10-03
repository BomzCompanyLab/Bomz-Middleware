package kr.co.bomz.mw.ui;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.db.ControlResponse;
import kr.co.bomz.mw.db.Driver;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.SceneLoaderService;

/**
 * 	����̹�(��ġ/������) ��� UI ó��
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DriverRegController extends AbstractRegController{

	public static final URL fxml = DriverRegController.class.getResource("DriverReg.fxml");
	
	private boolean isDeviceDriver;
	
	/**	����̹� �����̸� ���̺�		*/
	@FXML
	private Label driverFileNameLb;
	
	/**	���/���� �� ����̹� ���� ��ü		*/
	private File driverJarFile;
	
	public DriverRegController(){
		super("NM_DR");
	}
	
	@Override
	void initialize(ResourceBundle resourceBundle) throws Exception{
		try{
			// ���� ȭ���� ��ġ����̹� ������� �����͵���̹� ������� ���� ó��
			this.isDeviceDriver = (Boolean)resourceBundle.getObject(CommonResource.CONTROL_DRIVER_TYPE);
		}catch(Exception e){
			// ������ ���� ó��. ���� �߻� �� ��ġ ȭ������ ó��
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("����̹� ���/����ȭ�� Ÿ�� �� ������ ��ġ����̹� ȭ�� ó��", e);
			
			this.isDeviceDriver = true;
		}
		
		try{
			// �⺻ �̸� �տ� '��ġ' �Ǵ� '������' �̸� �߰�
			super.title = super.languageResourceBundle.getResourceStringValue( this.isDeviceDriver ? "NM_DEV" : "NM_REP") + " " + super.title;
		}catch(Exception e){}		// �߿��� ������ �ƴϹǷ� �������� �����Ѵ�
	}
	
	
	/**	���ȭ�� �ʱ�ȭ		*/
	@Override
	void initRegPage() throws Exception{}
	
	
	/**	����ȭ�� �ʱ�ȭ		*/
	@Override
	void initUpdPage(int driverId) throws Exception{
		Driver driver = new Driver();
		driver.setDriverId(driverId);
		driver.setDriverTarget( this.getDriverTarget() );
		
		driver = DatabaseService.getInstance().selectDriverInfo(driver);
		
		super.nameTf.setText(driver.getDriverName());						// ���� �̸� ����
		this.driverFileNameLb.setText(driver.getDriverFileName());		// ���� ���� ���� �̸�
	}
	
	@Override
	void handleSubmit(BomzProgressBar progressBar, EventHandler<ActionEvent> eventHandler){
		Driver driver = this.toInputData();
		if( driver == null )	{
			eventHandler.handle(new ActionEvent());
		}else{
			if( super.isReg() )		ControllerService.getInstance().addDriver(driver, progressBar, eventHandler);
			else							ControllerService.getInstance().updDriver(driver, progressBar, eventHandler);
		}
	}
	
	/**		���/���� �� �Է� ���� Device ���·� ��ȯ		*/ 
	private Driver toInputData(){
		Driver driver = new Driver();
		driver.setDriverTarget( this.getDriverTarget() );		// ����̹� ���� ����
		if( !super.isReg() )		driver.setDriverId(super.getUpdateItemId());		// ����ó���� ��� ������ ������ ���̵�
		
		driver.setDriverName(super.nameTf.getText());		// ��ġ�� 
		
		if( this.driverJarFile != null ){
			// ����̹� JAR ���� �б�
			byte[] jarData = this.readDriverJarFile();
			if( jarData == null ){
				new BomzPopup().showOkPopup(super.languageResourceBundle.getResourceValue("ERR_DR_2"));		// �˾� ǥ��
				return null;
			}
			
			driver.setDriverJarFile(jarData);
			driver.setDriverFileName(this.driverJarFile.getName());
		}
		
		if( super.isReg() && this.driverJarFile == null ){
			// ���ȭ���ε� ����̹� ������ �������� �ʾ��� ���
			new BomzPopup().showOkPopup(super.languageResourceBundle.getResourceValue("ERR_DR_1"));		// �˾� ǥ��
			return null;
		}
		
		return driver;
	}
	
	/**		����̹� ���� �б�		*/
	private byte[] readDriverJarFile(){
		FileInputStream fis = null;
		FileChannel fc = null;
		
		try{
			fis = new FileInputStream(this.driverJarFile);
			fc = fis.getChannel();
			ByteBuffer bb = ByteBuffer.allocateDirect(200);
			byte[] data = null, tmpData1, tmpData2;
			
			long position = 0;
			int size;
			while( true ){
				size = fc.read(bb, position);
				if( size <= 0 )		break;
				position += size;
				bb.flip();
				
				if( data == null ){		// ù �б�
					data = new byte[size];
					bb.get(data);
				}else{
					tmpData1 = new byte[size];
					bb.get(tmpData1);
					
					// �迭 ��ġ��
					tmpData2 = new byte[data.length + size];
					System.arraycopy(data, 0, tmpData2, 0, data.length);
					System.arraycopy(tmpData1, 0, tmpData2, data.length, tmpData1.length);
					
					data = tmpData2;
					tmpData1 = null;
					tmpData2 = null;
				}
				
				bb.flip();
			}
			
			return data;
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("����̹� ���� �б� ����", e);
			
			return null;
		}finally{
			if( fc != null )		try{	fc.close();		}catch(Exception e){}
			if( fis != null )		try{	fis.close();		}catch(Exception e){}
		}
		
	}
	
	@Override
	URL getListPageURL(){
		return DriverListController.fxml;
	}

	@Override
	void initLanguage() {}
	
	private char getDriverTarget(){
		return this.isDeviceDriver ? ControlResponse.TARGET_DEVICE_DRIVER : ControlResponse.TARGET_REPORTER_DRIVER;
	}
	
	/**	����̹� ���� �˻� ��ư Ŭ�� �̺�Ʈ		*/
	@FXML
	void handleDriverFileChooserAction(){
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("driver jar files", "*.jar"));
		this.driverJarFile = fc.showOpenDialog(SceneLoaderService.getInstance().getStage());
		
		if( this.driverJarFile == null )		this.driverFileNameLb.setText("");
		else												this.driverFileNameLb.setText(this.driverJarFile.getName());
	}
	
	/**		���/����ȭ�鿡�� ���ȭ������ Ư���� ������ ���ҽ� ���� ���� ��� ó��	*/
	@Override
	protected Map<String, Object> getListPageResource(){
		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put(CommonResource.CONTROL_DRIVER_TYPE, this.isDeviceDriver);
		
		return map;
	}
}
