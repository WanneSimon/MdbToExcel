package cc.wanforme.mdbexcel.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import cc.wanforme.mdbexcel.assist.MdbQueryAssistant;

/** 读取 *.mdb 文件，读取某个表的全部数据，或所有表的数据（内存占用较大）<br>
 * 所有方法返回的数据需要手动处理<br> 请使用 MdbPageReader 代替
 * @see cc.wanforme.mdbexcel.reader.MdbPageReader
 * @author wanne
 * @since 2021-06-23
 */
public class MdbReader extends BaseMdbReader{
	
	public MdbReader(String fileAbsPath) throws Exception {
		super(new MdbQueryAssistant(fileAbsPath));
	}

	/** 读取所有表的数据，适合数据量不大时读取。（占内存）
	 * @param absPath
	 * @throws Exception
	 */
	public ResultRecoder queryAllTablesData()throws Exception  {
		return this.queryAllTablesData(this::queryTableAllData);
	}
	
	/** 读取所有表的数据，适合数据量不大时读取。（占内存）
	 * @param absPath
	 * @throws Exception
	 */
	public ResultRecoder queryAllTablesData(
			Function<? super String, ? extends List<Map<String, String>>> mapper) throws Exception {
		// 1. 获取所有表名
		List<String> tables = assistant.queryAllTables();
		
		// 2. 遍历每张表
//		ResultRecoder rr = new ResultRecoder();
		recoder.reset();
		if(tables != null) {
			Set<List<Map<String, String>>> set = tables.stream()
				.map(mapper).collect(Collectors.toSet());
			recoder.getTableResults().addAll(set);
		}
		return recoder;
	}
	
	

	/** 查询单个表的所有数据
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
	
	
	// 样例
//	public ResultRecoder queryAllTablesData(String absPath) throws Exception {
//		
//		// 1. 获取所有表名
//		List<String> tables = assistant.queryAllTables();
//		
//		// 2. 遍历每张表
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
