package cc.wanforme.mdbexcel.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import cc.wanforme.mdbexcel.assist.MdbQueryAssistant;

/** ��ȡ *.mdb �ļ�����ȡĳ�����ȫ�����ݣ������б�����ݣ��ڴ�ռ�ýϴ�<br>
 * ���з������ص�������Ҫ�ֶ�����<br> ��ʹ�� MdbPageReader ����
 * @see cc.wanforme.mdbexcel.reader.MdbPageReader
 * @author wanne
 * @since 2021-06-23
 */
public class MdbReader extends BaseMdbReader{
	
	public MdbReader(String fileAbsPath) throws Exception {
		super(new MdbQueryAssistant(fileAbsPath));
	}

	/** ��ȡ���б�����ݣ��ʺ�����������ʱ��ȡ����ռ�ڴ棩
	 * @param absPath
	 * @throws Exception
	 */
	public ResultRecoder queryAllTablesData()throws Exception  {
		return this.queryAllTablesData(this::queryTableAllData);
	}
	
	/** ��ȡ���б�����ݣ��ʺ�����������ʱ��ȡ����ռ�ڴ棩
	 * @param absPath
	 * @throws Exception
	 */
	public ResultRecoder queryAllTablesData(
			Function<? super String, ? extends List<Map<String, String>>> mapper) throws Exception {
		// 1. ��ȡ���б���
		List<String> tables = assistant.queryAllTables();
		
		// 2. ����ÿ�ű�
//		ResultRecoder rr = new ResultRecoder();
		recoder.reset();
		if(tables != null) {
			Set<List<Map<String, String>>> set = tables.stream()
				.map(mapper).collect(Collectors.toSet());
			recoder.getTableResults().addAll(set);
		}
		return recoder;
	}
	
	

	/** ��ѯ���������������
	 * @param table
	 * @return
	 */
	public List<Map<String, String>> queryTableAllData( String table ) {
		String sql = "select * from ?";
		Map<Integer, String> param = new HashMap<>(1);
		param.put(1, table);
		
		List<Map<String, String>> result = null;
		try {
			result = ((MdbQueryAssistant)assistant).executeQueryPreparedStatement(sql, param);
		} catch (Exception e) {
			recoder.getFailQuery().put(sql+"; ["+table+"]", e);
		}
		return result;
	}
	
	
	// ����
//	public ResultRecoder queryAllTablesData(String absPath) throws Exception {
//		
//		// 1. ��ȡ���б���
//		List<String> tables = assistant.queryAllTables();
//		
//		// 2. ����ÿ�ű�
//		String sql = "select * from ?";
////		Map<String, Exception> failQuery = new HashMap<>();
//		ResultRecoder rr = new ResultRecoder();
//		rr.setFailQuery(new HashMap<>());
//		
//		if(tables != null) {
//			Set<List<Map<String, String>>> set = tables.stream()
//					.map( tb -> {
//						List<Map<String, String>> result = null;
//						try {
//							result = assistant.executeQueryPreparedStatement(sql, tb);
//						} catch (Exception e) {
////						failQuery.put(sql+"; ["+tb+"]", e);
//							rr.getFailQuery().put(sql+"; ["+tb+"]", e);
//						}
//						return result;
//					}).collect(Collectors.toSet());
//			rr.setTableResults(set);
//		}
//		
//		return rr;
//	}

	

}
