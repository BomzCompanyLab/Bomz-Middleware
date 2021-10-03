package kr.co.bomz.mw.soap;

import java.util.ArrayList;

import kr.co.bomz.mw.db.ReporterLogicalGroupMap;

/**
 * 	�����Ϳ� ������ �����׷� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class ReporterLogicalGroupMapWs {

	/**		�����Ϳ� ����� �����׷� ���̵� ���		*/
	private ArrayList<ReporterLogicalGroupMap> logicalGroupMapList;
	
	public ReporterLogicalGroupMapWs(){}

	public ArrayList<ReporterLogicalGroupMap> getLogicalGroupMapList() {
		return logicalGroupMapList;
	}

	public void setLogicalGroupMapList(ArrayList<ReporterLogicalGroupMap> logicalGroupMapList) {
		this.logicalGroupMapList = logicalGroupMapList;
	}
	
	public void addLogicalGroupMap(ReporterLogicalGroupMap rlgMap){
		if( rlgMap == null )		return;
		
		if( this.logicalGroupMapList == null )		this.logicalGroupMapList = new ArrayList<ReporterLogicalGroupMap>();
		
		this.logicalGroupMapList.add(rlgMap);
	}
	
}
