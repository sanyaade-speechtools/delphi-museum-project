mysqlimport -u delphi -p -v --fields-terminated-by="|" --local delphi obj_cats.Color_1.sql
mysqlimport -u delphi -p -v --fields-terminated-by="|" --local delphi obj_cats.CulturalGroup_1.sql
mysqlimport -u delphi -p -v --fields-terminated-by="|" --local delphi obj_cats.Material_1.sql
mysqlimport -u delphi -p -v --fields-terminated-by="|" --local delphi obj_cats.SiteorProvenience_1.sql
mysqlimport -u delphi -p -v --fields-terminated-by="|" --local delphi obj_cats.TechniqueDesignorDecoration_1.sql
mysqlimport -u delphi -p -v --fields-terminated-by="|" --local delphi obj_cats.UseorContext_1.sql

-- For pahma_dev:
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3031 --fields-terminated-by="|" pahma_dev obj_cats.Color_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3031 --fields-terminated-by="|" pahma_dev obj_cats.CulturalGroup_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3031 --fields-terminated-by="|" pahma_dev obj_cats.Material_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3031 --fields-terminated-by="|" pahma_dev obj_cats.SiteorProvenience_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3031 --fields-terminated-by="|" pahma_dev obj_cats.TechniqueDesignorDecoration_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3031 --fields-terminated-by="|" pahma_dev obj_cats.UseorContext_1.sql

-- For pahma_prod:
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3032 --fields-terminated-by="|" pahma_prod obj_cats.Color_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3032 --fields-terminated-by="|" pahma_prod obj_cats.CulturalGroup_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3032 --fields-terminated-by="|" pahma_prod obj_cats.Material_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3032 --fields-terminated-by="|" pahma_prod obj_cats.SiteorProvenience_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3032 --fields-terminated-by="|" pahma_prod obj_cats.TechniqueDesignorDecoration_1.sql
mysqlimport -u admin -p -v -L -h sierra.ist.berkeley.edu -P 3032 --fields-terminated-by="|" pahma_prod obj_cats.UseorContext_1.sql
