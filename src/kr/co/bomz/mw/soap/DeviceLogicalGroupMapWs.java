package kr.co.bomz.mw.soap;

import java.util.ArrayList;

import kr.co.bomz.mw.db.DeviceLogicalGroupMap;

/**
 * 	��ġ ���׳��� ������ �����׷� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class DeviceLogicalGroupMapWs {

	private ArrayList<DeviceLogicalGroupMap> logicalGroupMapList;
	
	public DeviceLogicalGroupMapWs(){}
	
	public ArrayList<DeviceLogicalGroupMap> getLogicalGroupMapList() {
		return this.logicalGroupMapList;
	}
	
	public void setLogicalGroupMapList(ArrayList<DeviceLogicalGroupMap> logicalGroupMapList) {
		this.logicalGroupMapList = logicalGroupMapList;
	}

	public void addLogicalGroupMap(DeviceLogicalGroupMap dlgMap){
		if( dlgMap == null )		return;
		
		if( this.logicalGroupMapList == null ) 
			this.logicalGroupMapList = new ArrayList<DeviceLogicalGroupMap>();
		
		this.logicalGroupMapList.add(dlgMap);
	}
	
}
