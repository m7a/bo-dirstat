package ma.dirstat.eval;

import java.util.ArrayList;

import ma.tools2.gui.DataTableModel;
import ma.tools2.util.NotImplementedException;

class KVTableModel extends DataTableModel {

	private static class KV {

		private final String k;
		private final String v;

		private KV(String k, String v) {
			super();
			this.k = k;
			this.v = v;
		}

	}

	private static final String[] HEADER = { "Key", "Value" };

	private final ArrayList<KV> data;

	KVTableModel() {
		super();
		headers = HEADER;
		data = new ArrayList<KV>();
	}

	void put(String k, Object v) {
		data.add(new KV(k, v.toString()));
	}

	void clear() {
		data.clear();
	}

	public int getColumnCount() {
		return HEADER.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		KV x = data.get(rowIndex);
		switch(columnIndex) {
		case 0:  return x.k;
		case 1:  return x.v;
		default: throw new NotImplementedException();
		}
	}

}
