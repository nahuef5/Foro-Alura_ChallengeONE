CREATE TABLE usuarios(
     id BIGINT NOT NULL AUTO_INCREMENT,
     nombre VARCHAR(60) NOT NULL,
     apellido VARCHAR(60) NOT NULL,
     fecha_nacimiento DATE NOT NULL,
     pais VARCHAR(60) NOT NULL,
     provincia VARCHAR(60) NOT NULL,
     localidad VARCHAR(60) NOT NULL,
     email VARCHAR(60) NOT NULL,
     contrasena VARCHAR(60) NOT NULL,
     activo BOOLEAN DEFAULT FALSE,
     PRIMARY KEY (id)
);