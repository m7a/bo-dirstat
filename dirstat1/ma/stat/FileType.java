package ma.stat;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;


public class FileType {

	private String              title;	
	private String              pattern;
	private Sort                sort;
	private boolean             emr;
	private Format.Type         stats;
	private ArrayList<FileType> subtypes;
	
	private FileType supertype;
	
	/*
	 * <!ATTLIST format value (title|no-ext|zero-byte|non-files|folders|files-between|
	 * 	max-depth|biggest-folder-size|biggest-folder-files|biggest-file|doubles|size|
	 * 	files|time|link-traps|avg-files-per-dir|avg-file-size|avg-speed-files-per-second|
	 * 	avg-speed-MiB-per-second) "title">
	 */
	// Statistical information
	
	ArrayList<File> zero_byte_list;
	ArrayList<File> non_files;
	double          biggest_file_MiB;
	File            biggest_file_link;
	double          size_MiB;
	long            files;
	int             hidden;
	int             executable;
	int             unreadable;
	int             readonly;
	
	public FileType(String title) {
		super();
		this.title = title;
		pattern    = null;
		sort       = null;
		emr        = false;
		stats      = null;
		subtypes   = null;
		supertype  = null;
		
		zero_byte_list    = new ArrayList<File>();
		non_files         = new ArrayList<File>();
		biggest_file_MiB  = 0.0d;
		biggest_file_link = null;
		size_MiB          = 0.0d;
		files             = 0;
		hidden            = 0;
		executable        = 0;
		unreadable        = 0;
		readonly          = 0;
	}
	
	void setSort(Sort sort) { 
		this.sort = sort;
		if(sort == Sort.CATEGORY) {
			subtypes = new ArrayList<FileType>();
		}
	}
	
	Sort getSort() { 
		if(sort == null) {
			if(pattern == null) {
				sort = Sort.CATEGORY;
			} else {
				sort = Sort.EXTENSION;
			}
		}
		return sort; 
	}
	
	boolean matches(String name2, String extension2) {
		String name = name2;
		String extension = extension2;
		switch(sort) {
			case EXTENSION: {
				if(extension == null) { return false; }
				if(!emr) { extension = extension.toLowerCase(); }
				return extension.equals(pattern);
			}
			case NAME:      if(!emr) { name = name.toLowerCase(); } return name.equals(pattern);
			case REGEX:     if(!emr) { name = name.toLowerCase(); } return name.matches(pattern);
			case EXTENSION_REGEX: {
				if(extension == null) { return false; }
				if(!emr) { extension = extension.toLowerCase(); }
				return extension.matches(pattern);
			}
			case CATEGORY:  return false;
			default:        return false;
		}
	}
	
	void print(Main main, StatsObj formatter) {
		ArrayList<ArrayList<String>> unformattedTable = print(formatter);
		ArrayList<Integer> columnSizes = new ArrayList<Integer>();
		Iterator<ArrayList<String>> lines = unformattedTable.iterator();
		ArrayList<String> line;
		int width, listWidth, diff, i;
		Iterator<String> lineCnt;
		while(lines.hasNext()) {
			line = lines.next();
			width = line.size();
			if(width == 1) {
				continue;
			}
			listWidth = columnSizes.size();
			diff = width - listWidth;
			for(i = 0; i < diff; i++) {
				columnSizes.add(0);
			}
			lineCnt = line.iterator();
			i = 0;
			while(lineCnt.hasNext()) {
				int colWidth = lineCnt.next().length();
				if(columnSizes.get(i) < colWidth) {
					columnSizes.set(i, colWidth);
				}
				i++;
			}
		}
		lines = unformattedTable.iterator();
		StringBuffer lineBuf;
		String entry;
		int cnr;
		while(lines.hasNext()) {
			lineBuf = new StringBuffer();
			lineCnt = lines.next().iterator();
			cnr = 0;
			while(lineCnt.hasNext()) {
				entry = lineCnt.next();
				width = entry.length();
				lineBuf.append(entry);
				for(i = 0; i < (columnSizes.get(cnr) - width); i++) {
					lineBuf.append(' ');
				}
				lineBuf.append(' ');
				cnr++;
			}
			main.output(lineBuf.toString());
		}
	}
	
	private ArrayList<ArrayList<String>> print(StatsObj formatter) {
		ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
		if(files == 0) {
			return ret; 
		}
		ArrayList<String> sStat = new ArrayList<String>();
		sStat.add(title);
		if(pattern != null && sort != null) {
			sStat.add(sort.toChar() + "=" + pattern);
		}
		Format usedFormat = formatter.statsConf.get(stats);
		if(usedFormat == null) {
			ret.add(Format.cll1e("Warning: Format " + stats + " not defined!"));
		} else {
			if(stats == Format.Type.SUB) {
				if(sort == Sort.CATEGORY) {
					sStat.add(" ");
				}
				Iterator<ArrayList<String>> listOrginal = usedFormat.format(this, formatter).iterator();
				while(listOrginal.hasNext()) {
					Iterator<String> cList = listOrginal.next().iterator();
					sStat.add(cList.next() + "=" + cList.next());
					while(cList.hasNext()) {
						sStat.add(cList.next());
					}
				}
			}
		}
		ret.add(sStat);
		Iterator<ArrayList<String>>  osubi;
		if(sort == Sort.CATEGORY) {
			Iterator<FileType> subs = iterateOverSubtypes();
			ArrayList<ArrayList<String>> osub;
			while(subs.hasNext()) {
				osub = subs.next().print(formatter);
				osubi = osub.iterator();
				while(osubi.hasNext()) {
					ArrayList<String> cl = osubi.next();
					cl.set(0, "  " + cl.get(0));
					ret.add(cl);
				}
			}
		}
		if(usedFormat != null && stats != Format.Type.SUB) {
			osubi = usedFormat.format(this, formatter).iterator();
			while(osubi.hasNext()) {
				ret.add(osubi.next());
			}
		}
		return ret;
	}

	void fillStats() {
		if(sort == Sort.CATEGORY) {
			Iterator<FileType> subTypes = iterateOverSubtypes();
			while(subTypes.hasNext()) {
				subTypes.next().fillStats();
			}
		}
		if(supertype == null) {
			return;
		}
		supertype.zero_byte_list.addAll(zero_byte_list);
		supertype.non_files.addAll(non_files);
		if(biggest_file_MiB > supertype.biggest_file_MiB) {
			supertype.biggest_file_MiB  = biggest_file_MiB;
			supertype.biggest_file_link = biggest_file_link;
		}
		supertype.size_MiB   += size_MiB;
		supertype.files      += files;
		supertype.hidden     += hidden;
		supertype.executable += executable;
		supertype.unreadable += unreadable;
		supertype.readonly   += readonly;
	}
	
	Iterator<FileType> getTreeType() {
		ArrayDeque<FileType> ret = new ArrayDeque<FileType>();
		FileType supertype = this;
		ret.add(supertype);
		while((supertype = supertype.getSupertype()) != null)  {
			ret.add(supertype);
		}
		return ret.descendingIterator();
	}
	
	void setPattern(String pattern) { this.pattern = pattern; }
	void setExactMatchRequired() { emr = true; }
	void setStats(Format.Type stats) { this.stats = stats; }
	void addSubtype(FileType sub) { subtypes.add(sub); }
	void setSupertype(FileType supertype) { this.supertype = supertype; }
	
	String getTitle() { return title; }
	boolean isExactMatchRequired() { return emr; }
	Format.Type getStats() { return stats; }
	Iterator<FileType> iterateOverSubtypes() { return subtypes.iterator(); }
	FileType getSupertype() { return supertype; }
	String getPattern() { return pattern; }
	
	public enum Sort { 
		
		CATEGORY        ("category"), 
		EXTENSION       ("extension"), 
		REGEX           ("regex"),
		EXTENSION_REGEX ("special-extension"),
		NAME            ("name");

		private static final Sort[] sorts = Sort.values();
		
		private final String title;
		
		Sort(String title) {
			this.title = title;
		}
		
		static Sort sortForString(String sortString) {
			for(int i = 0; i < sorts.length; i++) {
				if(sorts[i].title.equals(sortString)) {
					return sorts[i];
				}
			}
			if(sortString.equals("extension-regex")) {
				return EXTENSION_REGEX;
			}
			throw new IllegalArgumentException("Sort string \"" + sortString + "\" not defined.");
		}
		
		public String toString() {
			return title;
		}
		
		public char toChar() {
			return title.charAt(0);
		}
		
	}

}
