package kr.co.bomz.mw.service;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kr.co.bomz.mw.BomzMiddleware;

/**
 * 	FXML �ε� ����
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class SceneLoaderService {

	private static final SceneLoaderService _this = new SceneLoaderService();
	
	/**	���� ��������		*/
	private Stage stage;
	
	private SceneLoaderService(){}
	
	public static final SceneLoaderService getInstance(){
		return _this;
	}
	
	/**
	 * 	FXML Load
	 * @param url		FXML URL
	 */
	public void load(URL url){
		this.load(url, null);
	}
	
	/**
	 * 	FXML Load
	 * @param url		FXML URL
	 * @param resource		FXML ResourceBundle
	 */
	public void load(URL url, ResourceBundle resource){
		Platform.runLater( ()->{
			
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(url);
				if( resource != null )		loader.setResources(resource);
				
				Scene scene = this.stage.getScene();
				if( scene == null ){
					this.stage.setScene( new Scene(loader.load()) );
					this.stage.centerOnScreen();
				}else{
					scene.setRoot(loader.load());
				}
				
				if( !this.stage.isShowing() )		this.stage.show();
				
			} catch (Exception e) {
				Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
				if( logger.isErrorEnabled() )		logger.error("�ε���ȭ�� �ε� ����", e);
			}
			
		});
	}
	
	/**		Stage ����		*/
	public void setStage(Stage stage){
		this.stage = stage;
	}
	
	/**		Stage ����		*/
	public Stage getStage(){
		return this.stage;
	}
}
