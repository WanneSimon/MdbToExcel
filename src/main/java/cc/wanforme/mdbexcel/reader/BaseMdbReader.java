package cc.wanforme.mdbexcel.reader;

import java.sql.SQLException;

import cc.wanforme.mdbexcel.assist.BaseAssistant;

/** 读取 *.mdb 文件父类
 * @author wanne
 * @since 2021-06-23
 */
public class BaseMdbReader {

	// db辅助类
	protected BaseAssistant assistant = null;
	// 结果记录器
	protected ResultRecoder recoder;
	
	public BaseMdbReader(BaseAssistant assistant) throws Exception {
//		assistant = new MdbQueryAssistant(fileAbsPath);
		this.assistant = assistant;
		recoder = new ResultRecoder();
	}
	
	/** 关闭相关资源
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		assistant.close();
	}
	
	/** 查询所有表时，清楚之前的查询结果*/
	public void reset() {
		recoder.reset();
	}
	
	public ResultRecoder getRecoder() {
		return recoder;
	}
	
	public BaseAssistant getAssistant() {
		return assistant;
	}
	
}
