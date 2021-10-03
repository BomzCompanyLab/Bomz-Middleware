package kr.co.bomz.mw.service;

/**
 * 	ControlService ó�� ��û ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public enum ControllerState {

	DEVICE_ADD,
	DEVICE_UPD,
	DEVICE_DEL,
	
	LOGICALGROUP_ADD,
	LOGICALGROUP_UPD,
	LOGICALGROUP_DEL,
	
	REPORTER_ADD,
	REPORTER_UPD,
	REPORTER_DEL,
	
	DRIVER_ADD,
	DRIVER_UPD,
	DRIVER_DEVICE_DEL,
	DRIVER_REPORTER_DEL
}
