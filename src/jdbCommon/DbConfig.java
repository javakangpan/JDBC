package jdbCommon;

import java.io.IOException;
import java.util.Properties;

public class DbConfig {
	private static Properties pro = new Properties();
	static {
		try {
			pro.load(DbConfig.class.getResourceAsStream("../dbconfig/dbconfig.properties"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static final String URL = 
			pro.getProperty("DATABASE_URL") 
		  + pro.getProperty("SERVER_IP") + ":" 
		  + pro.getProperty("SERVER_PORT") + "/" 
		  + pro.getProperty("DATABASE_NAME");
	public static final String USER = pro.getProperty("USER");
	public static final String PASSWORD = pro.getProperty("PASSWORD");
	public static final String CLASS_NAME = pro.getProperty("CLASS_NAME");
}
