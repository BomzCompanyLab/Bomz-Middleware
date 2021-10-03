package kr.co.bomz.mw.db;

import java.util.List;

/**
 * 	������ ���̺� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public interface ReporterMapper {

	/**		������ �⺻ ���� ���		*/
	public void addReporter(Reporter reporter);
	
	/**		������ �⺻ ���� ����		*/
	public void updReporter(Reporter reporter);
	
	/**		������ �⺻ ���� ����		*/
	public void delReporter(Reporter reporter);
	
	/**		�����Ϳ� ����� �����׷� ���� ���		*/
	public void addReporterLogicalGroupMap(Reporter reporter);
	
	/**		�����Ϳ� ����� �����׷� ���� ����		*/
	public void delReporterLogicalGroupMap(Reporter reporter);
	
	/**		������ ��� ���� ���� ����		*/
	public void updateReporterConnectState(Reporter reporter);
	
	/**		��� ���������� �˻�		*/
	public List<Reporter> selectAllReporterList();
	
	/**		���ǿ� �´� ������ ���� �˻�		*/
	public List<Reporter> selectReporterListToTerm(SelectTerms terms);
	
	/**		Ư�� ������ ���� �˻�		*/
	public Reporter selectReporterInfo(int reporterId);
	
	/**		�����Ϳ� ����� �����׷� ���� �˻�		*/
	public List<ReporterLogicalGroupMap> selectReporterLogicalGroupMap(Reporter reporter);
	
	/**		������ ���۽��� �±׷α� ����		*/
	public void insertReporterWriteFailTagLog(TagList tagList);
	
	/**		������ ���۽��� �±׷α� �˻�		*/
	public List<Tag> selectReporterWriteFailTagLog(int reporterId);
	
	/**		������ ���۽��� �α� ����			*/
	public void removeReporterWriteFailTagLog(String logIds);
	
	/**		Ư�� �������� ���۽��� �α� ��� ����		*/
	public void clearReporterWriteFailTagLog(int reporterId);
}
