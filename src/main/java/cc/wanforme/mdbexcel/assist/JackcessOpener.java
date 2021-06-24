package cc.wanforme.mdbexcel.assist;

import java.io.File;
import java.io.IOException;

import com.healthmarketscience.jackcess.CryptCodecProvider;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import net.ucanaccess.jdbc.JackcessOpenerInterface;

/**
 * @author wanne
 * @since 2021-06-22
 */
public class JackcessOpener implements JackcessOpenerInterface{

	@Override
	public Database open(File file, String pwd) throws IOException {
		 DatabaseBuilder builder = new DatabaseBuilder(file);
	     builder.setAutoSync(false);
	     builder.setCodecProvider(new CryptCodecProvider(pwd));
	     builder.setReadOnly(false);
	     return builder.open();
	}

}
