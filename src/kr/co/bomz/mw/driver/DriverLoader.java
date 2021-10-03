package kr.co.bomz.mw.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import kr.co.bomz.mw.comm.AbstractDevice;
import kr.co.bomz.mw.comm.AbstractReporter;

/**
 * 
 * 	����̹� jar ������ �ε��Ѵ�
 * 
 * @author Bomz
 * @version 1.0
 * @since 1.0
 *
 */
public class DriverLoader{

	
	/**
	 * 	������ ����̹��� �����Ѵ�
	 * 	NULL �� �������� ������, ������ ���ܰ� �߻��Ѵ�
	 */
	public AbstractDevice newInstanceDevice(int driverId, byte[] driverFile) throws Throwable{
		
		Object obj = this.newInstance(driverFile, true);
		
		if( obj instanceof AbstractDevice ){
			return (AbstractDevice)obj;
		}else{
			throw new ClassCastException("[" + driverId + " file cannot be cast to AbstractDevice");
		}
	}
	
	/**
	 * 	������ ����̹��� �����Ѵ�
	 * 	NULL �� �������� ������, ������ ���ܰ� �߻��Ѵ�
	 *
	 */
	public AbstractReporter newInstanceIReporter(int driverId, byte[] driverFile) throws Throwable{
		Object obj = this.newInstance(driverFile, false);
		
		if( obj instanceof AbstractReporter ){
			return (AbstractReporter)obj;
		}else{
			throw new ClassCastException("[" + driverId  + " file cannot be cast to AbstractReporter");
		}
	}

	/**	����� �� ����̹� �������� �˻�		*/
	public boolean validationDriver(byte[] driverFile, boolean device){
		File file = null;
		try{
			file = this.makeTempJarFile(driverFile);
			return this.newInstance(file, device) != null;
		}catch(Throwable e){
			return false;
		}finally{
			if( file != null )		file.delete();
		}
	}
	
	private Object newInstance(byte[] driverFile, boolean device) throws Throwable{
		File file = this.makeTempJarFile(driverFile);
		try{
			return this.newInstance(file, device).newInstance();
		}finally{
			if( file != null )		file.delete();
		}
	}
	
	private File makeTempJarFile(byte[] driverFile) throws DriverFileMakeException{
		FileOutputStream fos = null;
		FileChannel fc = null;
		try{
			File file = File.createTempFile("bomz_", ".jar");
			fos = new FileOutputStream(file);
			fc = fos.getChannel();
			ByteBuffer bb = ByteBuffer.allocateDirect(driverFile.length);
			bb.put(driverFile);
			bb.flip();
			fc.write(bb);
			
			return file;
		}catch(Exception e){
			throw new DriverFileMakeException(e);
		}finally{
			if( fc != null )		try{		fc.close();			}catch(Exception e){}
			if( fos != null )		try{		fos.close();		}catch(Exception e){}
		}
	}
	
	/**
	 * 		JAR ������ �о� ����̹� ��ü�� ����
	 */
	@SuppressWarnings("resource")
	private Class<?> newInstance(File file, boolean device) throws Throwable{
		
		OSGiJarLoader loader = null;
		
		try{
			loader = new OSGiJarLoader(file);
			return loader.findJarClass(device);
		}finally{
			if( loader != null )		try{		loader.closeJarFile();		}catch(Exception e){}
		}
	}
}
