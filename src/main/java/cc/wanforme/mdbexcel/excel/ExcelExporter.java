package cc.wanforme.mdbexcel.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

/** excel ����������
 * @author wanne
 * @since 2021-06-23
 */
public class ExcelExporter {
	private File file;
	private List<String> head;
	private String sheetName;
	
	private ExcelWriter excelWriter;
	private WriteSheet writeSheet;
	
	private volatile boolean isInitial = false;
	
	public ExcelExporter() {}
	
	/** ��ʼ����ֻ��Ҫ����һ�Σ���ε�����Ч
	 * @param exportPath �����ļ���·��
	 * @param head ����һ����Ϊͷ
	 * @param sheetName sheet��
	 */
	public void init(String exportPath, List<String> head, String sheetName){
		if(!isInitial) {
			this.file = new File(exportPath);
			this.head = head;
			this.sheetName = sheetName;
			
			// @see https://www.yuque.com/easyexcel/doc/write
			excelWriter = EasyExcel.write(file)
					.head(this.easyExcelHead(head))
					.build();
			writeSheet = EasyExcel.writerSheet(sheetName).build();
			
			isInitial = true;
		}
	}
	
	/** ��Excel ��׷������*/
	public void eppendToExcel(List<List<String>> data) {
		excelWriter.write(data, writeSheet);
	}
	
	/** д�����Ҫ���� finish*/
	public void finish() {
		// ǧ�������finish ���æ�ر���
        if (excelWriter != null) {
            excelWriter.finish();
        }
	}
	
	/** �����ǵı�ͷת���� EasyExcel ������ʶ�ı�ͷ
	 * @param heads
	 * @return
	 */
	private List<List<String>> easyExcelHead(List<String> heads){
		List<List<String>> hList = heads.stream()
			.map( e-> {
				List<String> l = new ArrayList<>(1);
				l.add(e);
				return l;
			}).collect(Collectors.toList());
		return hList;
	}

	
	public File getFile() {
		return file;
	}
	public List<String> getHead() {
		return head;
	}
	public String getSheetName() {
		return sheetName;
	}
}
