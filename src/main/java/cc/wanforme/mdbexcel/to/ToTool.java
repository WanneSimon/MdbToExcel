package cc.wanforme.mdbexcel.to;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import cc.wanforme.mdbexcel.reader.MdbPageReader;
import cc.wanforme.mdbexcel.reader.handler.PageDataHandler;

/** 读取工具
 * @author wanne
 *
 */
public class ToTool {
	private static final Logger LOG = LoggerFactory.getLogger(ToTool.class);
//	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	/** java -jar **.jar mdbToExcel.yml 
	 * @param args
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static SimConfig readConfig(String[] args) {
		LOG.info(Arrays.toString(args));
		String file = "mdbToExcel.yml";
		
		if(args!=null && args.length==1) {
			file = args[0];
		} 
		
		LOG.info("use file: " + file);
		
		Yaml yaml = new Yaml();
		
		try (InputStreamReader reader = new InputStreamReader(
				new FileInputStream(file), StandardCharsets.UTF_8)){
			SimConfig config = yaml.loadAs(reader, SimConfig.class);
			return config;
		} catch (IOException e) {
			LOG.error("error!", e);
			LOG.info("example: java -jar **.jar mdbToExcel.yml");
		}
		return null;
	}
	
	/** 读取单个文件内的多表
	 * @throws Exception
	 */
	public static void convertMultiTable(SimConfig config) throws Exception {
		String mdbFolder =  config.getMdbFolder();
		String outDir = config.getOut();
		
		File folder = new File(mdbFolder);
		if(!folder.exists()) {
			LOG.info("Source folder is not existed! " + folder.getAbsolutePath());
			return;
		}
		
		File[] fs = folder.listFiles();
		if(fs == null || fs.length==0) {
			return;
		}
		
		for (File f : fs) {
			String mdbFile = f.getPath();
			if(!mdbFile.endsWith(".mdb")) {
				continue;
			}
				
			LOG.info("loading file '"+mdbFile+"'");
			
			// 第1页开始读取
			int startPage = 1;
			// 每次读取1000条数据
			int readPageSize = 1000;
			// 每个文件的 sheet 最大存储数量，xls 最大为 65535
			int maxSheetRow = config.getMaxSheetRow(); 
			
			if(maxSheetRow > 65535) {
				maxSheetRow = 60000;
			}
			
//			File f = new File(mdbFile);
			MdbPageReader reader = new MdbPageReader(mdbFile, startPage, readPageSize);
//			FileOutputStream logfos = new FileOutputStream(new File(f.getParent(), "batch.log"), true); 
			
			// 读取所有表名
			List<String> tables = reader.getAssistant().queryAllTables();
			for (String table : tables) {
				LOG.info("loading table '"+table+"'");
//				String outFileName = mdbFile + "-" + table + ".xls";
				String outFileName = outDir + "/" + f.getName() + "-" + table + config.getExcelType();
				
				PageDataHandler dataHandler = new PageDataHandler(outFileName, maxSheetRow);
				reader.setDataHandler(dataHandler);
				reader.reset();
				
				reader.queryTableAllData(table);
				dataHandler.finished();
				
				LOG.info("'"+table+"' >> " + outFileName);
			}
			
			reader.close(); 	
		}
		
	}
	
	
}
