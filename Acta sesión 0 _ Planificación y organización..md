# Acta sesión 2: Pruebas con Curl
## Información de la reunión.
**Fecha:** 25 de Abril del 2022
**Hora:** 17:30h
**Identificador del grupo:** 3ti21_G2
**Tipo de reunión:** Online
**Plataforma:** Discord
**Asistentes:**
- Sales García ,Gabriel
- Malerba, Alessio
- Pomplio, Matteo

## Resumen de la reunión:
En última reunión hemos desarrollado la última parte del primer hito. Esta consta de la interacción con la API rest   "centroEducativo"por medio de órdenes "Curl". También hemos fijado las tareas necesarias para la siguiente reunión y hemos determinado como se hará la entrega del hito 1.

## 1. Órdenes Curl:
El objetivo es familiarizarse con la aplicación Centro educativo. Para ello utilizaremos las órdenes curl, una pequeña aplicación, de todos los sitemas Unix, que nos permite realizar peticiones http y recibir respuestas. Son simplemente pruebas que hemos realizado.
Como ayuda, se ha empleado la documentación de la API accesible desde la página <code>/Centro/Educativo/swagger-ui.html</code>.  Desde aquí, podemos seleccionar una operación, consultar detalles e invocarla. Para este proceso hemos seguido los siguientes pasos:

(1)  Instalamos meditante la URL:[http://memex.dsic.upv.es:8080/instalarJava8_CE.sh.gz](link){:target="_blank"} la versión Java8. Debido a que la evolución de Java envejece aplicaciones con dependencias, es necesaria esta versión para poder ejcutar Centro educativo. Instalamos mediante el comando: ````sudo apt -y install openjdk-8-jre-headless````

(2)  Ponemos en marcha la aplicación ejecutando: ````/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java -jar es.upv.etsinf.ti.centroeducativo-0.2.0.jar````
Ya podemos empezar con las órdenes CURL.

(3) Inició sesión: A partir de un identificador (usuario) y una contraseña, devuelve una KEY o clave de sesión. La usaremos a modo de "Token" y es necesaria para las siguientes operaciones.
- Respuesta: ij677rp0kh4371tgc5sopuctpo
- ````KEY=$(curl -s --data '{"dni":"23456733H","password":"123456"}' \ -X POST -H "content-type: application/json" http://$URL.dsic.cloud:9090/CentroEducativo/login \ -H "accept: application/json" -c cucu -b cucu)````

(4)  Consultar alumno y sus asignaturas: Devuelve un documento JSON con una entrada por alumno encontrado. Se muestra información del alumno (nombre, apellido, password y dni) y las asignturas en las que esta matriculado.
- ````user@dew-mpompil-2122:~$ curl -s -X GET 'http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnosyasignaturas?key=ij677rp0kh4371tgc5sopuctpo' \ -H "accept: application/json" -c cucu -b cucu````
- Respuesta: 
 ````[{"apellidos":"Garcia Sanchez","password":"123456","nombre":"Pepe","asignaturas":["DCU","IAP","DEW"],"dni":"12345678W"},{"apellidos":"Fernandez Gómez","password":"123456","nombre":"Maria","asignaturas":["DEW","DCU"],"dni":"23456387R"},{"apellidos":"Hernandez Llopis","password":"123456","nombre":"Miguel","asignaturas":["DCU","IAP"],"dni":"34567891F"},{"apellidos":"Benitez Torres","password":"123456","nombre":"Laura","asignaturas":["IAP","DEW"],"dni":"93847525G"},{"apellidos":"Alonso Pérez","password":"123456","nombre":"Minerva","asignaturas":[],"dni":"37264096W"}]````

 (5)  Añadir alumno: Añade un alumno a la BD del sistema. Además devuelve un mensaje de Ok. Para este caso hemos tenido que iniciar sesión con un rol administrativo. Hemos hecho lo mismo que en el punto 3, pero con 'dni=111111111' y 'pasword=654321'. Nos ha devuelto una nueva key que almacenamos en la variable '$KEY'.
 - ````KEY= $(curl -s --data '{"dni":"111111111","password":"654321"}' \ -X POST -H "content-type: application/json" http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/login \ -H "accept: application/json" -c cucu -b cucu)````
 Ahora, añadimos alumno (usamos la nueva KEY):
- ````user@dew-mpompil-2122: ~$curl -s --data '{"apellidos": "Rodrigo Sanchez", "dni": "33445566X", "nombre": "Bienvenido","password": "inkreible"}' \ -X POST -H "content-type: application/json" 'http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnos?key=%27$KEY \  -c cucu -b cucu ````
 Para comprobar el resultado hemos vuelto a hacer una nueva consulta GET para alumnos y asignaturas, tal como hemos hecho en el punto 5. Este ha sido el resultado:
- Respuesta:
  ````[{"apellidos":"Garcia Sanchez","password":"123456","nombre":"Pepe","asignaturas":["IAP","DCU","DEW"],"dni":"12345678W"},{"apellidos":"Fernandez Gómez","password":"123456","nombre":"Maria","asignaturas":["DCU","DEW"],"dni":"23456387R"},{"apellidos":"Hernandez Llopis","password":"123456","nombre":"Miguel","asignaturas":["IAP","DCU"],"dni":"34567891F"},{"apellidos":"Benitez Torres","password":"123456","nombre":"Laura","asignaturas":["DEW","IAP"],"dni":"93847525G"},{"apellidos":"Alonso Pérez","password":"123456","nombre":"Minerva","asignaturas":[],"dni":"37264096W"},{"apellidos":"Rodrigo Sanchez","password":"inkreible","nombre":"Bienvenido","asignaturas":[],"dni":"33445566X"}]````
Vemos que al final del fichero JSON devuelto se encuentra en alumno que habíamos insertado.


## 2 Próximas tareas:
Consideramos que ya hemos acabado el trabajo necesario para este primer hito. Fijaremos otra reunion para el próximo jueves 28 de Mayo de 2022. Simplemente, repasaremos todo el trabajo que hemos hecho y un miembro del equipo se encargará de subir el documento a la tarea abierta. 
Hemos considerado que ya que es una reunion "informal" no será necesario redactar un acta a no ser que se realice algún cambio importante.
Esperaremos a la próxima clase de laboratorio para fijar los objetivos y la organización para el próximo hito.

Firmado: Gabriel Sales García el 28 de Abril de 2022.



