package ma.stat;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;


public class FileScanner {

	private long progressing;
	
	public FileScanner() {
		super();
		progressing = 0;
	}
	
	void scan(File file, StatsObj stats) {
		scan2(file, stats, 0);
	}
	
	private void scan2(File file, StatsObj stats, int depth) {
		progressing++;
		if(depth > stats.max_depth) {
			stats.max_depth      = depth;
			stats.max_depth_link = file;
		}
		String name = file.getName();
		if(file.isDirectory()) {
			// Link trap detector (can be disabled for better performace if it is secured that there is no link trap)
			File parent = file.getParentFile();
			if(parent != null) {
				do {
					if(name.equals(parent.getName()) && isDirContentEqual(file, parent)) {
						stats.link_traps.add(file); return;	
					}
				} while((parent = parent.getParentFile()) != null);
			}
			
			stats.folders++;
			String[] list = file.list();
			if(list == null) {
				stats.unreadable_dirs.add(file);
			} else {
				int depthPlusOne = depth + 1;
				for(int i = 0; i < list.length; i++) {
					scan2(new File(file, list[i]), stats, depthPlusOne);
				}
			}
		} else {
			String extension = extensionFor(name);
			FileType type = determineType(name, extension, stats.getType());
			if(type == null) {
				type = determineType(name, extension, stats.getUnknwonType());
				if(type == null) {
					if(extension == null) {
						type = stats.getUnknwonTypeWithoutExtension();
					} else {
						type = new FileType("*." + extension);
						type.setPattern(extension);
						type.setStats(Format.Type.SUB);
						type.setSupertype(stats.getUnknwonType());
						stats.getUnknwonType().addSubtype(type);
					}
				}
			}
			if(file.canExecute()) { type.executable++; }
			if(!file.canRead())   { type.unreadable++; }
			if(!file.canWrite())  { type.readonly++; }
			if(!file.isFile())    { type.non_files.add(file); }
			if(file.isHidden())   { type.hidden++; }
			type.files++;
			double mibSize = file.length() / 1024.0d / 1024.0d;
			if(mibSize == 0) {
				type.zero_byte_list.add(file);
			} else {
				if(mibSize > type.biggest_file_MiB) {
					type.biggest_file_MiB  = mibSize;
					type.biggest_file_link = file;
				}
				type.size_MiB += mibSize;
			}
		}
	}
	
	private boolean isDirContentEqual(File f1, File f2) {
		String[] parentList = f1.list();
		String[] subList    = f2.list();
		boolean bothNull = subList == null && parentList == null;
		if((parentList == null || subList == null) && !bothNull) {
			return false;
		} else if(bothNull) {
			return true;
		}
		// Eclipse is wrong: There cannot be any null pointer here...
		if(parentList.length == subList.length) {
			LinkedList<String> parentsNames = new LinkedList<String>();
			for(int i = 0; i < parentList.length; i++) {
				parentsNames.add(parentList[i]);
			}
			parentList = null;
			for(int i = 0; i  < subList.length; i++) {
				parentsNames.remove(subList[i]);
			}
			return parentsNames.isEmpty();
		} else {
			return false;
		}
	}
	
	private String extensionFor(String name) {
		int dotIndex = name.lastIndexOf('.') + 1;
		if(dotIndex != 0) {
			return name.substring(dotIndex);
		} else {
			return null;
		}
	}
	
	FileType determineType(File file, FileType in) {
		String name = file.getName();
		return determineType(name, extensionFor(name), in);
	}
	
	private FileType determineType(String name, String extension, FileType type) {
		if(type.getSort() == FileType.Sort.CATEGORY) {
			Iterator<FileType> subTypes = type.iterateOverSubtypes();
			while(subTypes.hasNext()) {
				FileType result = determineType(name, extension, subTypes.next());
				if(result != null) {
					return result;
				}
			}
		} else if(type.matches(name, extension)) {
			return type;
		}
		return null;
	}
	
	long getProgressing() {
		return progressing;
	}
	
}
