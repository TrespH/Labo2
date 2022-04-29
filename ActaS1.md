# <span style = "color: #0070C0"> Acta Sesión 1 - Experimentación básica </span>

## <span style = "color: #0070C0"> 0. Información de la reunión </span>

**Fecha:** Miércoles, 28 de abril de 2021 

**Hora:** 11:30 

**Identificador del grupo:** 3TI12_g04

**Tipo de reunión:** Online

**Ubicación:** Plataforma Microsoft *Teams* en el equipo ‘DEW – 3TI12_g04’ 

**Asistentes:**   
Camarena Conde, Jorge  
Germes Sisternas, Adrian  
Michis, Stefan Vasile  
Pruñonosa Soler, Guillem  
Serrano López, Laura  
Úbeda Campos, Víctor

**Documento firmado por:**   
        
    Stefan Vasile Michis  
    Secretario

## <span style = "color: #0070C0"> 1. Resumen </span>
- **Uso de formularios para interactuar con los servlets log**  
Se ha creado una serie de servlets log y estudiado cómo utilizar los formularios que interactuarán con los servlets log de la aplicación final.
- **Documentación necesaria para el usuario**  
Tras la realización y estudio de los servlets se ha generado una documentación de interes sobre su uso dirigida al usuario.  
- **Órdenes "Curl" (Invocaciones con detalles HTTP)**  
 También se ha estudiado una secuencia de órdenes para interactuar con la aplicación CentroEducativo. 
- **Interacciones con un servidor REST**
Además, se ha contado con la extensión RESTED para Firefox como alternativa para interactuar con la aplicación. 
- **Nuevas tareas a preparar**  
Por último, se ha acordado la fecha de realización de las tareas a preparar para la siguiente sesión.

## <span style = "color: #0070C0"> 2. Uso de formularios para interactuar con los servlets log </span>
Se ha diseñado tres prototipos incrementales de log (log0, log1 y log2), los cuales han sido puestos a prueba mediante un formulario.

### <span style = "color: #0070C0"> Servlet Log0 </span>

Este log tiene únicamente como función imprimir la información por pantalla en un documento html. Recogemos la toda la información en unos Strings recogiendo información del Servlet y de quién hace la petición (Request). Para obtener la fecha utilizamos el objeto <code>LocalDateTime</code> . Una vez tengamos concatenada toda la información creamos una sesión con la que guardaremos toda la información, esa sesión será sobreescrita cada vez que se envie el formulario y será lo que se escriba en el documento .html que verá el usuario.

### <span style = "color: #0070C0"> Servlet Log1 </span>

Además de imprimir por pantalla la información por pantalla, este servlet guarda la información en un archivo llamado <code>logs.txt</code>. Para poder crear/modificar este archivo
utilizamos los metodos de las librerías File y FileWriter, en este servlet creamos el archivo <code>logs.txt</code> en un directorio que este dentro del servlet (en Log2 esto no será así). Podemos recoger la dirección en la que se encuentra el servlet con <code>this.getServletContext().getRealPath("/")</code>, a esta dirección añadimos o bien un directorio interior en el que deseemos guardar el servlet o bien añadimos directamente el String<code>logs.txt</code> para crearlo en ese mismo directorio. Una vez tenemos la dirección procedemos a crear/modificar el archivo. El método <code>.createFile()</code> permite comprobar si existe el archivo en dicha dirección, en caso de que no exista dicho archivo, se crea, sino, no sucede nada. FileWriter permite modificar archivos, debemos crear un objeto FileWriter con el archivo anterior y con el parametro <code>true</code> para que nos sea permitido sobreescribir el archivo. Utilizaremos el método <code>append</code> para sobreescribir.

### <span style = "color: #0070C0"> Servlet Log2 </span>

La única diferencia que reside en este log es la forma de obtener la dirección en la que crear/modificar el archivo, en vez de escribirlo directamente en el código, recurrimos a <code>web.xml</code> para saber cuál es nuestra dirección. Para ello, debe hacer un parámetro en <code>web.xml</code> que nos indique cuál es el nombre del archivo y cuál es su dirección:

	  		<context-param>
    			<param-name>contextName</param-name>
    			<param-value>logs.txt</param-value>
  			</context-param>
    		<context-param>
    			<param-name>contextPath</param-name>
    			<param-value>/home/user/Escritorio/</param-value>
  			</context-param>


En este caso accedemos a un directorio que no necesita permisos de administrador para ser accedido, en caso de que intentemos acceder a un directorio que si que los necesite, en la máquina que tengamos el servidor tomcar deberemos ejecutar el script <code>startup.sh</code> con permisos de administrador, de esta forma todos los directorios serán accesibles para el servidor. 

En nuestro caso recogeremos 2 variables del <code>web.xml</code> utlizando <code>request.getInitParameter(*nombre_del_parámetro*)</code>, las sumaremos y obtendremos nuestra dirección.

## <span style = "color: #0070C0"> 3. Documentación necesaria para el usuario </span>

### <span style = "color: #0070C0"> Consulta de logs </span>
Para la consulta de cualquier log simplemente es necesario un usuario y una contraseña. En este caso, cualquier par no nulo de estos valores es válido pues no está implementada la autenticación.
### <span style = "color: #0070C0"> Ubicación de los ficheros generados </span>
En el caso de Log1, la ubicación del fichero generado estará en la misma ruta de ejecución del proyecto no obstante, la ubicación del fichero generado en Log2 estará indicada en web.xml como la suma de *contextName* y *contextPath*. Ambas ubicaciones serán indicadas al generar los Logs.

## <span style = "color: #0070C0"> 4. Órdenes "Curl" (Invocaciones con detalles HTTP) </span>
Con el objetivo de familiarizarse con la aplicación CentroEducativo, puesta en marcha ejecutando el comando <code>java -jar es.upv.etsinf.ti.centroeducativo-0.2.0.jar</code>, y con la ayuda de las órdenes CURL, se han emitido una serie de peticiones HTTP y observado las respuestas recibidas.  

Como ayuda, se ha empleado la documentación de la API accesible desde la página <code>/Centro/Educativo/swagger-ui.html</code>, donde se puede seleccionar una operación, consultar los detalles de la misma e incluso invocarla.

#### Inicio sesión usuario: devuelve una 'key' que sirve como 'token' que debe ser usado a lo largo de la sesión en la utilización de los métodos, como demostración de que se trata de un usuario ya identificado. ####

- Operación invocada por POST, codificando en el cuerpo del mensaje un objeto JSON con el dni y la clave del usuario.

curl -s --data '{"dni":"23456733H","password":"123456"}' \ -X POST -H "content-type: application/json" http://dew-login-2021.dsic.cloud:9090/CentroEducativo/login \ -H "accept: application/json" -c cucu -b cucu
 
    Respuesta: jtegljko24tb962ta4ehot2fn8


#### Inicio sesión administrador: devuelve una 'key' que sirve como 'token' que debe ser usado a lo largo de la sesión en la utilización de los métodos, como demostración de que se trata de un administrador ya identificado. ####

- Operación invocada por POST, codificando en el cuerpo del mensaje un objeto JSON con el dni y la clave del administrador.

curl -s --data '{"dni":"111111111","password":"654321"}' \ -X POST -H "content-type: application/json" http://dew-login-2021.dsic.cloud:9090/CentroEducativo/login \ -H "accept: application/json" -c cucu -b cucu

    Respuesta: f13hgbri16dbv8oimm3077aom0


#### Consultar lista de alumnos: devuelve un vector JSON en el que aparece una entrada por cada alumno encontrado. Se muestra la información de dicho alumno. ####

- Operación invocada por GET, sin argumentos adicionales.

curl -s -X GET 'http://dew-login-2021.dsic.cloud:9090/CentroEducativo/alumnos?key='f13hgbri16dbv8oimm3077aom0 \ -H "accept: application/json" -c cucu -b cucu

    Respuesta: [{"dni":"12345678W","nombre":"Pepe","apellidos":"Garcia Sanchez"},{"dni":"23456387R","nombre":"Maria","apellidos":"Fernandez Gómez"},{"dni":"34567891F","nombre":"Miguel","apellidos":"Hernandez Llopis"},{"dni":"93847525G","nombre":"Laura","apellidos":"Benitez Torres"},{"dni":"37264096W","nombre":"Minerva","apellidos":"Alonso Pérez"}]


#### Consultar alumnos y sus asignaturas: devuelve un vector JSON en el que aparece una entrada por cada alumno encontrado. Se muestra la información de dicho alumno y las asignaturas en las que está matriculado. ####

- Operación invocada por GET, sin argumentos adicionales.

curl -s -X GET 'http://dew-login-2021.dsic.cloud:9090/CentroEducativo/alumnosyasignaturas?key='f13hgbri16dbv8oimm3077aom0 \ -H "accept: application/json" -c cucu -b cucu

    Respuesta: [{"apellidos":"Garcia Sanchez","password":"123456","nombre":"Pepe","asignaturas":["DEW","DCU","IAP"],"dni":"12345678W"},{"apellidos":"Fernandez Gómez","password":"123456","nombre":"Maria","asignaturas":["DCU","DEW"],"dni":"23456387R"},{"apellidos":"Hernandez Llopis","password":"123456","nombre":"Miguel","asignaturas":["DCU","IAP"],"dni":"34567891F"},{"apellidos":"Benitez Torres","password":"123456","nombre":"Laura","asignaturas":["DEW","IAP"],"dni":"93847525G"},{"apellidos":"Alonso Pérez","password":"123456","nombre":"Minerva","asignaturas":[],"dni":"37264096W"}]


#### Añadir alumno: devuelve un mensaje 'OK'. Agrega un alumno a la base de datos del centro educativo. ####

- Operación invocada por POST, codificando en el cuerpo del mensaje un objeto JSON con el apellido, dni, nombre y password del alumno.

curl -s --data '{"apellidos": "Prueba", "dni": "99999999X", "nombre": "Prueba","password": "pass"}' \ -X POST -H "content-type: application/json" 'http://dew-login-2021.dsic.cloud:9090/CentroEducativo/alumnos?key='f13hgbri16dbv8oimm3077aom0 \ -H "accept: application/json" -c cucu -b cucu

    Respuesta: OK


#### Matricular alumno en una asignatura: devuelve un mensaje 'OK'. Matricula al alumno en la asignatura. ####

- Operación invocada por POST, codificando en el cuerpo del mensaje el dni del alumno.

curl -s --data '99999999X' \ -X POST -H "content-type: application/json" 'http://dew-login-2021.dsic.cloud:9090/CentroEducativo/asignaturas/DEW/alumnos?key='f13hgbri16dbv8oimm3077aom0 \ -H "accept: application/json" -c cucu -b cucu

    Respuesta: OK


#### Consultar información de una asignatura: devuelve un objeto JSON con la información de la asignatura. ####

- Operación invocada por GET, sin argumentos adicionales.

curl -s -X GET 'http://dew-login-2021.dsic.cloud:9090/CentroEducativo/asignaturas/DEW?key='f13hgbri16dbv8oimm3077aom0 \ -H "accept: application/json" -c cucu -b cucu

    Respuesta: {"acronimo":"DEW","nombre":"Desarrollo Web","curso":3,"cuatrimestre":"B","creditos":4.5}


#### Consultar lista de alumnos de una asignatura: devuelve un vector JSON en el que aparece una entrada por cada alumno matriculado en la asignatura. Se muestra el dni de dicho alumno y su nota. ####

- Operación invocada por GET, sin argumentos adicionales.

curl -s -X GET 'http://dew-login-2021.dsic.cloud:9090/CentroEducativo/asignaturas/DEW/alumnos?key='f13hgbri16dbv8oimm3077aom0 \ -H "accept: application/json" -c cucu -b cucu

    Respuesta: [{"alumno":"12345678W","nota":""},{"alumno":"23456387R","nota":""},{"alumno":"93847525G","nota":""},{"alumno":"99995566X","nota":"0.0"}]


#### Eliminar alumno: devuelve un mensaje 'OK'. Elimina al alumno de la base de datos del centro educativo. ####

- Operación invocada por DELETE, sin argumentos adicionales.

curl -s -X DELETE 'http://dew-login-2021.dsic.cloud:9090/CentroEducativo/alumnos/99999999X?key='f13hgbri16dbv8oimm3077aom0 \ -H "accept: application/json" -c cucu -b cucu

    Respuesta: OK

## <span style = "color: #0070C0"> 5. Interacciones con un servidor REST </span>

Como alternativa a las órdenes CURL, se ha contado con la extensión RESTED para Firefox. Mediante esta extensión se puede interactuar con la API REST, seleccionando método, contenido de cabeceras, etc. A continuación, se muestran dos imágenes de como se puede interactuar con CentroEducativo desde la extensión RESTED.

En la primera imagén se muestra una la operación "login", por POST, codificando en el cuerpo del mensaje un objeto JSON con el dni y clave del actor. La respuesta recibida es una confirmación con código HTTP 200. Además, devuelve una 'key' que sirve como 'token' de acceso para poder realizar consultas o modificaciones sobre Centro Educativo.

![rest1](http://personales.alumno.upv.es/jorcacon/data/rested1.png)

En esta segunda imagen se ha realizado una consulta invocada por GET, con la 'key' obtenida en el login, para obtener los alumnos que pertenecen al Centro Educativo. Como se puede observar, RESTED devuelve un objeto JSON mostrando un listado con los alumnos pertenecientes al Centro Educativo.

![rest2](http://personales.alumno.upv.es/jorcacon/data/rested2.png)

## <span style = "color: #0070C0"> 6. Nuevas tareas a preparar </span>

**Las tareas a preparar para la siguiente sesión son:**
- Repasar el funcionamiento de la interacción con servicios REST (uso de CURL para operaciones automatizadas)  
- Estudiar el uso de bibliotecas JAVA para realizar interacciones con los servicios REST
- Comprender la autenticación y mantenimiento de sesiones en diferentes niveles del servicio

**Fecha acordada para la realización:** Domingo 02 de Mayo a las 10:00