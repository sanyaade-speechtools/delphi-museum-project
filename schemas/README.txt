0) Create the DB, with UTF8 and preferred collation (PAHMA uses general_ci). Set the name to something meaningful. You will either need to set the DB within mysql and SOURCE the files, or use the command line tool specifying the default DB as an argument. 
1) Load the user identity schema:  
     schemas/delphi_ident.sql
2) Run the script to create the base user info: 
     schemas/addBaseRolesAndPerms.sql
   You can modify the admin password via the Delphi UI once this is done.
3) Load the main schema:
     schemas/mainObjInfo.sql
4) Load the featured sets schema
     schemas/featured.sql
5) Load the news  schema
     schemas/news_content.sql
6) Load the termStats  schema
     schemas/termStats.sql
7) Run the grants script (with at least password modified for local instance).
     schemas/grants.sql

Once data is loaded, scripts like cat_counts.sql, fill_obj_cats_wimgs.sql, updateObjectsFromMedia.sql are used to calculate some values. They have been superceded by code that does this in the app.

All the scripts that specify “use onto_maint” are not needed for core Delphi functionality: addTestInferences.sql, complexInference.sql

delphi_visit.sql supports future functionality NYI. 
