INSERT INTO obj_cats_wimgs (obj_id, cat_id, inferred, reliability)
     SELECT oc.obj_id, oc.cat_id, oc.inferred, oc.reliability
     FROM obj_cats oc, objects o WHERE oc.obj_id=o.id AND NOT o.img_path IS NULL;

