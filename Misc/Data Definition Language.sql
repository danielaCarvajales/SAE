DROP DATABASE IF EXISTS SAE;
CREATE DATABASE IF NOT EXISTS SAE;
USE SAE;


###################################
####    TABLAS PRIMARIAS     ######
###################################


CREATE TABLE USUARIO (
    codigo INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100),
    contrasena VARCHAR(100),
    email VARCHAR(100),
    codigo_pais VARCHAR(4),
    numero_movil VARCHAR(15),
    rol INT NOT NULL,
    PRIMARY KEY (codigo)
);

CREATE TABLE FAQ (
    codigo INT NOT NULL AUTO_INCREMENT,
    pregunta TEXT,
    respuesta TEXT,
    aprobado_superadmin BOOLEAN DEFAULT FALSE,
    categoria INT NOT NULL,
    pregunta_realizada_por INT NOT NULL,
    respuesta_realizada_por INT NOT NULL,
    PRIMARY KEY (codigo)
);

CREATE TABLE EVENTO (
    codigo INT NOT NULL AUTO_INCREMENT,
    asunto TEXT,
    porcentaje_progreso VARCHAR(4),
    fecha_hora_inicio DATETIME,
    fecha_hora_fin DATETIME,
    tiempo_duracion FLOAT,
    descripcion TEXT,
    codigo_consultor INT NOT NULL,
    codigo_empresario INT NOT NULL,
    estado INT NOT NULL,
    tipo INT NOT NULL,
    visibilidad INT NOT NULL,
    notificar_por INT NOT NULL,
    cuenta_asignada INT NOT NULL,
    contacto_asignado INT NOT NULL,
    PRIMARY KEY (codigo)
);

CREATE TABLE EMAIL (
	codigo TEXT NOT NULL,
    remitente TEXT,
    destinatario TEXT,
    asunto TEXT,
    contenido TEXT,
    fecha_enviado TIMESTAMP,
    fecha_recibido TIMESTAMP,
    usuario_asignado INT NOT NULL,
    evento_asignado INT,
    PRIMARY KEY (codigo(255))
);

CREATE TABLE CUENTA (
    codigo INT NOT NULL AUTO_INCREMENT,
    numero_identificacion VARCHAR(30),
    email VARCHAR(100),
    pagina_web VARCHAR(100),
    codigo_pais VARCHAR(4),
    telefono_fijo VARCHAR(15),
    telefono_secundario VARCHAR(15),
    sector_economico VARCHAR(150),
    pais VARCHAR(100),
    departamento VARCHAR(100),
    ciudad VARCHAR(100),
    numero_empleados INT,
    tipo_identificacion INT NOT NULL,
    tipo_servicio INT NOT NULL,
    codigo_usuario INT NOT NULL,
    codigo_consultor_asignado INT NOT NULL,
    PRIMARY KEY (codigo)
);

CREATE TABLE CONTACTO (
    codigo INT NOT NULL AUTO_INCREMENT,
    numero_identificacion VARCHAR(30),
    nombres VARCHAR(100),
    apellidos VARCHAR(100),
    pais VARCHAR(100),
    departamento VARCHAR(100),
    ciudad VARCHAR(100),
    direccion VARCHAR(100),
    codigo_pais VARCHAR(4),
    movil VARCHAR(15),
    email VARCHAR(100),
    fecha_nacimiento DATE,
    ruta_imagen VARCHAR(255),
    tipo_identificacion INT NOT NULL,
    cuenta INT NOT NULL,
    PRIMARY KEY (codigo)
);

CREATE TABLE TAREA (
    codigo INT NOT NULL AUTO_INCREMENT,
    asunto TEXT,
    fecha_hora_inicio DATETIME,
    fecha_hora_fin DATETIME,
    porcentaje_progreso VARCHAR(4),
    descripcion TEXT,
    observacion TEXT,
    codigo_consultor_asignado INT NOT NULL,
    codigo_empresario_asignado INT NOT NULL,
    codigo_cuenta_asignada INT NOT NULL,
    estado INT NOT NULL,
    tipo INT NOT NULL,
    PRIMARY KEY (codigo)
);

CREATE TABLE ACTIVIDAD (
    codigo INT NOT NULL AUTO_INCREMENT,
    porcentaje_progreso VARCHAR(4),
    fecha_hora_inicio DATETIME,
    fecha_hora_fin DATETIME,
    descripcion TEXT,
    horas_trabajo INT,
    codigo_tarea INT NOT NULL,
    contacto_asignado INT NOT NULL,
    estado INT NOT NULL,
    unidad_trabajo INT NOT NULL,
    PRIMARY KEY (codigo)
);

CREATE TABLE INCIDENCIA (
    codigo INT NOT NULL AUTO_INCREMENT,
    pregunta TEXT,
    respuesta TEXT,
    fecha_hora_inicio DATETIME,
    fecha_hora_fin DATETIME,
    codigo_consultor INT NOT NULL,
    codigo_empresario INT NOT NULL,
    categoria INT NOT NULL,
    estado INT NOT NULL,
    PRIMARY KEY (codigo)
);


###################################
####    TABLAS SECUNDARIAS     ####
###################################


CREATE TABLE ROLES (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(100) NOT NULL
);

CREATE TABLE CATEGORIAS_FAQ (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE ESTADOS_EVENTO (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(100) NOT NULL
);

CREATE TABLE TIPOS_EVENTO (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo VARCHAR(100) NOT NULL
);

CREATE TABLE VISIBILIDADES_EVENTO (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_visibilidad VARCHAR(100) NOT NULL
);

CREATE TABLE MEDIOS_NOTIFICACION_EVENTO (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_medio VARCHAR(100) NOT NULL
);

CREATE TABLE TIPOS_SERVICIO_CUENTA (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo VARCHAR(100) NOT NULL
);

CREATE TABLE TIPOS_IDENTIFICACION (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo VARCHAR(100) NOT NULL
);

CREATE TABLE ESTADOS_TAREA (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(100) NOT NULL
);

CREATE TABLE TIPOS_TAREA (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo VARCHAR(100) NOT NULL
);

CREATE TABLE ESTADOS_ACTIVIDAD (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(100) NOT NULL
);

CREATE TABLE UNIDADES_TRABAJO_ACTIVIDAD (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_unidad VARCHAR(100) NOT NULL
);

CREATE TABLE CATEGORIAS_INCIDENCIA (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE ESTADOS_INCIDENCIA (
    codigo INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(100) NOT NULL
);


###################################
####    LLAVES FORÁNEAS     #######
###################################


# USUARIO
alter table USUARIO add foreign key (rol) references ROLES(codigo);

# FAQ
alter table FAQ add foreign key (categoria) references CATEGORIAS_FAQ(codigo);
alter table FAQ add foreign key (pregunta_realizada_por) references USUARIO(codigo);
alter table FAQ add foreign key (respuesta_realizada_por) references USUARIO(codigo);

# EVENTO
alter table EVENTO add foreign key (codigo_consultor) references USUARIO(codigo);
alter table EVENTO add foreign key (codigo_empresario) references USUARIO(codigo);
alter table EVENTO add foreign key (estado) references ESTADOS_EVENTO(codigo);
alter table EVENTO add foreign key (tipo) references TIPOS_EVENTO(codigo);
alter table EVENTO add foreign key (visibilidad) references VISIBILIDADES_EVENTO(codigo);
alter table EVENTO add foreign key (notificar_por) references MEDIOS_NOTIFICACION_EVENTO(codigo);
alter table EVENTO add foreign key (cuenta_asignada) references CUENTA(codigo);
alter table EVENTO add foreign key (contacto_asignado) references CONTACTO(codigo);

# EMAIL
alter table EMAIL add foreign key (usuario_asignado) references USUARIO(codigo);
alter table EMAIL add foreign key (evento_asignado) references EVENTO(codigo);

# CUENTA
alter table CUENTA add foreign key (tipo_identificacion) references TIPOS_IDENTIFICACION(codigo);
alter table CUENTA add foreign key (tipo_servicio) references TIPOS_SERVICIO_CUENTA(codigo);
alter table CUENTA add foreign key (codigo_usuario) references USUARIO(codigo);
alter table CUENTA add foreign key (codigo_consultor_asignado) references USUARIO(codigo);

# CONTACTO
alter table CONTACTO add foreign key (tipo_identificacion) references TIPOS_IDENTIFICACION(codigo);
alter table CONTACTO add foreign key (cuenta) references CUENTA(codigo);

# TAREA
alter table TAREA add foreign key (codigo_cuenta_asignada) references CUENTA(codigo);
alter table TAREA add foreign key (codigo_consultor_asignado) references USUARIO(codigo);
alter table TAREA add foreign key (codigo_empresario_asignado) references USUARIO(codigo);
alter table TAREA add foreign key (estado) references ESTADOS_TAREA(codigo);
alter table TAREA add foreign key (tipo) references TIPOS_TAREA(codigo);

# ACTIVIDAD
alter table ACTIVIDAD add foreign key (codigo_tarea) references TAREA(codigo);
alter table ACTIVIDAD add foreign key (contacto_asignado) references CONTACTO(codigo);
alter table ACTIVIDAD add foreign key (estado) references ESTADOS_ACTIVIDAD(codigo);
alter table ACTIVIDAD add foreign key (unidad_trabajo) references UNIDADES_TRABAJO_ACTIVIDAD(codigo);

# INCIDENCIA
alter table INCIDENCIA add foreign key (codigo_consultor) references USUARIO(codigo);
alter table INCIDENCIA add foreign key (codigo_empresario) references USUARIO(codigo);
alter table INCIDENCIA add foreign key (categoria) references CATEGORIAS_INCIDENCIA(codigo);
alter table INCIDENCIA add foreign key (estado) references ESTADOS_INCIDENCIA(codigo);

#########################################
####    DATOS PREDETERMIANADOS     ######
#########################################

insert into ROLES(nombre_rol) values ("Consultor"),("Empresario");
insert into TIPOS_IDENTIFICACION(nombre_tipo) values ("Cédula"),("NIT");
insert into VISIBILIDADES_EVENTO(nombre_visibilidad) values ("Público"), ("Privado");
insert into ESTADOS_INCIDENCIA(nombre_estado) values ("Revisada"), ("En curso"), ("Resuelta");
insert into MEDIOS_NOTIFICACION_EVENTO(nombre_medio) values ("WhatsApp"), ("Email"), ("Todas");
insert into TIPOS_EVENTO(nombre_tipo) values ("Chat WhatsApp"), ("Email"), ("Llamada"), ("Reunión"), ("Messenger");
insert into ESTADOS_EVENTO(nombre_estado) values ("Planeado"), ("Realizado"), ("Pendiente"), ("Cancelado");
insert into TIPOS_TAREA(nombre_tipo) values ("Legal"), ("Contable"), ("Tecnológica"), ("General"), ("Todas");
insert into ESTADOS_TAREA(nombre_estado) values ("No iniciada"), ("En progreso"), ("Terminada"), ("Planeada");
insert into CATEGORIAS_FAQ(nombre_categoria) values ("Legales"), ("Contables"), ("Tecnológicas"), ("Generales");
insert into CATEGORIAS_INCIDENCIA(nombre_categoria) Values ("Legal"), ("Contable"), ("Tecnológica"), ("General");
insert into ESTADOS_ACTIVIDAD(nombre_estado) values ("Abierto"), ("En curso"), ("Terminada"), ("Diferida"), ("Cancelada");
insert into UNIDADES_TRABAJO_ACTIVIDAD(nombre_unidad) Values ("Horas trabajadas"), ("Registros digitalizados"), ("Documentos revisados");
insert into TIPOS_SERVICIO_CUENTA(nombre_tipo) values ("Legal"), ("Contable"), ("Tecnológicas"), ("Generales"), ("Legal y contable"), ("Contable y tecnológico"), ("Todos");