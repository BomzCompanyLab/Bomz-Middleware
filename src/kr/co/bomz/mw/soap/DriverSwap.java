package kr.co.bomz.mw.soap;

import kr.co.bomz.mw.db.ControlResponse;
import kr.co.bomz.mw.db.Driver;

/**
 * �⺻ ����̹� ������ �����񽺿� ����̹������� �����ϰų�
 * �����񽺿� ����̹� ������ �⺻ ����̹������� �����ϴ� ��ƿ
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class DriverSwap {

	DriverWs swapDriver(Driver driver){
		if( driver == null )		return null;
		
		DriverWs ws = new DriverWs();
		ws.setDriverId(driver.getDriverId());
		ws.setDriverName(driver.getDriverName());
		ws.setDriverFileName(driver.getDriverFileName());
		ws.setDriverJarFile(driver.getDriverJarFile());
		
		return ws;
	}
	
	Driver swapDriver(DriverWs ws, boolean deviceDriver){
		if( ws == null )		return null;
		
		Driver driver = new Driver();
		driver.setDriverId(ws.getDriverId());
		driver.setDriverName(ws.getDriverName());
		driver.setDriverFileName(ws.getDriverFileName());
		driver.setDriverJarFile(ws.getDriverJarFile());
		driver.setDriverTarget(deviceDriver ? ControlResponse.TARGET_DEVICE_DRIVER : ControlResponse.TARGET_REPORTER_DRIVER);
		
		return driver;
	}
}
