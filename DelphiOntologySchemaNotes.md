# Intro #

These notes describe the XML schema used to describe the faceted ontologies within the Delphi framework. The ontologies serve the linguistic needs of the Natural Language Processing (NLP) engine, the knowledge modeling support for the given domain (including metadata enrichment through inference) and also must serve as the basis of the faceted browser UI for the Delphi collections browser. These diverse needs have dictated the required features of the schema.

We designed the schema for simplicity and ease of use in our code, and not to replace existing common exchange schema like SKOS, OWL (and variants), and others. We will look at translations to other schemas if and when the need arises (it likely will, but is not a high priority at this point).

The current schema uses some element names that may be somewhat confusing, and the last section of this document includes a list proposed changes to address this. For example, we use "category", "concept" and "heading" to refer to the same idea. Bear with us until we get this straightened out.

We present an overview of the principles and terms we use, and then describe the element syntax in detail.

# Overview #

We talk about an ontology as a collection of facets, each of which contains groupings of concepts and some linguistic hints about how to recognize the concepts in some input text. Each facet is described in a separate XML file to simplify maintenance, and then these files are merged with a simple script (buildVocab.bat) to produce the ontology file read by the Delphi tools.

Each facet file begins with a 

&lt;taxonomy&gt;

 element at the top, which has attributes to declare an id and the title string for the facet. The id string is constrained by XML rules to only use certain characters (in particular, no spaces), and is mostly for internal reference. The title is a human-friendly string used in the UI to identify the facet. It should be brief enough to fit where facets are labeled in the UI, but can have spaces and other characters. The 

&lt;taxonomy&gt;

 element has two descriptive child elements (

&lt;description&gt;

 and 

&lt;notes&gt;

), and then the concept 

&lt;heading&gt;

 elements.
  * The 

&lt;description&gt;

 contains text intended to explain the purpose and model for that facet to the intended audience for the collections browser. The contents of the  

&lt;description&gt;

 text will appear in the category browser UI.
  * The 

&lt;notes&gt;

 element contains text that documents issues, development plans, etc. for the facet, and is intended for use by the editors and development staff for the ontology. The contents of the 

&lt;notes&gt;

 text do not currently appear in the UI.
The rest of the element children of a 

&lt;taxonomy&gt;

 element are concept 

&lt;heading&gt;

 elements, which in turn contain more concept 

&lt;heading&gt;

 elements. The 

&lt;heading&gt;

 element also has an id attribute for internal reference, and a title attribute that defines the user-friendly name for the concept. The title can be thought of as the preferred name for the concept. The title is also generally used by the NLP processor as one of the strings that indicate a concept (although this can be controlled with additional attributes described below).

It is important that the id values for the facet 

&lt;taxonomy&gt;

 elements and for the concept 

&lt;heading&gt;

 elements be unique, or other aspects of the ontology will break!

## Linguistic Support ##
In order to recognize the concepts in the metadata text for collections objects, we need to describe the different words or phrases that can be used to indicate the concept. For some concepts, e.g., the material gold, there are just a few words or abbreviations that would commonly indicate the concept: "gold", "au". In this case, we often use the title of the concept ("gold") and then describe a few synonyms or "hooks" that should also be associated with the concept (e.g., "au").

For other concepts, e.g., _deer designs or motifs_, a simple word is often not enough and may even be misleading: "deer" could refer to bones or teeth as well as to a design. It is often the case that certain "entailment phrases" can be used to recognize these concepts (e.g., "deer pattern", "painting of a deer"). Furthermore, these entailment phrases tend to come in patterns that apply to a number of related concepts. We describe these patterns as prefix and suffix strings that are combined with the basic tokens for the concept. Thus, we can combine the prefix "painting of a " with the token "deer" to produce the entailment string. The 

&lt;prefix&gt;

 and 

&lt;suffix&gt;

 elements describe the patterns that can be combined with tokens. To make it easier to reuse these patterns, 

&lt;prefix&gt;

 and 

&lt;suffix&gt;

 definitions apply not just to the concept 

&lt;heading&gt;

 element that contains the 

&lt;prefix&gt;

 or a 

&lt;suffix&gt;

 element, _but to all the descendent_

&lt;heading&gt;

 elements as well_._

It is often the case that a given word may have several meanings. In some cases, it may be easier to use a simple word or string to describe a concept and then define a few exceptions or exclusions that sufficiently disambiguate the concept in a given corpus or collection. For example, "plate" can refer to the object on which food is served, to the glass objects used in photography, to a section of the earth's crust (in tectonics), and other meanings as well. For a given collection (e.g., in a museum of anthropology), it is often easiest to simply specify the word "plate" for a concept (e.g., the one for food) and then exclude the few other uses that actually occur in the metadata for that collection (e.g., "photographic plate"). The 

&lt;excl&gt;

 element is used to define the exclusions.

## Structure and organizing principles ##
It is important to remember that the titles, structure and organization of the headings are more than just a model for you as a collections manager or taxonomist. These features are also used directly to produce the UI for your collections browser. To make the browser easy to use by your intended audience, you need to consider some principles of usability as you develop the ontology. Here are some things to think about:

  * Select titles that will make sense to your audience, rather than jargon that may be common among experts. E.g., if your audience is the general public, use common names for plants and animals, and not the Latin names.
  * Consider organizations of concepts that match the common point of view of your audience. For a public audience, this may mean using folk organizations of living things rather than Linnaean taxonomy.
  * As you gather concepts into groups, remember that the faceted browser will present related concepts to support query refinement. If you group 40 or 50 concepts under a single parent concept, the UI for refinement might have to present a long list that would be awkward at best. In addition, it makes it harder for a user to get an overview of how objects are distributed if they have to scan a long list of concepts and compare all the associated object counts. For example, rather than just grouping all the U.S. states under the concept for the country, you should consider adding regional groupings of states with perhaps 5 to 10 states per region. The result is a deeper and narrower structure than might be used by other standard vocabularies.

## Knowledge modeling and reasoning support ##
The NLP engine is designed to associate individual concepts to objects in the collections. However, since the ontology generally models broader and narrower concepts, the engine infers the broader concepts (the parent and ascendant concepts), and associates these concepts to the objects as well. In addition, however, the ontology can model real-world knowledge that allows additional inferences to be made from concepts that are associated from the text processing.

In the current version, the only additional inferences are defined with the `implies` attribute on the concept 

&lt;heading&gt;

 element. As described in the Proposed Changes appendix, we need to add support for complex inferences.
## I18n and Unicode issues ##
The Delphi codebase supports the use of Unicode (UTF-8), and the XML for the ontology definitions.