package ma.tools2.gui;

import javax.swing.table.AbstractTableModel;

/**
 * Stellt ein Modell f&uuml;r Tabellen zur verf&uuml;gung, deren Inhalt
 * nicht ge&auml;ndert werden kann und auch vom Programm nicht mehr
 * ge&auml;ndert werden muss. Dieses Tabellenmodel ist also nur n&uuml;tzlich,
 * wenn Statische Daten in der Tabelle dargestellt werden sollen. Ansonsten
 * muss man eine neue Klasse aus dem DataTableModel ableiten. Dazu ist es
 * wichtig, den Elementaren Aufau eines DataTableModels zu kennen. <br>
 * Der Elementare Aufbau eines DataTableModels :
 * 
 * <pre>
 * packgage ma.tools.gui;
 * 
 * import javax.swing.table.AbstractTableModel;
 * 
 * public class DataTableModel extends AbstractTableModel {
 * 
 *  private final String[][] data;
 * 	private final String[] headers;
 * 
 * 	protected DataTableModel() {
 * 		super();
 * 		data = null;
 * 		headers = null;
 * 	}
 * 	
 * 	public DataTableModel(String[][] data, String[] headers) {
 * 		super();
 * 		this.data = data;
 * 		this.headers = headers;
 * 	}
 * 	
 * 	public int getColumnCount() {
 * 		return data[0].length;
 * 	}
 * 	
 * 	
 * 	public int getRowCount() {
 * 		return data.length;
 * 	}
 * 	
 * 	public boolean isChellEditable(int rowIndex, int columnIndex) {
 * 		return false;
 * 	}
 * 	
 * 	public String getColumnName(int x) {
 * 		return headers[x];
 * 	}
 * 	
 * 	public Object getValueAt(int rowIndex, int columnIndex) {
 * 		return data[rowIndex][columnIndex];
 * 	}
 * }
 * </pre>
 * 
 * Das DataTableModel ist auch sehr einfach f&uuml;r eigene Programme zu nutzen.
 * Hier ein Beispiel f&uuml;r ein kleines Programm, welches seine Daten mittels
 * DataTableModel darstellt :
 * 
 * <pre>
 * import ma.tools.gui.DataTableModel;
 * import javax.swing.JTable;
 * import javax.swing.JScrollPane;
 * import javax.swing.JOptionPane;
 * 
 * public class TTSDataTableModel {
 * 	
 * 	public static final String[] TITLES = {
 * 		&quot;TITLE 1&quot;, &quot;TITLE 2&quot;
 * 	};
 * 	public static final String[][] FIELDS = {
 * 		{ &quot;CELL A&quot;, &quot;CELL B&quot; },
 * 		{ &quot;CELL A&quot;, &quot;CELL B&quot; },
 * 		{ &quot;CELL A&quot;, &quot;CELL B&quot; },
 * 		{ &quot;CELL A&quot;, &quot;CELL B&quot; },
 * 		{ &quot;CELL A&quot;, &quot;CELL B&quot; },
 * 	};
 * 	
 * 	public static void main(String[] args) {
 * 		DataTableModel model = new DataTableModel(FIELDS, TITLES);
 * 		JTable table = new JTable(model);
 * 		JOpitonPane.showMessageDialog(null, new JScrollPane(table));
 * 	}
 * }
 * 
 * </pre>
 * 
 * @author Linux-Fan, Ma_Sys.ma
 * @version 1.1.0.0
 * @since Tools 2.0
 */
public class DataTableModel extends AbstractTableModel {
	
	/**
	 * Die im DataTableModel enthaltenen Zellendaten
	 */
	protected String[][] data;
	
	/**
	 * Die anzuzeigenden Tabellenspaltentitel
	 */
	protected String[] headers;
	
	/**
	 * Dieser Konstruktor ist nur dazu gedacht, dass man aus dieser Klasse
	 * ableitet. Dann m&uuml;ssen aber folgende Methoden Implementiert werden :
	 * <code>getColumnCount()</code>, <code>getRowCount()</code>, <code>getColumnName()</code>,
	 * <code>getValueAt</code>.
	 * Das liegt daran, dass die Felder <code>data</code> und <code>headers</code> mit
	 * <code>null</code> initialisiert werden, wodurch eine <code>NullPointerException</code> in der
	 * Tabelle entst&uuml;nde.
	 * Dieser Konstruktor wird von der Klasse LogTable verwendet.
	 * 
	 * @see javax.swing.JTable
	 * @see java.lang.NullPointerException
	 * @see ma.tools.gui.LogTable
	 */
	protected DataTableModel() {
		super();
		data    = null;
		headers = null;
	}
	
	/**
	 * Instanziert ein neues DataTableModel mit den Angegebenen Daten.
	 * 
	 * @param data
	 *            Die Daten f&uuml;r den Inhalt der Tabelle
	 * @param headers
	 *            Die Daten f&uuml;r die Spalten&uuml;berschriften der Tabelle.
	 */
	public DataTableModel(String[][] data, String[] headers) {
		super();
		this.data    = data;
		this.headers = headers;
	}
	
	/**
	 * Findet die Anzahl der Spalten heraus.
	 * 
	 * @return Die Anzahl der Spalten aus der Menge &#8469; (mit 0)..
	 */
	public int getColumnCount() {
		if(data.length > 0) {
			return data[0].length;
		} else {
			return 0;
		}
	}
	
	/**
	 * Findet die Anzahl der Zeilen heraus.
	 * 
	 * @return Die Anzahl der Spalten aus der Menge &#8469;\{0}.
	 */
	public int getRowCount() { return data.length; }
	
	/**
	 * Findet den Namen des angegebenen Spaltentitels heraus.
	 * Die Zahl kann von <code>0</code> bis <code>headers.length-1</code> gehen
	 * 
	 * @param x
	 *            Der Index der Spalte
	 * @return den Name der angegebenen Spalte
	 */
	public String getColumnName(int x) { return headers[x]; }
	
	/**
	 * Findet den Wert der Angegebenen Zelle heraus.
	 * Der Wert ist immer in Form eines String Objektes.
	 * 
	 * @param rowIndex
	 *            Der Zeilenindex (0 - getRowCount()-1)
	 * @see java.lang.String Der Spaltenindex (0 - getColumnCount()-1)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) { return data[rowIndex][columnIndex]; }
	
}
