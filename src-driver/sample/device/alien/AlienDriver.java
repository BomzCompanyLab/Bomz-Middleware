package sample.device.alien;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import kr.co.bomz.mw.comm.AbstractDevice;
import kr.co.bomz.mw.db.Tag;

/**
 * 	RFID Alien ��ġ ����̹�
 * 
 * @author Bomz
 *
 */
public class AlienDriver extends AbstractDevice{

	private BufferedReader bufferedReader;
	
	private int readDataSize;
	private char[] readData = new char[500];
	private StringBuilder readMessage = new StringBuilder();
	private String returnMessage;
	
	private int startPoint;
	private int lastPoint;
	
	// parse ���
	private int textParseStartIndex;
	private String[] tags;
	private String[] tag_values;
	private String[] antenna_values;
	
	private String readTime;
	private NodeList nodeList;
	private Element element;
	private int size;
	private Document document;
	
	/*
	 *  -1 : ���� �ȵ�.
	 * 	 0 : XML
	 *   1 : TEXT
	 */
	private int messageType;
	
	@Override
	public boolean execute() {
		
		if( this.bufferedReader == null )
			this.bufferedReader = new BufferedReader(new InputStreamReader(super.getInputStream()));
			
		try {
			
			if( super.writeMessage(new byte[]{0x00}) ){	// ���� Ȯ�� �޼��� ����
				readMessage();
				if (!returnMessage.equals("")) {
					super.insertIntoQueue( this.parse(returnMessage) ); // queueHandle.enQueue(pmlDoc);
				}
			}else{
				super.disconnect();
			}
		} catch ( NullPointerException e1 ){
			// ���� �� close() �Ǵ� exit() �� ȣ��� �� bufferedReader �� null �� �� �� ����
			super.disconnect();
			
		} catch (Exception e1) {
			if( logger.isWarnEnabled() )
				logger.warn("Alien 9640 ������ ó�� ���� [��ġID:{}, ��ġ��:{}]", super.getDeviceId(), super.getDeviceName(), e1);
			super.disconnect();
		}
					
		return true;
	}

	@Override
	public void close() {
		if( this.bufferedReader != null )
			try{		this.bufferedReader.close();		}catch(Exception e){}
		this.bufferedReader = null;
	}
	
	public void readMessage() throws Exception{
		
		messageType = -1;
		
		while(true){
			
			readDataSize = bufferedReader.read(readData);
			
			if( readDataSize == -1 ){
				super.disconnect();
				returnMessage = "";
				return;
			}
			
			if( readDataSize > 0){
				readMessage.append( String.valueOf(readData, 0, readDataSize).trim() );
			}
			
			switch(messageType){
			case -1:	// �ϴ� ���� ��ġ�� ã�ƾ� ��.
				startPoint = readMessage.indexOf("<Alien-RFID-Reader-Auto-Notification>");
				if( startPoint == -1 ){
					startPoint = readMessage.indexOf("#Alien RFID Reader Auto Notification Message");
					
					if( startPoint != -1 ){
						// TEXT �������� ���� ������ ã�Ҵٸ�.. �������� �տ��ִ� �ʿ���� �����ʹ� �����.
						if( startPoint > 0 ){
							readMessage.delete(0, startPoint);	// ���ڸ��� �������Ƿ� �ٽ� ���� ������ üũ
							startPoint = readMessage.indexOf("#Alien RFID Reader Auto Notification Message");
						}
						
						messageType = 1;
					}
				}else{
					// XML �������� ���� ������ ã�Ҵٸ�.. �������� �տ��ִ� �ʿ���� �����ʹ� �����.
					if( startPoint > 0 ){
						if( !(startPoint == 1 && readMessage.charAt(0) == '\n') ){
							// '\n' �� �� �տ� ���� ��� �����ڵ�� �����ϹǷ� \n �� �������� �α� ��¾��� �׳� ����
//							errorCodeLogger.info("[Alien 9640 (" + port + ") ] " + readMessage.substring(0, startPoint));
						}
						
						readMessage.delete(0, startPoint);	// ���ڸ��� �������Ƿ� �ٽ� ���� ������ üũ
						startPoint = readMessage.indexOf("<Alien-RFID-Reader-Auto-Notification>");
					}
					messageType = 0;
				}
				break;
				
			case 0:	// ���������� XML �̶�� ��������� ã�´�.
				lastPoint = readMessage.indexOf("</Alien-RFID-Reader-Auto-Notification>");
				if( lastPoint != -1 ){
					returnMessage = readMessage.substring(startPoint, lastPoint + 38);	// 38 �� </Alien-RFID-Reader-Auto-Notification> �� length()
					readMessage.delete(startPoint, lastPoint + 38);
					return;
				}
				break;
				
			case 1:
				lastPoint = readMessage.indexOf("#End of Notification Message");
				if( lastPoint != -1 ){
					returnMessage = readMessage.substring(startPoint, lastPoint + 28);	// 28 �� #End of Notification Message �� length()
					readMessage.delete(startPoint, lastPoint + 28);
					return;
				}
				break;
			}
		}
	}
	
	public Tag[] parse(String message){
		
		if( message.startsWith("#") ){	// Text mode
			return this.getTextParse( message );			
			
		}else{	// XML mode
			return this.getXmlParse( message );
		}
	}
	
	private Tag[] getTextParse(String message){
		
		try{
			textParseStartIndex = message.indexOf("#Time: ") + 7;
			readTime = message.substring( textParseStartIndex , textParseStartIndex + 19 );
			
			tags = message.split("Tag: ");
			
			size = tags.length;
			if( size <= 0 )	return null;
			
			Tag[] resultTags = new Tag[size - 1];
						
			for(int i=1; i < size; i++){
				
				Tag tag = new Tag();
				
				tag.setReadTime( readTime );
				
				tag_values = tags[i].trim().split(",");
				try{
					antenna_values = tag_values[3].split(":");
					tag.setAntenna( Integer.parseInt( antenna_values[1].trim().substring(0, 1) ) );
				}catch(Exception e){
					if( logger.isWarnEnabled() )
						logger.warn("Alien 9640 ���׳� ������ �±����� �ս� [��ġID:{}, ��ġ��:{}]", super.getDeviceId(), super.getDeviceName(), e);
					return null;
				}
				
				tag.setTagId( tag_values[0].trim() );
				
				resultTags[i-1] = tag;
			}
			
			return resultTags;
			
		}catch(Exception e){
			if( logger.isWarnEnabled() )
				logger.warn("Alien 9640 Text �޼��� �Ľ� ���� [��ġID:{}, ��ġ��:{}]", super.getDeviceId(), super.getDeviceName(), e);
			return null;
		}finally{
			tags = null;
			tag_values = null;
			readTime = null;
		}
	}
	
	private Tag[] getXmlParse(String message ){
		
		try{
			this.setDocument(message);
			
			nodeList = document.getElementsByTagName("Time");	// �ð� ��������
			if( nodeList.getLength() != 1 )			return null;	// �߸��� ����
			readTime = nodeList.item(0).getTextContent();
			
			nodeList = document.getElementsByTagName("Alien-RFID-Tag");
			
			size = nodeList.getLength();
			
			if( size <= 0 )	return null;
			
			Tag[] resultTags = new Tag[ size ];
		
			for(int i=0; i < size; i++){	
				
				Tag tag = new Tag();
				tag.setReadTime( readTime );
				
				element = (Element)nodeList.item(i);
				
				try{
					tag.setAntenna(
						Integer.parseInt(element.getElementsByTagName("Antenna").item(0).getTextContent().trim()) );
				}catch(Exception e){
					if( logger.isWarnEnabled() )
						logger.warn("Alien 9640 ���׳� ������ �±����� �ս� [��ġID:{}, ��ġ��:{}]", super.getDeviceId(), super.getDeviceName(), e);
					return null;
				}
				
				tag.setTagId( element.getElementsByTagName("TagID").item(0).getTextContent() );
				
				resultTags[i] = tag;
			}
			
			return resultTags;
			
		}catch(Exception e){
			if( logger.isWarnEnabled() )
				logger.warn("Alien 9640 XML �޼��� �Ľ� ���� [��ġID:{}, ��ġ��:{}]", super.getDeviceId(), super.getDeviceName(), e);
			
			return null;
		}finally{
			readTime = null;
			nodeList = null;
			document = null;
			element = null;
			bais = null;
			builder = null;
		}
	}
		
	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder;
	private ByteArrayInputStream bais;
	
	private void setDocument(final String specDoc) throws Exception{
		try{
			bais = new ByteArrayInputStream(specDoc.getBytes());
			builder = builderFactory.newDocumentBuilder();
			document = builder.parse(bais);
		}catch(Exception e){
			throw new Exception("Read Data Parsing Exception");
		}
	}


}
