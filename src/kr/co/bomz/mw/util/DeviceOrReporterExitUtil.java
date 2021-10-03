package kr.co.bomz.mw.util;

import java.util.List;

import kr.co.bomz.mw.comm.AbstractDefault;
import kr.co.bomz.mw.comm.AbstractDevice;
import kr.co.bomz.mw.comm.AbstractReporter;

/**
 * 	�ټ��� ��ġ �Ǵ� ������ ���� �� ���� ó���� ���� �����带 ����Ͽ� ó���ϴ� ��ƿ
 *  
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DeviceOrReporterExitUtil{

	private final List<Integer> countList;
	
	private final AbstractDefault abstractDefault;
	
	private AbstractDevice abstractDevice;
	private AbstractReporter abstractReporter;
	
	public DeviceOrReporterExitUtil(List<Integer> countList, AbstractDefault abstractDefault, AbstractDevice abstractDevice){
		this.countList = countList;
		this.abstractDefault = abstractDefault;
		this.abstractDevice = abstractDevice;
	}
	
	public DeviceOrReporterExitUtil(List<Integer> countList, AbstractDefault abstractDefault, AbstractReporter abstractReporter){
		this.countList = countList;
		this.abstractDefault = abstractDefault;
		this.abstractReporter = abstractReporter;
	}
	
	/**		���� ó�� �˻� ����		*/
	public void exitCheckStart(){
		new Thread(()->execute()).start();
	}
	
	/**		���� ȣ��		*/
	private void execute(){
		this.abstractDefault.exit();		// ����. �ð��� �� �� �ɸ����� �ִ�
		
		if( this.abstractDevice != null )		this.abstractDevice.deviceStart();
		if( this.abstractReporter != null )	this.abstractReporter.reporterStart();
		
		this.countList.add(1);
	}
}
