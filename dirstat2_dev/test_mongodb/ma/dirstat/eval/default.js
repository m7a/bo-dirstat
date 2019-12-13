/**
 * Ma_Sys.ma Dir Stat 2.0.0.0 Default JavaScript Functions available to every
 * (user-created) view, Copyright (c) 2014 Ma_Sys.ma.
 * For further info send an e-mail to Ma_Sys.ma@web.de.
 */

function ViewJS() { }
ViewJS.prototype = {

//----------------------------------------------------[ private and internal ]--

ref: null,
DEFAULT_AGGREGATION_FILTER: null,

setReferenceData: function(ref) {
	this.ref = ref;
	this.DEFAULT_AGGREGATION_FILTER = this.rquery({ $match: { _id: { $ne:
							"DIRSTAT_META" } } });
},

rquerys: function(data) { // private method
	if(Array.isArray(data)) {
		var ret = new com.mongodb.BasicDBList();
		for(var i = 0; i < data.length; i++)
			ret.add(this.rquerys(data[i]));
		return ret;
	} else if(typeof(data) === "object") {
		var ret = this.createDBObject();
		for(var property in data)
			ret.put(property, this.rquerys(data[property]));
		return ret;
	} else {
		return data;
	}
},

//-------------------------------------------------------[ outward interface ]--

equals: function(x) {
	return this.ref.obj.equals(x);
},

hashCode: function() {
	return this.ref.obj.hashCode();
},

getPrevMapReduceFilters: function() {
	return this.ref.mapReduceFilters;
},

getPrevAggreagtionFilters: function() {
	return this.ref.aggregationFilters;
},

//--------------------------------------------------------[ abstract methods ]--

create: function() {
	// abstract
},

getRoot: function() { // default implementation
	return this.root;
},

populateChildren: function(par) { // default implementation
	par.children = this.createChildArray(0);
	par.isReady  = true;
},

createAggregationFilter: function(below) { // default implementation
	var ret = this.createDBObject();
	ret.put("$match", this.createDefaultFilter(below));
	return ret;
},

createMapReduceFilter: function(below) { // default implementation
	return this.createDefaultFilter(below);
},

createDefaultFilter: function(below) {
	// abstract but not necessarily required to be implemented if
	// createMapReduceFilter and createAggregationFilter are both
	// overriden
	println("ERROR: Abstract method invoked.");
	return null;
},

//------------------------------------------------[ utilities for subclasses ]--

createNode: function(x) {
	return this.ref.fact.create(x);
},

appendToNode: function(node, data) {
	// http://jibbering.com/faq/faq_notes/type_convert.Html
	node.files  += (+data.get("files"));
	node.size   += (+data.get("size"));
	node.errors += (+data.get("errors"));
	node.empty  += (+data.get("empty"));
	var cb = data.get("biggest_bytes");
	if(cb != null && cb > node.biggestBytes) {
		node.biggestPath  = data.get("biggest_path");
		node.biggestBytes = cb;
	}
},

rquery: function(query_array) {
	if(Array.isArray(query_array)) {
		var ret = new Array(query_array.length);
		for(var i = 0; i < query_array.length; i++)
			ret[i] = this.rquerys(query_array[i]);
		return ret;
	} else {
		return this.rquerys(query_array);
	}
},

createDBObject: function() {
	return new com.mongodb.BasicDBObject();
},

createChildArray: function(n) {
	return java.lang.reflect.Array.newInstance(
					Packages.ma.dirstat.eval.ViewNode, n);
},

mapReduce: function(map, reduce, filter) {
	var query = this.createDBObject();

	var filterList = new com.mongodb.BasicDBList();
	for(var i = 0; i < this.ref.mapReduceFilters.length; i++)
		filterList.add(this.ref.mapReduceFilters[i]);
	filterList.add(filter);

	query.put("$and", filterList);

	return this.ref.db.mapReduce(map, reduce, null,
				com.mongodb.MapReduceCommand.OutputType.INLINE,
				query).results().iterator();
},

aggregate: function(raw) { // takes Objects
	var pipeline = new com.mongodb.BasicDBList();
	for(var i = 0; i < this.ref.aggregationFilters.length; i++)
		pipeline.add(this.ref.aggregationFilters[i]);
	pipeline.addAll(raw);

	var rest = pipeline.toArray(java.lang.reflect.Array.newInstance(
					com.mongodb.DBObject, pipeline.size()));

	return this.ref.db.aggregate(this.DEFAULT_AGGREGATION_FILTER,
						rest).results().iterator();
},

findMeta: function() {
	return this.ref.db.findOne(this.rquery({ _id: "DIRSTAT_META" }));
},

}
