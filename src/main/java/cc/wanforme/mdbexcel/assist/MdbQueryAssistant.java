package cc.wanforme.mdbexcel.assist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** *.mdb 文件的数据库查询操作封装-
 * @author wanne
 * @since 2021-06-23
 */
public class MdbQueryAssistant extends BaseAssistant {
	private static final Logger log = LoggerFactory.getLogger(MdbQueryAssistant.class);
	
	// 静态sql
	private Statement stm;
	// 动态sql
	private HashMap<String, PreparedStatement> pses;
	
	public MdbQueryAssistant(String file) throws Exception {
		super(file);
		this.stm = connection.createStatement();
		this.pses = new HashMap<>();
	}
	
	/** 执行静态sql
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> executeQueryStatement(String sql) throws SQLException {
		ResultSet rs = stm.executeQuery(sql);
		
		List<Map<String, String>> list = this.resultSetToHashMap(rs);
		
		rs.close();
		return list;
	}
	
	/** 注：ucanaccess 不能使用 ? 作为占用符，预编译优化的能力使用不了。方法内部将 ? 通过正则表达式替换，字符串没有自动加单引号
	 * @param sql
	 * @param param sql的参数，键是 PreparedStatement 中设置参数的下标，从1开始
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> executeQueryPreparedStatement(String sql, Map<Integer, ? extends Object> param) 
			throws SQLException {
		// ucanaccess 不能使用 ? 作为占位符
		if(param!=null) {
			Set<Integer> keys = param.keySet();
			for (Integer key : keys) {
//				ps.setObject(key, param.get(key));
				String val = param.get(key).toString();
//				if(param.get(key) instanceof String ) {
//					val = "'"+val+"'";
//				}
				
				sql = sql.replaceFirst("\\?", val);
			}
		}
		
//		System.out.println("sql: " + sql);
		log.debug("query: " + sql);
		PreparedStatement ps = this.getPreparedStatement(sql);
		// ucanaccess 不能使用 ? 作为占位符
//		if(param!=null) {
//			Set<Integer> keys = param.keySet();
//			for (Integer key : keys) {
//				ps.setObject(key, param.get(key));
//			}
//		}
		
		ResultSet rs = ps.executeQuery();
		List<Map<String, String>> list = this.resultSetToHashMap(rs);
		
		rs.close();
		return list;
	}
	
	/** 查询结果每一行转换成HashMap存储，每行键是列名
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, String>> resultSetToHashMap(ResultSet rs) throws SQLException{
		List<Map<String, String>> list = new ArrayList<>();
		ResultSetMetaData metaData = rs.getMetaData();
		while (rs.next()) {
			Map<String, String> row = new LinkedHashMap<>(metaData.getColumnCount());
			for (int i = 0; i < metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i + 1);
				String v = rs.getString(i + 1);
				row.put(columnName, v);
			}
			list.add(row);
		}
		
		return list;
	}
	
	/** 获取 PreparedStatement
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	private synchronized PreparedStatement getPreparedStatement(String sql) 
			throws SQLException {
		PreparedStatement ps = pses.get(sql);
		if(ps == null) {
			ps = connection.prepareStatement(sql);
			pses.put(sql, ps);
		}
		return ps;
	}
	
	/** 关闭链接，如果发生异常，建议再调用一次
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		// 优先关闭自己的连接，再关闭父类的
		if(stm != null && !stm.isClosed()) {
			stm.close();
		}
		
		Iterator<PreparedStatement> iterator = pses.values().iterator();
		while (iterator.hasNext()) {
			PreparedStatement ps = iterator.next();
			if( ps!=null && !ps.isClosed() ) {
				ps.close();
			}
		}
		
		super.close();
	}
	
	
}
