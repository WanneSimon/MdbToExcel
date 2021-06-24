package cc.wanforme.mdbexcel;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import cc.wanforme.mdbexcel.assist.JackcessOpener;

/** ucanaccess 操作 *.mdb 的样例
 * @author wanne
 *
 */
public class MdbUcanaccessDemo {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		String filePath = "D:\\DB_20210601_IT.mdb";
		String filePath = "D:\\d repo\\0622\\202106\\-DB_20210601_ANSI.mdb";
		String  url = "jdbc:ucanaccess://"+filePath;   //文件地址  
		
		Properties prop = new Properties();    
		prop.put("charSet", "gb2312");                //这里是解决中文乱码  
//		prop.put("jackcessopener", "com.cqvip.assist.JackcessOpener");
		// 必须要指定新的 JackcessOpener ，默认的会报错
		prop.put("jackcessopener", JackcessOpener.class.getCanonicalName());
//		prop.put("user", "");  
//		prop.put("password", "");  
		
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		Connection connection = DriverManager.getConnection(url, prop);
		
		Scanner in = new Scanner(System.in);
		
		Statement stm = connection.createStatement(); 
		
		// 查询所有 表格
		System.out.println("query tables:");
		DatabaseMetaData dbmd = connection.getMetaData();
		ResultSet rsTables = dbmd.getTables(null, null, "%", null);
		while ( rsTables.next() ) {
			ResultSetMetaData md = rsTables.getMetaData();
			List<String> tableNames = new ArrayList<>();
			for( int i=0; i< md.getColumnCount(); i++) {
				// 下标为3(i+1=3)（第三列），是表名
				tableNames.add( rsTables.getString(i+1) );
			}
			System.out.println(tableNames.toString());
		}
		
		// 
		while (true) {
			System.out.println("enter your sql ( 'exit' to stop):  ");
			String sql = in.nextLine();
//			String sql = "select * from ExportLog limit 0,2";
			if("exit".equals(sql)) {
				break;
			}
			
//			ResultSet rs = stm.executeQuery(sql);
			
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			
			while (rs.next()) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < metaData.getColumnCount(); i++) {
//						String columnName = metaData.getColumnName(i);
					String v = rs.getString(i + 1);
					sb.append(v + "\t");
				}
				System.out.println(sb.toString() + "\n");
			}
			rs.close();
			
			ps.close();
		}
		
		in.close();
		
		stm.close();
		connection.close();
	}

	
}
