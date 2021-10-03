package kr.co.bomz.mw.db;

import org.apache.ibatis.type.Alias;

/**
 * 	���� �Ķ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
@Alias("setting")
public class Setting {

	/**		���� ���̵�		*/
	private String paramId;
	
	/**		���� ��				*/
	private String paramValue;
	
	public Setting(){}
	
	public Setting(String paramId, String paramValue){
		this.paramId = paramId;
		this.paramValue = paramValue;
	}

	public String getParamId() {
		return paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
}
