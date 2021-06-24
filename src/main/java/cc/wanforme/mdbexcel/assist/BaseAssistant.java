package cc.wanforme.mdbexcel.assist;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** *.mdb �ļ�ʹ�� ucanaccess �����ĸ���
 * @author wanne
 * @since 2021-06-23
 */
public abstract class BaseAssistant {
	private static final Logger log = LoggerFactory.getLogger(BaseAssistant.class);

	// �����Ƿ��Ѽ���
	private static volatile boolean DRIVER_LOADED = false;
	// ����
	protected Connection connection;

	/**
	 * @param file
	 * @see #MdbDbAssistant(String, Properties)
	 * @throws Exception
	 */
	public BaseAssistant(String file) throws Exception {
		this(file, defaultConfig());
	}

	/**
	 * @param file  ����·��
	 * @param props �������ԣ� Ĭ�����ã�{@link #defaultConfig()}
	 * @throws Exception
	 */
	public BaseAssistant(String file, Properties props) throws Exception {
		log.info("loading driver...");
		loadDriver();

		log.info("connecting...");
		this.connection = DriverManager.getConnection("jdbc:ucanaccess://" + file, props);
	}

	/**
	 * ������Ϣ
	 * 
	 * @return
	 */
	public static Properties defaultConfig() {
		Properties prop = new Properties();
		prop.put("charSet", "gb2312"); // �����ǽ����������
//			prop.put("jackcessopener", "com.cqvip.assist.JackcessOpener");
		prop.put("jackcessopener", JackcessOpener.class.getCanonicalName());
		return prop;
	}

	/** �������� */
	public static void loadDriver() throws ClassNotFoundException {
		synchronized (MdbQueryAssistant.class) {
			if (!DRIVER_LOADED) {
				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
				DRIVER_LOADED = true;
			}
		}
	}

	/**
	 * ��ѯ���б���
	 * 
	 * @throws SQLException
	 */
	public List<String> queryAllTables() throws SQLException {
		DatabaseMetaData dbmd = connection.getMetaData();
		ResultSet rsTables = dbmd.getTables(null, null, "%", null);
		List<String> tableNames = new ArrayList<>();
		while (rsTables.next()) {
			tableNames.add(rsTables.getString(3)); // �������Ǳ���
		}

		rsTables.close();
		return tableNames;
	}

	
	/** �ر���Դ�����ݿ����ӵ�*/
	public  void close() throws SQLException{
		connection.close();
	}

	
}
