package cc.wanforme.mdbexcel.reader.handler;

import cc.wanforme.mdbexcel.reader.ResultRecoder;

/** �����ȡ����������
 * @author wanne
 * @since 2021-06-23
 */
public interface DataHandler {

	void handleData(ResultRecoder recoder);
	
	void finished();
}
