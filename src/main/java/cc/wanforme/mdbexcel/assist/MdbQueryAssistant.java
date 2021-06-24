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

/** *.mdb �ļ������ݿ��ѯ������װ-
 * @author wanne
 * @since 2021-06-23
 */
public class MdbQueryAssistant extends BaseAssistant {
	private static final Logger log = LoggerFactory.getLogger(MdbQueryAssistant.class);
	
	// ��̬sql
	private Statement stm;
	// ��̬sql
	private HashMap<String, PreparedStatement> pses;
	
	public MdbQueryAssistant(String file) throws Exception {
		super(file);
		this.stm = connection.createStatement();
		this.pses = new HashMap<>();
	}
	
	/** ִ�о�̬sql
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
	
	/** ע��ucanaccess ����ʹ�� ? ��Ϊռ�÷���Ԥ�����Ż�������ʹ�ò��ˡ������ڲ��� ? ͨ��������ʽ�滻���ַ���û���Զ��ӵ�����
	 * @param sql
	 * @param param sql�Ĳ��������� PreparedStatement �����ò������±꣬��1��ʼ
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> executeQueryPreparedStatement(String sql, Map<Integer, ? extends Object> param) 
			throws SQLException {
		// ucanaccess ����ʹ�� ? ��Ϊռλ��
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
		// ucanaccess ����ʹ�� ? ��Ϊռλ��
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
	
	/** ��ѯ���ÿһ��ת����HashMap�洢��ÿ�м�������
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
	
	/** ��ȡ PreparedStatement
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
	
	/** �ر����ӣ���������쳣�������ٵ���һ��
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		// ���ȹر��Լ������ӣ��ٹرո����
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
