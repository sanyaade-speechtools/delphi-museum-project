package museum.delphi;

public class Facet extends TaxoNode {
	public String rootHTitle;	// "root-heading-title" in facetmap schema
	public String supports;		// "supports" in facetmap schema
	public int nMasks;			// How many masks does this facet need?
	public int iMaskBase;		// first mask index for this facet

	Facet( String name, String displayName, int id,
			boolean inferredByChildren, boolean selectSingle,
			String sort, String rootHTitle, String supports ) {
		super( name, displayName, name, id, -1, -1, null, sort, false,
				inferredByChildren, selectSingle );

		this.rootHTitle = rootHTitle;
		this.facetid = this.id;		// Silly, but more consistent
		// supports must be one of 'People', 'GeoLoc', 'Quality', 'None'
		if( !supports.equals( "People" )
			&& !supports.equals( "GeoLoc" )
			&& !supports.equals( "Quality" )
			&& !supports.equals( "None" ))
			throw new RuntimeException( "Illegal value for supports: "
					+ supports + " on Facet: " + name );
		this.supports = supports;
		this.nMasks = 0;
		this.iMaskBase = 0;
	}
}

