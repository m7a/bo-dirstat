package ma.dirstat.eval;

public interface View {

	public void setReferenceData(ViewReferenceData data);

	public void create();

	public LoadingNode getLoading();
	public ViewNode getRoot();
	public void populateChildren(ViewNode parent);

	public PreparedFilter createFilter(ViewNode below);
	public PreparedFilter getPrevFilter();

}
