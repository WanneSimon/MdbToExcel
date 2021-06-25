package cc.wanforme.mdbexcel.example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.wanforme.mdbexcel.reader.MdbPageReader;
import cc.wanforme.mdbexcel.reader.handler.PageDataHandler;

/**
 * @author wanne
 *
 */
public class MdbExcelExample {
	private static final Logger log = LoggerFactory.getLogger(MdbExcelExample.class);
	
	public static void main(String[] args) throws Exception {
		// 单文件单表
		demoTest();
		// 多文件多表
		demoMultiTable();
	}

	public static void demoTest() throws Exception {
		String mdbPath = "F:\\TempTest\\20210601.mdb";
		String tableName = "US";
//		String tableName = "ExportLog";

		String outFile = "F:\\TempTest\\out.xls";
		int fileMaxSize = 4000;
		int page = 1;
		int pageSize = 10;

		log.info("\nstart...");
		
		PageDataHandler dataHandler = new PageDataHandler(outFile, fileMaxSize);
		
		MdbPageReader pageReader = new MdbPageReader(mdbPath, page, pageSize);
		// 查询之前必须设置数据处理器，但可以在查询之前查出所有表名
		// List<String> tableNames = pageReader.getAssistant().queryAllTables();
		pageReader.setDataHandler(dataHandler);
		// 只单表查询一页（查询结束后，页码自动加一，再次调用此方法会查询下一页的数据，页码从1开始）
		// pageReader.queryTableOnePage(tableName);
		// 再次调用查询下一页数据，不需要手动设置页码
		// pageReader.queryTableOnePage(tableName);
		// 可以通过 resetPage， 重置页码
		// pageReader.resetPage(1);
		// 查询单表所有数据
		pageReader.queryTableAllData(tableName);
		// 通知数据处理器结束（通知后，写入才会结束）
		dataHandler.finished();
		
		
		// 每个 PageDataHandler 只能处理一张表，如果想处理多张表，先给 MdbPageReader 设置新的 PageDataHandler ，再调用 MdbPageReader#reset()
		// 目前并不支持所有表都输出到同一个文件中，这会导致最终的文件是乱的
//		List<String> tableNames = pageReader.getAssistant().queryAllTables();
//		pageReader.setDataHandler(new PageDataHandler(outFile, fileMaxSize));
//		pageReader.reset(); // 在设置 dataHandler 之后调用
//		pageReader.queryTableOnePage(tableName);
//		dataHandler.finished();
		
		
		// 关闭有关连接
		pageReader.close(); 
		
		log.info("\nfinished!");
		
	}

	/** 读取单个文件内的多表
	 * @throws Exception
	 */
	public static void demoMultiTable() throws Exception {
		String mdbFile =  "F:\\TempTest\\20210601.mdb";
		
		// 第1页开始读取
		int startPage = 1;
		// 每次读取1000条数据
		int readPageSize = 1000;
		// 每个文件的 sheet 最大存储数量，xls 最大为 65535
		int maxSizeEachFileSheet = 1200; 
		MdbPageReader reader = new MdbPageReader(mdbFile, startPage, readPageSize);
		
		// 读取所有表名
		List<String> tables = reader.getAssistant().queryAllTables();
		for (String table : tables) {
			String outFileName = mdbFile + "-" + table + ".xls";
			
			PageDataHandler dataHandler = new PageDataHandler(outFileName, maxSizeEachFileSheet);
			reader.setDataHandler(dataHandler);
			reader.reset();
			
			reader.queryTableAllData(table);
			dataHandler.finished();
		}
		
		reader.close(); 
	}
	
}
