package kr.co.bomz.mw;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import kr.co.bomz.cmn.db.InitDatabase;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.DeviceService;
import kr.co.bomz.mw.service.LogicalGroupService;
import kr.co.bomz.mw.service.QueueManagerService;
import kr.co.bomz.mw.service.ReporterService;
import kr.co.bomz.mw.service.SceneLoaderService;
import kr.co.bomz.mw.service.SettingInfoService;
import kr.co.bomz.mw.service.SoapService;
import kr.co.bomz.mw.ui.IndexController;
import kr.co.bomz.mw.ui.TrayIconMenu;
import kr.co.bomz.util.resource.ResourceBundle;

/**
 * 	���� �̵����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class BomzMiddleware extends Application{

	/**		���� �̵���� ���� �ΰŸ�		*/
	public static final String MW_LOGGER_NAME = "BomzMw";
	
	/**		��ġ�� ���׳� �ִ� ��		 */
	public static final int MAX_DEVICE_ANTENNA_LENGTH = 4;
	
	/** 		��ġ, �����׷�, ������ �̸� �ּ� ���� ��		*/
	public static final int MIN_NAME_LENGTH = 2;
	
	/** 		��ġ, �����׷�, ������ �̸� �ִ� ���� ��		*/
	public static final int MAX_NAME_LENGTH = 30;
	
	public static void main(String[] args){
		kr.co.bomz.logger.Logger.setLogConfigFile(new java.io.File("./conf/logger.xml"));
		
		ResourceBundle resource = new ResourceBundle("conf", "jdbc.properties");
		InitDatabase dbService = new InitDatabase();
		dbService.initTable(
				BomzMiddleware.class.getResource("table.sql"), 
				resource.getResourceStringValue("dbId"), 		// ��� ���̵� �ҷ�����
				resource.getResourceStringValue("dbPw")		// ��� ��ȣ �ҷ�����
			);
		
		SettingInfoService.getInstance().initSetting();			// �ý��� ���� �� �ʱ�ȭ
		
		// ������ �������� �����Ǿ� ���� ��� ������ ����
		SoapService.getInstance().startOrStop();
		
		launch(args);
	}


	@Override
	public void start(Stage stage) throws Exception {
		Logger logger = LoggerFactory.getLogger(MW_LOGGER_NAME);
		
		if( !this.initMiddleware() ){
			// �⺻ ���� �ε� ������ �ý��� ���� ó��
			logger.error("�̵���� �⺻ ���� ���� ������ �ý��� ����");
			
			// �˾� ó�� ����
			System.exit(1);
			return;
		}
		
		stage.setTitle("Bomz Middleware");
		
		// �ּ� ũ�� ����
		stage.setMinWidth(800);
		stage.setMinHeight(500);
		stage.setMaximized(true);		// ��üȭ������ ����
		
		// FXML Loader ���񽺿��� Stage ����
		SceneLoaderService.getInstance().setStage(stage);
		
		
		
		// UI �� Ʈ���̾����� ���
		TrayIconMenu trayIconMenu = new TrayIconMenu();
		if( !trayIconMenu.initTrayIcon(stage) && logger.isWarnEnabled() )		logger.warn("�̵���� Ʈ���̾����� ��� ����");
		
		// �⺻ UI �̺�Ʈ ����
		this.initStageEvent(stage);
		
		logger.info("Bomz Middleware Boot");
		
		// �׽�Ʈ �ӽÿ�
		SceneLoaderService.getInstance().load(IndexController.fxml);
	}
	
	/**		UI ȭ��� Stage �⺻ �̺�Ʈ ����		*/
	private void initStageEvent(Stage stage){
		Platform.setImplicitExit(false);		// �̰� �����ָ� ������ݱ� �̺�Ʈ �� �ٽ� ȭ�麸�⸦ �ϸ� �� �ɷ��� �ȵȴ�
		
		stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e->{
			stage.close();
			stage.setScene(null);
			e.consume();
		});
		
	}
	
	/**		�̵���� ���� ���� �ʱ�ȭ		*/
	private boolean initMiddleware(){
		SqlSession session = null;
		try{
			session = DatabaseService.getInstance().openSession();
			
			List<Device> deviceList = DatabaseService.getInstance().selectAllDeviceList(session);
			List<LogicalGroup> logicalGroupList = DatabaseService.getInstance().selectAllLogicalGroupDetailList(session);
			List<Reporter> reporterList = DatabaseService.getInstance().selectAllReporterList(session);
			
			DeviceService.getInstance().initDevice(session, deviceList);				// ��ġ �ʱ�ȭ
			ReporterService.getInstance().initReporter(session, reporterList);		// ������ �ʱ�ȭ
			LogicalGroupService.getInstance().initLogicalGroup(logicalGroupList);		// �����׷� �� ��ġ/������ ���� ���� �ʱ�ȭ
			QueueManagerService.getInstance().updateQueues();		// ť ����
			
			return true;
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(MW_LOGGER_NAME);
			logger.error("�̵���� �⺻ ���� ���� �� ����", e);
			
			return false;
		}finally{
			if( session != null )		session.close();
		}
	}
	
}
