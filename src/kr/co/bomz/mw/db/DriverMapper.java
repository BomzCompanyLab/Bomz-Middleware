package kr.co.bomz.mw.db;

import java.util.List;

/**
 * 	��ġ/������ ����̹� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public interface DriverMapper {

	/**		��ϵ� ��� ��ġ ����̹� ��� �˻�		*/
	public List<Driver> selectDeviceDriverAllList();
	
	/**		��ϵ� ��� ������ ����̹� ��� �˻�		*/
	public List<Driver> selectReporterDriverAllList();
	
	/**		����̹� ����		*/
	public void delDriver(Driver driver);
	
	/**		��Ģ�� �´� ����̹����� �˻�		*/
	public List<Driver> selectDriverListToTerm(SelectTerms terms);
	
	/**		��Ģ�� �´� ����̹� �˻�		*/
	public Driver selectDriverInfo(Driver driver);
	
	/**		��ġ�� ����̹� JAR ���� ���� �˻�		*/
	public Driver selectDeviceDriverJar(Device device);
	
	/**		��ġ�� ����̹� ���� �˻�		*/
	public Driver selectDeviceDriverInfo(int driverId);
	
	/**		�������� ����̹� JAR ���� ���� �˻�		*/
	public Driver selectReporterDriverJar(Reporter reporter);
	
	/**		�������� ����̹� ���� �˻�		*/
	public Driver selectReporterDriverInfo(int driverId);
	
	/**		����̹� ���		*/
	public void addDriver(Driver driver);
	
	/**		����̹� ����		*/
	public void updDriver(Driver driver);
	
}
