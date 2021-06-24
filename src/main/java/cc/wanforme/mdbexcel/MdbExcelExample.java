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
		
		MdbPageReader pageReader = new MdbPageReader(mdbPath, page, pageSize, dataHandler);
		// ֻ�����ѯһҳ����ѯ������ҳ���һ���ٴε��ô˷������ѯ��һҳ�����ݣ�ҳ���1��ʼ��
		// pageReader.queryTableOnePage(tableName);
		// �ٴε��ò�ѯ��һҳ����
		// pageReader.queryTableOnePage(tableName);
		// ����ͨ�� resetPage�� ����ҳ��
		// pageReader.resetPage(1);
		// ��ѯ������������
		pageReader.queryTableAllData(tableName);
		// ֪ͨ���ݴ�����������֪ͨ��д��Ż������
		dataHandler.finished();
		
		
		// ÿ�� PageDataHandler ֻ�ܴ���һ�ű�����봦����ű��� reset���������µ� PageDataHandler
//		List<String> tableNames = pageReader.getAssistant().queryAllTables();
//		pageReader.reset();
//		pageReader.setDataHandler(new PageDataHandler(outFile, fileMaxSize));
//		pageReader.queryTableOnePage(tableName);
//		dataHandler.finished();
		
		
		// �ر��й�����
		pageReader.close(); 
		
		log.info("\nfinished!");
		
	}

}
