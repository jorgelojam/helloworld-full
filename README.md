# Ejemplo completo de backend (Java API for RESTful Web Services)

Necesita de un wildfly 20.x al que se le agrega el perfil de microprofile con la finalidad de utilizar caracteristicas como openapi, opentracing entre otras.

Para verificacion simple:

```bash
mvnw verify
```

Para compilar se incluye el plugins de docker para empaquetar todo el proyecto:

```bash
mvnw clean package docker:build
```

Para correr solo el contenedor de la aplicacion se debe ejecutar lo siguiente:

```bash
docker run -p 8080:8080 -p 9990:9990 --rm -it jtux/helloword-full
```

Para levantar el contenedor de la base de datos y el de aplicaciones, además de las dependencias; se debe ubicarse en el directorio donde se encuentra el archivo docker-compose.yml y ejecutar

```bash
docker-compose up
```

Para determinas las direcciones IP de los contenedores se debe consultar de la siguiente manera:

```bash
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' srvkeycloak
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' srvwildfly
```

Con estas direcciones se debe registrar en la maquina fisica en el archivo host para no tener problemas con validaciones de origenes en las cabeceras HTTP

Una vez iniciados los contenedores y registrado los hosts, se puede consumir los servicios publicos como se muestra a continuacion:

```bash
curl http://srvwildfly:8080/helloworld-full/api/parametros
```

Se debe acceder al servidor keycloak por medio de la siguiente URL

http://srvkeycloak:8080

Con las credenciales de administrador se debe crear un realm importando el archivo helloworldfull.json y dentro de este realm se debe crear usuario y asignar a lo roles definidos

Para emitir un token se debe autentificar con alguno de los usuarios creados, por ejemplo:

```bash
export TOKEN=$(\
curl -s -L -X POST 'http://srvkeycloak:8080/auth/realms/helloworldfull/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=helloworldfull-mp' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'scope=openid' \
--data-urlencode 'username=admin1' \
--data-urlencode 'password=password1'  | jq --raw-output '.access_token' \
 )
```

En el caso de que se requiere ver el contenido del JWT se puede ejecutar lo siguiente:

```bash
JWT=`echo $TOKEN | sed 's/[^.]*.\([^.]*\).*/\1/'`
echo $JWT | base64 -d | python -m json.tool
```

Una vez que se tiene el token se puede consumir el servicio utilizando el JWT generado, como se muestra a continuacion:

```bash
curl -H "Authorization: Bearer $TOKEN" http://srvwildfly:8080/helloworld-full/rest/param
```