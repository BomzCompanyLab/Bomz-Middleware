package sample.reporter;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import kr.co.bomz.mw.comm.AbstractReporter;
import kr.co.bomz.mw.db.Tag;

/**
 * 	������ ������ ����̹�
 * 
 * @author Bomz
 *
 */
public class SimpleReporter extends AbstractReporter{

	private Socket socket;
	private BufferedWriter bw;
	
	private StringBuilder buffer = null;
	
	@Override
	protected void disconnect() {
		if( this.bw != null ){
			try{		this.bw.close();		}catch(Exception e){}
			this.bw = null;
		}
		
		if( this.socket != null ){
			try{		this.socket.close();		}catch(Exception e){}
			this.socket = null;
		}
	}

	@Override
	protected boolean isConnect() {
		if( this.bw != null )		return true;
		
		try{
			this.socket = new Socket(super.getReporterIp(), super.getReporterPort());
			this.bw = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			logger.info("������ ���� ���� [ID:{}, �̸�:{}, IP:{}, PORT:{}]", super.getReporterId(), super.getReporterName(), super.getReporterIp(), super.getReporterPort());
			return true;
		}catch(Exception e){
			logger.warn("������ ���� ���� [ID:{}, �̸�:{}, IP:{}, PORT:{}]", super.getReporterId(), super.getReporterName(), super.getReporterIp(), super.getReporterPort(), e);
			return false;
		}
	}

	@Override
	public boolean execute(List<Tag> tagList) {
		if( this.buffer == null )		this.putTagInfos(tagList);	// ���� �������̴� �����Ͱ� ���� ��� ť���� ������
		if( this.buffer == null )		return true;				// ������ �����Ͱ� ť���� ���� ��� ������ ó�� ����
		
		try{
			this.bw.write(this.buffer.toString());
			this.bw.flush();
			
			this.buffer = null;		// ���� ������ ���� ����
			return true;
		}catch(Exception e){
			logger.warn("������ ������ ���� ���з� ���� ���� [ID:{}, �̸�:{}, IP:{}, PORT:{}]", super.getReporterId(), super.getReporterName(), super.getReporterIp(), super.getReporterPort(), e);
			return false;
		}
		
	}

	/**	������ ������ ���ۿ� ����		*/
	private void putTagInfos(List<Tag> tagList){
		if( tagList == null || tagList.isEmpty() )		return;
		
		this.buffer = new StringBuilder();
		this.buffer.append("<tags>");
		for(Tag tag : tagList){
			this.buffer.append("<tag>");
			this.buffer.append("<id>").append(tag.getTagId()).append("</id>");
			this.buffer.append("<time>").append(tag.getReadTime()).append("</time>");
			this.buffer.append("<dev_id>").append(tag.getDeviceId()).append("</dev_id>");
			this.buffer.append("<dev_name>").append(tag.getDeviceName()).append("</dev_name>");
			this.buffer.append("<dev_ant>").append(tag.getAntenna()).append("</dev_ant>");
			this.buffer.append("<group_id>").append(tag.getLogicalGroupId()).append("</group_id>");
			this.buffer.append("<group_name>").append(tag.getLogicalGroupName()).append("</group_name>");
			this.buffer.append("</tag>");
		}
		this.buffer.append("</tags>");
	}
}
