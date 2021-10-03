package sample.device;

import java.io.InputStream;

import kr.co.bomz.mw.comm.AbstractDevice;
import kr.co.bomz.mw.db.Tag;

/**
 * 	������ ��ġ ����̹� ����
 * 	TCP �Ǵ� SERIAL ������� ���Ź��� �����͸� �״�� ����
 * 
 * @author Bomz
 *
 */
public class EcoDevice extends AbstractDevice{

	private byte[] readData = new byte[100];
	
	private int readSize;
	
	@Override
	public void close() {}

	@Override
	public boolean execute() {
		InputStream is = super.getInputStream();
		try{
			this.readSize = is.read(this.readData);
			if( this.readSize == -1 )		return false;
			if( this.readSize == 0 )			return true;
			
			Tag tag = new Tag();
			tag.setAntenna(0);
			tag.setTagId(new String(this.readData, 0, this.readSize));
			tag.setReadTime(new EcoDeviceUtil().getTime());
			super.insertIntoQueue(new Tag[]{tag});
			return true;
		}catch(Exception e){
			return false;
		}
		
	}

}
