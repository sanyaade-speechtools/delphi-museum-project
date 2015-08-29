[Delphi Wiki Home](DelphiWikiHome.md)


# Introduction #

The tools began as a set of core capabilities for NLP, text mining, statistical reporting, etc., with a minimal UI to invoke the tools. This required considerable knowledge of the system to be useful, and so was not very user-friendly. We have been working to remedy this with a number of projects, mostly around refactoring code, conducting a simple needs assessment and application redesign, and then looking at other workflow issues (primarily around database access). Notes for this are gathered below.

---

# Reworking Workspace model for Delphi tools(mostly complete) #
_Note: this has mostly be accomplished as of Jan 2009. We reworked the UI to be project based. The tool remembers the most recent project. Projects contain path references to resources, and reload the resources when the project is opened. Menus are now structured around verbs, although it has been difficult to choose menu labels for actions like_{perform the text-mining function on the object metadata to extract the concept-associations for concepts represented in the referenced ontology}_. We also refactored a lot of code to make maintenance easier._

The current Delphi tools require users to load resources in a specific order, with little help on the dependencies. There is also no help on recently opened files, and similar functionality. A major problem is the lack of obvious workflow for a given project.
One idea that has promise is to move to a notion of a project-workspace as the core to the application. In this model, a user would create a project (workspace), and would then specify the pieces (resources) that were to be used. Once these were set, certain operations would be available to run, much like the current (partial) attempts to only enable menus when prerequisites have been met.

Several questions arise:
  * Do we support more than one type of project, or are there just different tasks on a project, constrained by the resources specified? E.g., If there is no ontology specified, but there is a metadata resource, is this a different project (for vocabulary analysis) from a text mining project?
  * Can we save the most recently opened workspace in a settings file? This would be really nice for a host of reasons.

If we make the UI have a project settings page, then we can show all the filepaths and means of setting them. This is instead of the menu-driven means. New Project creates an empty one, requiring a name. Could also allow setting up everything. Need a New Project, Open Project, Save Project. Need to track when the project is dirty, meaning we should change accessors to methods and track dirty state. Prompt to save on exit.

Project settings control all of the resources. Then, have actions to take:
Can we boil these down to just a few actions?
  1. Convert Generate and Save (XML) ontology from vocab (deprecated)
  1. Export Generate and Save SQL for Ontology (with load variants and incremental)
  1. Generate and Save SQL for Objects (with load variants and incremental)
  1. Generate and Save SQL for Objects-Concept associations (with load variants and incremental)
  1. Analyze Object Info and produce term usage report
  1. Analyze Objects-Concept association and produce unused-term usage report. By facet?

Rework menus based upon above. E.g.,
#### File ####
  * New Project ...
  * Open Project ...
  * Save Project
  * Save Project As ...
  * Exit
#### Edit ####
  * Cut
  * Copy
  * Paste
#### Analyze ####
  * Object Info Term Usage (requires ObjInfo file)
  * Object/Concept Association (requires ObjInfo file and Ontology. Later with load variants and an option to connect to DB). Incremental? [this as wizard that sets options for reporting and output](Do.md)
#### Images ####
  * Compute Orientations [export operation](Separate.md)
#### Export ####
  * Object Info (later with load variants and an option to connect to DB). Incremental?
  * Concept Ontology
    * As XML file
    * As SQL (later with load variants and an option to connect to DB). Incremental?
    * Hooks/Exclusions to SQL (later with load variants and an option to connect to DB). Incremental?
  * Image Paths
    * As SQL Insert Statement
    * As SQL Loadfile (later with option to connect to DB). Incremental?

---

# Reworking Tools to connect directly to databases (in progress) #
There are a number of steps in the workflow that are cumbersome, and require some care from a human to run sql commands or other scripts from the command line. This makes it harder for museum folks to handle the workflow themselves. These steps include:
  1. Extracting metadata from the existing CMS database
  1. Running filters and noise reduction tools to remove extraneous metadata and to elide certain classes of strings (like currencies).
  1. Loading ontology information into a database, including concepts, hooks and exclusions.
  1. Loading concept associations (the results of text mining) into a database.

We'll consider each of these in turn, but first will cover some utilities that will be needed for any of this:
## DB Connection configuration ##
This is outlined along with a schema for configuring the metadata extraction, in the document [Notes on Metadata Extraction schema](http://delphi-museum-project.googlecode.com/svn/trunk/docs/metadataExtractionNotes.htm)