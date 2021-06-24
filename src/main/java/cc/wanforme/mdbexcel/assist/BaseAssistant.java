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

/** *.mdb 文件使用 ucanaccess 操作的父类
 * @author wanne
 * @since 2021-06-23
 */
public abstract class BaseAssistant {
	private static final Logger log = LoggerFactory.getLogger(BaseAssistant.class);

	// 驱动是否已加载
	private static volatile boolean DRIVER_LOADED = false;
	// 链接
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
	 * @param file  绝对路径
	 * @param props 链接属性， 默认配置：{@link #defaultConfig()}
	 * @throws Exception
	 */
	public BaseAssistant(String file, Properties props) throws Exception {
		log.info("loading driver...");
		loadDriver();

		log.info("connecting...");
		this.connection = DriverManager.getConnection("jdbc:ucanaccess://" + file, props);
	}

	/**
	 * 配置信息
	 * 
	 * @return
	 */
	public static Properties defaultConfig() {
		Properties prop = new Properties();
		prop.put("charSet", "gb2312"); // 这里是解决中文乱码
//			prop.put("jackcessopener", "com.cqvip.assist.JackcessOpener");
		prop.put("jackcessopener", JackcessOpener.class.getCanonicalName());
		return prop;
	}

	/** 加载驱动 */
	public static void loadDriver() throws ClassNotFoundException {
		synchronized (MdbQueryAssistant.class) {
			if (!DRIVER_LOADED) {
				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
				DRIVER_LOADED = true;
			}
		}
	}

	/**
	 * 查询所有表名
	 * 
	 * @throws SQLException
	 */
	public List<String> queryAllTables() throws SQLException {
		DatabaseMetaData dbmd = connection.getMetaData();
		ResultSet rsTables = dbmd.getTables(null, null, "%", null);
		List<String> tableNames = new ArrayList<>();
		while (rsTables.next()) {
			tableNames.add(rsTables.getString(3)); // 第三列是表名
		}

		rsTables.close();
		return tableNames;
	}

	
	/** 关闭资源，数据库连接等*/
	public  void close() throws SQLException{
		connection.close();
	}

	
}
