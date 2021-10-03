package kr.co.bomz.mw.driver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.bomz.mw.BomzMiddleware;


/**
 * 
 * �������� JAR ������ ��� / ���� �� ���Ǵ� Ŭ�����δ�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class OSGiJarLoader extends URLClassLoader{
	
	/**		��ġ URL		*/
	private static final String DEVICE_CLASS_NAME = "kr.co.bomz.mw.comm.AbstractDevice";
	
	/**		������ URL		*/
	private static final String REPORTER_CLASS_NAME = "kr.co.bomz.mw.comm.AbstractReporter";
	
	/**		Ŭ���� �Ǵ� JAR ����		*/
	private final File loadFile;
	private JarFile jarFile;
		
	private String driverFileName;
	
	public OSGiJarLoader(File loadFile) throws Exception{
		super(new URL[]{loadFile.toURI().toURL()});
		
		this.loadFile = loadFile;
		this.jarFile = new JarFile(this.loadFile);
	}
	
	public Class<?> findJarClass(boolean device) throws ClassNotFoundException{
		
		try{
			this.loadClassData(device, this.jarFile.entries());
			if( this.driverFileName == null )		throw new Exception("����̹� ���� �˻� ����");
			return super.loadClass(this.driverFileName);
		}catch(ClassNotFoundException e){
			throw e;
		}catch(Exception e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("Ŭ���� �ε� �� ���� [{}]", driverFileName, e);
			return null;
		}
	}
	
	private void loadClassData(boolean device, Enumeration<JarEntry> e) throws Exception{

		Class<?> clazz;
		String className;
		while( e.hasMoreElements() ){
			JarEntry entry = e.nextElement();
			
			if( entry.isDirectory() )		continue;
			if( !entry.getName().endsWith(".class") )		continue;
			
			className = entry.getName();
			className = className.substring(0, className.length() - 6).replaceAll("/", ".").replaceAll("\\\\", ".");
			clazz = this.readClassByteData(
					className,  
					new BufferedInputStream(this.jarFile.getInputStream(entry))
				);
			
			if( this.checkJarMainDriverFile(device, clazz) )		this.driverFileName = className;
		}
		
	}
	
	/**		Jar ������ �������� ã��		*/
	private boolean checkJarMainDriverFile(boolean device, Class<?> clazz){
		if( clazz == null )		return false;
		
		if( clazz.getName().equals(device ? DEVICE_CLASS_NAME : REPORTER_CLASS_NAME) )		return true;
		return this.checkJarMainDriverFile(device, clazz.getSuperclass());
	}
	/**
	 * 		Ŭ���� ���� ������ �о� �м��Ѵ�
	 * 
	 * @param className		�м��� Ŭ������
	 * @param is					�м��� Ŭ���� ��Ʈ��
	 * @param printLog			���� �߻��� �������� ����Ʈ ����
	 * @return
	 */
	private Class<?> readClassByteData(String className, InputStream is){
		try{
			int size = is.available();
			byte[] data = new byte[size];
			is.read(data, 0, size);
			return super.defineClass(className, data, 0, size);
		}catch(Throwable e){
			Logger logger = LoggerFactory.getLogger(BomzMiddleware.MW_LOGGER_NAME);
			logger.error("Ŭ���� �ε� �� ���� [{}]", className, e);
			return null;
		}finally{
			try{		is.close();		}catch(Exception e){}
		}
	}
		
	/**		Jar ���� ����		*/
	public void closeJarFile(){
		try{
			this.jarFile.close();
		}catch(Exception e){}
	}
	
}
