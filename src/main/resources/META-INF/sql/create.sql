DROP TABLE parametros CASCADE;
DROP TABLE usuarios CASCADE;
CREATE TABLE parametros
(
    codigo    varchar(30),
    nombre    varchar(60),
    tipo      varchar(10),
    valor     varchar(120),
    eliminado boolean DEFAULT false,
    estado    boolean DEFAULT true,
    PRIMARY KEY (codigo)
);
CREATE TABLE usuarios
(
    username              varchar(50),
    nombres               varchar(150),
    apellido              varchar(150),
    edad                  smallint,
    fnacimiento            date
    PRIMARY KEY (username)
);