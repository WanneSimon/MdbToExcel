package cc.wanforme.mdbexcel.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

/** excel 到处工具类
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
	
	/** 初始化。只需要调用一次，多次调用无效
	 * @param exportPath 导出文件的路径
	 * @param head 首行一行作为头
	 * @param sheetName sheet名
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
	
	/** 向Excel 中追加内容*/
	public void eppendToExcel(List<List<String>> data) {
		excelWriter.write(data, writeSheet);
	}
	
	/** 写入结束要调用 finish*/
	public void finish() {
		// 千万别忘记finish 会帮忙关闭流
        if (excelWriter != null) {
            excelWriter.finish();
        }
	}
	
	/** 把我们的表头转换成 EasyExcel 可以认识的表头
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
