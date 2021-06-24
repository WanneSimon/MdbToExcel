package cc.wanforme.mdbexcel.reader;

import java.sql.SQLException;

import cc.wanforme.mdbexcel.assist.BaseAssistant;

/** ��ȡ *.mdb �ļ�����
 * @author wanne
 * @since 2021-06-23
 */
public class BaseMdbReader {

	// db������
	protected BaseAssistant assistant = null;
	// �����¼��
	protected ResultRecoder recoder;
	
	public BaseMdbReader(BaseAssistant assistant) throws Exception {
//		assistant = new MdbQueryAssistant(fileAbsPath);
		this.assistant = assistant;
		recoder = new ResultRecoder();
	}
	
	/** �ر������Դ
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		assistant.close();
	}
	
	/** ��ѯ���б�ʱ�����֮ǰ�Ĳ�ѯ���*/
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
