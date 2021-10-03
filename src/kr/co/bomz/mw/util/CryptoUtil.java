package kr.co.bomz.mw.util;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.bomz.mw.BomzMiddleware;

/**
 * 	��ȣȭ �� ���Ǵ� ��ƿ
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class CryptoUtil {

	private static final String DES_KEY = "BOMZ_MIDDLEWARE_SYSTEM_VER_1.0";
	private static final String DES_INSTANCE = "DESede/ECB/PKCS5Padding";
	
	private Key makeKey() throws Exception{
		DESedeKeySpec deskeyspec = new DESedeKeySpec(DES_KEY.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		return keyFactory.generateSecret(deskeyspec);
	}
	
	/**		��ȣȭ ����		*/
	public String encoding(String password){

		if( password == null )		return null;
			
		try{
			Key key = this.makeKey();
			
			Cipher cipher = Cipher.getInstance(DES_INSTANCE);
			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
			
			byte[] data = cipher.doFinal( password.getBytes("UTF-8") );
			return Base64.getEncoder().encodeToString(data);
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("������ ��ȣȭ ���� [{}]", password, e);
			return null;
		}
	}
	
	/**		��ȣȭ ����		*/
	public String decoding(String password){

		if( password == null )		return null;
		
		try{
			Key key = this.makeKey();
			
			Cipher cipher = Cipher.getInstance(DES_INSTANCE);
			cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
			
			byte[] data = cipher.doFinal( Base64.getDecoder().decode(password) );
			
			return new String(data, "UTF-8");
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("������ ��ȣȭ ���� [{}]", password, e);
			return null;
		}
	}
	
}
