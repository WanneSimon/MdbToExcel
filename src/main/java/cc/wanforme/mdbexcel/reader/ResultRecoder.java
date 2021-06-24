package cc.wanforme.mdbexcel.reader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 一次查询结果
 * @author wanne
 * @since 2021-06-23
 */
public class ResultRecoder {
	
	/** 查询失败的记录*/
	private Map<String, Exception> failQuery;
	
	/** 每一次查询的结果 Map<String, String>中的键是列名，值是值 <br>
	 * MdbReader中一个表的结果对应一个set中的元素
	 * MdbPageReader中一次分页查询的结果对应一个set中的元素
	 */
	private Set<List<Map<String, String>>> tableResults ;

	public ResultRecoder() {
		this.failQuery = new HashMap<>();
		this.tableResults = new HashSet<>();
	}
	
	public void reset() {
		if(failQuery != null) {
			failQuery.clear();
		}
		if(tableResults != null) {
			tableResults.clear();
		}
	}
	
	public Map<String, Exception> getFailQuery() {
		return failQuery;
	}

	public Set<List<Map<String, String>>> getTableResults() {
		return tableResults;
	}
	
	
}
