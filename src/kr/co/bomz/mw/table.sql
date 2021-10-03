CREATE DATABASE BOMZ_MW CHARACTER SET UTF8#

USE BOMZ_MW#

-- ���α׷� ���� ����
CREATE TABLE SETTING_INFO(
	PARAM_ID			VARCHAR(50) NOT NULL,			-- ���� ���̵� 
	PARAM_VALUE 	VARCHAR(50) NOT NULL,			-- ���� ��
	PRIMARY KEY(PARAM_ID)
)# 

-- ��ġ ����̹� ����
CREATE TABLE DEVICE_DRIVER_INFO(
	DRIVER_ID				INT AUTO_INCREMENT,			-- ���� ���̵�
	DRIVER_NAME		VARCHAR(60) NOT NULL,		-- �̸�
	DRIVER_FILE_NAME	VARCHAR(200) NOT NULL,	-- ���� ���� �̸�
	DRIVER_JAR				BLOB NOT NULL,						-- JAR ���� ����
	REG_DATE				TIMESTAMP NOT NULL,			-- �������
	UPD_DATE				TIMESTAMP NULL,					-- ��������
	PRIMARY KEY(DRIVER_ID)
)#

-- ������ ����̹� ����
CREATE TABLE REPORTER_DRIVER_INFO(
	DRIVER_ID				INT AUTO_INCREMENT,			-- ���� ���̵�
	DRIVER_NAME		VARCHAR(60) NOT NULL,		-- �̸�
	DRIVER_FILE_NAME	VARCHAR(200) NOT NULL,	-- ���� ���� �̸�
	DRIVER_JAR				BLOB NOT NULL,						-- JAR ���� ����
	REG_DATE				TIMESTAMP NOT NULL,			-- �������
	UPD_DATE				TIMESTAMP NULL,					-- ��������
	PRIMARY KEY(DRIVER_ID)
)#
	 	
-- ��ġ ����
CREATE TABLE DEVICE_INFO(
	DEV_ID				INT AUTO_INCREMENT,	-- ���� ���̵�
	DEV_NAME			VARCHAR(60),					-- �̸�
	DRIVER_ID			INT NOT NULL,					-- ����̹� ���̵�
	
	DEV_TYPE 			ENUM('T', 'S') NOT NULL,		-- ��ġ ��� Ÿ�� ('T':TCP , 'S':SERIAL)
	
	TCP_MODE			ENUM('S', 'C') NOT NULL,		-- TCP ����� ��� ����, Ŭ���̾�Ʈ ���� ('S':SERVER , 'C':CLIENT)  
	DEV_IP					VARCHAR(23),			-- TCP ��Ź���� ��� IP					
	DEV_PORT			INT NOT NULL,			-- TCP ��Ź���� ��� PORT	, SERIAL ����� ��� �ø�����Ʈ ��ȣ			
	
	DEV_BR				INT,		-- SERIAL ��Ź���� ��� BAUDRATE (��밪:'110', '300', '1200', '2400', '4800', '9600', '19200', '38400', '57600', '115200', '230400', '460800', '921600')
	DEV_FC				INT,		-- SERIAL ��Ź���� ��� FLOW CONTROL ('0':NONE, '1':RTS_CTS_IN, '2':RTS_CTS_OUT, '3':Xon_Xoff_IN, '4':Xon_Xoff_OUT)
	DEV_DT				INT,		-- SERIAL ��Ź���� ��� DATABIT (5~8)
	DEV_SB				INT,		-- SERIAL ��Ź���� ��� STOPBIT (1, 2, 3:1.5)
	DEV_PR				INT,		-- SERIAL ��Ź���� ��� PARITY ('0':NONE, '1':ODD, '2':EVEN, '3':MARK, '4':SPACE)

	DEV_STATE			ENUM('S', 'D') NOT NULL DEFAULT 'D',		-- ��ġ ������� ('S':����� , 'D':����ȵ�)
	
	REG_DATE			TIMESTAMP NOT NULL,
	UPD_DATE			TIMESTAMP,
	
	FOREIGN KEY(DRIVER_ID) REFERENCES DEVICE_DRIVER_INFO(DRIVER_ID),
	PRIMARY KEY(DEV_ID)
)# 

-- �����׷� ����
CREATE TABLE GROUP_INFO(
	GROUP_ID				INT AUTO_INCREMENT,			-- �����׷� ���̵�
	GROUP_NAME		VARCHAR(60) NOT NULL,		-- �����׷� �̸�
	REG_DATE				TIMESTAMP NOT NULL,
	UPD_DATE				TIMESTAMP,
	PRIMARY KEY(GROUP_ID)
)# 

-- �����׷�� ��ġ ���� ����
CREATE TABLE GROUP_MAP(
	GROUP_ID				INT NOT NULL,		-- �����׷� ���̵�
	DEV_ID					INT NOT NULL,		-- ��ġ ���̵�
	DEV_ANT					INT NOT NULL,		-- ��ġ ���׳�
	FOREIGN KEY(GROUP_ID) REFERENCES GROUP_INFO(GROUP_ID),
	FOREIGN KEY(DEV_ID) REFERENCES DEVICE_INFO(DEV_ID),
	PRIMARY KEY(GROUP_ID, DEV_ID, DEV_ANT)
)# 

-- ������ ����
CREATE TABLE REPORTER_INFO(
	REP_ID							INT AUTO_INCREMENT,			-- ������ ���̵�
	REP_NAME					VARCHAR(60) NOT NULL,		-- ������ �̸�
	REP_IP							VARCHAR(23) NOT NULL,		-- ������ ������ ������
	REP_PORT					INT(5) NOT NULL,					-- ������ ������ ��Ʈ
	REP_PARAM				VARCHAR(90),							-- ��Ÿ �ʿ��� �Ķ���� ����
	REP_PERIOD				INT,											-- ���� �ֱ� (0 �� ��� ��� ����)
	DRIVER_ID					INT NOT NULL,							-- ������ ����̹� ���̵�
	
	REP_STATE					ENUM('S', 'D') NOT NULL DEFAULT 'D',		-- ������ ������� ('S':����� , 'D':����ȵ�)
	
	REG_DATE					TIMESTAMP NOT NULL,			-- �������
	UPD_DATE					TIMESTAMP,							-- ��������
	FOREIGN KEY(DRIVER_ID) REFERENCES REPORTER_DRIVER_INFO(DRIVER_ID),
	PRIMARY KEY(REP_ID)
)# 

-- �����Ϳ� �����׷� ����
CREATE TABLE REPORTER_MAP(
	REP_ID				INT NOT NULL,		-- ������ ���̵�
	GROUP_ID		INT NOT NULL,		-- �����׷� ���̵�
	FOREIGN KEY(REP_ID) REFERENCES REPORTER_INFO(REP_ID),
	FOREIGN KEY(GROUP_ID) REFERENCES GROUP_INFO(GROUP_ID),
	PRIMARY KEY(REP_ID, GROUP_ID)
)# 

-- �����Ϳ��� ���� ������ ������ �α�
CREATE TABLE REPORTER_TAG_LOG(
	LOG_ID				INT AUTO_INCREMENT,			-- �α׾��̵�
	DEV_ID				INT NOT NULL,							-- ��ġ���̵�
	DEV_NAME			VARCHAR(60) NOT NULL,		-- ��ġ�̸�
	DEV_ANT				INT NOT NULL,							-- ��ġ���׳�
	GROUP_ID			INT NOT NULL,							-- �׷���̵�
	GROUP_NAME	VARCHAR(60) NOT NULL,		-- �׷��̸�
	REP_ID					INT NOT NULL,							-- �����;��̵�
	REP_NAME			VARCHAR(60) NOT NULL,		-- �������̸�
	READ_TIME			VARCHAR(19) NOT NULL,		-- �ð�
	TAG_ID				VARCHAR(30) NOT NULL,		-- �±׾��̵�
	REG_DATE			TIMESTAMP NOT NULL,			-- ����ð�
	PRIMARY KEY(LOG_ID)
)#

-- ������ ���� ���� �α� ���̺� �ε��� ����
CREATE INDEX INDEX_REPORTER_TAG_LOG ON REPORTER_TAG_LOG(REP_ID)#


-- ���ȭ�� �˻� �� ���Ǵ� �Լ�
DROP FUNCTION IF EXISTS getListStartPage;#
CREATE FUNCTION getListStartPage(
	ALL_PAGE_COUNT		INT,
	PAGE_ITEM_LENGTH	INT,
	REQ_PAGE_NO			INT
	) RETURNS		INT
BEGIN
	DECLARE 	RES_PAGE_NO INT;
	SET				RES_PAGE_NO = CEIL(ALL_PAGE_COUNT / PAGE_ITEM_LENGTH) - 1;
	SET				REQ_PAGE_NO = REQ_PAGE_NO - 1;
	RETURN		CASE
						WHEN		RES_PAGE_NO < 0	THEN	0
						WHEN		RES_PAGE_NO <= REQ_PAGE_NO	THEN	 (RES_PAGE_NO * PAGE_ITEM_LENGTH)
						ELSE		(REQ_PAGE_NO * PAGE_ITEM_LENGTH)
						END;
END;#