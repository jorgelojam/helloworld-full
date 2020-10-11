insert into parametros(codigo, nombre, tipo, valor)
values ('timeout', 'Tiempo de inactividad de sesión (minutos)', 'entero', '30');
insert into parametros(codigo, nombre, tipo, valor)
values ('smtp_host', 'Host SMTP', 'texto', 'smtp.gmail.com');
insert into parametros(codigo, nombre, tipo, valor)
values ('smtp_port', 'Puerto SMTP', 'entero', '465');
insert into parametros(codigo, nombre, tipo, valor)
values ('smtp_username', 'Usuario SMTP', 'texto', 'keycloak.test@gmail.com');
insert into parametros(codigo, nombre, tipo, valor)
values ('smtp_password', 'Contraseña SMTP', 'texto', 'adminkeycloak');
insert into parametros(codigo, nombre, tipo, valor)
values ('user_inactivity', 'Tiempo de inactividad usuario (dias)', 'entero', '60');
INSERT INTO usuarios(username, nombres, apellido, edad, fnacimiento)
VALUES ('admin', 'Administrador', 'Adm', 30, current_date);
INSERT INTO usuarios(username, nombres, apellido, edad, fnacimiento)
VALUES ('monitor', 'Monitor', 'Mon', 30, current_date);
INSERT INTO usuarios(username, nombres, apellido, edad, fnacimiento)
VALUES ('supervisor', 'Supervisor', 'Sup', 30, current_date);
INSERT INTO usuarios(username, nombres, apellido, edad, fnacimiento)
VALUES ('usuario', 'Usuario', 'Usu', 30, current_date);