package kr.co.bomz.mw.soap;

import java.util.List;

import kr.co.bomz.mw.db.Reporter;
import kr.co.bomz.mw.db.ReporterLogicalGroupMap;

/**
 * �⺻ ������ ������ �����񽺿� ������������ �����ϰų�
 * �����񽺿� ������ ������ �⺻ ������������ �����ϴ� ��ƿ
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 */
public class ReporterSwap {

	/**		�⺻������ ������ �����񽺿� ������ ������ ��ȯ		*/
	ReporterWs swapReporter(Reporter rep){
		if( rep == null )		return null;
		
		ReporterWs ws = new ReporterWs();
		ws.setReporterId(rep.getReporterId());
		ws.setReporterName(rep.getReporterName());
		ws.setTimerRepeatTime(rep.getTimerRepeatTime());
		ws.setReporterIp(rep.getReporterIp());
		ws.setReporterPort(rep.getReporterPort());
		ws.setReporterParam(rep.getReporterParam());
		ws.setDriverId(rep.getDriverId());
		
		ReporterLogicalGroupMapWs logicalGroupMap = new ReporterLogicalGroupMapWs();
		ws.setLogicalGroupMap(logicalGroupMap);
		List<ReporterLogicalGroupMap> logicalGroupMapList = rep.getLogicalGroupMapList();
		if( logicalGroupMapList != null )
			for(ReporterLogicalGroupMap rlgMap : logicalGroupMapList)	logicalGroupMap.addLogicalGroupMap(rlgMap);
		
		return ws;
	}
	
	/**		�����񽺿� ������ ������ �⺻ ������ ������ ��ȯ		*/
	Reporter swapReporter(ReporterWs ws){
		if( ws == null )		return null;
		
		Reporter rep = new Reporter();
		rep.setReporterId(ws.getReporterId());
		rep.setReporterName(ws.getReporterName());
		rep.setTimerRepeatTime(ws.getTimerRepeatTime());
		rep.setReporterIp(ws.getReporterIp());
		rep.setReporterPort(ws.getReporterPort());
		rep.setReporterParam(ws.getReporterParam());
		rep.setDriverId(ws.getDriverId());
		
		ReporterLogicalGroupMapWs rlgMap = ws.getLogicalGroupMap();
		if( rlgMap != null )		rep.setLogicalGroupMapList(rlgMap.getLogicalGroupMapList());
		
		return rep;
	}
}
