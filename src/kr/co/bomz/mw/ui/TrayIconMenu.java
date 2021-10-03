package kr.co.bomz.mw.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Stage;
import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.service.SceneLoaderService;

/**
 * 	OS �� Ʈ���̾����� UI �ʱ�ȭ
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class TrayIconMenu {

	private TrayIcon trayIcon;
	
	/**		�̵���� Ʈ���̾����� UI �ʱ�ȭ		*/
	public boolean initTrayIcon(Stage stage){
		if( this.trayIcon != null )		return true;
		
		if( !SystemTray.isSupported() ){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			if( logger.isWarnEnabled() )		logger.warn("OS���� �ý���Ʈ���� ������ ���������� Ʈ���̾����� �߰� ����");
			return false;
		}
		
		Image trayIconImage = null;
		try{
			trayIconImage = ImageIO.read(TrayIconMenu.class.getResource("bomz.png"));
		}catch(IOException e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			if( logger.isWarnEnabled() )		logger.warn("Ʈ���̾����� �̹��� �ε� ����", e);
		}
		
		this.trayIcon = new TrayIcon( 
				trayIconImage,		// Icon
				"Bomz",				// Title
				this.createTrayIconPopup(stage)		// Popup
			);
		
		
		try {
			SystemTray.getSystemTray().add(this.trayIcon);
		} catch (AWTException e) {
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			if( logger.isWarnEnabled() )		logger.warn("�ý���Ʈ���� ������ ��� ����", e);
			return false;
		}
		
		
		return true;
	}
	
	/**		Ʈ���̾����� ������ ��ư Ŭ�� �� ������ �˾��޴� ����		*/
	private PopupMenu createTrayIconPopup(Stage stage){
		PopupMenu popup = new PopupMenu();
		
		popup.add(this.createShowMenuItem(stage));		// ȭ�麸�� �޴� ���
		popup.add(this.createExitMenuItem());					// ���� �޴� ���
		
		return popup;
	}
	
	/**		'ȭ�麸��' �޴� ����		*/
	private MenuItem createShowMenuItem(Stage stage){
		MenuItem item = new MenuItem("ȭ�麸��");
		
		// �޴� Ŭ�� �� �̺�Ʈ ���
		item.addActionListener(e->{
			SceneLoaderService.getInstance().load(IndexController.fxml);
		});
		
		return item;
	}
	
	/**		'����' �޴� ����		*/
	private MenuItem createExitMenuItem(){
		MenuItem item = new MenuItem("����");
		
		// �޴� Ŭ�� �� �̺�Ʈ ���
		item.addActionListener(e->{
			/*
			 * TODO �˾��� ���¹� �����鼭 ��ġ/����Ʈ ���������Ű��
			 */
			
			SystemTray.getSystemTray().remove(this.trayIcon);		// Ʈ���̾����� ����
			System.exit(1);
		});
		
		return item;
	}
	
}
