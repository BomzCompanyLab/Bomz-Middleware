package kr.co.bomz.mw.db;

import java.util.List;

/**
 * 	��ġ ���̺� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public interface DeviceMapper {

	/**		��ġ �⺻ ���� ���		*/
	public void addDevice(Device device);
	
	/**		��ġ �⺻ ���� ����		*/
	public void updDevice(Device device);
	
	/**		��ġ �⺻ ���� ����		*/
	public void delDevice(Device device);
	
	/**		��ġ�� ����� �����׷� ���� ���		*/
	public void addDeviceLogicalGroupMap(Device device);
	
	/**		��ġ�� ����� �����׷� ���� ����		*/
	public void delDeviceLogicalGroupMap(Device device);
	
	/**		��ġ ��� ���� ���� ����		*/
	public void updateDeviceConnectState(Device device);
	
	/**		��� ��ġ���� �˻�		*/
	public List<Device> selectAllDeviceList();
	
	/**		��Ģ�� �´� ��ġ���� �˻�		*/
	public List<Device> selectDeviceListToTerm(SelectTerms terms);
	
	/**		Ư�� ��ġ���� �˻�		*/
	public Device selectDeviceInfo(int deviceId);
	
	/**		Ư�� ��ġ�� ����� �����׷� ��� �˻�		*/ 
	public List<DeviceLogicalGroupMap> selectDeviceLogicalGroupMap(Device device);
}
