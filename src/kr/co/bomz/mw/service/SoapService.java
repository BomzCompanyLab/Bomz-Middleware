package kr.co.bomz.mw.service;

import javax.xml.ws.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.bomz.mw.soap.WebServiceImpl;

/**
 * 	������
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class SoapService {

	private static final SoapService _this = new SoapService();
	
	private final Logger logger = LoggerFactory.getLogger("BomzMw");
	
	private Endpoint endpoint;
	
	private SoapService(){}
	
	public static final SoapService getInstance(){
		return _this;
	}
	
	/**	������ ���� �Ǵ� ����		*/
	public synchronized void startOrStop(){
		if( SettingInfoService.getInstance().getWebService() )		this.start();
		else				this.stop();
	}
	
	/**		������ ����		*/
	private void start(){
		this.stop();
		
		String url = "http://localhost:" + SettingInfoService.getInstance().getWebServicePort() + "/bomz";
		this.endpoint = Endpoint.publish(url, new WebServiceImpl());
		if( this.logger.isInfoEnabled() ){
			this.logger.info("������ ����");
			this.logger.info("������ URL : {}", url);
			this.logger.info("������ WSDL : {}{}", url, "/mw?wsdl");
		}
	}
	
	/**		������ ����		*/
	private void stop(){
		if( this.endpoint != null ){
			this.endpoint.stop();
			if( this.logger.isInfoEnabled() )			this.logger.info("������ ����");
		}
		
		this.endpoint = null;
	}
	
}
