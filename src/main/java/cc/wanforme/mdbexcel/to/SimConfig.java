package cc.wanforme.mdbexcel.to;

/** 简单配置
 * @author wanne
 *
 */
public class SimConfig {
	// 源文件
	private String mdbFile;
	// 输出文件夹
	private String out;
	// 输出的 excel 类型
	private String excelType;
	// excel 单 sheet 最大行数
	private Integer maxSheetRow = 60000;
	// 忽略的表名
	private String[] ignoreTables;
	
	public SimConfig() {}
	
	public SimConfig(String mdbFile, String out, String excelType, Integer maxSheetRow, String[] ignoreTables) {
		super();
		this.mdbFile = mdbFile;
		this.out = out;
		this.excelType = excelType;
		this.maxSheetRow = maxSheetRow;
		this.ignoreTables = ignoreTables;
	}

	public String getMdbFile() {
		return mdbFile;
	}
	public void setMdbFile(String mdbFile) {
		this.mdbFile = mdbFile;
	}
	public String getOut() {
		return out;
	}
	public void setOut(String out) {
		this.out = out;
	}
	public String getExcelType() {
		return excelType;
	}
	public void setExcelType(String excelType) {
		this.excelType = excelType;
	}
	public Integer getMaxSheetRow() {
		return maxSheetRow;
	}
	public void setMaxSheetRow(Integer maxSheetRow) {
		this.maxSheetRow = maxSheetRow;
	}
	public String[] getIgnoreTables() {
		return ignoreTables;
	}
	public void setIgnoreTables(String[] ignoreTables) {
		this.ignoreTables = ignoreTables;
	}

}
