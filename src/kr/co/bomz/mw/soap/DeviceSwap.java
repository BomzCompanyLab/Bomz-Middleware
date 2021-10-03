package kr.co.bomz.mw.soap;

import java.util.List;

import kr.co.bomz.mw.db.Device;
import kr.co.bomz.mw.db.DeviceLogicalGroupMap;

/**
 * �⺻ ��ġ ������ �����񽺿� ��ġ������ �����ϰų�
 * �����񽺿� ��ġ ������ �⺻ ��ġ������ �����ϴ� ��ƿ
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class DeviceSwap {

	/**		�⺻��ġ ������ �����񽺿� ��ġ ������ ��ȯ		*/
	DeviceWs swapDevice(Device dev){
		if( dev == null )		return null;
		
		DeviceWs ws = new DeviceWs();
		ws.setDeviceId(dev.getDeviceId());
		ws.setDeviceName(dev.getDeviceName());
		ws.setCommTcpType(dev.getCommType() == Device.COMM_TCP_TYPE);
		ws.setTcpServerMode(dev.getTcpMode() == Device.TCP_MODE_SERVER);
		ws.setTcpIp(dev.getTcpIp());
		ws.setPort(dev.getPort());
		ws.setBaudRate(dev.getBaudRate());
		ws.setFlowControl(dev.getFlowControl());
		ws.setDataBits(dev.getDataBits());
		ws.setStopBits(dev.getStopBits());
		ws.setParity(dev.getParity());
		ws.setDriverId(dev.getDriverId());
		
		DeviceLogicalGroupMapWs mapWs = new DeviceLogicalGroupMapWs();
		ws.setLogicalGroupMap(mapWs);
		
		List<DeviceLogicalGroupMap> list = dev.getLogicalGroupMapList();
		if( list != null )
			for(DeviceLogicalGroupMap dlgMap : list)		mapWs.addLogicalGroupMap(dlgMap);
		
		return ws;
	}
	
	/**		�����񽺿� ��ġ ������ �⺻��ġ ������ ��ȯ		*/
	Device swapDevice(DeviceWs ws){
		if( ws == null )		return null;
		
		Device dev = new Device();
		dev.setDeviceName(ws.getDeviceName());
		dev.setCommType(ws.isCommTcpType() ?Device.COMM_TCP_TYPE : Device.COMM_SERIAL_TYPE);
		dev.setTcpMode(ws.isTcpServerMode() ? Device.TCP_MODE_SERVER : Device.TCP_MODE_CLIENT);
		dev.setTcpIp(ws.getTcpIp());
		dev.setPort(ws.getPort());
		dev.setBaudRate(ws.getBaudRate());
		dev.setFlowControl(ws.getFlowControl());
		dev.setDataBits(ws.getDataBits());
		dev.setStopBits(ws.getStopBits());
		dev.setParity(ws.getParity());
		dev.setDriverId(ws.getDriverId());
		
		DeviceLogicalGroupMapWs dlgMap = ws.getLogicalGroupMap();
		if( dlgMap != null )		dev.setLogicalGroupMapList(dlgMap.getLogicalGroupMapList());
		
		return dev;
	}
	
}
