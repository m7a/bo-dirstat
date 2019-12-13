package ma.dirstat;

import java.util.Properties;

import java.sql.Connection;
import java.sql.DriverManager;

import ma.tools2.args.*;

class DBConverter implements TypeConverter<Connection> {

	DBConverter() {
		super();
	}

	public void parse(InputParameter param, Parameter<Connection> into)
					throws ParameterFormatException {
		String raw = param.getCurrentValue();
		Properties props = new Properties();

		if(raw.startsWith("INSECURE:"))
			raw = raw.substring(9);
		else
			props.setProperty("ssl", "true");

		int atpos, colonpos;
		// lastIndex => allows passwords with '@' chars
		if((atpos = raw.lastIndexOf('@')) != -1) {
			String login = raw.substring(0, atpos);
			if((colonpos = login.indexOf(':')) != -1) {
				props.setProperty("password", login.substring(
								colonpos + 1));
				login = login.substring(0, colonpos);
			}
			props.setProperty("user", login);
			raw = raw.substring(atpos + 1);
		}
		
		try {
			into.setValue(createConnection(raw, props));
		} catch(Exception ex) {
			throw new ParameterFormatException(
						"Failed to set database.", ex);
		}
	}

	private static Connection createConnection(String path,
					Properties props) throws Exception {
		Class.forName("org.postgresql.Driver"); // Initialize driver
		String url = "jdbc:postgresql://" + path + "/" + Args.DB_NAME;
		return DriverManager.getConnection(url, props);
	}

	static Connection createDefaultConnection() throws Exception {
		return createConnection("127.0.0.1:5432", new Properties());
	}

	public String getUsagePattern() {
		return "[INSECURE:]user:password@host:port";
	}

}
