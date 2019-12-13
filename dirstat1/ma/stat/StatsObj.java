package ma.stat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class StatsObj {

	/*
	 * TODO Remaining
	 * 
	 * <!ATTLIST format value |files-between|doubles|
	 * 	(time)|(avg-files-per-dir)|(avg-file-size)|(avg-speed-files-per-second)|
	 * 	(avg-speed-MiB-per-second) "title">
	 */
	
	private FileType type;
	private FileType unknown;
	private FileType unknown_without_extension;
	
	HashMap<Format.Type, Format> statsConf;
	
	ArrayList<File> doubles;
	ArrayList<File> link_traps;
	ArrayList<File> unreadable_dirs;
	
	long folders;
	int max_depth;
	File max_depth_link;
	double biggest_folder_MiB;
	File biggest_folder_link;
	int biggest_folder_files;
	File biggest_folder_files_link;
	
	private long start;
	
	public StatsObj() {
		super();
		start = System.currentTimeMillis();
		type = null;
		statsConf = new HashMap<Format.Type, Format>();

		unknown = new FileType("Unknown Type");
		unknown.setSort(FileType.Sort.CATEGORY);
		unknown.setStats(Format.Type.BIG);
		unknown_without_extension = new FileType("Unknown Type Without Extension");
		unknown_without_extension.setSort(FileType.Sort.CATEGORY);
		unknown_without_extension.setStats(Format.Type.BIG);
		
		doubles = new ArrayList<File>();
		link_traps = new ArrayList<File>();
		unreadable_dirs = new ArrayList<File>();
		folders = 0;
		max_depth = 0;
		max_depth_link = null;
		biggest_folder_MiB = 0.0d;
		biggest_folder_link = null;
		biggest_folder_files = 0;
		biggest_folder_files_link = null;
	}
	
	void initFileType(String title) {
		type = new FileType(title);
		unknown.setSupertype(type);
		unknown_without_extension.setSupertype(type);
	}
	
	FileType getType() {
		return type;
	}

	FileType getUnknwonType() {
		return unknown;
	}
	
	FileType getUnknwonTypeWithoutExtension() {
		return unknown_without_extension;
	}
	
	long getTime() {
		return System.currentTimeMillis() - start;
	}
	
	public String toString() {
		return "TRAPS\n " + java.util.Arrays.toString(link_traps.toArray()) + "\nFILETYPES \n" + type.toString() + "\nFORMATS \n" +  statsConf.toString() + "\nUNKNOWN\n" + unknown.toString();
	}

}
