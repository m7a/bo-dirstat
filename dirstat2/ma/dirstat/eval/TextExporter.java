package ma.dirstat.eval;

import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;

import java.text.SimpleDateFormat;

import ma.tools2.text.TextTable;

import ma.dirstat.Args;
import ma.dirstat.FileSize;

class TextExporter {

	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

	private final ViewNode root;
	private final DirStatMeta meta;

	TextExporter(ViewNode root, DirStatMeta meta) {
		super();
		this.root = root;
		this.meta = meta;
	}

	void export(File file) throws IOException {
		try(BufferedWriter out = new BufferedWriter(new FileWriter(file
									))) {
			export(out);
		}
	}

	private void export(BufferedWriter out) throws IOException {
		writeHeader(out);
		writeMeta(out);
		writeTable(out);
	}

	private static void writeHeader(BufferedWriter out) throws IOException {
		out.write("GENERATED DIR STAT 2 REPORT");
		out.newLine();
		out.newLine();
		out.write("This is a generated report file created by");
		out.newLine();
		out.write(Args.NAME + " " + Args.VERSION + ", Copyright (c) " +
					Args.COPYRIGHT_YEARS + " Ma_Sys.ma.");
		out.newLine();
		out.write("For further info send an e-mail to " +
							"Ma_Sys.ma@web.de.");
		out.newLine();
		out.write("The copyright statement only applies to the " +
					"program, not the (report) data.");
		out.newLine();
		out.newLine();
	}

	private void writeMeta(BufferedWriter out) throws IOException {
		out.write("METADATA");
		out.newLine();
		out.newLine();

		writeGeneralMetaLine(out);
		writePaths(out);
		writeConfigurationInformation(out);
		writeOSInformation(out);

		out.newLine();
	}

	private void writeGeneralMetaLine(BufferedWriter out)
							throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		out.write("Scan " + meta.id + " \"" + meta.name +
				"\", created " + sdf.format(meta.creation) +
				" with separator '" + meta.sep + "'");
		out.newLine();
	}

	private void writePaths(BufferedWriter out) throws IOException {
		out.write("Scan root paths");
		out.newLine();
		for(String i: meta.paths) {
			out.write(" * " + i);
			out.newLine();
		}
	}

	private void writeConfigurationInformation(BufferedWriter out)
							throws IOException {
		if(meta.conf.exists || meta.conf.owner || meta.conf.hidden ||
				meta.conf.r || meta.conf.w || meta.conf.x) {
			out.write("Special data included within the scan");
			out.newLine();
			wit(out, "File existence",         meta.conf.exists);
			wit(out, "Owner usernames",        meta.conf.owner);
			wit(out, "Hidden attribute",       meta.conf.hidden);
			wit(out, "Read permissions",       meta.conf.r);
			wit(out, "Write permissions",      meta.conf.w);
			wit(out, "Executable permissions", meta.conf.x);
		}
	}

	private static void wit(BufferedWriter out, String title, boolean value)
							throws IOException {
		if(value) {
			out.write(" * " + title);
			out.newLine();
		}
	}

	private void writeOSInformation(BufferedWriter out) throws IOException {
		out.write("Scan by " + meta.user_name + " (" + meta.user_home +
						") on " + meta.os_name +
						", Version " + meta.os_version);
		out.newLine();
	}

	private void writeTable(BufferedWriter out) throws IOException {
		out.write("TREE TABLE");
		out.newLine();
		out.newLine();

		ArrayList<String[]> rows = new ArrayList<String[]>();
		addTitles(rows);
		radd2rows(rows, root, 0);

		TextTable tbl = new TextTable(rows.toArray(
						new String[rows.size()][]));
		tbl.writeTo(out);

		out.newLine();
		out.write("END OF REPORT");
	}

	private static void addTitles(ArrayList<String[]> rows) {
		String[] titles = new String[DirStatTree.COLUMNS.length + 1];
		titles[0] = "Path";
		System.arraycopy(DirStatTree.COLUMNS, 0, titles, 1,
						DirStatTree.COLUMNS.length);
		rows.add(titles);
	}

	private static void radd2rows(ArrayList<String[]> rows, ViewNode root,
								int level) {
		rows.add(new String[] {
			createRowName(root, level),
			root.pattern,
			String.valueOf(root.files),
			FileSize.format(root.size),
			String.valueOf(root.errors),
			String.valueOf(root.empty),
			(root.biggestBytes == -1? "N/A":
					FileSize.format(root.biggestBytes)),
			(root.biggestPath == null? "N/A": root.biggestPath),
		});

		if(root.getState() == State.READY) {
			int nextLevel = level + 1;
			for(ViewNode i: root.children)
				radd2rows(rows, i, nextLevel);
		}
	}

	private static String createRowName(ViewNode root, int level) {
		char[] indentation = new char[level * 2];
		Arrays.fill(indentation, ' ');
		String name = new String(indentation);
		if(level != 0) // * := loading, o := ok
			name += (root.getState() == State.READY? "o ": "* ");
		return name + root.id;
	}

}
