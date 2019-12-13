/*
 * Ma_Sys.ma Dir Stat 2.0.0.0 Simple Filter By Extension as an Example,
 * Copyright (c) 2014 Ma_Sys.ma.
 * For further info send an e-mail to Ma_Sys.ma@web.de.
 */

function ExtensionView() { }
ExtensionView.prototype = new ViewJS();
var ref = ExtensionView.prototype;

ref.root = null;

ref.create = function() {
	this.summarize(this.performAggregation());
}

ref.performAggregation = function(db, filters) {
	return this.aggregate(this.rquery([
		{ $sort: { size: 1 } },
		{ $group: {
			_id: { $cond: [
				{ $eq: [
					{ $ifNull: [ "$ext", "N_EXT" ] },
					"N_EXT"
				] },
				"N_EXT",
				{ $toLower: "$ext" },
			] },
			files:  { $sum:  1 },
			size:   { $sum: "$size" },
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
		{ $sort: { _id: 1 } }
	]));
}

ref.summarize = function(pre_children) {
	this.root = this.createNode("root");
	this.root.biggestBytes = -1;

	var list = new java.util.ArrayList();
	while(pre_children.hasNext())
		this.sumChild(pre_children.next(), list);

	this.root.children = list.toArray(this.createChildArray(list.size()));
	this.root.id = "Extension";
	this.root.pattern = "*.*";
	this.root.setReady();
}

ref.sumChild = function(i, list) {
	this.appendToNode(this.root, i);
	var v = this.createNode("cl1");
	v.id = i.get("_id");
	v.pattern = v.id == "N_EXT"? "*": "*." + v.id;
	this.appendToNode(v, i);
	v.children = this.createChildArray(0); // Boost performace
	v.setReady();
	list.add(v);
}

ref.createDefaultFilter = function(below) {
	if(below.equals(this.root))
		return null;

	// TODO DOCUMENT THE STRING AND ARRAY HANDLING ODDITIES
	var extension = String(below.id); // Important to prevent oddities
	if(extension == "N_EXT")
		return this.rquery({ ext: { $exists: false } });
	else
		return this.rquery({ ext: extension });
}

function create_view() { return new ExtensionView(); }
