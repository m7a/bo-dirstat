package ma.stat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;


public class Main {
	
	// TODO IMPLEMENT MISSING FUNCTIONS REq
	
	private PrintWriter transcript;
	
	public Main(String[] args) {
		super();
		final File path;
		if(args.length == 3) {
			try {
				transcript = new PrintWriter(new FileWriter(args[1]));
				printCopyright(transcript);
				System.out.println("Transcript goes to " + args[1]);
			} catch(IOException ex) {
				System.err.println("Error: Could not write transcript to " + args[1]);
				ex.printStackTrace();
			} finally {
				path = new File(args[2]);
				System.out.println();
			}
		} else {
			transcript = null;
			path = new File(args[1]);
		}
		if(!path.exists()) {
			output("Error: " + path.getAbsolutePath() + " does not exit.");
			exit(1);
		}
		File conf = new File("conf.xml");
		final StatsObj obj;
		try {
			obj = new ConfigurationParser(this).parse(conf);
		} catch(Exception ex) {
			output("Error: Could not read configuration: " + ex.toString());
			exit(1);
			return;
		}
		final FileScanner scanner = new FileScanner();
		switch(args[0].charAt(0)) {
			case 's': {
				output("Creating statistics for " + path.getAbsolutePath() + "...");
				final Runtime runtime = Runtime.getRuntime();
				Thread status = new Thread() {
					
					private String mibFree() {
						return (int)(runtime.freeMemory() / 1024.0f / 1024.0f) + " MiB free";
					}
					
					public void run() {
						long prev = 0;
						long now;
						while(!isInterrupted()) {
							now = scanner.getProgressing();
							output("scan() " + scanner.getProgressing() + ", (" + (now - prev) + "/s), " + mibFree());
							prev = now;
							try {
								sleep(1000);
							} catch(InterruptedException ex) {
								break;
							}
						}
						output("Progressed " + scanner.getProgressing() + " scans, " + mibFree() + "\n");
					}
				};
				status.start();
				scanner.scan(path, obj);
				status.interrupt();
				
				FileType types = obj.getType();
				types.addSubtype(obj.getUnknwonType());
				types.addSubtype(obj.getUnknwonTypeWithoutExtension());
				types.fillStats();
				types.print(this, obj);
				
				break;
			}
			case 'd': {
				output("Assuming file type of " + path.getAbsolutePath() + "...\n");
				FileType type = scanner.determineType(path, obj.getType());
				if(type != null) {
					Iterator<FileType> tree = type.getTreeType();
					String pre = "";
					String post = "";
					FileType cType;
					while(tree.hasNext()) {
						cType = tree.next();
						if(cType.getSort() == FileType.Sort.CATEGORY) {
							post = "";
						} else {
							post = " (" + cType.getSort() + ": " + cType.getPattern() + ")";
						}
						output(pre + cType.getTitle() + post);
						pre += ' ';
					}
				} else {
					output("Unknown Type");
				}
				break;
			}
			case 'o': {
				output("Error: This is not implemented yet!");
				break;
			}
			default: {
				output("Try \"java ma.stat.Main --help\" for help.");
			}
		}
		exit(0);
	}
	
	void exit(int status) {
		if(transcript != null) {
			transcript.close();
		}
		System.exit(status);
	}
	
	void output(String data) {
		if(transcript != null) {
			transcript.println(data);
		}
		System.out.println(data);
	}
	
	private static void printCopyright(Appendable out) throws IOException {
		out.append("Ma_Sys.ma Dir Stat 0.1.0.1, Copyright (c) 2011 Ma_Sys.ma.\n");
		out.append("For further info send an e-mail to Ma_Sys.ma@web.de.\n\n");
	}
	
	public static void main(String[] args) {
		try {
			printCopyright(System.out);
		} catch(IOException ex) {
			System.err.println("Could not write copyright, is your terminal working correctly?");
			ex.printStackTrace();
		}
		if(args.length < 2) {
			System.out.println("USAGE: java ma.stat.Main <scan|decide|open> [transcript] <PATH>");
			System.out.println();
			System.out.println("scan    Scans the given <PATH> and prints out statistics.");
			System.out.println("        Manipulate these statistics using the configuration file conf.xml.");
			System.out.println("        The output can also be written to [transcript] (if given).");
			System.out.println("decide  Tries to decide the type of the given PATH.");
			System.out.println("open    Tries to open the given <PATH> with the associated program.");
		} else {
			new Main(args);
		}
	}
	
}
