package ma.tools2.text;

import java.io.IOException;
import java.io.BufferedWriter;

/**
 * @version 1.1.0.1
 * @since Tools 1.0
 */
public class TextTable {
	
	private String[][] data;
	private int[] cell_max;
	
	public TextTable(Object[][] data) {
		cell_max = new int[data[0].length];
		
		for(int j = 0; j < data[0].length; j++) {
			int longest = 0;
			for(int i = 0; i < data.length; i++) {
				int len = data[i][j].toString().length();
				if(len > longest)
					longest = len;
			}
			cell_max[j] = longest;
		}
		
		this.data = new String[data.length][data[0].length];
		for(int i = 0; i < data[0].length; i++)
			for(int j = 0; j < data.length; j++)
				this.data[j][i] = createTableEntry(cell_max[i],
							data[j][i].toString()); 
	}
	
	private static String createTableEntry(int length, String entry) {
		int entryLength = entry.length();
		StringBuffer entry_buf = new StringBuffer(" " + entry);
		for(int i = 0; i < (length - entryLength); i++)
			entry_buf.append(" ");
		entry_buf.append(" ");
		return entry_buf.toString();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < cell_max.length; j++)
				buf.append(data[i][j]);
			buf.append("\n");
		}
		return buf.toString();
	}

	public void writeTo(BufferedWriter out) throws IOException {
		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < cell_max.length; j++)
				out.write(data[i][j]);
			out.newLine();
		}
	}

}
