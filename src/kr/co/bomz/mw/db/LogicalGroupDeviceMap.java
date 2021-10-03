package kr.co.bomz.mw.db;

import org.apache.ibatis.type.Alias;

import kr.co.bomz.cmn.ui.transfer.TransferSelectItem;

/**
 * �����׷쿡 ����� ��ġ ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("logicalGroupDeviceMap")
public class LogicalGroupDeviceMap implements TransferSelectItem<Integer>{

	/**		��ġ ���̵�		*/
	private int deviceId;
	
	/**		��ġ��				*/
	private String deviceName;
	
	/**		��ġ ���׳� ���̵�		*/
	private int antennaId;
	
	public LogicalGroupDeviceMap(){}
	
	public LogicalGroupDeviceMap(int deviceId, int antennaId, String deviceName){
		this.deviceId = deviceId;
		this.antennaId = antennaId;
		this.deviceName = deviceName;
	}
	
	public LogicalGroupDeviceMap(int deviceId, int antennaId){
		this.deviceId = deviceId;
		this.antennaId = antennaId;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public int getAntennaId() {
		return antennaId;
	}

	public void setAntennaId(int antennaId) {
		this.antennaId = antennaId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	@Override
	public Integer getItemId() {
		return (this.deviceId * 100) + this.antennaId;
	}

	@Override
	public String getItemName() {
		return "[" + this.antennaId + "] " + this.deviceName;
	}
	
}
