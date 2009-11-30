-- This should be in a new DB on dev that is just for maintaining the ontologies.

use onto_maint;

select id @c from categories where name = 'Carved';
select id @w from categories where name = 'Wood';
select id @m from categories where name = 'Mask';
select id @f from categories where name = 'FigurativeOrRepresentational';

INSERT INTO inferred_concepts( id, name, notes, infer, reliability, req_all, excl_all, creation_time ) VALUES 
( 1, 'Wood_Mask_Carved', 'Wood+Mask => Carved', @c, 9, 1, 0, now() ),
( 2, 'Wood_Fig_Carved', 'Wood+Figurative => Carved', @c, 7, 1, 0, now() );

INSERT INTO inf_required( cat_id, inf_id, creation_time ) VALUES 
   ( @w, 1, now() ),
   ( @m, 1, now() ),
   ( @w, 2, now() ),
   ( @f, 2, now() );

-- Note that we have no exclusions yet. Should test one.

-- Note that we need to test multi-step inference. Could posit some bogus thing like
-- Carved plus San Francisco implies culture Ashanti, 
--   and then Mask plus Ashanti implies copper unless painted.


