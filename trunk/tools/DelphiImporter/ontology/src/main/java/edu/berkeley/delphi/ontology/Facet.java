package edu.berkeley.delphi.ontology;

public class Facet extends TaxoNode {
	private static final float NGRAM_DISCOUNT_DEFAULT = (float)0.3;
	private static final float NGRAM_DISCOUNT_MAX = (float)0.9;

	public String rootHTitle;	// "root-heading-title" in facetmap schema
	public String supports;		// "supports" in facetmap schema
	public String description;
	public String notes;
	public float nGramDiscount;	// How much do we discount confidence for nGrams
	public int nMasks;			// How many masks does this facet need?
	public int iMaskBase;		// first mask index for this facet

	public Facet( String name, String displayName, int id,
			String description, String notes, float nGramDiscount,
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
		this.description = description;
		this.notes = notes;
		this.nGramDiscount = (nGramDiscount<0||nGramDiscount>NGRAM_DISCOUNT_MAX)?
								NGRAM_DISCOUNT_DEFAULT:nGramDiscount;
		this.nMasks = 0;
		this.iMaskBase = 0;
	}
}
