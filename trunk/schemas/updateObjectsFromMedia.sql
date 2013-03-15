UPDATE objects o, media m set o.img_path=m.path, o.img_ar=m.aspectR where o.id=m.obj_id and media.type='image' and o.id=5;


