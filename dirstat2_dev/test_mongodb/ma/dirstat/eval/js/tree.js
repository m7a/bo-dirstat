/*
 * Ma_Sys.ma DirStat 2.0.0.0 TreeView, Copyright (c) 2014 Ma_Sys.ma.
 * For further information send an e-mail to Ma_Sys.ma@web.de.
 */

function TreeView() { }
TreeView.prototype = new ViewJS();
var ref = TreeView.prototype;

ref.root = null;
ref.sep  = null;

ref.create = function() {
	this.root = this.createFSTreeNode(this.findMeta(), "FS Tree", "/", [ ]);
	this.sep = this.root.userdata.get("sep");
}

ref.createFSTreeNode = function(user_object, id, pattern, filters) {
	var stats = this.aggregate(filters.concat(this.rquery([
		{ $sort: { size: 1 } },
		{ $group: {
			_id: "all",
			files: { $sum: 1 },
			size:  { $sum: "$size" },
			errors: { $sum: { $cond: [
				{ $eq: [ { $ifNull: [ "$error", "no_error" ] },
								"no_error" ] },
				0,
				1
			] } },
			empty: { $sum: { $cond: [ { $eq: [ "$size", 0 ] },
								1, 0, ] } },
			biggest_path:  { $last: "$_id" },
			biggest_bytes: { $last: "$size" },
		} },
	])));
	var node = this.createNode(user_object);
	node.id = id;
	node.pattern = pattern;
	if(stats.hasNext())
		this.appendToNode(node, stats.next());
	return node;
}

ref.populateChildren = function(par) {
	if(par.userdata.get == null)
		this.populateSubNode(par);
	else
		this.populateRootNode();
	par.setReady();
}

ref.populateRootNode = function() {
	var paths = this.root.userdata.get("paths");
	this.root.children = this.createChildArray(paths.size());
	var i = 0;
	var iterator = paths.iterator();
	while(iterator.hasNext())
		this.root.children[i++] = this.createPathNode(iterator.next());
}

ref.createPathNode = function(path) {
	if(!path.endsWith(this.sep)) // TODO NICHT GANZ SAUBER? DIFF FILES/DIRS
		path = path + this.sep;
	var filter = this.rquery({ $match: {  } });
	filter.get("$match").put("_id", this.pathRegex(path));
	return this.createFSTreeNode("", path, "(sub)", [ filter ]);
}

ref.pathRegex = function(path) {
	return java.util.regex.Pattern.compile("^" + this.regexQuote(path) +
									".*$");
}

// http://stackoverflow.com/questions/2593637/how-to-escape-regular-expression-
// 								in-javascript
ref.regexQuote = function(str) {
	return String(str).replace(/([-()\[\]{}+?*.$\^|,:#<!\\\/])/g,
					"\\$1").replace(/\x08/g, '\\x08');
}

ref.populateSubNode = function(node) {
	var path = this.dirPath(node);

	var filter = this.createDBObject();
	filter.put("_id", this.pathRegex(path));

	var entries = [ "files", "size", "errors", "empty" ];
	var emptySet = "";
	var addCommands = "";
	for(var i = 0; i < entries.length; i++) {
		emptySet += entries[i] + ": 0,";
		addCommands += "ret." + entries[i] + " += (+values[i]." +
							entries[i] + ");";
	}

	var rawResult = this.mapReduce(
		"function() {" +
			"var rest = this._id.substring(" +
							path.length() + ");" +
			"if(rest.length == 0)" +
				"return;" +
			"var sep = rest.indexOf('" + this.addslashes(this.sep) +
									"');" +
			"if(sep != -1)" +
				"rest = rest.substring(0, sep);" +
			"emit(rest, {" +
				"files: 1," +
				"size: (this.size === null? 0: this.size)," +
				"errors: (this.error == null? 0: 1)," +
				"empty: (this.size === 0? 1: 0)," +
				"biggest_path: this._id," +
				"biggest_bytes: this.size" +
			"});" +
		"}",
		"function(key, values) {" +
			"var ret = {" +
				emptySet +
				"biggest_path: 0," +
				"biggest_bytes: 0," +
			"};" +
			"for(var i = 0; i < values.length; i++) {" +
				addCommands +
				"if(values[i].biggest_bytes > " +
							"ret.biggest_bytes) {" +
					"ret.biggest_bytes = values[i]." +
							"biggest_bytes;" +
					"ret.biggest_path = values[i]." +
							"biggest_path;" +
				"}" +
			"}" +
			"return ret;" +
		"}",
		filter
	);

	var resultList = new java.util.ArrayList();
	while(rawResult.hasNext()) {
		var cobj = rawResult.next();
		var subnode = this.createNode(path);
		subnode.id = cobj.get("_id");
		subnode.pattern = "(sub)";
		this.appendToNode(subnode, cobj.get("value"));
		resultList.add(subnode);
	}

	node.children = resultList.toArray(this.createChildArray(
							resultList.size()));
}

ref.dirPath = function(node) {
	// It is required for the path to be consistently represented as a Java
	// string because otherwise the whole thing just does nothing for no
	// visible reason. Consider the code below:
	// <fragile>
	var path = new java.lang.String(node.userdata).concat(node.id);
	if(path.endsWith(this.sep))
		return path;
	else
		return path.concat(this.sep);
	// </fragile>
}

ref.addslashes = function(str) { // http://phpjs.org/functions/addslashes/
	return String(str).replace(/[\\']/g, '\\$&');
}

ref.createDefaultFilter = function(node) {
	if(node.equals(this.root))
		return null;
	var filter = this.createDBObject();
	filter.put("_id", this.pathRegex(this.dirPath(node)));
	return filter;
}

function create_view() { return new TreeView(); }
