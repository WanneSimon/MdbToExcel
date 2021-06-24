package cc.wanforme.mdbexcel.reader.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.wanforme.mdbexcel.excel.ExcelExporter;
import cc.wanforme.mdbexcel.reader.ResultRecoder;

/** 处理每页的数据
 * @author wanne
 * @since 2021-06-23
 */
public class PageDataHandler implements DataHandler{
	private static final Logger log = LoggerFactory.getLogger(PageDataHandler.class);
			
	private String fileName;
	private String sheetName = "sheet2"; // 表格名字
	private int seg = 1; // 文件的序号（文件分片）
	
	private ExcelExporter exporter;
	private volatile AtomicInteger count = new AtomicInteger(0);
	private volatile AtomicLong total = new AtomicLong(0);
	
	/** 每个文件最大写多少条*/
	private Integer fileMaxSize = 10000;
	
	public PageDataHandler(String fileName, int fileMaxSize){
		this.fileName = fileName;
		this.fileMaxSize = fileMaxSize;
		exporter = new ExcelExporter();
	}
	
	@Override
	public void handleData(ResultRecoder recoder) {
		// 先简单打印出来，测试程序，后面再考略如何转成 excel
//		this.printDatas(recoder);
		
		Set<List<Map<String, String>>> re = recoder.getTableResults();
		if(re!=null) {
			Iterator<List<Map<String, String>>> iterator = re.iterator();
			while( iterator.hasNext() ) {
				List<Map<String, String>> page = iterator.next();
				
				if(page.isEmpty()) {
					continue;
				}
				
				// 列名
				Set<String> columnName = page.get(0).keySet();
				
				// 一页中所有的值
//				Collection<String> pageColumnValues = page.stream()
//					.map( row -> row.values() )
//					.reduce( (a,b) -> { 
//						List<String> t = new ArrayList<String>(a);
//						t.addAll(b);
//						return t;
//					}).get();

				// 将 List<Map> 转换成List<List> ，里面的 list 就是 Map 中的值
				List<List<String>> data = page.stream()
					.map( row -> new ArrayList<String>(row.values()) )
					.collect(Collectors.toList());
				
				// 是否需要分片，即当前数据需要写入到两个文件中，（当前文件剩余容纳数量不能写完所有数据）
				boolean needSegFile = count.get() + page.size() > fileMaxSize;
				
				// 继续在当前文件中读写
				if(!needSegFile) {
					String file = getFileName(false);
					// 每个 exporter 对象只有第一次调用这个方法有效，后续调用会忽略
					this.exporter.init(file, new ArrayList<String>(columnName), sheetName);
					// 导出
					this.exporter.eppendToExcel(data);
					count.addAndGet(page.size());
				} else {
					// 需要分片，先写完当前文件，再创建新的文件
					int remainSize = fileMaxSize - count.get(); // 当前文件还可以写入多少
					String file = getFileName(false);
							
					List<List<String>> listA = data.subList(0, remainSize);
					// 每个 exporter 对象只有第一次调用这个方法有效，后续调用会忽略
					this.exporter.init(file, new ArrayList<String>(columnName), sheetName);
					// 导出
					this.exporter.eppendToExcel(listA);
					
					this.exporter.finish();
					
					// 新文件，重新创建 exporter
					exporter = new ExcelExporter();
					file = getFileName(true);
					List<List<String>> listB = data.subList(remainSize, data.size());
					exporter.init(file, new ArrayList<String>(columnName), sheetName);
					// 导出
					this.exporter.eppendToExcel(listB);
					count.set(data.size()-remainSize);
				}
				
				total.addAndGet(page.size());
			}
			
		}
		
	}
	
	/**
	 * @param isNext 是否下一个分片文件
	 * @return
	 */
	private String getFileName(boolean isNext) {
		seg = isNext ? seg+1 : seg;
		
		int dot = fileName.lastIndexOf('.');
		String a = fileName; // "扩展名左边的部分"
		String b = ""; // 扩展名
		
		if(dot != -1) {
			a = fileName.substring(0, dot);
			b = fileName.substring(dot+1);
		}
		
		String finalName = null;
		if(isNext) {
			finalName = a+" ("+seg+")."+b;
		} else {
			if( seg > 1 ) { // 当前已经是某个分片文件了
				finalName = a+" ("+seg+")."+b;
			} else { // 还是最开始的文件，没有分片
				finalName = fileName;
			}
		}
		
		return finalName.endsWith(".") ? finalName.substring(0, finalName.length()-1) : finalName;
	}
	
	protected void printDatas(ResultRecoder recoder) {
		Set<List<Map<String, String>>> data = recoder.getTableResults();
		if(data != null) {
			Iterator<List<Map<String, String>>> iterator = data.iterator();
			while (iterator.hasNext()) {
				List<Map<String, String>> rows = iterator.next();
				
				for (Map<String, String> row : rows) {
					StringBuilder sb = new StringBuilder();
					
					row.keySet().forEach( key->  sb.append(row.get(key)+",") );
					
					log.info(sb.toString() + "\n");
				}
			}
		}
		
		if(recoder.getFailQuery() != null) {
			recoder.getFailQuery().keySet().forEach( key -> {
				String msg = recoder.getFailQuery().get(key).getMessage();
				log.info(key+"\t"+msg);
			});
		}
		
	}

	/** 该方法只是触发读取结束的一个操作，最后一次读取的数据已经被处理，这里不需要再次处理 */
	@Override
	public void finished() {
		this.exporter.finish();
	}
	
	public void resetSeg(){
		this.seg = 0;
	}
	
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
//	public static void main(String[] args) {
//		PageDataHandler handler = new PageDataHandler("abcss");
//		PageDataHandler handler = new PageDataHandler("abc.txt");
//		System.out.println(handler.getFileName(false)); // 0
//		System.out.println(handler.getFileName(false)); // 0
//		System.out.println(handler.getFileName(true)); // 1
//		System.out.println(handler.getFileName(true)); // 2
//		System.out.println(handler.getFileName(true)); // 3
//		System.out.println(handler.getFileName(false)); // 3 
//		System.out.println(handler.getFileName(true)); //4 
//	
//	}
	
	
}
