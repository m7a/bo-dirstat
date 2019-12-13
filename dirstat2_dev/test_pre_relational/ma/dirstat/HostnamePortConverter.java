package ma.dirstat;

import ma.tools2.args.*;

class HostnamePortConverter implements TypeConverter<HostnamePort> {

	HostnamePortConverter() {
		super();
	}

	public void parse(InputParameter param, Parameter<HostnamePort> into)
					throws ParameterFormatException {
		String str = param.getCurrentValue();
		int pos = str.indexOf(':');
		if(pos == -1)
			throw new ParameterFormatException("A suitable host " +
				"and port specification includes a colon to " +
				"separate host and port.");
		String host = str.substring(0, pos);
		int port;
		try {
			port = Integer.parseInt(str.substring(pos + 1));
		} catch(NumberFormatException ex) {
			throw new ParameterFormatException(
						"Invalid port number.", ex);
		}
		into.setValue(new HostnamePort(host, port));
	}

	public String getUsagePattern() {
		return "host:port";
	}

}
