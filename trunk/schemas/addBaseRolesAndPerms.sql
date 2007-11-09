INSERT INTO role( id, name, description, creation_time ) VALUES 
   ( 1, 'Admin', 'Manages users, roles, permissions and other aspects of the system.', now() ),
   ( 2, 'Authenticated', 'An authenticated user of the system with basic usage rights.', now() );

INSERT INTO permission( id, name, description, creation_time ) VALUES 
   ( 1, 'EditRoles', 'Permission to add new roles, delete existing roles and to change the permissions associated with existing roles. Permission to edit comments associated with existing roles.', now() ),
   ( 2, 'EditPerms', 'Permission to add new permissons and delete existing permissions. Permission to edit comments associated with existing permissions.', now() ),
   ( 3, 'AssignRoles', 'Permission to set roles for a user.', now() ),
   ( 4, 'EditSets', 'Permission to add new sets and delete existing sets. Permission to add and remove items from existing sets. Permission to set titles, comments and tags on owned sets.', now() ),
   ( 5, 'MarkFavorites', 'Permission to add and remove items from default Favorites set.', now() );

INSERT INTO role_perms( role_id, perm_id, creation_time ) VALUES 
   ( 1, 1, now() ),
   ( 1, 2, now() ),
   ( 1, 3, now() ),
   ( 2, 4, now() ),
   ( 2, 5, now() );

INSERT INTO user( id, username, passwdmd5, email, creation_time ) VALUES
( 1, 'admin', md5('delphi'), 'blackhole@ludicrum.org', now() );

INSERT INTO user_roles( user_id, role_id, approver_id, creation_time ) VALUES
( 1, 1, 1, now() );
