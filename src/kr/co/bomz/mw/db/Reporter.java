package kr.co.bomz.mw.db;

import java.util.List;

import org.apache.ibatis.type.Alias;

import kr.co.bomz.cmn.ui.transfer.TransferSelectItem;
import kr.co.bomz.mw.comm.AbstractReporter;

/**
 * 	������ ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("reporter")
public class Reporter extends ListItem implements TransferSelectItem<Integer>{
	
	/**		��� ���� ���� : �����		*/
	public static final char COMM_STATE_YES = 'S';
	
	/**		��� ���� ���� : ��������		*/
	public static final char COMM_STATE_NO = 'D';

	/**		������ ���̵�		*/
	private int reporterId;
	
	/**		������ �̸�		*/
	private String reporterName;
	
	/**
	 * 		Ÿ�̸� �ð� (�ʴ���)
	 * 		0���� ���� ��� ��� ����
	 */
	private int timerRepeatTime;
	
	/**		������ ������ ���� ������		*/
	private String reporterIp;
	
	/**		������ ������ ���� ��Ʈ		*/
	private int reporterPort;
	
	/**		������ Ư���� ������		*/
	private String reporterParam;
	
	/**
	 * 		������ ���� ����
	 * 
	 * 	@see	Reporter.COMM_STATE_YES
	 * 	@see	Reporter.COMM_STATE_NO
	 */
	private char commState;
	
	/**		�����Ϳ� ����� �����׷� ���̵� ���		*/
	private List<ReporterLogicalGroupMap> logicalGroupMapList;
	
	/**		������ ����̹� ���̵�		*/
	private int driverId;
	
	/**		������ ����̹� �̸�		*/
	private String driverName;
	
	private AbstractReporter reporter;
	
	public Reporter(){}

	public int getReporterId() {
		return reporterId;
	}

	public void setReporterId(int reporterId) {
		this.reporterId = reporterId;
	}

	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	public AbstractReporter getReporter() {
		return reporter;
	}

	public void setReporter(AbstractReporter reporter) {
		this.reporter = reporter;
	}
	
	public List<ReporterLogicalGroupMap> getLogicalGroupMapList() {
		return logicalGroupMapList;
	}

	public void setLogicalGroupMapList(List<ReporterLogicalGroupMap> logicalGroupMapList) {
		this.logicalGroupMapList = logicalGroupMapList;
	}
	
	public boolean isNullLogicalGroupMapList(){
		return this.logicalGroupMapList == null;
	}

	public int getTimerRepeatTime() {
		return timerRepeatTime;
	}

	public void setTimerRepeatTime(int timerRepeatTime) {
		this.timerRepeatTime = timerRepeatTime;
	}

	public String getReporterIp() {
		return reporterIp;
	}

	public void setReporterIp(String reporterIp) {
		this.reporterIp = reporterIp;
	}

	public int getReporterPort() {
		return reporterPort;
	}

	public void setReporterPort(int reporterPort) {
		this.reporterPort = reporterPort;
	}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public String getReporterParam() {
		return reporterParam;
	}

	public void setReporterParam(String reporterParam) {
		this.reporterParam = reporterParam;
	}

	public char getCommState() {
		return commState;
	}

	public void setCommState(char commState) {
		this.commState = commState;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public boolean isNullReporterParam(){
		return this.reporterParam == null;
	}
	
	@Override
	public Integer getItemId() {
		return this.reporterId;
	}

	@Override
	public String getItemName() {
		return this.reporterName;
	}
	
	/**		List UI ȭ�� ǥ�ÿ�. ��Ź�Ŀ� ���� ��� ���� ����		*/
	public String getComm(){
		return this.reporterIp + ":" + this.reporterPort;
	}
}
