package cc.wanforme.mdbexcel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import cc.wanforme.mdbexcel.to.SimConfig;
import cc.wanforme.mdbexcel.to.ToTool;

@SpringBootApplication
public class MdbToExcel implements CommandLineRunner {
	private final Environment env;

	public MdbToExcel(Environment env) {
		this.env = env;
	}

	public Environment getEnv() {
		return env;
	}

	public static void main(String[] args) {
		SpringApplication.run(MdbToExcel.class, args).getEnvironment();
	}

	@Override
	public void run(String... args) throws Exception {
		SimConfig config = ToTool.readConfig(args);
		if(config != null) {
			ToTool.convertMultiTable(config);
		}
	}

}
