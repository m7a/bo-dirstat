/*
 * Ma_Sys.ma DirStat 2.0.0.0 Simple Filter By Extension,
 * Copyright (c) 2014 Ma_Sys.ma.
 * For further info send an e-mail to Ma_Sys.ma@web.de.
 */

function ExtensionView() { }
ExtensionView.prototype = new ViewJS();
var ref = ExtensionView.prototype;

ref.root = null;

ref.create = function() {
	var query = this.createExtensionQuery();
	var result = query.executeQuery();

	this.root = this.createNode(null);
	this.root.id = "Extension";
	this.root.pattern = "*.*";
	this.root.createChildren();

	var prev_name = null;
	while(result.next()) {
		var name = result.getString("ext");
		if(name == prev_name)
			continue;
		var child = this.createNode(new java.lang.String(name));
		child.id = name;
		child.pattern = name == "N_EXT"? "*": "*." + name;
		this.addToNode(child, result);
		this.addToNode(this.root, result);
		child.createChildren();
		child.setReady();
		this.root.children.add(child);
		prev_name = name;
	}

	query.close();
	this.root.setReady();
}

ref.createExtensionQuery = function() {
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

ref.createFilter = function(node) {
	if(node.userdata == null)
		return this.createDefaultFilter("TRUE", []);
	else
		return this.createDefaultFilter("files.ext = ?",
							[ node.userdata ]);
}

function create_view() { return new ExtensionView(); }
