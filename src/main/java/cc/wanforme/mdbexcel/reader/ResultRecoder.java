package cc.wanforme.mdbexcel.reader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** һ�β�ѯ���
 * @author wanne
 * @since 2021-06-23
 */
public class ResultRecoder {
	
	/** ��ѯʧ�ܵļ�¼*/
	private Map<String, Exception> failQuery;
	
	/** ÿһ�β�ѯ�Ľ�� Map<String, String>�еļ���������ֵ��ֵ <br>
	 * MdbReader��һ����Ľ����Ӧһ��set�е�Ԫ��
	 * MdbPageReader��һ�η�ҳ��ѯ�Ľ����Ӧһ��set�е�Ԫ��
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
