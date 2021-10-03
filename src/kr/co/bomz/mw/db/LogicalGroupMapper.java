package kr.co.bomz.mw.db;

import java.util.List;

/**
 * 	�����׷� ���̺� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public interface LogicalGroupMapper {
	
	/**		�����׷� �⺻ ���� ���		*/
	public void addLogicalGroup(LogicalGroup logicalGroup);
	
	/**		�����׷� �⺻ ���� ����		*/
	public void updLogicalGroup(LogicalGroup logicalGroup);
	
	/**		�����׷� �⺻ ���� ����		*/
	public void delLogicalGroup(LogicalGroup logicalGroup);
	
	/**		�����׷쿡 ����� ��ġ ���� ���		*/
	public void addLogicalGroupDeviceMap(LogicalGroup logicalGroup);
	
	/**		�����׷쿡 ����� ��ġ ���� ����		*/
	public void delLogicalGroupDeviceMap(LogicalGroup logicalGroup);
	
	/**		�����׷쿡 ����� ������ ���� ���		*/
	public void addLogicalGroupReporterMap(LogicalGroup logicalGroup);
	
	/**		�����׷쿡 ����� ������ ���� ����		*/
	public void delLogicalGroupReporterMap(LogicalGroup logicalGroup);
	
	/**		��� �����׷����� �˻�		*/
	public List<LogicalGroup> selectAllLogicalGroupList();
	
	/**		�����׷쿡 ����� ��ġ ���� �˻�		*/
	public List<LogicalGroupDeviceMap> selectLogicalGroupDeviceMapList(LogicalGroup logicalGroup);
	
	/**		�����׷쿡 ����� ������ ���� �˻�		*/
	public List<LogicalGroupReporterMap> selectLogicalGroupReporterMapList(LogicalGroup logicalGroup);
	
	/**		��Ģ�� �´� �����׷� ���� �˻�		*/
	public List<LogicalGroup> selectLogicalGroupListToTerm(SelectTerms terms);
	
	/**		Ư�� �����׷� ���� �˻�		*/
	public LogicalGroup selectLogicalGroupInfo(int logicalGroupId);
}
