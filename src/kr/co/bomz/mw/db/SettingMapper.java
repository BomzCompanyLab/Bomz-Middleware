package kr.co.bomz.mw.db;

import java.util.List;

/**
 * 	���� ���� ���̺� ����
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public interface SettingMapper {

	/**		��ϵ� ��� ���� ���� �˻�		*/
	public List<Setting> selectSettingInfoList();
	
	/**		���� ���� ���/����		*/
	public void mergeSetting(Setting st);
}
