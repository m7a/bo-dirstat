package ma.dirstat.eval;

interface View {

	void setReferenceData(ViewReferenceData data);

	void create();

	LoadingNode getLoading();
	ViewNode getRoot();
	void populateChildren(ViewNode parent);

	PreparedFilter createFilter(ViewNode below);
	PreparedFilter getPrevFilter();

}
