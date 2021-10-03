package kr.co.bomz.mw.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.bomz.mw.BomzMiddleware;
import kr.co.bomz.mw.db.Setting;
import kr.co.bomz.mw.util.CryptoUtil;

/**
 * 	���� ���� ���� ����
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class SettingInfoService {

	private static final SettingInfoService _this = new SettingInfoService();
	
	/**		���̵� �� : ���� �ڵ� ��		*/
	private static final String ID_LANGUAGE = "lang";
	
	/**		���̵� �� : ȭ�� �� ǥ�� �Խù� ��		*/
	private static final String ID_LIST_ITEM_LENGTH = "itemlen";

	/**		���̵� �� : ������ ��� ����		*/
	private static final String ID_WEBSERVICE = "soap";
	
	/**		���̵� �� : ������ ��� ����		*/
	private static final String ID_WEBSERVICE_PORT = "soap_port";
	
	/**		���̵� �� : ������ ��ȣ		*/
	private static final String ID_WEBSERVICE_PW = "soap_pw";
	
	/**		��� ������ ����� ����		*/
	private final String[] OK_LANGUAGES = new String[]{"kr", "en"};
	
	/**		��� ������ ȭ�� �� ǥ�� �Խù� �ִ� ��		*/
	private final int OK_LIST_ITEM_MAX_LENGTH = 100;
	
	/**		ON, OFF ��뿩�� ��		*/
	private final String[] ON_OFF = new String[]{"on", "off"};
	
	/**		������ �⺻ ��Ʈ ��		*/
	private final int WEB_SERVICE_PORT = 9001;
	
	/**		������ ��û �⺻ ��ȣ ��		*/
	private final String WEB_SERVICE_PW = "bomzmiddleware";
	
	/**
	 * 		��� ����
	 * 		ko : �ѱ���
	 */
	private String language = OK_LANGUAGES[0];
	
	/**
	 * 		���ȭ�� ������ ǥ�� ��
	 * 		�⺻ �� : 20��
	 */
	private int pageItemLength = 20;
	
	/**
	 * 		������ ��� ����
	 * 		��� : on
	 * 		������ : off
	 */
	private String webService = ON_OFF[1];
	
	/**		������ ��Ʈ		*/
	private int webServicePort = WEB_SERVICE_PORT;
	
	/**		������ ��ȣ		*/
	private String webServicePw = WEB_SERVICE_PW;
	
	private SettingInfoService(){}
	
	public static final SettingInfoService getInstance(){
		return _this;
	}
	
	
	/**		��� ������ �� �ʱ�ȭ		*/
	public final void initSetting(){
		SqlSession session = null;
		try{
			session = DatabaseService.getInstance().openSession();
			
			Map<String, Setting> settingMap = this.toSettingMap( DatabaseService.getInstance().selectSettingInfoList(session) );
			
			this.initLanguage(session, settingMap.get(ID_LANGUAGE));							// ��� ����
			this.initPageItemLength(session, settingMap.get(ID_LIST_ITEM_LENGTH));	// ȭ�� �� ǥ�� ������ ��
			this.initWebService(session, settingMap.get(ID_WEBSERVICE));						// ������ ��� ���� ����
			this.initWebServicePort(session, settingMap.get(ID_WEBSERVICE_PORT));		// ������ ��Ʈ ��
			this.initWebServicePw(session, settingMap.get(ID_WEBSERVICE_PW));		// ������ ��ȣ ��
			
			session.commit();
		}catch(Exception e){
			if( session != null )		session.rollback();
			
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("���� ���� �ʱ�ȭ �� ����", e);
		}finally{
			if( session != null )		session.close();
		}
		
	}

	/**		������ ��뿩�� ���� �ʱ�ȭ			*/
	private void initWebService(SqlSession session, Setting st) throws Exception{
		if( st != null ){
			// ��� �������� ���� ��� ����� �� �ִ� �������� Ȯ��
			String value = st.getParamValue();
			
			if( value.equalsIgnoreCase(ON_OFF[0]) ){
				this.webService = ON_OFF[0];
				return;
			}else if( value.equalsIgnoreCase(ON_OFF[1]) ){
				this.webService = ON_OFF[1];
				return;
			}
			
			// ����� �� ���� ������� �����Ǿ����Ƿ� ��� ���� �� �����ؾ� ��
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.warn("������ ��뿩�� ���� �� ������ �⺻ �� ���� �� ��� �� ���� [����������:{}]", value);
		}else{
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.info("������ ��뿩�� ���� ���� ���� �����Ƿ� �⺻ �� ���� �� ��� �� ���");
		}
		
		// ���� �Դٸ� ��� �⺻ ���� ���� ���ų� �߸��� ���� ���� ������� ����̹Ƿ� �⺻ ������ ��� ����
		DatabaseService.getInstance().mergeSetting(session, new Setting(ID_WEBSERVICE, this.webService));
	}
	
	/**		������ ��ȣ ���� �ʱ�ȭ		*/
	private void initWebServicePw(SqlSession session, Setting st) throws Exception{
		if( st != null ){
			// ��� �������� ���� ��� ����� �� �ִ� ��Ʈ ������ Ȯ��
			
			try{
				this.webServicePw = new CryptoUtil().decoding(st.getParamValue());
				return;
			}catch(Exception e){}
			
			// ����� �� ���� ������� �����Ǿ����Ƿ� ��� ���� �� �����ؾ� ��
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.warn("������ ��ȣ �� ������ �⺻ �� ���� �� ��� �� ���� [����������:{}]", st.getParamValue());
		}else{
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.info("������ ��ȣ ���� �����Ƿ� �⺻ �� ���� �� ��� �� ���");
		}
		
		// ���� �Դٸ� ��� �⺻ ���� ���� ���ų� �߸��� ���� ���� ������� ����̹Ƿ� �⺻ ������ ��� ����
		DatabaseService.getInstance().mergeSetting(session, new Setting(ID_WEBSERVICE_PW, new CryptoUtil().encoding(this.webServicePw)));
	}
	
	/**		������ ��Ʈ ���� �ʱ�ȭ		*/
	private void initWebServicePort(SqlSession session, Setting st) throws Exception{
		if( st != null ){
			// ��� �������� ���� ��� ����� �� �ִ� ��Ʈ ������ Ȯ��
			int value;
			
			try{
				value = Integer.parseInt(st.getParamValue());
				if( this.checkWebServicePort(value) ){
					this.webServicePort = value;
					return;		
				}
			}catch(Exception e){}
			
			// ����� �� ���� ������� �����Ǿ����Ƿ� ��� ���� �� �����ؾ� ��
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.warn("������ ��Ʈ �� ������ �⺻ �� ���� �� ��� �� ���� [����������:{}]", st.getParamValue());
		}else{
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.info("������ ��Ʈ ���� �����Ƿ� �⺻ �� ���� �� ��� �� ���");
		}
		
		// ���� �Դٸ� ��� �⺻ ���� ���� ���ų� �߸��� ���� ���� ������� ����̹Ƿ� �⺻ ������ ��� ����
		DatabaseService.getInstance().mergeSetting(session, new Setting(ID_WEBSERVICE_PORT, String.valueOf(this.webServicePort)));
	}
	
	/**		��� ȭ�� ������ �� ���� �ʱ�ȭ		*/
	private void initPageItemLength(SqlSession session, Setting st) throws Exception{
		if( st != null ){
			// ��� �������� ���� ��� ����� �� �ִ� �������� Ȯ��
			int value;
			
			try{
				value = Integer.parseInt(st.getParamValue());
				if( value > 0 && value <= OK_LIST_ITEM_MAX_LENGTH ){
					this.pageItemLength = value;
					return;		
				}
			}catch(Exception e){}
			
			// ����� �� ���� ������� �����Ǿ����Ƿ� ��� ���� �� �����ؾ� ��
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.warn("��� ȭ�� ������ �� ���� �� ������ �⺻ �� ���� �� ��� �� ���� [����������:{}]", st.getParamValue());
		}else{
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.info("��� ȭ�� ������ �� ���� ���� ���� �����Ƿ� �⺻ �� ���� �� ��� �� ���");
		}
		
		// ���� �Դٸ� ��� �⺻ ���� ���� ���ų� �߸��� ���� ���� ������� ����̹Ƿ� �⺻ ������ ��� ����
		DatabaseService.getInstance().mergeSetting(session, new Setting(ID_LIST_ITEM_LENGTH, String.valueOf(this.pageItemLength)));
	}
	
	/**		��� ���� �ʱ�ȭ		*/
	private void initLanguage(SqlSession session, Setting st) throws Exception{
		if( st != null ){
			// ��� �������� ���� ��� ����� �� �ִ� �������� Ȯ��
			String value = st.getParamValue();
			if( this.validationLanguage(value) ){
				this.language = value;
				return;
			}
			
			// ����� �� ���� ������� �����Ǿ����Ƿ� ��� ���� �� �����ؾ� ��
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.warn("��� ���� ���� �� ������ �⺻ �ѱ۾�� ���� �� ��� �� ���� [����������:{}]", value);
		}else{
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.info("��� ���� ���� ���� �����Ƿ� �⺻ �ѱ۾�� ���� �� ��� �� ���");
		}
		
		// ���� �Դٸ� ��� �⺻ ���� ���� ���ų� �߸��� ���� ���� ������� ����̹Ƿ� �⺻ ������ ��� ����
		DatabaseService.getInstance().mergeSetting(session, new Setting(ID_LANGUAGE, this.language));
	}
	
	/**		���� ���� �˻� ����� Map �������� ��ȯ		*/ 
	private Map<String, Setting> toSettingMap(List<Setting> settingList){
		
		if( settingList == null || settingList.isEmpty() )		return new HashMap<String, Setting>(0);
		
		Map<String, Setting> map = new HashMap<String, Setting>(settingList.size());
		
		for(Setting st : settingList)	map.put(st.getParamId(), st);
		
		return map;
	}
	
	/**		���� ��� �ڵ�		*/
	public String getLanguage(){
		return this.language;
	}
	
	/**		��� ���� ���� ���		*/
	public String getLanguagePath(){
		return "conf" + File.separatorChar + "lang" + File.separatorChar + this.language;
	}
	
	/**		���ȭ�� ������ �ִ� ǥ�� ��		*/
	public int getPageItemLength() {
		return pageItemLength;
	}
	
	/**		������ ��� ����		*/
	public boolean getWebService(){
		return this.webService.equals(ON_OFF[0]);
	}
	
	/**		������ ��Ʈ ��ȣ		*/
	public int getWebServicePort(){
		return this.webServicePort;
	}
	
	public String getWebServicePw(){
		return this.webServicePw;
	}
	
	/**		����� �� �ִ� ��� �ڵ� ����		*/
	public String[] getSuccessLanguages(){
		return OK_LANGUAGES;
	}
	
	/**		���� �� ����		*/
	public void updateSettingInfo(kr.co.bomz.util.resource.ResourceBundle languageResourceBundle, 
			String language, int pageItemLength, boolean webService, 
			String webServicePort, String webServicePw) throws Exception{
		
		// ����� ���� �� ��ȿ�� �˻�
		this.validationLanguage(languageResourceBundle, language);
		// ��� ȭ�� ������ ǥ�� �� ��ȿ�� �˻�
		this.validationPageItemLength(languageResourceBundle, pageItemLength);
		// ������ ��Ʈ �� ��ȿ�� �˻�
		int webServicePortToInt = this.validationWebServicePort(languageResourceBundle, webServicePort);
		// ������ ��ȣ �� ��ȿ�� �˻�
		String encodingWebServicePw = this.validationWebServicePw(languageResourceBundle, webServicePw);
		
		SqlSession session = null;
		
		try{
			session = DatabaseService.getInstance().openSession();
			
			DatabaseService.getInstance().mergeSetting(session, new Setting(ID_LANGUAGE, language));	// ����� ����
			DatabaseService.getInstance().mergeSetting(session, new Setting(ID_LIST_ITEM_LENGTH, String.valueOf(pageItemLength)));	// ��ϴ� ������ ǥ�� �� ����
			DatabaseService.getInstance().mergeSetting(session, new Setting(ID_WEBSERVICE, ON_OFF[webService?0:1]));				// ������ ��뿩�� ����
			DatabaseService.getInstance().mergeSetting(session, new Setting(ID_WEBSERVICE_PORT, webServicePort.trim()));		// ������ ��Ʈ �� ����
			DatabaseService.getInstance().mergeSetting(session, new Setting(ID_WEBSERVICE_PW, encodingWebServicePw));		// ������ ��ȣ �� ����
			
			session.commit();
			
			this.language = language;
			this.pageItemLength = pageItemLength;
			this.webService = ON_OFF[webService?0:1];
			this.webServicePort = webServicePortToInt;
			this.webServicePw = webServicePw;
			
		}catch(Exception e){
			if( session != null )		session.rollback();
			
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("���� ���� ���� ��� ����", e);
			
			throw new Exception( languageResourceBundle.getResourceValue("ERR_4") );
		}finally{
			if( session != null )		session.close();
		}
	}
	
	/**		������ ��ȣ ��ȿ�� �˻�		*/
	private String validationWebServicePw(kr.co.bomz.util.resource.ResourceBundle languageResourceBundle, String webServicePw) throws Exception{
		if( webServicePw == null )		throw new Exception( languageResourceBundle.getResourceValue("ERR_6_1") );
		
		if( webServicePw.indexOf(" ") != -1 )
			throw new Exception( languageResourceBundle.getResourceValue("ERR_6_2") );
		
		int size = webServicePw.length();
		if( size < 8 || size > 15 )		throw new Exception( languageResourceBundle.getResourceValue("ERR_6_3") );
		
		try{
			return new CryptoUtil().encoding(webServicePw);
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("������ ��ȣ �� ��ȣȭ ���� [{}]", webServicePw, e);
			throw new Exception( languageResourceBundle.getResourceValue("ERR_6_1") );
		}
	}
	
	/**		������ ��Ʈ ��ȿ�� �˻�		*/
	private int validationWebServicePort(kr.co.bomz.util.resource.ResourceBundle languageResourceBundle, String webServicePort) throws Exception{
		int webServicePortToInt;
		try{
			if( webServicePort == null )		throw new Exception();
			webServicePortToInt = Integer.parseInt(webServicePort.trim());
			if( !this.checkWebServicePort(webServicePortToInt) )	throw new Exception();
			return webServicePortToInt;
		}catch(Exception e){
			throw new Exception( languageResourceBundle.getResourceValue("ERR_5_1") + webServicePort + languageResourceBundle.getResourceValue("ERR_5_2") );
		}
	}
	
	/**		���ȭ�� ������ �� ��ȿ�� �˻�		*/
	private void validationPageItemLength(kr.co.bomz.util.resource.ResourceBundle languageResourceBundle, int pageItemLength) throws Exception{
		if( pageItemLength <= 0 || pageItemLength >= OK_LIST_ITEM_MAX_LENGTH )
			throw new Exception( languageResourceBundle.getResourceValue("ERR_3") );
	}
	
	/**		��� ���� �� ��ȿ�� �˻�		*/
	private void validationLanguage(kr.co.bomz.util.resource.ResourceBundle languageResourceBundle, String language) throws Exception{
		if( language == null )		throw new Exception( languageResourceBundle.getResourceValue("ERR_1") );
		if( !this.validationLanguage(language) )		throw new Exception( languageResourceBundle.getResourceValue("ERR_2") );
	}
	
	/**		����� �� �ִ� ��������� �˻�		*/
	private boolean validationLanguage(String code){
		if( code == null )		return false;
		
		for(String v : OK_LANGUAGES)
			if( v.equals(code) )		return true;
		
		return false;
	}
	
	/**		������ ��Ʈ ���� ��ȿ�� �˻�		*/
	private boolean checkWebServicePort(int port){
		return  port >= 50 && port <= 65535;
	}
}
