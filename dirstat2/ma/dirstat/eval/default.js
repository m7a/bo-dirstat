/**
 * Ma_Sys.ma DirStat 2.0.0.2 Default JavaScript Functions available to every
 * (user-created) view, Copyright (c) 2014 Ma_Sys.ma.
 * For further info send an e-mail to Ma_Sys.ma@web.de.
 */

function ViewJS() { }
ViewJS.prototype = {

//----------------------------------------------------[ private and internal ]--

ref: null,

setReferenceData: function(ref) {
	this.ref = ref;
},

//-------------------------------------------------------[ outward interface ]--

equals: function(x) {
	return x.hashCode() == this.hashCode();
},

hashCode: function() {
	return this.ref.obj.hashCode();
},

getPrevFilter: function() {
	return this.ref.filter;
},

getLoading: function() {
	return this.ref.loading;
},

//--------------------------------------------------------[ abstract methods ]--

create: function() { // abstract
	throw new ma.tools2.util.NotImplementedException(
					"Abstract method not implemented");
},

getRoot: function() { // default implementation
	return this.root;
},

populateChildren: function(par) { // abstract
	throw new ma.tools2.util.NotImplementedException(
					"Abstract method not implemented");
},

createFilter: function(below) {
	return null; // abstract
},

//------------------------------------------------[ utilities for subclasses ]--

createNode: function(x) {
	return new Packages.ma.dirstat.eval.ViewNode(this.ref.view, x);
},

prepareQuery: function(query) {
	return this.ref.db.prepareStatement(query);
},

prepareSelect: function(select) {
	return this.ref.filter.prepare(this.ref.db, select);
},

appendFilterValues: function(st, pos) {
	return this.ref.filter.append(st, pos);
},

createDefaultFilter: function(query, data) {
	var list = new java.util.ArrayList();
	for(var i = 0; i < data.length; i++)
		list.add(data[i]);
	var filter = new Packages.ma.dirstat.eval.PreparedFilter();
	filter.set(query, list);
	return filter;
},

getMeta: function() {
	return this.ref.meta;
},

addToNode: function(node, result) {
	node.files  += result.getLong("num_files");
	node.size   += result.getLong("ssize");
	node.errors += result.getLong("num_errors");
	node.empty  += result.getLong("empty");
	var sz       = result.getLong("biggest_bytes");
	if(sz > node.biggestBytes) {
		node.biggestPath  = result.getString("bgst");
		node.biggestBytes = sz;
	}
},

escapeLike: function(text) {
	return new java.lang.String(String(text).replace(/[\\%_]/g, "\\$&"));
}

}
