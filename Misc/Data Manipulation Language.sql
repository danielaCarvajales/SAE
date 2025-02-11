use sae;

## BORRA INFORMACION DE TODAS LAS TABLAS (funciona si ON DELETE CASCADE esta activo)
DELETE FROM usuario WHERE codigo > 0;
DELETE FROM faq WHERE codigo > 0;
DELETE FROM evento WHERE codigo > 0;
DELETE FROM cuenta WHERE codigo > 0;
DELETE FROM contacto WHERE codigo > 0;
DELETE FROM tarea WHERE codigo > 0;
DELETE FROM actividad WHERE codigo > 0;
DELETE FROM incidencia WHERE codigo > 0;

## ESTABLECE AUTO_INCREMENT A 1
ALTER TABLE usuario AUTO_INCREMENT = 1;
ALTER TABLE faq AUTO_INCREMENT = 1;
ALTER TABLE evento AUTO_INCREMENT = 1;
ALTER TABLE cuenta AUTO_INCREMENT = 1;
ALTER TABLE contacto AUTO_INCREMENT = 1;
ALTER TABLE tarea AUTO_INCREMENT = 1;
ALTER TABLE actividad AUTO_INCREMENT = 1;
ALTER TABLE incidencia AUTO_INCREMENT = 1;

## INGRESA INFORMACION DENTRO DE LAS TABLAS
INSERT INTO
    usuario
VALUES
    (1,'santiago','123','santiago@jdc.edu.co','+57','3211111111',1),
    (2,'tomas','231','tomas@jdc.edu.co','+57','3211111112',1),
    (3,'diego','111','diego@jdc.edu.co','+57','3211111113',1),
    (4,'roberto','132','roberto@jdc.edu.co','+57','3211111114',1),
    (5,'josefina','236','josefina@jdc.edu.co','+57','3211111115',1),
    (6,'julio','11f','julio@jdc.edu.co','+57','3211111115',1),
    (7,'roberto','1s','roberto@gmail.com','+57','3211111116',1),
    (8,'barian','11f','barian@jdc.edu.co','+57','3211111118',1),
    (9,'bulian','236','bulian@jdc.edu.co','+57','3211111117',2),
    (10,'castaño','236','castaño@jdc.edu.co','+57','3211111120',2),
    (11,'salio','1s','salio@gmail.com','+57','3211111122',2),
    (12,'marcos','236','marcos@jdc.edu.co','+57','3211111123',2),
    (13,'myria','11f','myria@jdc.edu.co','+57','3211111124',2),
    (14,'alin','1s','alin@gmail.com','+57','3211111125',2),
    (15,'ally','1s3','ally@gmail.com','+57','3211111125',2),
    (16,'juan','133','juan@jdc.edu.co','+57','3211111126',2);

INSERT INTO
    faq
VALUES
    (1, 'pregunta 1', 'respuesta 1', 0, 1, 2, 1),
    (2, 'pregunta 2', 'respuesta 2', 0, 2, 2, 1),
    (3, 'pregunta 3', 'respuesta 3', 1, 3, 2, 1),
    (4, 'pregunta 4', 'respuesta 4', 0, 1, 5, 1),
    (5, 'pregunta 5', 'respuesta 5', 0, 2, 2, 1),
    (6, 'pregunta 6', 'respuesta 6', 1, 3, 2, 1),
    (7, 'pregunta 7', 'respuesta 7', 0, 1, 5, 1),
    (8, 'pregunta 8', 'respuesta 8', 0, 2, 2, 1),
    (9, 'pregunta 9', 'respuesta 9', 1, 3, 2, 1),
    (10, 'pregunta 10', 'respuesta 10', 0, 1, 5, 1),
    (11, 'pregunta 11', 'respuesta 11', 0, 1, 5, 1);

INSERT INTO
    cuenta
VALUES
    (1,'1005','1005@gmail.com','1005.com','+57','321',NULL,'secundario','colombia','boyaca','tunja',12,1,1,1,2),
    (2,'1006','1006@gmail.com','1006.com','+57','322',NULL,'secundario','colombia','boyaca','tunja',13,1,2,1,2),
    (3,'1007','1007@gmail.com','1007.com','+57','323',NULL,'primario','colombia','boyaca','tunja',2,1,2,2,2),
    (4,'1008','1008@gmail.com','1008.com','+57','324',NULL,'primario','colombia','boyaca','tunja',5,1,2,2,2),
    (5,'1009','1009@gmail.com','1009.com','+57','325',NULL,'secundario','colombia','boyaca','tunja',13,1,2,1,2),
    (6,'1010','1010@gmail.com','1010.com','+57','326',NULL,'primario','colombia','boyaca','tunja',2,1,2,2,2),
    (7,'1011','1011@gmail.com','1011.com','+57','327',NULL,'primario','colombia','boyaca','tunja',5,1,2,2,2),
    (8,'1012','1012@gmail.com','1012.com','+57','328',NULL,'secundario','colombia','boyaca','tunja',13,1,2,1,2),
    (9,'1013','1013@gmail.com','1013.com','+57','329',NULL,'primario','colombia','boyaca','tunja',2,1,2,2,2),
    (10,'1014','1014@gmail.com','1014.com','+57','330',NULL,'primario','colombia','boyaca','tunja',5,1,2,2,2),
    (11,'1015','1015@gmail.com','1015.com','+57','331',NULL,'secundario','colombia','boyaca','tunja',7,1,1,1,2);

INSERT INTO
    contacto
VALUES
    (1,'1010','juan','jose','colombia','boyaca','tunja','cra 1','+57','3210000000','juan@gmail.com','2002-01-22',NULL,1,1),
    (2,'1011','roberto','alvarez','colombia','boyaca','tunja','cra 1','+57','3210000000','roberto@gmail.com','2002-02-01',NULL,1,2),
    (3,'1012','santiago','torres','colombia','boyaca','tunja','cra 1','+57','3210000000','santiago@gmail.com','2002-03-01',NULL,2,3),
    (4,'1013','almiro','ramirez','colombia','boyaca','tunja','cra 1','+57','3210000001','almiro@gmail.com','2002-04-01',NULL,1,2),
    (5,'1014','gomez','bolaños','colombia','boyaca','tunja','cra 1','+57','3210000002','gomez@gmail.com','2002-02-01',NULL,1,2),
    (6,'1015','castillo','aguilar','colombia','boyaca','tunja','cra 1','+57','3210000000','castillo@gmail.com','2002-03-01',NULL,2,3),
    (7,'1016','andres','cepeda','colombia','boyaca','tunja','cra 1','+57','3210000003','andres@gmail.com','2002-04-01',NULL,2,2),
    (8,'1017','anon','ramirez','colombia','boyaca','tunja','cra 1','+57','3210000004','anon@gmail.com','2002-04-01',NULL,1,2),
    (9,'1018','kyriu','bolaños','colombia','boyaca','tunja','cra 1','+57','3210000005','kyriu@gmail.com','2002-02-01',NULL,1,2),
    (10,'1019','shirogane','aguilar','colombia','boyaca','tunja','cra 1','+57','3210000000','shirogane@gmail.com','2002-03-01',NULL,2,3),
    (11,'1020','sonic','cepeda','colombia','boyaca','tunja','cra 1','+57','3210000006','sonic@gmail.com','2002-04-01',NULL,1,2),
    (12,'1021','silver','ramirez','colombia','boyaca','tunja','cra 1','+57','3210000007','silver@gmail.com','2002-04-01',NULL,1,2),
    (13,'1022','amy','bolaños','colombia','boyaca','tunja','cra 1','+57','3210000008','amy@gmail.com','2002-02-01',NULL,1,2),
    (14,'1023','shadow','aguilar','colombia','boyaca','tunja','cra 1','+57','3210000000','shadow@gmail.com','2002-03-01',NULL,2,3),
    (15,'1024','tails','cepeda','colombia','boyaca','tunja','cra 1','+57','3210000009','tails@gmail.com','2002-04-01',NULL,1,2),
    (16,'1025','julian','castillo','colombia','boyaca','tunja','cra 1','+57','3210000010','julian@gmail.com','2002-05-01',NULL,1,2);

INSERT INTO
    evento
VALUES
    (1,'asunto 1','0','2024-03-15 12:43:29','2024-03-16 12:43:29',100,'descripcion 1',1,12,2,1,1,1,2,2),
    (2,'asunto 2','50','2024-03-15 12:43:29','2024-03-17 12:43:29',500,'descripcion 2',1,12,1,1,1,1,2,4),
    (3,'asunto 3','30','2024-03-15 12:43:29','2024-03-18 12:43:29',30,'descripcion 3',1,12,1,1,1,1,2,16),
    (4,'asunto 4','0','2024-03-15 12:43:29','2024-03-19 12:43:29',40,'descripcion 4',1,12,1,1,1,1,2,15),
    (5,'asunto 5','30','2024-03-15 12:43:29','2024-03-18 12:43:29',30,'descripcion 5',1,12,1,1,1,1,2,13),
    (6,'asunto 6','40','2024-03-15 12:43:29','2024-03-19 12:43:29',40,'descripcion 6',1,12,1,1,1,1,2,11),
    (7,'asunto 7','30','2024-03-15 12:43:29','2024-03-18 12:43:29',30,'descripcion 7',1,12,1,1,1,1,2,12),
    (8,'asunto 8','40','2024-03-15 12:43:29','2024-03-19 12:43:29',40,'descripcion 8',1,12,1,1,1,1,2,2),
    (9,'asunto 9','30','2024-03-15 12:43:29','2024-03-18 12:43:29',30,'descripcion 9',1,12,1,1,1,1,2,9),
    (10,'asunto 10','40','2024-03-15 12:43:29','2024-03-19 12:43:29',40,'descripcion 10',1,12,1,1,1,1,2,2),
    (11,'asunto 11','0','2024-03-15 12:43:29','2024-03-20 12:43:29',100,'descripcion 11',1,12,1,1,1,1,2,5);

INSERT INTO
    tarea
VALUES
    (1,'asunto 1','2024-03-15 12:43:29','2024-03-16 12:43:29','10','descripcion 1','observacion 1',1,9,2,2,2),
    (2,'asunto 2','2024-03-15 12:43:29','2024-03-17 12:43:29','60','descripcion 2','observacion 2',1,11,2,1,3),
    (3,'asunto 3','2024-03-15 12:43:29','2024-03-18 12:43:29','40','descripcion 3','observacion 3',1,15,2,2,2),
    (4,'asunto 4','2024-03-15 12:43:29','2024-03-19 12:43:29','100','descripcion 4','observacion 4',1,15,2,2,4),
    (5,'asunto 5','2024-03-15 12:43:29','2024-03-17 12:43:29','50','descripcion 5','observacion 5',1,11,2,3,3),
    (6,'asunto 6','2024-03-15 12:43:29','2024-03-18 12:43:29','100','descripcion 6','observacion 6',1,11,2,1,2),
    (7,'asunto 7','2024-03-15 12:43:29','2024-03-19 12:43:29','70','descripcion 7','observacion 7',1,11,2,3,2),
    (8,'asunto 8','2024-03-15 12:43:29','2024-03-17 12:43:29','100','descripcion 8','observacion 8',1,11,2,1,2),
    (9,'asunto 9','2024-03-15 12:43:29','2024-03-18 12:43:29','100','descripcion 9','observacion 9',1,11,2,1,3),
    (10,'asunto 10','2024-03-15 12:43:29','2024-03-19 12:43:29','100','descripcion 10','observacion 10',1,11,2,1,2),
    (11,'asunto 11','2024-03-15 12:43:29','2024-03-17 12:43:29','100','descripcion 11','observacion 11',1,16,2,3,2),
    (12,'asunto 12','2024-03-15 12:43:29','2024-03-18 12:43:29','100','descripcion 12','observacion 12',1,11,2,1,2),
    (13,'asunto 13','2024-03-15 12:43:29','2024-03-19 12:43:29','20','descripcion 13','observacion 13',1,13,2,2,2),
    (14,'asunto 14','2024-03-15 12:43:29','2024-03-20 12:43:29','10','descripcion 14','observacion 14',1,11,2,1,2);
 
INSERT INTO
    actividad
VALUES
    (1,'10','2024-03-15 12:43:29','2024-03-16 12:43:29','descripcion 1',12,1,1,1,2),
    (2,'20','2024-03-15 12:43:29','2024-03-17 12:43:29','descripcion 2',13,2,2,1,1),
    (3,'30','2024-03-15 12:43:29','2024-03-18 12:43:29','descripcion 3',14,3,4,3,1),
    (4,'40','2024-03-15 12:43:29','2024-03-19 12:43:29','descripcion 4',15,2,3,4,2),
    (5,'20','2024-03-15 12:43:29','2024-03-17 12:43:29','descripcion 5',13,2,2,1,1),
    (6,'30','2024-03-15 12:43:29','2024-03-18 12:43:29','descripcion 6',14,3,4,3,1),
    (7,'40','2024-03-15 12:43:29','2024-03-19 12:43:29','descripcion 7',15,2,3,4,2),
    (8,'20','2024-03-15 12:43:29','2024-03-17 12:43:29','descripcion 8',13,2,2,1,1),
    (9,'30','2024-03-15 12:43:29','2024-03-18 12:43:29','descripcion 9',14,3,4,3,1),
    (10,'40','2024-03-15 12:43:29','2024-03-19 12:43:29','descripcion 10',15,2,3,4,2),
    (11,'20','2024-03-15 12:43:29','2024-03-17 12:43:29','descripcion 11',13,2,2,1,1),
    (12,'30','2024-03-15 12:43:29','2024-03-18 12:43:29','descripcion 12',14,3,4,3,1),
    (13,'40','2024-03-15 12:43:29','2024-03-19 12:43:29','descripcion 13',15,2,3,4,2),
    (14,'20','2024-03-15 12:43:29','2024-03-17 12:43:29','descripcion 14',13,2,2,1,1),
    (15,'30','2024-03-15 12:43:29','2024-03-18 12:43:29','descripcion 15',14,3,4,3,1),
    (16,'40','2024-03-15 12:43:29','2024-03-19 12:43:29','descripcion 16',15,2,3,4,2),
    (17,'50','2024-03-15 12:43:29','2024-03-20 12:43:29','descripcion 17',16,1,1,1,2);

INSERT INTO
    incidencia
VALUES
    (1,'pregunta 1','respuesta 1','2024-03-15 12:43:29','2024-03-16 12:43:29',1,9,3,1),
    (2,'pregunta 2','respuesta 2','2024-03-15 12:43:29','2024-03-18 12:43:29',5,9,2,3),
    (3,'pregunta 3','respuesta 3','2024-03-15 12:43:29','2024-03-17 12:43:29',1,9,3,2),
    (4,'pregunta 4','respuesta 4','2024-03-15 12:43:29','2024-03-20 12:43:29',5,10,1,3),
    (5,'pregunta 5','respuesta 5','2024-03-15 12:43:29','2024-03-18 12:43:29',5,9,2,3),
    (6,'pregunta 6','respuesta 6','2024-03-15 12:43:29','2024-03-17 12:43:29',1,10,3,2),
    (7,'pregunta 7','respuesta 7','2024-03-15 12:43:29','2024-03-20 12:43:29',5,10,1,3),
    (8,'pregunta 8','respuesta 8','2024-03-15 12:43:29','2024-03-18 12:43:29',5,9,2,3),
    (9,'pregunta 9','respuesta 9','2024-03-15 12:43:29','2024-03-17 12:43:29',1,14,3,2),
    (10,'pregunta 10','respuesta 10','2024-03-15 12:43:29','2024-03-20 12:43:29',5,10,1,3),
    (11,'pregunta 11','respuesta 11','2024-03-15 12:43:29','2024-03-18 12:43:29',5,9,2,3),
    (12,'pregunta 12','respuesta 12','2024-03-15 12:43:29','2024-03-17 12:43:29',1,9,3,2),
    (13,'pregunta 13','respuesta 13','2024-03-15 12:43:29','2024-03-20 12:43:29',5,10,1,3),
    (14,'pregunta 14','respuesta 14','2024-03-15 12:43:29','2024-03-18 12:43:29',5,9,2,3),
    (15,'pregunta 15','respuesta 15','2024-03-15 12:43:29','2024-03-17 12:43:29',1,16,3,2),
    (16,'pregunta 16','respuesta 16','2024-03-15 12:43:29','2024-03-20 12:43:29',5,9,1,3),
    (17,'pregunta 17','respuesta 17','2024-03-15 12:43:29','2024-03-18 12:43:29',5,9,2,3),
    (18,'pregunta 18','respuesta 18','2024-03-15 12:43:29','2024-03-17 12:43:29',1,9,3,2),
    (19,'pregunta 19','respuesta 19','2024-03-15 12:43:29','2024-03-20 12:43:29',5,10,1,3),
    (20,'pregunta 20','respuesta 20','2024-03-15 12:43:29','2024-03-18 12:43:29',1,11,2,1);