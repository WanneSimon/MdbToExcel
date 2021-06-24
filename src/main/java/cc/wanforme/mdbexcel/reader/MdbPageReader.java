package cc.wanforme.mdbexcel.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wanforme.mdbexcel.assist.MdbQueryAssistant;
import cc.wanforme.mdbexcel.reader.handler.PageDataHandler;

/** 分页式读取，单个对象重复利用前调用 reset 进行重置，否则页码会乱
 * @author wanne
 * @since 2021-06-23
 */
public class MdbPageReader extends BaseMdbReader {
	// 页码
	private int page;
	// 每页大小
	private int pageSize;
	// 每页的数据处理器
	private PageDataHandler dataHandler;
	
	/**
	 * @param fileAbsPath *.mdb文件绝对路径
	 * @param page 初始页码
	 * @param pageSize 每页大小
	 * @param dataHandler 每页数据处理器 
	 * @throws Exception
	 */
	public MdbPageReader(String fileAbsPath, int page, int pageSize) throws Exception {
		super(new MdbQueryAssistant(fileAbsPath));
		this.page = page;
		this.pageSize = pageSize;
	}

	/** 分页查询单个表的数据，查询结束后，页码+1 <br>
	 *  可以通过 结果的长度和pageSize是否相等，判断查询是否结束
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
			// 本次查询结果存入 recorder 中
			recoder.getTableResults().add(result);
		} catch (Exception e) {
			recoder.getFailQuery().put(sql+"; ["+table+"]", e);
		}
		
		page++;
		return result;
	}
	
	/** 查询一次分页数据
	 * @param table
	 */
	public void queryTableOnePage( String table ) {
		// 清除之前查询的结果
		this.recoder.reset();
		this.dataHandler.resetSeg();
		
		this.queryTablePageData(table);
		
		dataHandler.handleData(recoder);
//		dataHandler.finished();
	}
	
	/** 查询单个表的所有数据
	 * @param table
	 * @return
	 */
	public void queryTableAllData( String table ) {
		List<Map<String, String>> list = null;
		do {
			// 清除之前查询的结果
			this.recoder.reset();
			this.dataHandler.resetSeg();
			
			list = this.queryTablePageData(table);
//			if(list!=null && !list.isEmpty()) {
				dataHandler.handleData(recoder);
//			}
		} while (list!=null && !list.isEmpty() && list.size()==pageSize );
		
//		dataHandler.finished();
	}
	

	// 不能在这里提供读取所有表的方法，
//	/** 查询所有表的数据
// 	 * @param exceptTables 不需要的表名
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
	 * 清除查询结果，重置页码到1，(只能在 dataHandler 设置后调用)
	 */
	@Override
	public void reset() {
		this.reset(1, pageSize);
	}
	
	/** 清除查询结果，重置分页配置(只能在 dataHandler 设置后调用)
	 * @param page
	 * @param pageSize
	 */
	public void reset(int page, int pageSize) {
		super.reset();
		this.page = page;
		this.pageSize = pageSize;
		this.dataHandler.resetSeg();
	}
	
	public void resetPage(int page) {
		this.page = page;
	}

	public PageDataHandler getDataHandler() {
		return dataHandler;
	}
	
	public void setDataHandler(PageDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}
}
