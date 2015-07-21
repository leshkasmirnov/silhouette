mysqldump -u root -p1234 silhouette > ./silhouette_backup.sql
mysql -u root -p1234 silhouette < ./changes20150304.sql