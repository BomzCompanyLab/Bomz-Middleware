package kr.co.bomz.mw.soap;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.RXTXPort;
import kr.co.bomz.mw.db.ControlResponse;
import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.Driver;
import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.service.ControllerService;
import kr.co.bomz.mw.service.DatabaseService;
import kr.co.bomz.mw.service.DeviceService;
import kr.co.bomz.mw.service.LogicalGroupService;
import kr.co.bomz.mw.service.ReporterService;
import kr.co.bomz.mw.service.SettingInfoService;

/**
 * 	������ ����ü
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@WebService(portName="mw")
public class WebServiceImpl {

	private final Logger logger = LoggerFactory.getLogger("WebService");
	
	/**		��ϵ� ��ġ ��� ��û		*/
	public ArrayList<DeviceWs> getDeviceList(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("getDeviceList ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getDeviceList ���� ����");
		
		ArrayList<DeviceWs> list = new ArrayList<DeviceWs>();
		DeviceSwap swap = new DeviceSwap();

		for(Device dev : DeviceService.getInstance().getDeviceAll())
			list.add( swap.swapDevice(dev) );
		
		return list;
	}
	
	/**		Ư�� ��ġ ���� ��û		*/
	public DeviceWs getDeviceInfo(String password, int deviceId){
		if( this.logger.isInfoEnabled() )		this.logger.info("getDeviceInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getDeviceInfo ���� ����");
		
		DeviceSwap swap = new DeviceSwap();
		return swap.swapDevice(DeviceService.getInstance().getDevice(deviceId));
	}

	/**		��ġ ���		*/
	public ControlResponse addDeviceInfo(String password, DeviceWs device){
		if( this.logger.isInfoEnabled() )		this.logger.info("addDeviceInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("addDeviceInfo ���� ����");
		
		DeviceSwap swap = new DeviceSwap();
		return ControllerService.getInstance().addDevice(swap.swapDevice(device));
	}
	
	/**		��ġ ����		*/
	public ControlResponse updDeviceInfo(String password, DeviceWs device){
		if( this.logger.isInfoEnabled() )		this.logger.info("updDeviceInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("updDeviceInfo ���� ����");
		
		DeviceSwap swap = new DeviceSwap();
		return ControllerService.getInstance().updDevice(swap.swapDevice(device));
	}
	
	/**		��ġ ����		*/
	public ControlResponse delDeviceInfo(String password, int deviceId){
		if( this.logger.isInfoEnabled() )		this.logger.info("delDeviceInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("delDeviceInfo ���� ����");
		
		return ControllerService.getInstance().delDevice(deviceId);
	}
	
	/**		��ġ �ø��� ��� �� BaudRate ��ȿ �� 		*/
	public String infoSerialBaudRate(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialBaudRate ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialBaudRate ���� ����");
		
		return "110, 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400, 460800, 921600";
	}
	
	/**		��ġ �ø��� ��� �� FlowControl ��ȿ �� 		*/
	public String infoSerialFlowControl(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialFlowControl ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialFlowControl ���� ����");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("None:").append(RXTXPort.FLOWCONTROL_NONE).append(", ");
		buffer.append("RtsCts In:").append(RXTXPort.FLOWCONTROL_RTSCTS_IN).append(", ");
		buffer.append("RtsCts Out:").append(RXTXPort.FLOWCONTROL_RTSCTS_OUT).append(", ");
		buffer.append("XonXoff In:").append(RXTXPort.FLOWCONTROL_XONXOFF_IN).append(", ");
		buffer.append("XonXoff Out:").append(RXTXPort.FLOWCONTROL_XONXOFF_OUT);
		
		return buffer.toString();
	}
	
	/**		��ġ �ø��� ��� �� DataBits ��ȿ �� 		*/
	public String infoSerialDataBits(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialDataBits ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialDataBits ���� ����");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("DataBit5:").append(RXTXPort.DATABITS_5).append(", ");
		buffer.append("DataBit6:").append(RXTXPort.DATABITS_6).append(", ");
		buffer.append("DataBit7:").append(RXTXPort.DATABITS_7).append(", ");
		buffer.append("DataBit8:").append(RXTXPort.DATABITS_8);
		
		return buffer.toString();
	}
	
	/**		��ġ �ø��� ��� �� StopBits ��ȿ �� 		*/
	public String infoSerialStopBits(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialStopBits ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialStopBits ���� ����");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("StopBit1:").append(RXTXPort.STOPBITS_1).append(", ");
		buffer.append("StopBit1.5:").append(RXTXPort.STOPBITS_1_5).append(", ");
		buffer.append("StopBit2:").append(RXTXPort.STOPBITS_2);
		
		return buffer.toString();
	}
	
	/**		��ġ �ø��� ��� �� Parity ��ȿ �� 		*/
	public String infoSerialParity(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialParity ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("infoSerialParity ���� ����");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("Even:").append(RXTXPort.PARITY_EVEN).append(", ");
		buffer.append("Mark:").append(RXTXPort.PARITY_MARK).append(", ");
		buffer.append("None:").append(RXTXPort.PARITY_NONE).append(", ");
		buffer.append("Odd:").append(RXTXPort.PARITY_ODD).append(", ");
		buffer.append("Space:").append(RXTXPort.PARITY_SPACE);
		
		return buffer.toString();
	}
	
	/**		��ϵ� �����׷� ��� ��û		*/
	public ArrayList<LogicalGroupWs> getLogicalGroupList(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("getLogicalGroupList ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getLogicalGroupList ���� ����");
		
		ArrayList<LogicalGroupWs> list = new ArrayList<LogicalGroupWs>();
		LogicalGroupSwap swap = new LogicalGroupSwap();
		
		for(LogicalGroup lg : LogicalGroupService.getInstance().getLogicalGroupAll())
			list.add( swap.swapLogicalGroup(lg) );
		
		return list;
	}
	
	/**		Ư�� �����׷� ���� ��û		*/
	public LogicalGroupWs getLogicalGroupInfo(String password, int logicalGroupId){
		if( this.logger.isInfoEnabled() )		this.logger.info("getLogicalGroupInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getLogicalGroupInfo ���� ����");
		
		LogicalGroupSwap swap = new LogicalGroupSwap();
		return swap.swapLogicalGroup(LogicalGroupService.getInstance().getLogicalGroup(logicalGroupId));
	}
	
	/**		�����׷� ���		*/
	public ControlResponse addLogicalGroupInfo(String password, LogicalGroupWs logicalGroup){
		if( this.logger.isInfoEnabled() )		this.logger.info("addLogicalGroupInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("addLogicalGroupInfo ���� ����");
		
		LogicalGroupSwap swap = new LogicalGroupSwap();
		return ControllerService.getInstance().addLogicalGroup(swap.swapLogicalGroup(logicalGroup));
	}
	
	/**		�����׷� ����		*/
	public ControlResponse updLogicalGroupInfo(String password, LogicalGroupWs logicalGroup){
		if( this.logger.isInfoEnabled() )		this.logger.info("updLogicalGroupInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("updLogicalGroupInfo ���� ����");
		
		LogicalGroupSwap swap = new LogicalGroupSwap();
		return ControllerService.getInstance().updLogicalGroup(swap.swapLogicalGroup(logicalGroup));
	}
	
	/**		�����׷� ����		*/
	public ControlResponse delLogicalGroupInfo(String password, int logicalGroupId){
		if( this.logger.isInfoEnabled() )		this.logger.info("delLogicalGroupInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("delLogicalGroupInfo ���� ����");
		
		return ControllerService.getInstance().delLogicalGroup(logicalGroupId);
	}
	
	
	/**		��ϵ� ������ ��� ��û		*/
	public ArrayList<ReporterWs> getReporterList(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("getReporterList ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getReporterList ���� ����");
		
		ArrayList<ReporterWs> list = new ArrayList<ReporterWs>();
		ReporterSwap swap = new ReporterSwap();
		
		for(Reporter rep : ReporterService.getInstance().getReporterAll())
			list.add( swap.swapReporter(rep) );
		
		return list;
	}
	
	/**		Ư�� ������ ���� ��û		*/
	public ReporterWs getReporterInfo(String password, int reporterId){
		if( this.logger.isInfoEnabled() )		this.logger.info("getReporterInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getReporterInfo ���� ����");
		
		ReporterSwap swap = new ReporterSwap();
		return swap.swapReporter(ReporterService.getInstance().getReporter(reporterId));
	}
	
	/**		������ ���		*/
	public ControlResponse addReporterInfo(String password, ReporterWs reporter){
		if( this.logger.isInfoEnabled() )		this.logger.info("addReporterInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("addReporterInfo ���� ����");
		
		ReporterSwap swap = new ReporterSwap();
		return ControllerService.getInstance().addReporter(swap.swapReporter(reporter));
	}
	
	/**		������ ����		*/
	public ControlResponse updReporter(String password, ReporterWs reporter){
		if( this.logger.isInfoEnabled() )		this.logger.info("updReporter ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("updReporter ���� ����");
		
		ReporterSwap swap = new ReporterSwap();
		return ControllerService.getInstance().updReporter(swap.swapReporter(reporter));
	}
	
	/**		������ ����		*/
	public ControlResponse delReporter(String password, int reporterId){
		if( this.logger.isInfoEnabled() )		this.logger.info("delReporter ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("delReporter ���� ����");
		
		return ControllerService.getInstance().delReporter(reporterId);
	}
	
	
	/**		��ϵ� ��ġ����̹� ��� ��û		*/
	public ArrayList<DriverWs> getDeviceDriverList(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("getDeviceDriverList ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getDeviceDriverList ���� ����");
		
		return this.getDriverList(true);
	}
	
	/**		��ϵ� �����͵���̹� ��� ��û		*/
	public ArrayList<DriverWs> getReporterDriverList(String password){
		if( this.logger.isInfoEnabled() )		this.logger.info("getReporterDriverList ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getReporterDriverList ���� ����");
		
		return this.getDriverList(false);
	}
	
	/**		����̹� �� ��û ó��		*/
	private ArrayList<DriverWs> getDriverList(boolean deviceDriver){
		try{
			List<Driver> list = deviceDriver ? 
					DatabaseService.getInstance().selectDeviceDriverAllList() :
					DatabaseService.getInstance().selectReporterDriverAllList();
					
			if( list == null || list.isEmpty() )		return new ArrayList<DriverWs>();
			
			DriverSwap swap = new DriverSwap();
			ArrayList<DriverWs> driverList = new ArrayList<DriverWs>(list.size());
			for(Driver driver : list)		driverList.add(swap.swapDriver(driver));
			
			return driverList;
		}catch(Exception e){
			return new ArrayList<DriverWs>();
		}
	}
	
	/**		Ư�� ��ġ����̹� ���� ��û		*/
	public DriverWs getDeviceDriverInfo(String password, int driverId){
		if( this.logger.isInfoEnabled() )		this.logger.info("getDeviceDriverInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getDeviceDriverInfo ���� ����");
		
		DriverSwap swap = new DriverSwap();
		return swap.swapDriver(DatabaseService.getInstance().selectDeviceDriverInfo(driverId));
	}
	
	/**		Ư�� �����͵���̹� ���� ��û		*/
	public DriverWs getReporterDriverInfo(String password, int driverId){
		if( this.logger.isInfoEnabled() )		this.logger.info("getReporterDriverInfo ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("getReporterDriverInfo ���� ����");
		
		DriverSwap swap = new DriverSwap();
		return swap.swapDriver(DatabaseService.getInstance().selectReporterDriverInfo(driverId));
	}
	
	/**		��ġ ����̹� ���		*/
	public ControlResponse addDeviceDriver(String password, DriverWs driver){
		if( this.logger.isInfoEnabled() )		this.logger.info("addDeviceDriver ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("addDeviceDriver ���� ����");
		
		return this.addDriver(driver, true);
	}
	
	/**		������ ����̹� ���		*/
	public ControlResponse addReporterDriver(String password, DriverWs driver){
		if( this.logger.isInfoEnabled() )		this.logger.info("addReporterDriver ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("addReporterDriver ���� ����");
		
		return this.addDriver(driver, false);
	}
	
	/**		����̹� ���		*/
	private ControlResponse addDriver(DriverWs driver, boolean deviceDriver){
		DriverSwap swap = new DriverSwap();
		if( deviceDriver )		return ControllerService.getInstance().addDeviceDriver(swap.swapDriver(driver, deviceDriver));
		else							return ControllerService.getInstance().addReporterDriver(swap.swapDriver(driver, deviceDriver));
	}
	
	/**		��ġ ����̹� ����		*/
	public ControlResponse updDeviceDriver(String password, DriverWs driver){
		if( this.logger.isInfoEnabled() )		this.logger.info("updDeviceDriver ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("updDeviceDriver ���� ����");
		
		return this.updDriver(driver, true);
	}
	
	/**		������ ����̹� ����		*/
	public ControlResponse updReporterDriver(String password, DriverWs driver){
		if( this.logger.isInfoEnabled() )		this.logger.info("updReporterDriver ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("updReporterDriver ���� ����");
		
		return this.updDriver(driver, false);
	}
	
	/**		����̹� ����		*/
	private ControlResponse updDriver(DriverWs driver, boolean deviceDriver){
		DriverSwap swap = new DriverSwap();
		if( deviceDriver )		return ControllerService.getInstance().updDeviceDriver(swap.swapDriver(driver, deviceDriver));
		else							return ControllerService.getInstance().updReporterDriver(swap.swapDriver(driver, deviceDriver));
	}
	
	/**		��ġ ����̹� ����		*/
	public ControlResponse delDeviceDriver(String password, int driverId){
		if( this.logger.isInfoEnabled() )		this.logger.info("delDeviceDriver ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("delDeviceDriver ���� ����");
		
		return this.delDriver(driverId, true);
	}
	
	/**		������ ����̹� ����		*/
	public ControlResponse delDeviceReporter(String password, int driverId){
		if( this.logger.isInfoEnabled() )		this.logger.info("delDeviceReporter ȣ��");
		if( !this.checkRequestPassword(password) )		return null;
		if( this.logger.isInfoEnabled() )		this.logger.info("delDeviceReporter ���� ����");
		
		return this.delDriver(driverId, false);
	}
	
	/**		����̹� ���� ó��		*/
	private ControlResponse delDriver(int driverId, boolean deviceDriver){
		Driver driver = new Driver();
		driver.setDriverId(driverId);
		
		return ControllerService.getInstance().delDriver(
				deviceDriver ? ControlResponse.TARGET_DEVICE_DRIVER : ControlResponse.TARGET_REPORTER_DRIVER, 
				driver
			);
	}
	
	/**		������ ��û �� ��ȣ Ȯ��		*/
	private boolean checkRequestPassword(String password){
		if( password == null )			return false;
		return password.equals(SettingInfoService.getInstance().getWebServicePw());
	}
	
}
