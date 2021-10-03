package kr.co.bomz.mw.soap;

import java.util.List;

import kr.co.bomz.mw.db.LogicalGroup;
import kr.co.bomz.mw.db.LogicalGroupDeviceMap;
import kr.co.bomz.mw.db.LogicalGroupReporterMap;

/**
 * �⺻ �����׷� ������ �����񽺿� �����׷������� �����ϰų�
 * �����񽺿� �����׷� ������ �⺻ �����׷������� �����ϴ� ��ƿ
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class LogicalGroupSwap {

	/**		�⺻�����׷� ������ �����񽺿� �����׷� ������ ��ȯ		*/
	LogicalGroupWs swapLogicalGroup(LogicalGroup lg){
		if( lg == null )		return null;
		
		LogicalGroupWs ws = new LogicalGroupWs();
		ws.setLogicalGroupId(lg.getLogicalGroupId());
		ws.setLogicalGroupName(lg.getLogicalGroupName());
		
		LogicalGroupDeviceMapWs deviceMap = new LogicalGroupDeviceMapWs();
		ws.setDeviceMap(deviceMap);
		List<LogicalGroupDeviceMap> devMapList = lg.getLogicalGroupDeviceMapList();
		if( devMapList != null )
			for(LogicalGroupDeviceMap lgdMap : devMapList)		deviceMap.addLogicalGroupDeviceMap(lgdMap);
		
		LogicalGroupReporterMapWs reporterMap = new LogicalGroupReporterMapWs();
		ws.setReporterMap(reporterMap);
		List<LogicalGroupReporterMap> reporterMapList = lg.getLogicalGroupReporterMapList();
		if( reporterMapList != null )
			for(LogicalGroupReporterMap lgrMap : reporterMapList)		reporterMap.addLogicalGroupReporterMap(lgrMap);
		
		
		return ws;
	}
	
	/**		�����񽺿� �����׷� ������ �⺻ �����׷� ������ ��ȯ		*/
	LogicalGroup swapLogicalGroup(LogicalGroupWs ws){
		if( ws == null )		return null;
		
		LogicalGroup lg = new LogicalGroup();
		lg.setLogicalGroupId(ws.getLogicalGroupId());
		lg.setLogicalGroupName(ws.getLogicalGroupName());
		
		LogicalGroupDeviceMapWs deviceMap = ws.getDeviceMap();
		if( deviceMap != null )		lg.setLogicalGroupDeviceMapList(deviceMap.getLogicalGroupDeviceMapList());
		
		LogicalGroupReporterMapWs reporterMap = ws.getReporterMap();
		if( reporterMap != null )	lg.setLogicalGroupReporterMapList(reporterMap.getLogicalGroupReporterMapList());
		
		return lg;
	}
}
