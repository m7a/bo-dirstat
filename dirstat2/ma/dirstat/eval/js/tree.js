/*
 * Ma_Sys.ma DirStat 2.0.0.0 TreeView, Copyright (c) 2014 Ma_Sys.ma.
 * For further information send an e-mail to Ma_Sys.ma@web.de.
 */

function TreeView() { }
TreeView.prototype = new ViewJS();
var ref = TreeView.prototype;

ref.sep      = null;
ref.root     = null;
ref.add2Root = null;

ref.create = function() {
	this.sep          = java.lang.Character.toString(this.getMeta().sep);
	this.root         = this.createNode(null);
	this.root.id      = "FS Tree";
	this.root.pattern = "/";
	this.add2Root     = false;
	this.populateRoot();
}

ref.populateChildren = function(node) {
	if(node.userdata == null)
		this.populateRoot();
	else
		this.populateSub(node);
}

ref.populateRoot = function() {
	this.root.createChildren();

	this.add2Root = true;
	var paths = this.getMeta().paths;
	for(var i = 0; i < paths.length; i++) {
		var node = this.createNode(paths[i]);
		node.id = paths[i];
		node.pattern = "scan path";
		this.populateSub(node);
		this.root.children.add(node);
	}
	this.add2Root = false;

	this.root.setReady();
}

ref.populateSub = function(node) {
	var query = this.prepareSelect(
		"WITH tmp AS ( " +
			"SELECT " +
				"SUBSTRING(" +
					"SUBSTRING(files.path FROM ?) " +
					"FROM ?" +
				") AS entry," +
				"COUNT(CASE " +
					"WHEN files.reg = TRUE " +
					"THEN 1 " +
					"ELSE NULL " +
				"END) AS num_files, " +
				"SUM(files.file_size) AS ssize, " +
				"COUNT(etype) AS num_errors, " +
				"COUNT(CASE " +
					"WHEN files.file_size = 0 AND "+
						"files.reg = TRUE " +
					"THEN 1 " +
					"ELSE NULL " +
				"END) AS empty, " +
				"MAX(files.file_size) AS biggest_bytes " +
			"FROM files " +
			"LEFT JOIN errors ON files.scan = errors.scan AND " +
						"files.path = errors.path " +
			"WHERE files.path LIKE ? " +
			"GROUP BY entry " +
		") " +
		"SELECT " +
			"tmp.entry, tmp.num_files, tmp.ssize, " +
			"tmp.num_errors, tmp.empty, tmp.biggest_bytes, " +
			"files.path AS bgst " +
		"FROM tmp " +
		"LEFT JOIN files ON files.file_size = tmp.biggest_bytes AND "+
				"files.path LIKE CONCAT(?, tmp.entry, '%') " +
		"WHERE ? " +
		"ORDER BY tmp.entry ASC "
	);

	var fn   = node.userdata.toString();
	var fnlp = fn.length() + 1;
	var fnr;
	if(fn.endsWith(this.sep)) {
		fnr = fn;
	} else {
		fnlp++;
		fnr = fn.concat(this.sep);
	}
	var pos = 1;

	query.setInt(pos++, fnlp);

	if(this.sep == "/")
		query.setString(pos++, "^[^/]*");
	else if(this.sep == "\\")
		query.setString(pos++, "^[^\\\\]*");
	else
		throw new Packages.ma.tools.util.NotImplementedException(
					"Unknown separator: " + this.sep);

	pos = this.appendFilterValues(query, pos);
	query.setString(pos++, this.escapeLike(fn).concat("%"));
	query.setString(pos++, this.escapeLike(fnr));
	pos = this.appendFilterValues(query, pos);

	var result = query.executeQuery();
	node.createChildren();
	var prev = null;

	while(result.next()) {
		var name = result.getString("entry");
		if(name == null || name.equals("") || name == prev)
			continue;
		var child = this.createNode(new java.lang.String(fnr + name));
		child.id = name;
		child.pattern = "(sub)";
		this.addToNode(child, result);
		if(node.pattern.equals("scan path"))
			this.addToNode(node, result);
		if(this.add2Root)
			this.addToNode(this.root, result);
		node.children.add(child);
		prev = name;
	}

	query.close();
	node.setReady();
}

ref.createFilter = function(node) {
	if(node.userdata == null)
		return this.createDefaultFilter("TRUE", [ ]);
	var path = node.userdata.toString();
	if(!path.endsWith(this.sep))
		path = path.concat(this.sep);
	return this.createDefaultFilter("files.path LIKE ?", [
		new java.lang.String(this.escapeLike(path) + "%")
	]);
}

function create_view() { return new TreeView(); }
