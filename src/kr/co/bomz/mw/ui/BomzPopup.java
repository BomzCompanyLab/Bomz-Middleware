package kr.co.bomz.mw.ui;

import kr.co.bomz.cmn.ui.popup.OkPopup;
import kr.co.bomz.mw.service.SceneLoaderService;

/**
 * 	�˾� ó�� 
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class BomzPopup {

	/**
	 * 	Ȯ�� ��ư�� �ִ� �˾� ǥ��
	 * @param msg		�˾� �޼���
	 */
	public void showOkPopup(String msg){
		new OkPopup(SceneLoaderService.getInstance().getStage(), msg).show();
	}
	
}
