package cc.wanforme.mdbexcel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.wanforme.mdbexcel.reader.MdbPageReader;
import cc.wanforme.mdbexcel.reader.handler.PageDataHandler;

/**
 * @author Administrator
 *
 */
public class MdbExcelExample {
	private static final Logger log = LoggerFactory.getLogger(MdbExcelExample.class);
	
	public static void main(String[] args) throws Exception {
		demoTest();
	}

	public static void demoTest() throws Exception {
//		String mdbPath = "D:\\d repo\\0622\\202106\\-DB_20210601_ANSI.mdb";
		String mdbPath = "F:\\TempTest\\-DB_20210601_ANSI.mdb";
		String tableName = "US";
//		String tableName = "ExportLog";

		String outFile = "F:\\TempTest\\ANSI-out.xls";
		int fileMaxSize = 4000;
		int page = 1;
		int pageSize = 10;

		log.info("\nstart...");
		
		PageDataHandler dataHandler = new PageDataHandler(outFile, fileMaxSize);
		
		MdbPageReader pageReader = new MdbPageReader(mdbPath, page, pageSize);
		// 查询之前必须设置数据处理器，但可以在查询所有表名
		// List<String> tableNames = pageReader.getAssistant().queryAllTables();
		pageReader.setDataHandler(dataHandler);
		// 只单表查询一页（查询结束后，页码加一，再次调用此方法会查询下一页的数据，页码从1开始）
		// pageReader.queryTableOnePage(tableName);
		// 再次调用查询下一页数据
		// pageReader.queryTableOnePage(tableName);
		// 可以通过 resetPage， 重置页码
		// pageReader.resetPage(1);
		// 查询单表所有数据
		pageReader.queryTableAllData(tableName);
		// 通知数据处理器结束（通知后，写入才会结束）
		dataHandler.finished();
		
		
		// 每个 PageDataHandler 只能处理一张表，如果想处理多张表，先 reset，再设置新的 PageDataHandler
//		List<String> tableNames = pageReader.getAssistant().queryAllTables();
//		pageReader.setDataHandler(new PageDataHandler(outFile, fileMaxSize));
//		pageReader.reset(); // 在设置 dataHandler 之后调用
//		pageReader.queryTableOnePage(tableName);
//		dataHandler.finished();
		
		
		// 关闭有关连接
		pageReader.close(); 
		
		log.info("\nfinished!");
		
	}

}
