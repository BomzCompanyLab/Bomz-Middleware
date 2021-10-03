package kr.co.bomz.mw.db;

import org.apache.ibatis.type.Alias;

/**
 * ��ġ�� ����� �����׷� ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("deviceLogicalGroupMap")
public class DeviceLogicalGroupMap {

	/**		��ġ�� ���׳� ���̵�		*/
	private int antennaId;
	
	/**		�����׷� ���̵�		*/
	private int logicalGroupId; 
	
	public DeviceLogicalGroupMap(){}

	public DeviceLogicalGroupMap(int antennaId, int logicalGroupId){
		this.antennaId = antennaId;
		this.logicalGroupId = logicalGroupId;
	}
	
	public int getAntennaId() {
		return antennaId;
	}

	public void setAntennaId(int antennaId) {
		this.antennaId = antennaId;
	}

	public int getLogicalGroupId() {
		return logicalGroupId;
	}

	public void setLogicalGroupId(int logicalGroupId) {
		this.logicalGroupId = logicalGroupId;
	}
	
}
