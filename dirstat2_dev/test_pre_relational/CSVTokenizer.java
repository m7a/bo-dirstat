import java.util.*;
import java.io.*;

public class CSVTokenizer implements Closeable {

	private final BufferedReader in;
	private final char sep;

	private char[] line;

	public CSVTokenizer(BufferedReader in, char sep) throws IOException {
		super();
		this.in = in;
		this.sep = sep;
		populate();
	}

	private void populate() throws IOException {
		String sl;
		do {
			String lineS = in.readLine();
			if(lineS == null)
				line = null;
			else
				line = lineS.trim().toCharArray();
		} while(line != null && line.length == 0);
	}

	public boolean hasNext() throws IOException {
		return line != null;
	}

	public List<String> next() throws IOException {
		if(!hasNext())
			return null;

		ArrayList<String> ret = new ArrayList<String>();
		String cache = "";

		boolean instr = false;
		for(int i = 0; i < line.length; i++) {
			if(line[i] == '"') {
				instr = !instr;
			} else if(line[i] == sep) {
				if(instr) {
					cache += ',';
				} else {
					ret.add(cache);
					cache = "";
				}
			} else {
				cache += line[i];
			}
		}

		ret.add(cache);
		populate();
		return ret;
	}

	public void close() throws IOException {
		in.close();
	}

}
