package ma.stat;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Format {

	private String title;
	private String value;
	private Type describes;
	private String data;
	private LinkedList<Format> subFormats;
	
	private Format superformat;
	
	public Format(String title) {
		super();
		this.title  = title;
		value       = null;
		describes   = null;
		data        = null;
		subFormats  = null;
		superformat = null;
	}
	
	void addSubFormat(Format format) {
		if(subFormats == null) {
			subFormats = new LinkedList<Format>();
		}
		subFormats.add(format);
	}
	
	void setValue(String value) { this.value = value; }
	void setDescribes(Type describes) { this.describes = describes; }
	void setSuperformat(Format superformat) { this.superformat = superformat; }
	void setData(String data) { this.data = data; }
	
	String getTitle() { return title; }
	String getData() { return data; }
	boolean hasSubFormats() { return subFormats != null; }
	String getValue() { return value; }
	Type getDescribes() { return describes; }
	Format getSuperformat() { return superformat; }
	Iterator<Format> subFormatsList() { return subFormats.iterator(); }
	
	private String formatMib(double mib) {
		return String.format("%.2f MiB", mib);
	}
	
	static ArrayList<String> cll1e(String in) {
		ArrayList<String> ret = new ArrayList<String>(1);
		ret.add(in);
		return ret;
	}
	
	ArrayList<ArrayList<String>> format(FileType type, StatsObj obj) {
		ArrayList<ArrayList<String>> tableContents = new ArrayList<ArrayList<String>>();
		Iterator<Format> subs = subFormatsList();
		Format cf;
		ArrayList<String> cList;
		String val;
		while(subs.hasNext()) {
			cf = subs.next();
			cList = new ArrayList<String>();
			tableContents.add(cList);
			cList.add(cf.getTitle());
			val = cf.getValue();
			if(val.equals("files")) {
				cList.add(String.valueOf(type.files));
			} else if(val.equals("size")) {
				cList.add(formatMib(type.size_MiB));
			} else if(val.equals("unreadable")) {
				cList.add(String.valueOf(type.unreadable));
			} else if(val.equals("doubles")) {
				cList.add("?");
			} else if(val.equals("zero-byte")) {
				cList.add(String.valueOf(type.zero_byte_list.size()));
				if(cf.hasSubFormats()) {
					Iterator<File> allFiles = type.zero_byte_list.iterator();
					while(allFiles.hasNext()) {
						tableContents.add(cll1e(" * " + allFiles.next().getAbsolutePath()));
					}
				}
			} else if(val.equals("non-files")) {
				cList.add(String.valueOf(type.non_files.size()));
				Iterator<File> allFiles = type.non_files.iterator();
				if(cf.hasSubFormats()) {
					while(allFiles.hasNext()) {
						tableContents.add(cll1e(" * " + allFiles.next().getAbsolutePath()));
					}
				}
			} else if(val.equals("biggest-file")) {
				cList.add(formatMib(type.biggest_file_MiB));
				if(cf.hasSubFormats() && type.biggest_file_link != null) {
					cList.add(type.biggest_file_link.getAbsolutePath());
				}
			} else if(val.equals("executable")) {
				cList.add(String.valueOf(type.executable));
			} else if(val.equals("readonly")) {
				cList.add(String.valueOf(type.readonly));
			} else if(val.equals("folders")) {
				cList.add(String.valueOf(obj.folders));
			} else if(val.equals("max-depth")) {
				cList.add(String.valueOf(obj.max_depth));
				if(cf.hasSubFormats()) {
					tableContents.add(cll1e(obj.max_depth_link.getAbsolutePath()));
				}
			} else if(val.equals("hidden")) {
				cList.add(String.valueOf(type.hidden));
			} else if(val.equals("link-traps")) {
				cList.add(String.valueOf(obj.link_traps.size()));
				Iterator<File> allFiles = obj.link_traps.iterator();
				if(cf.hasSubFormats()) {
					while(allFiles.hasNext()) {
						tableContents.add(cll1e(" * " + allFiles.next().getAbsolutePath()));
					}
				}
			} else if(val.equals("unreadable-dirs")) {
				cList.add(String.valueOf(obj.unreadable_dirs.size()));
				Iterator<File> allFiles = obj.unreadable_dirs.iterator();
				if(cf.hasSubFormats()) {
					while(allFiles.hasNext()) {
						tableContents.add(cll1e(" * " + allFiles.next().getAbsolutePath()));
					}
				}
			} else if(val.equals("time")) {
				cList.add(String.valueOf(obj.getTime()));
			} else if(val.equals("avg-files-per-dir")) {
				cList.add(String.valueOf(type.files / obj.folders));
			} else if(val.equals("avg-file-size")) {
				cList.add(formatMib(type.size_MiB / type.files));
			} else if(val.equals("avg-speed-files-per-second")) {
				cList.add(String.valueOf(type.files / obj.getTime()));
			} else if(val.equals("avg-speed-MiB-per-second")) {
				cList.add(formatMib(type.size_MiB / obj.getTime()));
			} else {
				cList.add(val);
			}
		}
		return tableContents;
	}
	
	public String toString() {
		StringBuffer ret = new StringBuffer("Format ");
		ret.append(title);
		ret.append(": ");
		ret.append(value);
		ret.append(", ");
		ret.append(describes);
		ret.append(", ");
		ret.append(data);
		ret.append('\n');
		if(hasSubFormats()) {
			ret.append("Sub formats List\n");
			Iterator<Format> subTypes = subFormatsList();
			while(subTypes.hasNext()) {
				ret.append(subTypes.next().toString());
			}
			ret.append("Sub formats List end\n");
		}
		return ret.toString();
	}
	/*
	public String format(Type stats, FileType type) {
		Format fmt = null;
		Iterator<Format> subs = subFormatsList();
		while(subs.hasNext()) {
			Format format = subs.next();
			if(format.describes == stats) {
				fmt = format;
				break;
			}
		}
		if(fmt == null) {
			return "no format defined";
		} else {
			
		}
		return null;
	}
	*/
	public enum Type { 
		
		END, BIG, SMALL, SUB;
		
		static Type typeForString(String typeString) {
			if(typeString.equals("end")) {
				return END;
			} else if(typeString.equals("big")) {
				return BIG;
			} else if(typeString.equals("small")) {
				return SMALL;
			} else {
				return SUB;
			}
		}
		
	}
	
}
