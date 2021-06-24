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

/** ����ÿҳ������
 * @author wanne
 * @since 2021-06-23
 */
public class PageDataHandler implements DataHandler{
	private static final Logger log = LoggerFactory.getLogger(PageDataHandler.class);
			
	private String fileName;
	private String sheetName = "sheet2"; // �������
	private int seg = 1; // �ļ�����ţ��ļ���Ƭ��
	
	private ExcelExporter exporter;
	private volatile AtomicInteger count = new AtomicInteger(0);
	private volatile AtomicLong total = new AtomicLong(0);
	
	/** ÿ���ļ����д������*/
	private Integer fileMaxSize = 10000;
	
	public PageDataHandler(String fileName, int fileMaxSize){
		this.fileName = fileName;
		this.fileMaxSize = fileMaxSize;
		exporter = new ExcelExporter();
	}
	
	@Override
	public void handleData(ResultRecoder recoder) {
		// �ȼ򵥴�ӡ���������Գ��򣬺����ٿ������ת�� excel
//		this.printDatas(recoder);
		
		Set<List<Map<String, String>>> re = recoder.getTableResults();
		if(re!=null) {
			Iterator<List<Map<String, String>>> iterator = re.iterator();
			while( iterator.hasNext() ) {
				List<Map<String, String>> page = iterator.next();
				
				if(page.isEmpty()) {
					continue;
				}
				
				// ����
				Set<String> columnName = page.get(0).keySet();
				
				// һҳ�����е�ֵ
//				Collection<String> pageColumnValues = page.stream()
//					.map( row -> row.values() )
//					.reduce( (a,b) -> { 
//						List<String> t = new ArrayList<String>(a);
//						t.addAll(b);
//						return t;
//					}).get();

				// �� List<Map> ת����List<List> ������� list ���� Map �е�ֵ
				List<List<String>> data = page.stream()
					.map( row -> new ArrayList<String>(row.values()) )
					.collect(Collectors.toList());
				
				// �Ƿ���Ҫ��Ƭ������ǰ������Ҫд�뵽�����ļ��У�����ǰ�ļ�ʣ��������������д���������ݣ�
				boolean needSegFile = count.get() + page.size() > fileMaxSize;
				
				// �����ڵ�ǰ�ļ��ж�д
				if(!needSegFile) {
					String file = getFileName(false);
					// ÿ�� exporter ����ֻ�е�һ�ε������������Ч���������û����
					this.exporter.init(file, new ArrayList<String>(columnName), sheetName);
					// ����
					this.exporter.eppendToExcel(data);
					count.addAndGet(page.size());
				} else {
					// ��Ҫ��Ƭ����д�굱ǰ�ļ����ٴ����µ��ļ�
					int remainSize = fileMaxSize - count.get(); // ��ǰ�ļ�������д�����
					String file = getFileName(false);
							
					List<List<String>> listA = data.subList(0, remainSize);
					// ÿ�� exporter ����ֻ�е�һ�ε������������Ч���������û����
					this.exporter.init(file, new ArrayList<String>(columnName), sheetName);
					// ����
					this.exporter.eppendToExcel(listA);
					
					this.exporter.finish();
					
					// ���ļ������´��� exporter
					exporter = new ExcelExporter();
					file = getFileName(true);
					List<List<String>> listB = data.subList(remainSize, data.size());
					exporter.init(file, new ArrayList<String>(columnName), sheetName);
					// ����
					this.exporter.eppendToExcel(listB);
					count.set(data.size()-remainSize);
				}
				
				total.addAndGet(page.size());
			}
			
		}
		
	}
	
	/**
	 * @param isNext �Ƿ���һ����Ƭ�ļ�
	 * @return
	 */
	private String getFileName(boolean isNext) {
		seg = isNext ? seg+1 : seg;
		
		int dot = fileName.lastIndexOf('.');
		String a = fileName; // "��չ����ߵĲ���"
		String b = ""; // ��չ��
		
		if(dot != -1) {
			a = fileName.substring(0, dot);
			b = fileName.substring(dot+1);
		}
		
		String finalName = null;
		if(isNext) {
			finalName = a+" ("+seg+")."+b;
		} else {
			if( seg > 1 ) { // ��ǰ�Ѿ���ĳ����Ƭ�ļ���
				finalName = a+" ("+seg+")."+b;
			} else { // �����ʼ���ļ���û�з�Ƭ
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

	/** �÷���ֻ�Ǵ�����ȡ������һ�����������һ�ζ�ȡ�������Ѿ����������ﲻ��Ҫ�ٴδ��� */
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
