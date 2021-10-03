package kr.co.bomz.mw.soap;

import java.util.ArrayList;

import kr.co.bomz.mw.db.LogicalGroupReporterMap;

/**
 * 	�����׷�� ������ ��ġ ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class LogicalGroupReporterMapWs {
	
	/**	�����׷� ��� ������ ���̵� ���		*/
	private ArrayList<LogicalGroupReporterMap> logicalGroupReporterMapList;

	public LogicalGroupReporterMapWs(){}
	
	public ArrayList<LogicalGroupReporterMap> getLogicalGroupReporterMapList() {
		return logicalGroupReporterMapList;
	}

	public void setLogicalGroupReporterMapList(ArrayList<LogicalGroupReporterMap> logicalGroupReporterMapList) {
		this.logicalGroupReporterMapList = logicalGroupReporterMapList;
	}

	public void addLogicalGroupReporterMap(LogicalGroupReporterMap map){
		if( map == null )		return;
		
		if( this.logicalGroupReporterMapList == null )		this.logicalGroupReporterMapList = new ArrayList<LogicalGroupReporterMap>();
		this.logicalGroupReporterMapList.add(map);
	}
}
