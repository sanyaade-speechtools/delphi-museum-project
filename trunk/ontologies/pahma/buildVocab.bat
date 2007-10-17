@REM if in OVERWRITE mode (-o), then toss the current file, if any
@IF EXIST mainVocab.xml (
@IF -%1 == --o @DEL /f /p mainVocab.xml
)
@IF EXIST mainVocab.xml (
  @ECHO File already exists. Delete or specify -o to overwrite
) ELSE (
@REM Put out the boilerplate for the facetmap
@cat MainVocabHeader.txt > mainVocab.xml
@REM
@REM Add each of the facets - EDIT HERE TO ADD MORE
@REM
@cat UseOrContext_taxonomy.xml >> mainVocab.xml
@cat Location_taxonomy.xml >> mainVocab.xml
@cat CulturalGroup_taxonomy.xml >> mainVocab.xml
@REM Period_taxonomy.xml >> mainVocab.xml
@cat Materials_taxonomy.xml >> mainVocab.xml
@cat Technique_Design_Decoration_taxonomy.xml >> mainVocab.xml
@cat Color_taxonomy.xml >> mainVocab.xml
@REM
@REM Put out the ending boilerplate for the facetmap
@cat MainVocabFooter.txt >> mainVocab.xml
@REM
@REM Make it read-only to disincent editing by hand
@attrib +r mainVocab.xml
@ECHO Done.
)
