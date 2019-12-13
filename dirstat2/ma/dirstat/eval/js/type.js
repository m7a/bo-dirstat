/*
 * Ma_Sys.ma DirStat 2.0.0.0 Filter by File Type, Copyright (c) 2014 Ma_Sys.ma.
 * For further info send an e-mail to Ma_Sys.ma@web.de.
 */

function TypeView() { }
TypeView.prototype = new ViewJS();
var ref = TypeView.prototype;

ref.root = null;

ref.create = function() {
	this.createFromStructure(this.createCountStructure());
}

ref.createCountStructure = function() {
return [
{ title: "Documents", match: [
	{ title: "Professional Documents", match: [
		{ title: "Portable Document Format",      match: "pdf" },
		{ title: "Post Script",                   match: "ps" },
		{ title: "Encapsulated Post Script",      match: "eps" },
		{ title: "TeX", match: [
			{ title: "TeX Output",            match: "dvi" },
			{ title: "TeX (often LaTeX)",     match: "tex" },
			{ title: "BibTeX Bibliography",   match: "bib" },
		]}
	]},
	{ title: "Microsoft Office", match: [
		{ title: "Old", match: [
			{ title: "Document",              match: "doc" },
			{ title: "Table",                 match: "xls" },
			{ title: "Presentation",          match: "ppt" },
		]},
		{ title: "New", match: [
			{ title: "Document",              match: "docx" },
			{ title: "Table",                 match: "xlsx" },
			{ title: "Presentation",          match: "pptx" },
		]},
	]},
	{ title: "Rich Text Format",                      match: "rtf" },
	{ title: "OpenOffice.org or LibreOffice", match: [
		{ title: "Star Office", match: [
			{ title: "Drawing",               match: "sxd" },
			{ title: "Table",                 match: "sxc" },
		]},
		{ title: "Open Document Text",            match: "odt" },
		{ title: "Table",                         match: "ods" },
		{ title: "Graphics",                      match: "odg" },
		{ title: "Presentation",                  match: "odp" },
	]},
	{ title: "Plain Text", match: [
		{ title: "Checksums", match: [
			{ title: "MD5", match: [ "md5", "md5sums" ] },
			{ title: "SHA1",                  match: "sha" },
		]},
		{ title: "Logs",                          match: "log" },
		{ title: "Comma Separated Value",         match: "csv" },
		{ title: "Unspecified Text file", match: [ "txt", "scp",
								"wtx" ]},
		{ title: "Dictionary",                    match: "dict" },
	]},
	{ title: "Certificates and Signatures", match: [
		{ title: "Signature/Catalogue",           match: "cat" },
		{ title: "Security certificate", match: [ "cer", "crt", "dfb"
									]},
		{ title: "Certificate lock list",         match: "crl" },
		{ title: "Certificate requirement",       match: "p10" },
		{ title: "Private Information Exchange", match: [ "p12", "pfx"
									]},
		{ title: "Digital ID File",               match: "p7c" },
		{ title: "PKCS #7 Certificate", match: [ "p7r", "p7b", "spc" ]},
		{ title: "PKCS #7 Signature",             match: "p7s" },
		{ title: "Pretty Good Privacy",           match: "pgp" },
		{ title: "GNU Privacy Guard",             match: "gpg" },
	]},
	{ title: "E-Mail file",                           match: "eml" },
]},
{ title: "Media", match: [
	{ title: "Image", match: [
		{ title: "Bitmap", match: [
			{ title: "Portable Network Graphics", match: "png" },
			{ title: "Graphics Interchange Format", match: "gif" },
			{ title: "Windows Bitmap",        match: "bmp" },
			{ title: "Windows Icon",          match: "ico" },
			{ title: "Truevision TGA",        match: "tga" },
			{ title: "TIFF", match: [ "tif", "tiff" ]},
			{ title: "JPEG", match: [ "jpg", "jpeg", "jfif" ]},
			{ title: "EMF",                   match: "emf" },
			{ title: "WMF",                   match: "wmf" },
			{ title: "Gimp native format",    match: "xcf" },
			{ title: "Photoshop Drawing",     match: "psd" },
			{ title: "Direct Draw Surface",   match: "dds" },
			{ title: "XPM",                   match: "xpm" },
		]},
		{ title: "Vector Graphics", match: [
			{ title: "Scalable Vector Graphics", match: "svg" },
			{ title: "DVG",                   match: "dvg" },
		]},
	]},
	{ title: "3D Model", match: [
		{ title: "OBJ",                           match: "obj" },
		{ title: "Net Immerse/Gamebryo (Mesh)",   match: "nif" },
		{ title: "Skeleton file",                 match: "skl" },
		{ title: "SKN mesh",                      match: "skn" },
		{ title: "DAE",                           match: "dae" },
		{ title: "PLY",                           match: "ply" },
		{ title: "DXF",                           match: "dxf" },
	]},
	{ title: "Audio", match: [
		{ title: "Wave",                          match: "wav" },
		{ title: "Windows Media Audio",           match: "wma" },
		{ title: "MP3",                           match: "mp3" },
		{ title: "OGG",                           match: "ogg" },
		{ title: "AIFF", match: [ "aifc", "aiff", "aif" ]},
		{ title: "AU", match: [ "au", "snd" ]},
		{ title: "CD Audio Track",                match: "cda" },
		{ title: "Midi Sequence", match: [ "mid", "midi", "rmi" ]},
	]},
	{ title: "Video", match: [
		{ title: "Shockwave Flash Object", match: [ "swf", "spl" ]},
		{ title: "Flash Video",                   match: "flv" },
		{ title: "AVI",                           match: "avi" },
		{ title: "Windows Media Video",           match: "wmv" },
		{ title: "M1V",                           match: "m1v" },
		{ title: "MP2", match: [ "mp2", "mp2v" ]},
		{ title: "MP4",                           match: "mp4" },
		{ title: "MPA",                           match: "mpa" },
		{ title: "MPE",                           match: "mpe" },
		{ title: "MPV2",                          match: "mpv2" },
		{ title: "MPEG", match: [ "mpeg", "mpg" ]},
		{ title: "3GP",                           match: "3gp" },
		{ title: "VOB DVD Video Container",       match: "vob" },
		{ title: "MOV",                           match: "mov" },
		{ title: "MKV",                           match: "mkv" },
	]},
	{ title: "Descent", match: [
		{ title: "Descent 3 Room",                match: "orf" },
		{ title: "Descent 3 Level Package",       match: "mn3" },
		{ title: "Descent 3 Level",               match: "d3l" },
		{ title: "Gam Table",                     match: "gam" },
		{ title: "Descent 3 Texture",             match: "ogf" },
		{ title: "Descent 3 Animation",           match: "oaf" },
		{ title: "Descent Generic Data", match: [ "pig", "hog" ]},
		{ title: "Descent 3 Movie",               match: "mve" },
	]},
	{ title: "Windows", match: [
		{ title: "ASF Media",                     match: "asf" },
		{ title: "WM Media",                      match: "wm" },
		{ title: "Playlist",                      match: "asx" },
		{ title: "Animated Cursor",               match: "ani" },
		{ title: "Cursor",                        match: "cur" },
		{ title: "Screensaver (executable)",      match: "scr" },
	]},
	{ title: "Font", match: [
		{ title: "True Type Font",                match: "ttf" },
		{ title: "Open Type Font",                match: "otf" },
		{ title: "Type 1 Font",                   match: "pfm" },
		{ title: "Font",                          match: "fon" },
	]},
]},
{ title: "Program", match: [
	{ title: "Java", match: [
		{ title: "Source Code",                   match: "java" },
		{ title: "Class",                         match: "class" },
		{ title: "Java Archive",                  match: "jar" },
		{ title: "Lejos NXJ Binary (deprecated)", match: "nxj" },
		{ title: "Manifest file",                 match: "mf" },
	]},
	{ title: "C and C++", match: [
		{ title: "C Source Code",                 match: "c" },
		{ title: "C++ Source Code",               match: "cpp" },
		{ title: "Header file",                   match: "h" },
		{ title: "Object",                        match: "o" },
	]},
	{ title: "Delphi and Pascal", match: [
		{ title: "Pascal Source Code",            match: "pas" },
		{ title: "DCU",                           match: "dcu" },
		{ title: "DDP",                           match: "ddp" },
		{ title: "DFM",                           match: "dfm" },
	]},
	{ title: "Basic",                                 match: "bas" },
	{ title: "Assembler",                             match: "asm" },
	{ title: "Windows executable",                    match: "exe" },
	{ title: "Windows Active X control",              match: "ocx" },
	{ title: "DOS Executable",                        match: "com" },
	{ title: "Generated Binary File", match: [
		{ title: "Unknown Binary File",           match: "bin" },
		{ title: "Temporary File", match: [
			{ title: "TMP", match: [ "tmp", "temp" ]},
			{ title: "Auxiliary file",        match: "aux" },
			{ title: "Lock file",             match: "lock" },
			{ title: "Swap file",             match: "swp" },
		]},
		{ title: "Unspecified output",            match: "out" },
		{ title: "Resource File",                 match: "res" },
		{ title: "Index",                         match: "index" },
	]},
	{ title: "Library",                               match: "lib" },
]},
{ title: "Script", match: [
	{ title: "Windows", match: [
		{ title: "Batch",                         match: "bat" },
		{ title: "Batch (new)",                   match: "cmd" },
		{ title: "Quick Basic (also DOS)",        match: "qb" },
		{ title: "VB Script", match: [ "vb", "vbe", "vbs" ]},
		{ title: "Windows Script File",           match: "wsf" },
		{ title: "AutoIT V3 Script",              match: "au3" },
	]},
	{ title: "Linux", match: [
		{ title: "Setup",                         match: "run" },
		{ title: "ShellScript",                   match: "sh" },
		{ title: "AWK Script",                    match: "awk" },
		{ title: "SED Script",                    match: "sed" },
	]},
	{ title: "Python", match: [
		{ title: "Script",                        match: "py" },
		{ title: "Binary",                        match: "pyc" },
		{ title: "Object",                        match: "pyo" },
	]},
	{ title: "LUA",                                   match: "lua" },
	{ title: "Include File",                          match: "inc" },
	{ title: "VI Improved File",                      match: "vim" },
]},
{ title: "Website", match: [
	{ title: "Markup", match: [
		{ title: "HTML", match: [ "shtml", "htm", "html", "phtml" ]},
		{ title: "XHTML", match: [ "xht", "xhtml" ]},
		{ title: "XML",                           match: "xml" },
		{ title: "XML Schema Definition",         match: "xsd" },
		{ title: "Document Type Defintion",       match: "dtd" },
		{ title: "Windows HTML Application",      match: "hta" },
		{ title: "Resource Description Framework", match: "rdf" },
		{ title: "Standard Generalized Markup Language",
								match: "sgml" },
	]},
	{ title: "Stylesheet", match: [
		{ title: "Cascading Style Sheet",         match: "css" },
		{ title: "Extensible Stylesheet Language", match: "xsl" },
	]},
	{ title: "Script", match: [
		{ title: "JavaScript",                    match: "js" },
		{ title: "JavaScript Object Notation",    match: "json" },
		{ title: "PHP Hypertext Processor",       match: "php" },
		{ title: "Common Gateway Interface",      match: "cgi" },
		{ title: "Template",                      match: "tpl" },
	]},
	{ title: "HT", match: [
		{ title: "HT Access",                     match: "htaccess" },
		{ title: "HT Password",                   match: "htpasswd" },
	]},
]},
{ title: "Configuration", match: [
	{ title: "Kev-Value Association", match: [
		{ title: "Microsoft INI",                 match: "ini" },
		{ title: "Microsoft Driver/Setup Information",  match: "inf" },
		{ title: "Java Properties",               match: "properties" },
		{ title: "Preferences",                   match: "prefs" },
		{ title: "Linux Configuration File",      match: "conf" },
		{ title: "Generic configuration",         match: "cfg" },
	]},
	{ title: "Microsoft", match: [
		{ title: "Management Console",            match: "msc" },
		{ title: "System Monitor",                match: "blg" },
		{ title: "Movie Maker Projct",            match: "mswmm" },
	]},
	{ title: "Serialized Object", match: [ "dat", "ser", ]},
	{ title: "Table of Contents",                     match: "toc" },
	{ title: "Database", match: [
		{ title: "Unspecified Database",          match: "db" },
		{ title: "SQL Database Code",             match: "sql" },
		{ title: "SQLite Database",               match: "sqlite" },
	]},
	{ title: "History",                               match: "history" },
	{ title: "Project Information",                   match: "project" },
	{ title: "STNE Colony Planner File", match: [ "kpl", "kpls" ]},
	{ title: "Color Profile", match: [ "icc", "icm" ]},
	{ title: "Manifest",                              match: "manifest" },
]},
{ title: "Archive", match: [
	{ title: "Tape Archive",                          match: "tar" },
	{ title: "GZip",                                  match: "gz" },
	{ title: "GZipped Tape Archive",                  match: "tgz" },
	{ title: "ZIP",                                   match: "zip" },
	{ title: "RAR",                                   match: "rar" },
	{ title: "BZip 2",                                match: "bz2" },
	{ title: "BZipped Tape Archive",                  match: "tbz" },
	{ title: "7-Zip",                                 match: "7z" },
	{ title: "Z",                                     match: "z" },
	{ title: "AES encrypted file",                    match: "aes" },
	{ title: "XZ",                                    match: "xz" },
	{ title: "XZed Tape Archive",                     match: "txz" },
	{ title: "Encrypted XZed Cpio",                   match: "cxe" },
	{ title: "Riot Archive File (League of Legends)", match: "raf" },
	{ title: "Bethesda Archive File",                 match: "bsa" },
	{ title: "Debian Archive",                        match: "deb" },
	{ title: "Windows", match: [
		{ title: "Compiled Help File",            match: "chm" },
		{ title: "Help File (deprecated)",        match: "hlp" },	
		{ title: "Windows Installer Package",     match: "msi" },
		{ title: "Windows Installer Patch",       match: "msp" },
		{ title: "Cabinet Archive",               match: "cab" },
	]},
	{ title: "Image", match: [
		{ title: "Optical media image",           match: "iso" },
		{ title: "HDD or FDD image",              match: "img" },
		{ title: "GameBoy Image",                 match: "gb" },
		{ title: "GameBoy Advance Image",         match: "gba" },
		{ title: "Squashfs (live file system)",   match: "squashfs" },
		{ title: "Virtual Machine Image", match: [
			{ title: "VirtualBox",            match: "vdi" },
			{ title: "VM Ware",               match: "vmdk" },
		]},
		{ title: "Self-extracting Archive prototype", match: "sfx" },
	]},
]},
{ title: "Link", match: [
	{ title: "Windows",                               match: "lnk" },
	{ title: "Windows Website Link",                  match: "url" },
	{ title: "MS DOS",                                match: "pif" },
	{ title: "Linux",                                 match: "desktop" },
]},
{ title: "Windows System", match: [
	{ title: "Restored File Fragments",               match: "chk" },
	{ title: "Dynamic Link Library (shared object)",  match: "dll" },
	{ title: "System file (Drivers, MSDOS, etc.)",    match: "sys" },
	{ title: "Registry Export",                       match: "reg" },
	{ title: "Virtual Device Driver", match: [ "386", "vxd" ]},
	{ title: "Driver",                                match: "drv" },
	{ title: "Backup File",                           match: "bkf" },
	{ title: "Control Panel",                         match: "cpl" },
	{ title: "Password Backup",                       match: "psw" },
	{ title: "Windows XP Activation File",            match: "dbl" },
]},
{ title: "Backup", match: [
	{ title: "Old Files",                             match: "old" },
	{ title: "Backup file",                           match: "bak" },
	{ title: "Save files", match: [ "sav", "sa1", "sa2", "sa3", "sa4",
								"sa5", ]},
	{ title: "Deleted files",                         match: "del" },
]},
{ title: "Miscelanneous", match: [
	{ title: "The Elder Scrolls Series", match: [
		{ title: "Elder Scrolls Plugin",          match: "esp" },
		{ title: "Elder Scrolls Master",          match: "esm" },
		{ title: "Elder Scrolls Savegame",        match: "ess" },
		{ title: "Video",                         match: "bik" },
	]},
	{ title: "LaTeX STY",                             match: "sty" },
	{ title: "Linux System: Shared Object",           match: "so" },
	{ title: "Mozilla Plugin",                        match: "xpi" },
]},
{ title: "No extension",                                  match: "N_EXT" },
{ title: "Empty extension",                               match: "" },
];
}

ref.createFromStructure = function(raw) {
	this.root = this.createNode(null);
	this.root.id = "File Type";
	this.root.pattern = "*";
	this.root.createChildren();

	var working_structure = this.rwork(raw, null);
	var query = this.createExtensionQuery();
	var result = query.executeQuery();

	var unknown = this.proc(result, working_structure);
	this.convBack(working_structure, unknown);

	query.close();
	this.root.setReady();
}

ref.rwork = function(raw, par2) {
	if(Array.isArray(raw)) {
		var ret = new Array(raw.length);
		for(var i = 0; i < raw.length; i++)
			ret[i] = this.rwork(raw[i], par2);
		return ret;
	} else if(typeof(raw) === "object") {
		var obj = {
			node: this.createNode(null),
			sub: null,
			par: par2
		};
		obj.node.id = raw.title;
		obj.node.createChildren();
		if(Array.isArray(raw.match)) {
			obj.sub = this.rwork(raw.match, obj);
		} else {
			obj.sub = [ raw.match ];
		}
		return obj;
	} else {
		return raw;
	}
}

ref.createExtensionQuery = function() { // Equal to extension.js
	var query = this.prepareSelect(
		"WITH tmp AS (" +
			"SELECT " +
				"ext, COUNT(*) AS num_files," +
				"SUM(files.file_size) AS ssize," +
				"COUNT(etype) AS num_errors," +
				"COUNT(CASE WHEN files.file_size = 0 " +
					"THEN 1 ELSE NULL END) AS empty," +
				"MAX(files.file_size) AS biggest_bytes " +
			"FROM files " +
			"LEFT JOIN errors ON files.scan = errors.scan AND " +
						"files.path = errors.path " +
			"WHERE reg = TRUE " +
			"GROUP BY ext " +
		") " +
		"SELECT " +
			"tmp.ext, tmp.num_files, tmp.ssize, tmp.num_errors, " +
			"tmp.empty, tmp.biggest_bytes, files.path AS bgst " +
		"FROM tmp " +
		"LEFT JOIN files ON files.ext = tmp.ext AND " +
					"files.file_size = tmp.biggest_bytes " +
		"WHERE ? " + 
		"ORDER BY tmp.ext ASC"
	);
	var next = 1;
	next = this.appendFilterValues(query, next);
	next = this.appendFilterValues(query, next);
	return query;
}

ref.proc = function(result, working_structure) {
	var prev_name = null;
	var unknown = new java.util.HashMap();
	while(result.next()) {
		var name = result.getString("ext");
		if(name == prev_name)
			continue;
		if(!this.rproc(name, result, working_structure, null)) {
			if(unknown.containsKey(name)) {
				this.addToNode(unknown.get(name), result);
			} else {
				var node = this.createNode(null);
				node.id = name;
				node.pattern = name;
				this.addToNode(node, result);
				node.createChildren();
				node.setReady();
				unknown.put(name, node);
			}
		}
		prev_name = name;
	}
	return unknown;
}

ref.rproc = function(name, result, working_structure, par) {
	if(Array.isArray(working_structure)) {
		if(typeof(working_structure[0]) === "object") {
			for(var i = 0; i < working_structure.length; i++)
				if(this.rproc(name, result,
						working_structure[i], par))
					return true;
		} else {
			for(var i = 0; i < working_structure.length; i++)
				if(working_structure[i] == name)
					return this.bubbleUp(result, par);
		}
	} else if(typeof(working_structure) === "object") {
		return this.rproc(name, result, working_structure.sub,
							working_structure);
	} else {
		throw new ma.tools2.util.NotImplementedException();
	}
	return false;
}

ref.bubbleUp = function(result, working_structure) {
	if(working_structure == null) {
		this.addToNode(this.root, result);
		return true;
	}

	if(typeof(working_structure.sub[0]) === "object")
		working_structure.node.pattern = "...";
	else
		working_structure.node.pattern = working_structure.sub.join();
	
	this.addToNode(working_structure.node, result);

	return this.bubbleUp(result, working_structure.par);
}

ref.convBack = function(working_structure, unknown) {
	this.radd(this.root, working_structure);
	if(!unknown.isEmpty())
		this.root.children.add(this.createUnknown(unknown));
}

ref.radd = function(node_to_add_to, working_structure) {
	if(Array.isArray(working_structure)) {
		for(var i = 0; i < working_structure.length; i++)
			this.radd(node_to_add_to, working_structure[i]);
	} else if(working_structure instanceof
					Packages.ma.dirstat.eval.ViewNode) {
		node_to_add_to.children.add(working_structure);
	} else if(working_structure.node.pattern != null) {
		if(typeof(working_structure.sub[0]) === "object")
			for(var i = 0; i < working_structure.sub.length; i++)
				this.radd(working_structure.node,
						working_structure.sub[i]);
		working_structure.node.setReady();
		node_to_add_to.children.add(working_structure.node);
	}
}

ref.createUnknown = function(unknown) {
	var uroot = this.createNode(null);
	uroot.id = "Unknown Extensions";
	uroot.pattern = "...";
	uroot.createChildren();

	var iter = unknown.values().iterator();
	while(iter.hasNext()) {
		var node = iter.next();
		this.addValuesToNode(uroot, node);
		this.addValuesToNode(this.root, node);
		uroot.children.add(node);
	}

	uroot.setReady();
	return uroot;
}

ref.addValuesToNode = function(node, add) {
	node.files  += add.files;
	node.size   += add.size;
	node.errors += add.errors;
	node.empty  += add.empty;
	if(node.biggestBytes < add.biggestBytes) {
		node.biggestBytes = add.biggestBytes;
		node.biggestPath  = add.biggestPath;
	}
}

ref.createFilter = function(node) {
	if(node.pattern.equals("*"))
		return this.createDefaultFilter("TRUE", [ ]);

	var filter = this.rfilter(node, []);
	var query = "files.ext IN (";
	for(var i = 0; i < filter.length; i++) {
		if(i != 0)
			query += ",";
		query += "?";
	}
	query += ")";

	return this.createDefaultFilter(query, filter);
}

ref.rfilter = function(node, fqd) { // fqd := filter query data
	if(node.pattern.equals("...")) {
		var iter = node.children.iterator();
		while(iter.hasNext())
			fqd = this.rfilter(iter.next(), fqd);
	} else {
		var iter = node.children.iterator();
		var entries = String(node.pattern).split(",");
		for(var i = 0; i < entries.length; i++)
			fqd.push(entries[i]);
	}
	return fqd;
}

function create_view() { return new TypeView(); }
