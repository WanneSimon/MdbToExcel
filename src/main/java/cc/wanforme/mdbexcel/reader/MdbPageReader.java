package cc.wanforme.mdbexcel.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wanforme.mdbexcel.assist.MdbQueryAssistant;
import cc.wanforme.mdbexcel.reader.handler.PageDataHandler;

/** ��ҳʽ��ȡ�����������ظ�����ǰ���� reset �������ã�����ҳ�����
 * @author wanne
 * @since 2021-06-23
 */
public class MdbPageReader extends BaseMdbReader {
	// ҳ��
	private int page;
	// ÿҳ��С
	private int pageSize;
	// ÿҳ�����ݴ�����
	private PageDataHandler dataHandler;
	
	/**
	 * @param fileAbsPath *.mdb�ļ�����·��
	 * @param page ��ʼҳ��
	 * @param pageSize ÿҳ��С
	 * @param dataHandler ÿҳ���ݴ����� 
	 * @throws Exception
	 */
	public MdbPageReader(String fileAbsPath, int page, int pageSize,
			PageDataHandler dataHandler) throws Exception {
		super(new MdbQueryAssistant(fileAbsPath));
		this.page = page;
		this.pageSize = pageSize;
		this.dataHandler = dataHandler;
	}

	/** ��ҳ��ѯ����������ݣ���ѯ������ҳ��+1 <br>
	 *  ����ͨ�� ����ĳ��Ⱥ�pageSize�Ƿ���ȣ��жϲ�ѯ�Ƿ����
	 * @param table
	 * @return
	 */
	private List<Map<String, String>> queryTablePageData( String table ) {
		String sql = "select * from ? limit ?,?";
		int start = (page-1)*pageSize;
		Map<Integer, Object> param = new HashMap<>();
		param.put(1, table);
		param.put(2, new Integer(start));
		param.put(3, new Integer(pageSize));
		
		List<Map<String, String>> result = null;
		try {
			result = ((MdbQueryAssistant)assistant).executeQueryPreparedStatement(sql, param);
			// ���β�ѯ������� recorder ��
			recoder.getTableResults().add(result);
		} catch (Exception e) {
			recoder.getFailQuery().put(sql+"; ["+table+"]", e);
		}
		
		page++;
		return result;
	}
	
	/** ��ѯһ�η�ҳ����
	 * @param table
	 */
	public void queryTableOnePage( String table ) {
		// ���֮ǰ��ѯ�Ľ��
		this.recoder.reset();
		this.dataHandler.resetSeg();
		
		this.queryTablePageData(table);
		
		dataHandler.handleData(recoder);
//		dataHandler.finished();
	}
	
	/** ��ѯ���������������
	 * @param table
	 * @return
	 */
	public void queryTableAllData( String table ) {
		List<Map<String, String>> list = null;
		do {
			// ���֮ǰ��ѯ�Ľ��
			this.recoder.reset();
			this.dataHandler.resetSeg();
			
			list = this.queryTablePageData(table);
//			if(list!=null && !list.isEmpty()) {
				dataHandler.handleData(recoder);
//			}
		} while (list!=null && !list.isEmpty() && list.size()==pageSize );
		
//		dataHandler.finished();
	}
	

	// �����������ṩ��ȡ���б�ķ�����
//	/** ��ѯ���б������
// 	 * @param exceptTables ����Ҫ�ı���
//	 * @throws SQLException
//	 */
//	public void queryAllTablesData(String... exceptTables) throws SQLException {
//		List<String> es = new ArrayList<>(); 
//		if(exceptTables !=null ) {
//			es.addAll(Arrays.asList(exceptTables));
//		}
//		
//		List<String> tables = assistant.queryAllTables();
//		for (String tb : tables) {
//			if(es.contains(tb) ) {
//				continue;
//			}
//			this.queryTableAllData(tb);
//		}
//	}
	
	/**
	 * �����ѯ���������ҳ�뵽1
	 */
	@Override
	public void reset() {
		this.reset(1, pageSize);
	}
	
	public void resetPage(int page) {
		this.page = page;
	}
	
	/** �����ѯ��������÷�ҳ����
	 * @param page
	 * @param pageSize
	 */
	public void reset(int page, int pageSize) {
		super.reset();
		this.page = page;
		this.pageSize = pageSize;
		this.dataHandler.resetSeg();
	}

	public PageDataHandler getDataHandler() {
		return dataHandler;
	}
	
	public void setDataHandler(PageDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}
}
