package kr.co.bomz.mw.soap;

import java.util.ArrayList;

import kr.co.bomz.mw.db.LogicalGroupDeviceMap;

/**
 * 	�����׷�� ������ ��ġ ���׳� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class LogicalGroupDeviceMapWs {

	private ArrayList<LogicalGroupDeviceMap> logicalGroupDeviceMapList;
	
	public LogicalGroupDeviceMapWs(){}

	public ArrayList<LogicalGroupDeviceMap> getLogicalGroupDeviceMapList() {
		return logicalGroupDeviceMapList;
	}

	public void setLogicalGroupDeviceMapList(ArrayList<LogicalGroupDeviceMap> logicalGroupDeviceMapList) {
		this.logicalGroupDeviceMapList = logicalGroupDeviceMapList;
	}

	public void addLogicalGroupDeviceMap(LogicalGroupDeviceMap logicalGroupDeviceMap){
		if( logicalGroupDeviceMap == null )		return;
		
		if( this.logicalGroupDeviceMapList == null )		this.logicalGroupDeviceMapList = new ArrayList<LogicalGroupDeviceMap>();
		this.logicalGroupDeviceMapList.add(logicalGroupDeviceMap);
	}
}
