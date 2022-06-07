## <span style = "color: #00a3e8"> MEMORIA APLICACIÓN NOL </span>
Hemos creado una aplicación de Notas Online **NOL** para gestionar las calificaciones de los alumnos.

Podremos entrar a la aplicación como alumnos o como profesores.
- En el caso de los **alumnos**, podrán consultar sus asignaturas y sus notas.

- En el caso de los **profesores** podrán consultar las asignaturas que imparte, los alumnos matriculados y asignarles una nota.

Hemos utilizado:

- **Bootstrap** para el acabado uniforme de todas las páginas
- Logs como filtro controlable
- Versión **AJAX** 
- Autenticación básica (Basic)
- **Expediente final** el cual sera imprimible


Tenemos una aplicación en la que interactúan 3 niveles:
1. El nivel de **interfaz de usuario** se implementa mediante las páginas de hipertexto devueltas por el servidor web y mostradas por el navegador del cliente.
2. El nivel de **aplicación** (NOL, en nuestro caso) contiene la lógica para atender las solicitudes interactuando con la información disponible.
3. El nivel de **datos** (CentroEducativo, en nuestro caso) mantiene los datos persistentes que ofrece al nivel de aplicación. 




## <span style = "color: #00a3e8"> Índice </span>

1. [Información del equipo](#item1)
2. [Partes significativas del código](#item2)
3. [Interacciones con AJAX](#item3)
4. [Cómo se insertan las informaciones en las páginas?](#item4)
5. [Anotaciones de accesos](#item5)
6. [Anexo: Actas Hito 1](#item6)
    6.1 [Acta Sesion 0](#item6.1)
    6.2 [Acta 1 Sesion 1](#item6.2)
    6.3 [Acta 2 Sesion 1](#item6.3)
7. [Anexo: Actas Hito 2](#item7)
    7.1 [Acta 1 Sesion 2](#item7.1)
    7.2 [Acta 2 Sesion 2](#item7.2)
    7.3 [Acta 3 Sesion 2](#item7.3)
8. [Anexo: Actas Hito 3](#item8)
    8.1 [Acta 1 Sesion 3](#item8.1)
    8.2 [Acta 2 Sesion 3](#item8.2)
    8.3 [Acta 3 Sesion 3](#item8.3)

## <span style = "color: #00a3e8"> 1. Información del equipo</span>

Turno de laboratorio: DEW_Labo3 L_17.00h (3TI21)

Identificador de grupo: 3ti21_G2

Miembros del grupo: 
- Sales García, Gabriel

- Malerba, Alessio

- Pompilio, Matteo

- Lopez Honrubia, Pablo

**Máquina de portal en la que se ha dejado instalada la aplicación:**

El servidor que hemos utilizado es el de nuestro compañero Mateo, dew-mpompil-2021.dsic.cloud


**Arrancar la aplicación**

1. Primero iniciamos la maquina dew del portal de dsic que accedemos a través de *https://portal-ng.dsic.cloud/virtual-machines/*
2. Accedemos a la máquina de portal utilizando el comando ssh
    ssh user@dew-mpompil-2122.dsicv.upv.es
3. A continuación, arrancamos el servicio de CentroEducativo
````
/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java -jar es.upv.etsinf.ti.centroeducativo-0.2.0.jar
````
4. Subimos al servidor tomcat el archivo .WAR que ejecutaremos
````
scp NOL.war user@dew-mpompil-2122.dsicv.upv.es:tomcat/webapps
````
Aqui hemos indicado el directorio de la máquina donde queremos guardar el archivo, tomcat/webapps.

5. Arrancamos el servidor tomcat: 
````
tomcat/bin/startup.sh
````
6. Accedemos a nuestro servicio web con :
````
http://dew-mpompil-2122.dsicv.upv.es:8080/NOL/
````
7. Detener los servicios
````
   tomcat/bin/shutdown.sh
````

## <span style = "color: #00a3e8"> 2. Partes significativas del código</span>

### 1.Autenticación 

Este filtro se activará cada vez que llamemos a los servlets de "LoginAlumno" y "LoginPro". Como resultado se crea una KEY que necesitaremos para hacer las consultas al Centro Educativo. Cuando generamos la KEY debemos guardala como un parámetro de la sesión. Cuando tenemos la KEY generada la mantenemos gracias al uso de las cookies.
````
String url = request.getLocalName();
            
           try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookies).build()) {
                HttpPost post = new HttpPost("http://" + url + ":9090/CentroEducativo/login");
                post.setEntity(entity);
                post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                CloseableHttpResponse resp = httpclient.execute(post);
                sesion.setAttribute("cookie", cookies.getCookies());
                String t = EntityUtils.toString(resp.getEntity());                              
                httpclient.close();
                sesion.setAttribute("key", t);
            }           
            catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 

````

### 2. Roles diferentes

Como ya sabemos, el profesor no tiene las mismas funciones que el alumno, el profesor tiene la "ventaja" de poder modificar las notas de los alumnos. Para ello hemos creado dos roles diferentes, rolpro y rolalu. 

En el loginProf le asignamos con setAttribute() el **rolpro**: 
````
  session.setAttribute("rol", "rolpro");
        String parametro_asignatura = "acronimo";
        String asignaturas = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/profesores/" + dni + "/asignaturas/?key=" + key))
                .returnContent().toString();
````

En el loginAlumno le asignamos con setAttibute() el **rolalu**:
````
    session.setAttribute("rol", "rolalu");
        String parametro_asignatura = "asignatura";
        String asignaturas = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/alumnos/" + dni + "/asignaturas/?key=" + key))
                .returnContent().toString();
````

### 3. Fotos

Para añadir las fotos, primero renombramos las fotos del archivo **fotos.zip** y las colocamos en su lugar correspondiente. 
Con un método GET devuelve un objeto JSON que contiene el DNI y la imagen recuperada del fichero(codificado en base64).
````
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String f = request.getParameter("dni"); 
        ServletContext context = getServletContext();
        String carpeta = context.getRealPath("/WEB-INF/imgs");	    
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        BufferedReader origen = new BufferedReader(new FileReader(carpeta + "/" + f +".pngb64"));
        PrintWriter out = response.getWriter();
        out.print("{\"dni\": \"" + f + "\", \"img\": \"");
        String linea = origen.readLine(); 
        out.print(linea);
        while ((linea = origen.readLine()) != null) {out.print("\n"+linea);}
        out.print("\"}");
        out.close(); 
        origen.close();
    }
````

## <span style = "color: #00a3e8"> 3. Interacciones con AJAX</span>

Hemos utilizado **AJAX** para comunicar los servlets en 4 ocasiones:

- Para la comunicacion de **Detalles** con **Foto** con el que añadimos la foto del alumno.

- Para la comunicacion de **Detalles** con **ModificacionNotas** con el que el profesor puedo cambiar la calificación de los alumnos.

- Para la comunicacion de **LoginAlumno** con **LogOut** con el que podemos cerrar la sesion como alumno

-  Para la comunicacion de **LoginProf** con **LogOut** con el que podemos cerrar la sesion como profesor


## <span style = "color: #00a3e8"> 4. Cómo se insertan las informaciones en las páginas?</span>

Para poder mostrar información por la pantalla utilizamos el objeto Printwriter con el que mostramos la salida de texto. 
````
String titulo = "<!DOCTYPE html>" + 
                "<html lang=\"es-es\">" + 
                "<head>" + 
                "<meta charset=\"UTF-8\">" + 
                "<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p\" crossorigin=\"anonymous\"></script>" +
                "<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3\" crossorigin=\"anonymous\">" +
                "<title>Expediente</title>" +
                "<script src=\"https://code.jquery.com/jquery-3.6.0.min.js\"></script>" +
                "</head>";
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(titulo);
````

Por ejemplo, para que un profesor sepa los alumnos que tiene en una asignatura, hacemos la llamada necesaria a CentroEducativo y :
````
out.println(titulo);
        String acronimo = request.getParameter("acronimo");
        out.println("<body>" + 
                "<div class=\"row\">" + 
                "<h1><b>Notas OnLine. </b>Alumnos de la asignatura " + acronimo + "</h1>" + 
                "<p><b>Aqu� tienes sus alumnos</b></p>" + 
                "</div><br><div>");
`````

## <span style = "color: #00a3e8"> 5. Anotaciones de accesos</span>


Por problemas de tiempo no hemos podido implemntar este apartado, pero si vamos a ver una explicación de como tendría que haberse hecho.
Es un proceso similar que el que hicimos en el hito1. Debemos guardar información sobre el acceso en la misma máquina donde se este ejecutando la aplicación en ese momento.
Esto ya lo hicimos, la diferencia es que ahora hay que implementarlo por medio de un filtro.
El filtro se encargará de registrar el acceso a cualquier servlet de nuestra aplicación (que vea el usuario. Servlets como "foto" no, porque es una cosa ajena al usuario). Por ello tenemos que modificar el archivo web.xml:
````
<filter>
    <display-name>Registrar</display-name>
    <filter-name>Registrar</filter-name>
    <filter-class>Registrar</filter-class>
</filter>
<filter-mapping>
<filter-name>Registrar</filter-name> 
    <servlet-name>LoginAlumno</servlet-name>
    <servlet-name>LoginProf</servlet-name>
    <servlet-name>Detalles</servlet-name>
    <servlet-name>ListaAlumnos</servlet-name>
    <servlet-name>Expediente</servlet-name>
</filter-mapping>     
````
Posteriormente deberíamos crear el filtro dentro de nuesto proyecto. El código sería muy siminar al que hicimos en el anterior hito1.
Anotaríamos:
- URI
- Método de acceso
- Usuario de acceso
- Nombre servlet
- Fecha de acceso


## <span style = "color: #00a3e8"> 6. Anexo: Actas Hito 1</span>

## <span style = "color: #00a3e8"> 6.1. Acta Sesion 0</span>

# Acta sesión 0 : Planificación y organización.
### Información de la reunión:
**Fecha:**  10/04/2022

**Hora:**  15:00 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Online

**Plataforma:** Discord

**Asistentes:**
- Sales García, Gabriel

- Malerba, Alessio

- Pompilio, Matteo


### 1. Resumen de la reunión:
Hemos determinado todos los aspectos fundamentales para la organización y desarrollo de un "Trabajo en equipo", así como marcar los objetivos tanto para  del equipo como para cada miembro del mismo y ciertas reglas que deberemos cumplir para que todo fluya como se espera.
Asimismo cada asistente, ha hecho su exposición correspondiente sobre el documento *Aspectos esenciales para el trabajo en equipo*. La finalidad de dicha exposición ha sido averiguar puntos fuertes y/o debilidades de la expresividad de cada miembro.
Por último también hemos fijado objetivos para la siguiente reunión.

### 2. Aspectos fundamentales para el trabajo en equipo:
- **Medios de contacto:** Aplicación *"Whattsap"* para organizar las reuniones y comunicación rápida. Las reuniones se desarrolaran vía *"Discord"*.
- **Horario de las reuniones:** Horario de laboratorio (Lunes-17:00 h / 18:30) y domingos entre (17:00h - 20:00h). Aunque se planearán en función de la necesidad y disponibilidad de cada miembro. 
- **Déposito de material:** Hemos creado una cuenta en GitHub para guardar todo el trabajo. Nos parecía la via más sencilla para compartir.
- **Reglas a cumplir:**    
    1. Respeto entre los integrantes del grupo.
    2. Compromiso con el trabajo en equipo, es decir realizar el trabajo necesario, conectarsese a las reuniones ...
    3. Compromiso a hacer un trabajo de calidad.
    4. Hemos determinado también que el desarrollo de cada acta se repartirá entre los miembros. Cada vez hará alguien distinto el acta. Empieza el alumno Gabriel Sales. El resto de trabajo se realizará durante las reuniones conjuntas.
    
- **Motivos de expulsion del tgrabajo de equipo:** No cumplir con las reglas descritas anteriormente.
- **Expectativas del proyecto**: Hemos estado debatiendo que es lo que esperamos conseguir con este trabajo, tanto a nivel académico como personal. Hemos llegado a una conclusión similar: Aspirar a una nota notable dentro del proyecto y conseguir ampliar nuestros conocimientos en el desarrollo de la  tecnología web. 

### 3. Valoración de la exposición:
Destacamos, que hemos asistido tres personas a la reunión cuando deberíamos haber sido cinco.
Por esta razón en un origen cada uno se ha preparado el tema que le ha parecido (siguiendo la norma de que ninguna agrupación se quede sin ver) y el resto de temas los hemos debatido de forma común durante la reunón.
| Agrupación 1 - Apartados Comunicación, Objetivos, Resolución de Problemas | Agrupación 2 - Apartados Expectativas, BrainStoriming | Agrupación 3 - Apartados Conflictos, Gestión de Conflictos |
|:-------------------------------------------------------------------------:|:-----------------------------------------------------:|:----------------------------------------------------------:|
| Sales García, Gabriel                                                    | Pomplio, Matteo                       | Debate común                              |
| Debate común| Debate común    | Malerba, Alessio                                      |

Valoración de cada miembro: 

### Pomplio, Matteo:
**Valoración por parte del miembro Alessio Malerba:**  Facilidad de expresión y sintetiza lo suficiente.

**Valoración por parte del miembro Gabriel Sales:**  Buena expresividad y se sabe centrar en lo importante. Destacar que es un compañeto de erasmus y que por lo tanto tambieén esta aprendiendo castellano. Aún así, hizo la exposición en este idioma y le entendimos todos a la perfección. 


### Malerba, Alessio:
**Valoración por parte del miembro Mateo Pomplio:** La exposición oral está rápida y eficaz, con un buen uso de la expresión corporal.

**Valoración por parte del miembtro Gabriel Sales:** Destacar que es un compañeto de erasmus y que por lo tanto tambieén esta aprendiendo castellano. Aún así, hizo la exposición en este idioma y le entendimos todos a la perfección. Mensaje claro y bien resumido.

### Sales García, Gabriel:
**Valoración por parte del compañero Mateo Pomplio:** El miembro describe la sección con claridad y expresividad, el mensaje desarrollado está completamente claro.

**Valoración por parte del compañero Alessio Malerba:** A portación de ideas propias y mensaje con claridad.

### 4. Objetivos para la próxima reunión:
**Las tareas a preparar para la siguiente sesión son:**
- Creación en Eclipse de un proyecto web dinamico.
- Publicación del proyecto en la Máquina Virtual del portal.
- Leer la parte del pdf a fondo, donde se explica los puntos nececesarios para el siguiente hito.

Valencia, 11/04/2022, Gabriel Sales García


## <span style = "color: #00a3e8"> 6.2. Acta 1 Sesion 1</span>

# Acta 1 sesión 1 : Creación de los primeros Logs
### Información de la reunión:
**Fecha:**  12/04/2022

**Hora:**  17:00 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Online

**Plataforma:** Discord

**Asistentes:**

Sales García, Gabriel

Pompilio, Matteo


### 1. Resumen de la reunión:
Hemos creado un proyecto web dinámico en el cual se han creados 2 Servelts (Log0, Log1), donde Log1 está basado en evoluciónes de las funcionalidades de Log0: para cada Servlet vamos a mostrar como hemos resuelto el problema requerido.
Además, el desarrollo de una pagina *index.html*, desde la cual es posible inserir los datos del usuario (username y password) para cada Servlet diferente, así que este pueda recibir los 2 datos y eventualmente procesarlos.
En el desarrollo en generál no se han encontrados grandes problemas: el proyecto se ha crado y desarrollado enteramente en la VM, entonces el único limite para un trabajo fluido fueron las bajas prestaciones de la conexión con la máquina DEW.
### 2. Desarrollo de Log0
Se ha intervenido principalmente en la función *doGet()*, en la qual se han recogido los 2 parametros de input del usuario y otras variables propias de la solicitúd HTTP:
```java
String fechaHora =  LocalDateTime.now().toString();
String user = request.getParameter("user");
String password = request.getParameter("password");
String URI = request.getRequestURL() + request.getContextPath() + 
             request.getServletPath() + request.getPathInfo();
```
Y imprimidas en pantalla en formato HTML:
```java
response.setContentType("text/html");
PrintWriter out = response.getWriter();
out.println("<!DOCTYPE html>\n<html lang='es-es'><head><title>Log0</title></head><body><table>");
out.println("<tr><td colspan=2><b>Info</b></td><tr>");
out.println("<tr><td>Usuario</td><td>"+user+"</td><tr>");
out.println("<tr><td>Contraseña</td><td>"+password+"</td><tr>");
out.println("<tr><td>Fecha y Hora</td><td>"+fechaHora+"</td><tr>");
out.println("<tr><td>Request URI</td><td>"+URI+"</td><tr>");
out.println("<tr><td colspan=2><b>Protocolo HTTP</b></td><tr>");
out.println("<tr><td>Metodo</td><td>"+request.getMethod()+"</td><tr>");			
out.println("</table></body></html>");
```
### 3. El Servlet Log1
Se han importados las librerías *java.io.File* y *java.io.FileWriter* para la creación y la escritura de los datos en el fichero *LOG1.txt*, entre de un mecanismo try-catch:
```java
try {
  File myObj = new File(path);
  myObj.createNewFile();
  String res = fechaHora + " " + user + " " + request.getLocalAddr() + " " + URI + " acceso " + request.getMethod() + "\n";
  FileWriter textFile = new FileWriter(myObj, true);
  textFile.append(res); 
  textFile.close();
} catch (IOException e) {
  out.println("Un error ha occurrido.");
  e.printStackTrace();
}
```
Donde  el segundo argumento de *FileWriter()* se pone ```true``` para activar la modalitá *apend*, y donde la variable *path* se obtene añadendo el nombre del file a la ruta del mismo: ```this.getServletContext().getRealPath("/")```
### 4. La pagina de index
Aqí se muestra el codigo, muy sencillo, de los forms del Servlet Log0, que permiten de invocar ambos los métodos GET y POST.
Este codigo será el mismo para los 2 siguientes Servlets Log1 y Log2:
```html
<h2><span>LOG 0 - Impresión en pantalla</span></h2>
<form action="Log0" method="GET">
    <h4>MÉTODO POR GET</h4>
    Usuario: 
    <input type="text" name="user" required="required"><br>
    Contraseña: 
    <input type="password" name="password" required="required"><br>
    <input type="submit" value="Enviar">
</form>
<form action="Log0" method="POST">
    <h4>MÉTODO POR POST</h4>
    Usuario: 
    <input type="text" name="user" required="required"><br>
    Contraseña: 
    <input type="password" name="password" required="required"><br>
    <input type="submit" value="Enviar">
</form>
```
### 5. Objetivos para la próxima reunión:
**Las tareas a preparar para la siguiente sesión son:**
- Estudio del funcionamento del fichero *web.xml*
- Indrodución a los tags que permiten de crear variables de contexto

*Valencia, 13/04/2022, Matteo Pompilio*



## <span style = "color: #00a3e8"> 6.3. Acta 2 Sesion 1</span>
# Acta 2 sesión 1 : Log2 y primeras dudas.
### Información de la reunión:
**Fecha:**  21/04/2022

**Hora:**  17:30 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Online

**Plataforma:** Discord

**Asistentes:**

Sales García, Gabriel

Malerba, Alessio

Pompilio, Matteo


### 1. Resumen de la reunión:
Hemos continuado con las partes restantes de la sesión 1. Habiendo completado ya Log0 y Log1, hemos implementado una versión para Log2. El trabajo prosiguió sin obstáculos, pero luego tuvimos un pequeño problema con las partes restantes, a saber, Log3 y los comandos curl de prueba. 

### 2. Desarollo de Log2:
Para esto, hemos configurado la ruta del archivo "PrimeroMP" colocándolo en la sección context-param añadida dentro del archivo "web.xml", utilizando las etiquetas param-name y param-value. Aquì está el código añadido:

    <context-param>
        <param-name>Path</param-name>
        <param-value>/home/user/apache-tomcat-9.0.62/webapps/primeroMP/</param-value>
    </context-param>

En cuanto al código del servlet, en Log2.java utilizamos un método especial para encontrar la ruta del archivo, llamado getInitParameter. Esto fue una adición significativa al código java previamente escrito para Log0 y Log1, como puedes ver en el siguiente *snippet*:

	response.setContentType("text/html");
	String output = "";
	String path = this.getServletContext().getInitParameter("Path") + "LOG2.txt";
	PrintWriter out = response.getWriter();
	String fechaHora =  LocalDateTime.now().toString();
	String user = request.getParameter("user");
	String password = request.getParameter("password");
	String URI = request.getRequestURL() + request.getContextPath() + request.getServletPath() + request.getPathInfo();

### 3. Dudas sobre Log3:
Log3 fue el que nos detuvo, ya que no pudimos entender bien qué significaba convertir un servlet en un filtro. Por eso hemos decidido de enviar un correo al profe explicando directamente nuestras dudas, ya que es posible que el trabajo sólo deba realizarse en el futuro con conocimientos que aún no poseemos

### 4. EL comando *curl*:
Nuestro desconocimiento del comando curl, que también se utilizaba en Log2, nos hizo suspender la ruina más adelante. Hemos decidido hablar entre nosotros de todos modos en una futura reunión muy cercana para estudiar mejor y por nuestra cuenta cómo funcionaban las opciones individuales que teníamos que utilizar de la instrucción bash.

### 5. Objetivos para la próxima reunión:
**Las tareas a preparar para la siguiente sesión son:**
- Estudio del funcionamento del comando de bash *curl*
- Esperar que el profe nos conteste y discutir en el grupo whatsapp sobre que hacer sobre Log3

*Genova, 21/04/2022, Malerba Alessio*


## <span style = "color: #00a3e8"> 7. Anexo: Actas Hito 2</span>

## <span style = "color: #00a3e8"> 7.1 Acta 1 Sesion 2</span>

# Acta 1 sesión 2: Pruebas con Curl
### Información de la reunión.
**Fecha:** 25/04/2022

**Hora:** 17:30h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Online

**Plataforma:** Discord

**Asistentes:**

- Sales García ,Gabriel

- Malerba, Alessio

- Pompilio, Matteo

-Lopez Honrubia, Pablo

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

- ````KEY=$(curl -s --data '{"dni":"23456733H","password":"123456"}' \ -X POST -H "content-type: application/json" http://dew-mpompil-2122.dsic.cloud:9090/CentroEducativo/login \ -H "accept: application/json" -c cucu -b cucu)````

(4)  Consultar alumno y sus asignaturas: Devuelve un documento JSON con una entrada por alumno encontrado. Se muestra información del alumno (nombre, apellido, password y dni) y las asignturas en las que esta matriculado.
- ````user@dew-mpompil-2122:~$ curl -s -X GET 'http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnosyasignaturas?key=ij677rp0kh4371tgc5sopuctpo' \ -H "accept: application/json" -c cucu -b cucu````
- Respuesta: 
 ````[{"apellidos":"Garcia Sanchez","password":"123456","nombre":"Pepe","asignaturas":["DCU","IAP","DEW"],"dni":"12345678W"},{"apellidos":"Fernandez Gómez","password":"123456","nombre":"Maria","asignaturas":["DEW","DCU"],"dni":"23456387R"},{"apellidos":"Hernandez Llopis","password":"123456","nombre":"Miguel","asignaturas":["DCU","IAP"],"dni":"34567891F"},{"apellidos":"Benitez Torres","password":"123456","nombre":"Laura","asignaturas":["IAP","DEW"],"dni":"93847525G"},{"apellidos":"Alonso Pérez","password":"123456","nombre":"Minerva","asignaturas":[],"dni":"37264096W"}]````

 (5)  Añadir alumno: Añade un alumno a la BD del sistema. Para este caso hemos tenido que iniciar sesión con un rol administrativo. Hemos hecho lo mismo que en el punto 3, pero con 'dni=111111111' y 'pasword=654321'. Nos ha devuelto una nueva key que almacenamos en la variable '$KEY'.
 - ````KEY= $(curl -s --data '{"dni":"111111111","password":"654321"}' \ -X POST -H "content-type: application/json" http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/login \ -H "accept: application/json" -c cucu -b cucu)````
 
 Ahora, añadimos alumno (usamos la nueva KEY):

- ````user@dew-mpompil-2122: ~$curl -s --data '{"apellidos": "Rodrigo Sanchez", "dni": "33445566X", "nombre": "Bienvenido","password": "inkreible"}' \ -X POST -H "content-type: application/json" 'http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnos?key=%27$KEY \  -c cucu -b cucu ````

 Para comprobar el resultado hemos vuelto a hacer una nueva consulta GET para alumnos y asignaturas, tal como hemos hecho en el punto 4. Este ha sido el resultado:

- ````curl -s -X GET 'http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnosyasignaturas?key=%27$KEY' \ -H "accept: application/json" -c cucu -b cucu ````

- Respuesta:
  ````[{"apellidos":"Garcia Sanchez","password":"123456","nombre":"Pepe","asignaturas":["IAP","DCU","DEW"],"dni":"12345678W"},{"apellidos":"Fernandez Gómez","password":"123456","nombre":"Maria","asignaturas":["DCU","DEW"],"dni":"23456387R"},{"apellidos":"Hernandez Llopis","password":"123456","nombre":"Miguel","asignaturas":["IAP","DCU"],"dni":"34567891F"},{"apellidos":"Benitez Torres","password":"123456","nombre":"Laura","asignaturas":["DEW","IAP"],"dni":"93847525G"},{"apellidos":"Alonso Pérez","password":"123456","nombre":"Minerva","asignaturas":[],"dni":"37264096W"},{"apellidos":"Rodrigo Sanchez","password":"inkreible","nombre":"Bienvenido","asignaturas":[],"dni":"33445566X"}]````

Vemos que al final del fichero JSON devuelto se encuentra en alumno que habíamos insertado.


## 2.Próximas tareas:
Consideramos que ya hemos acabado el trabajo necesario para este primer hito. Fijaremos otra reunion para el próximo jueves 28 de Mayo de 2022. Simplemente, repasaremos todo el trabajo que hemos hecho y un miembro del equipo se encargará de subir el documento a la tarea abierta. 
Hemos considerado que ya que es una reunion "informal" no será necesario redactar un acta a no ser que se realice algún cambio importante.
Esperaremos a la próxima clase de laboratorio para fijar los objetivos y la organización para el próximo hito.
Destacar que para este primer hito hemos estado trabajando tres personas (los tres que hemos asistido a esta reunión) de las seis que deberíamos haber sido. Ya se lo hemos trasladado al profesor.

Valencia, 28/04/2022, Gabriel Sales García.


## <span style = "color: #00a3e8"> 7.2 Acta 2 Sesion 2</span>

# Acta 2 sesión 2: Pruebas con Curl
## Información de la reunión.
**Fecha:** 7 de Mayo del 2022

**Hora:** 16:00h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Online

**Plataforma:** Discord

**Asistentes:**

- Sales García, Gabriel

- Lopez, Pablo

- Pompilio, Matteo


## Resumen de la reunión:
Hemos testado las funcionalidades del plugin RESTED para Firefox y en segundo lugar hemos creado un servlet para explorar el API *TheCatAPI*

## 1. El plugin RESTED
Como en la ultima sesión hemos interactuado con el CentroEducativo a travez de la herramienta *curl*, ahora utilizamos un suporte grafico, el plugin *RESTED* (version 2.5.0 para Firefox).
La primera operación a testar es el login del administrador, con metodo POST al URL http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/login, especificando el Context-Type en el header de la petición HTTP y los dos parametros del login:![Screenshot_29.png](https://www.dropbox.com/s/5zb2f4oid1vac97/Screenshot_29.png?dl=0&raw=1)
Después de haber recibido la respuesta con codigo 200 y con la clave de sesión *935d6i9m0inial2amd12habsgh*, solicitamos una segunda petición para añadir un estudiante:![Screenshot_30.png](https://www.dropbox.com/s/eabxhwfyegswqaw/Screenshot_30.png?dl=0&raw=1)
Y al final, con metodo GET y al URL http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnosyasignaturas?key=935d6i9m0inial2amd12habsgh, el CentroEducativo nos devolverá en formato JSON una lista de los alumnos, entre los cuales el nuevo, Alessio. 

## 2. El API TheCatAPI
En este apartado vamos a crear un servlet *CatApi* en el cual, trás de recibir un submit desde la pagina HTML:
```html
<!DOCTYPE html>
<html>
    <head><title>gatos</title></head>
    <body>
        <form action="CatApi" method="get">
            <input type="submit" value="Enviar"/>
        </form>
    </body>
</html>
```
... el servlet solicitará una petición HTTP a travéz de la librería *java.net.HttpURLConnection* al servicio web TheCatAPI (https://api.thecatapi.com/v1/images/search), el cual responderá con un documento contenente an array de estructuras JSON.
A través de la función *url.openStream()* y de la librería *java.util.Scanner* leeremos el stream recibido y lo convertiremos en formato JSON: la conversión será posible gracias a una librería externa (https://mvnrepository.com/artifact/org.json/json/20180813), importando el file .jar en la carpeta del proyecto Eclipse *src/main/webapp/WEB-INF/lib*.
En este caso el tamaño del array será 1, así que accederemos al primer elemento y extraeremos el atributo *url*: el url será al final printado en la estructura html de output:
```java
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.servlet.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

[...]

//doGet:
URL url = new URL("https://api.thecatapi.com/v1/images/search");
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setRequestProperty("get", "application/json");
connection.connect();
Scanner sc = new Scanner(url.openStream());
String respuesta = "";
while(sc.hasNext()) respuesta += sc.nextLine();

response.setContentType("text/html");
PrintWriter out = response.getWriter();
String output = "<!DOCTYPE html>\n<html>\n<head>\n" 
    + "<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\"/><title>Foto gato'</title></head>";
try {
    JSONArray ja;
    ja = new JSONArray(respuesta);
    JSONObject json = ja.getJSONObject(0);
    String fotoUrl = json.get("url").toString();
    output += "<body><h3>Te devuelvo la foto</h3><img src=" + fotoUrl + "></body></html>";
    out.println(output);            
} catch (JSONException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

## 2. Próximas tareas:

Valencia, 09/05/2022, Matteo Pompilio

## <span style = "color: #00a3e8"> 7.3 Acta 3 Sesion 2</span>

# Acta 3 sesión 2 : Login y Autenticacion
### Información de la reunión:
**Fecha:**  11/05/2022

**Hora:**  16:30 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Presencial

**Asistentes:**

- Sales García, Gabriel

- Malerba, Alessio

- Pompilio, Matteo

- Lopez, Pablo


### 1. Resumen de la reunión:
En esta sesión hemos desarrollado un escenario de inicio tanto para una alumna como para un profesor. Hemos creado un filtro de autentificación, que se encarga de generar una key para poder hacer las peticiones necesarias a la API de centro educativo y por último hemos creado las vistas asociadas a estas funcionalidades por medio de la plataforma boostrap, aunque mencionar que no son ni serán la versión final.
Ambos escenarios presentan muchas similitudes pero también algunas diferencias que hemos podido entender durante esta sesión. 

### 2. Filtro "Autentificador.java":
La finalidad de este filtro es conseguir la autentificación web. Este apartado ya lo vimos en la pasada sesión de preparación pero ahora la idea es adaptarlo a nuestra aplicación. 
Por defecto el filtro se activará cada vez que se llame a los servlets de "LoginAlumno" y "LoginPro". El resultado es la creación de una KEY necesaria para poder hacer las consultas a centro educativo. Se puede decir que la funcionalidad de este filtro es idéntica a la orden curl por la cual iniciabamos sesión. Una vez generada la KEY debemos guardarla como un parámetro de la sesión, básicamente para que pueda ser utilizada más adelante.
La primera parte del código es igual a la autentificación que hicimos en la pasada sesión, por lo que solamente nos centramos en el código nuevo, que es la petición de la KEY y su posterior almacenaje.

```java
String url = request.getLocalName();
            
           try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookies).build()) {
                HttpPost post = new HttpPost("http://" + url + ":9090/CentroEducativo/login");
                post.setEntity(entity);
                post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                CloseableHttpResponse resp = httpclient.execute(post);
                sesion.setAttribute("cookie", cookies.getCookies());
                String t = EntityUtils.toString(resp.getEntity());                              
                httpclient.close();
                sesion.setAttribute("key", t);
            }           
            catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 
```
Ya hemos generado la key, hemos instanciado la sesión y además vamos a poder mantenerla gracias al uso de las cookies.
        
### 3. LoginAlumno
Este es el servlet que simboliza el inicio sesión o login de alumno. Se invoca a traves del index.html (html o vista inicial de nuestra aplicación), concretamente a través del botón que simboliza al alumno. Este servlet generá el filtro anteriormente explicado y por lo tanto en su contexto ya tenemos generada la KEY necesaria para poder interactuar con nuestra API.
Ahora pues el alumno podrá hacer las peticiones necesarias para que pueda ver la información de las asignaturas en las que esta matriculadas. 
Este pequeño resumen representa la funcionalidad del servlet en si, pero su desarrollo no es tan sencillo. En este acta vamos a dividir nuestro código en pequeños trozos explicando que es lo que se hace en cada uno, de forma breve y sencilla.

1.  Recuperación de la sesión:   Para que el alumno pueda hacer la nueva petición al centro educativo, necesitaremos su dni y su KEY. Estos datos, al igual que los cookies que identifican la sesión, los recogemos gracias a que la sesión ha sido instanciada por el filtro, ya que hemos guardado estos datos previamente durante la autentificación.

```java
HttpSession sesionalu = request.getSession();
        String dni = sesionalu.getAttribute("dni").toString();
        String key = sesionalu.getAttribute("key").toString();
        List<Cookie> cookie_list = (List<Cookie>) sesionalu.getAttribute("cookies");
        String url = request.getLocalName();

        
        BasicCookieStore cookies = new BasicCookieStore();
        cookies.addCookie(cookie_list.get(0));
```
2.  Peticiones: 
    Para recuperar los datos del alumno logado, hacemos las peticines necesarias a nuestra API. Si el alumno existe devolverá la información. Primero hacemos una petición para averiguar su nombre/apellidos y luego una petición para averiguar sus asignaturas.
    Al igual que nos pasaba en la sesión 2 con la autentificación la forma de conectarnos a la api "CatApi" no nos sirve para conectarno a "Centro educativo".
    De esta forma nos topamos con la libreria *"Fluent"* de HttpsComponents útil para las peticiones http. Para los objetos JSoN si que utilizamos la misma libreria. Realmente el comportamiento es análogo, solamente que la forma de realizarlo es diferente. A día de hoy continuamos sin saber porque la otra forma no va, si es muy similar. Como vemos también creamos código html con la información recogida.

```java
Executor executor = Executor.newInstance();
        String t = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/alumnos/" + dni + "?key=" + key))
                .returnContent().toString();

        JSONObject alumno = new JSONObject(t);
        out.println("<body>" + 
                "<div class=\"row\">" + 
                "<h1><b>Notas OnLine. </b>Asignaturas de " + alumno.get("nombre").toString() + " " + alumno.get("apellidos").toString() + "</h1>" + 
                "<p><b>Aquí tienes tus asignaturas</b></p>" + 
                "<p><b>Aquí tienes tus notas</b></p>" + 
                "</div><br> " + 
                "<div class=\"row\">" + 
                "<div class=\"col-7\">" + 
                "<div class=\"row\">");

        sesionalu.setAttribute("rol", "rolalu");
        String asignaturas = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/alumnos/" + dni + "/asignaturas/?key=" + key))
                .returnContent().toString();
```
3. Petición detalles asignaturas.
La petición anterior digamos que no devuelve toda la información completa de la asigantura, solamente devuelve un acrónimo de ella (bueno, y su nombre). Como el alumno quiere conocer la asignatura en su totalidad o más detalladamente (para poder ver notas asignadas), volvemos a hacer una petición.
A partir del objeto devuelto, hacemos un array de JSON y lo recorremos. Por cada elemento una petición. Centro educativo devuelve una lista de JSON, por eso es necesario este paso.


```java
JSONArray array = new JSONArray(asignaturas);
for (int i = 0; i < array.length(); i++) {
        JSONObject asignatura_json = array.getJSONObject(i);
        String acronimo = asignatura_json.getString("asignatura");
        
        String detallesAsignaturas = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/asignaturas/" + acronimo + "/?key=" + key))
                .returnContent().toString();
        JSONObject detalles = new JSONObject(detallesAsignaturas);
        String nombreD = detalles.getString("nombre");
        
        out.println("<form action='Asignaturas' method='GET'>"
                + "<h2>" + "<input type='submit' value='" + nombreD + "' name='" + acronimo + "'></form>" + "</h2>");
}
out.println("</div></div>" + 
        "<div class=\"col-4\" id=\"nombres\">" + 
        "<h3>Grupo 3TI12_G02</h3>" + 
        "<ol>" + 
        "<li>Sales Garcia, Gabriel</li>" + 
        "<li>Malerba, Alessio</li>" + 
        "<li>Pomplio, Matteo</li>" + 
        "<li>Lopez, Pablo</li>" + 
        "</ol></div></div><br>" +
        "<div id=\"abajo\">" + 
        "<p>Trabajo en grupo realizado por el grupo 3TI12_G2 en el curso DEW 2021/2022</p>" + 
        "</div></body></html>");
```	
Destacamos esta última parte, donde ponenmos nuestros nomvre y nombre de grupo.

### 4. LoginProfesor:
La funcionalidad es muy similar al servlet LoginAlumno ya que estamos hablando de un escenario inicial donde solamente hay que iniciar sesión, pero al cambiar el rol y por lo tanto su funcionalidad cambian algunas cosas.
Cambiamos el atributo *rol* de la sesión.
```java
sesionprof.setAttribute("rol", "rolpro");
```
Y no hacemos una petición para detalles asignatura. El profesor solamente quiere ver las asignaturas que imparte, no tiene ninguna nota asociada por lo que no necesita ver más.
Destacamos que no hemos podido añadir ningun profesor porque no podiamos pasar el filtro. Los profesores nos pone en el documento que no tienen una password asociada. Entonces no podemos generar la KEY.

### 5. Objetivos para la próxima reunión:
- Organizar el trabajo para poder entregar este segundo hito.


Valencia, 11/05/2022, Lopez Honrubia Pablo


## <span style = "color: #00a3e8"> 8. Anexo: Actas Hito 3</span>

## <span style = "color: #00a3e8"> 8.1 Acta 1 Sesion 3</span>

# Acta 1 sesión 3: Detalles de los alumnos 
## Información de la reunión.
**Fecha:** 18 de Mayo del 2022

**Hora:** 16:30h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** En presencial

**Lugar:** Aula studio de Arquitectura

**Asistentes:**

- Sales García, Gabriel

- Lopez Honrubia, Pablo

- Malerba, Alessio

- Pompilio, Matteo


## Resumen de la reunión:
En este encuentro, nos centramos en entender cómo obtener los datos del usuario que está entrando en la aplicación a través del login, pasando efectivamente a través de un enlace de LoginAlumno.java a Detalle.java. 

## 1. Conexión con el Servlet:
Una vez pasado la autenticación como rolalumno, encontramos una página donde podemos ver las asignaturas a las que estamos matriculados. 

````
 out.println("<body>" + 
                "<div class=\"row\">" + 
                "<h1><b>Notas OnLine. </b>Asignaturas de " + nombre_alumno + " " + apellido_alumno + "</h1>" + 
                "<p><b>Aqu� tienes tus asignaturas</b></p>" + 
                "</div><br><div>");
            
````
También observaremos un botón llamado "Imprimir expediente", el cual explicaremos en el punto 3.
````
       out.println("<form action='Expediente' method='GET'><h2>" + 
                "<input type='hidden' name='dni' value='" + dni + "'/>" +
                "<input type='hidden' name='nombre' value='" + nombre_alumno + "'/>" +
                "<input type='hidden' name='apellido' value='" + apellido_alumno + "'/>" +
                "<input type='submit' id='expe' value='Imprimir Expediente'/>" + 
                "</h2></form>");

````

## 2. Servlet Detalles:

Cuando seleccionemos una asignatura de las que estamos matriculados, nos dirigirá a un servlet nuevo, llamado "Detalles". En el se mostrará una página donde podremos ver información sobre la asignatura y sobre el alumno, una foto y su correspondiente nota (en el caso que no tenga nota mostrará "Sin calificar").

````
String nota_asignatura = "0"; 
        for(int i = 0; i < notas_json.length(); i++) {
            JSONObject notas_obj = notas_json.getJSONObject(i);
            String nota = notas_obj.get("nota").toString();
            if(nota.length() == 0) {nota = "Sin calificacion";}
            if(notas_obj.get("asignatura").toString().equals(acronimo)) {
                nota_asignatura = nota;
                out.println("<br><p id='calificacion'> Nota: " + nota + "</p>"); 
            }
        }
````

Para cargar la foto, inicialmente cogimos las fotos del "fotos.zip" proporcionado por el profesor, las renombramos con su correspondiente DNI.
A continuación, creamos un servlet que hemos llamado "FOTO" donde proporcionamos un código con el método GET que devuelve un objeto JSON que contiene el DNI y la imagen recuperada del fichero(codificado en base64).

````
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String f = request.getParameter("dni"); 
        ServletContext context = getServletContext();
        String carpeta = context.getRealPath("/WEB-INF/imgs");	    
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        BufferedReader origen = new BufferedReader(new FileReader(carpeta + "/" + f +".pngb64"));
        PrintWriter out = response.getWriter();
        out.print("{\"dni\": \"" + f + "\", \"img\": \"");
        String linea = origen.readLine(); 
        out.print(linea);
        while ((linea = origen.readLine()) != null) {out.print("\n"+linea);}
        out.print("\"}");
        out.close(); 
        origen.close();
    }
    
````    

La invocación de este servlet y su resultado lo hemos resuelto gracias a AJAX, con el que se consulta un DNI concreto y obteniendo el resultado deseado.

`````
out.println("<script>"
                + "   $(document).ready(function() {\n"
                + "       $.getJSON(\"Foto?dni=" + dni + "\")\r\n"
                + "           .done(function(response) {\r\n"
                + "               $('#fotodni').attr(\"src\", \"data:image/png;base64,\"+response.img);\r\n"
                + "            })\r\n"
                + "           .fail(function(jqxhr, textStatus, error ) {\r\n"
                + "               var err = jqxhr.response.replace(\",\", \"\\n\"); // Pequeños ajustes\r\n"
                + "               alert(\"Algo mal: \"+error);\r\n"
                + "           });"
                + "   });\n"
                + "</script>");	
                
`````

## 3. Imprimir expediente:

Como hemos nombrado anteriormente, en la página mostramos las asignaturas y el expediente. Cuando seleccionamos "Imprimir Expediente" nos dirigimos a un servlet llamado "Expediente".



En él, encontraremos un servlet en el que podemos ver información del alumno, su foto, y las asignaturas que esta matriculado con las notas puestas por el profesor( en el caso que no tenga nota mostrará "Sin calificar").

````
for (int i = 0; i < asignaturas_array.length(); i++) {
            out.println("<h2>Prueba 2</h2>");
            JSONObject asignatura_json = asignaturas_array.getJSONObject(i);
            String acronimo = asignatura_json.getString("asignatura");
            String nota = asignatura_json.getString("nota");
            if(nota.length() == 0) {nota = "Sin calificar";}
            String detallesAsignaturas = executor.use(cookies)
                    .execute(Request.get("http://" + url + ":9090/CentroEducativo/asignaturas/" + acronimo + "/?key=" + key))
                    .returnContent().toString();
            JSONObject detalles = new JSONObject(detallesAsignaturas);
            String nombre = detalles.getString("nombre");
            out.println("<h2>Prueba 3</h2>");
            out.println("<div><p> Expediente: </p> <tr>" + 
                    "<td>" + acronimo + "</td>" + 
                    "<td>" + nombre + "</td>" + 
                    "<td>" + nota + "</td></tr></div>");
        }

````

##4. Siguiente sesión

En la siguiente sesión, pretendemos realizar el servlet para que el profesor pueda ver sus asignaturas con sus alumnos matriculados, y poder modificar las notas a éstos.


*Valencia, 18/05/2022, Alessio Malerba.*



## <span style = "color: #00a3e8"> 8.2 Acta 2 Sesion 3</span>

# Acta 2 sesión 3: Modificar Nota
### Información de la reunión:
**Fecha:**  01/06/2022

**Hora:**  16:30 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Presencial

**Asistentes:**

- Sales García, Gabriel

- Malerba, Alessio

- Pompilio, Matteo

- Lopez, Pablo


### 1. Resumen de la reunión:

En esta sesion lo que hemos hecho es seguir con las funciones que debe tener el profesor, que son ver las asignaturas que imparte, ver sus alumnos de cada asignatura, y poder añadir notas y mofidificarlas de sus alumnos.
 

### 2. Ver asignaturas que imparte

Una vez pasado el filtro de autenticación, ya estamos en la página donde el profesor podra ver las asignaturas que imparte con sus respectivas notas. 

Cuando clique en una de las asignaturas que imparte, podra visualizar los alumnos que tiene en cada asignatura. 
```java
  out.println("<body>" + 
                "<div class=\"row\">" + 
                "<h1><b>Notas OnLine. </b>Alumnos de la asignatura " + acronimo + "</h1>" + 
                "<p><b>Aquí tienes sus alumnos</b></p>" + 
                "</div><br><div>");
```


Ésto lo consigue accediendo al servlet de ListaAlumnos; lo mostrará indicando su DNI, y abajo pondra la última nota que el profesor le ha puesto. Si no le ha modificado aún la nota mostrara un mensaje de "Sin calificar", y en cuanto la modifique, se actualizará con la nota deseada.

```java
 for (int i = 0; i < lista_alumnos_array.length(); i++) {
            JSONObject lista_alumnos_json = lista_alumnos_array.getJSONObject(i);
            String dni_alumno = lista_alumnos_json.getString("alumno");
            String nota = lista_alumnos_json.getString("nota");
            if(nota.length() == 0) {nota = "Sin calificar";}
            
```



### 3. ModificarNota

Cuando el profesor acceda a una asignatura, verá los alumnos matriculados a esa asignatura como hemos dicho en el punto anterior. El profesor tiene el "poder" de poder modificar la nota a sus alumnos. Por lo tanto cuando entre en una asignatura
clicara en el boton de "VAMOS!" con el que accedera al servlet Detalles para poder modificarlas.

A continuación, se mostrará una página muy parecida a la mostrada al alumno, con la foto de la alumno, la información de éste y la información de la asignatura, pero además incluira el formulario donde podra actualizar la nota del alumno: 

```java
if (session.getAttribute("rol").toString().equals("rolpro")) {
            out.println("<form action='ModificacionNotas' method='post'> <p> Introduzca la nueva nota(): "
                    + "<input type='hidden' name='dni' value='" + dni + "'/>"
                    + "<input type='hidden' name='acronimo' value='" + acronimo + "'/>"
                    + "<input type='number' id='nota' name='nota' placeholder='" + nota_asignatura + "' min='1' max='10'/>"
                    + "<input type='submit' value='Modifica'/> </p> </form>");

````

La llamada al servlet ModificacionNotas la hemos conseguida gracias a una petición AJAX. 

````
$.ajax({   \r\n"
                    + " url: 'ModificacionNotas',\r\n"
                    + " type: 'POST',\r\n"
                    + " contentType: 'application/json',\r\n"
                    + " async: true,\r\n"
                    + " data: \"dni=" + dni + "&acronimo=" + acronimo + "&nota=5\",\r\n"
                    + " success: function(responseText) {alert(\"Has a�adido la nota \" + $('#nota').attr('value')  + \"con exito! -- \" + responseText);},\r\n"
                    + " error: function(responseText) {alert(\"No has a�adido la nota \" + $('#nota').attr('value') + \"! -- \" + responseText);}\r\n"
                    + " });\r\n"
                    + " return false;});};\r\n"
                    
````



### 5. Objetivos para la próxima reunión:

En la ultima sesion, queremos crear un servlet para poder cerrar sesion. 

Valencia, 01/06/2022, Pablo López Honrubia


## <span style = "color: #00a3e8"> 8.3 Acta 3 Sesion 3</span>

# Acta 3 sesión 3: Cerrar Sesion
### Información de la reunión:
**Fecha:**  05/06/2022

**Hora:**  11:00 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Online, via Discord

**Asistentes:**

- Sales García, Gabriel

- Malerba, Alessio

- Pompilio, Matteo

- Lopez, Pablo


### 1. Resumen de la reunión:

En esta sesion lo que hemos hecho es crear un servlet final para poder cerrar sesion con alumno y también como profesor. 

 

### 2. LogOut

El Servlet que hemos creado para esta función lo hemos llamado *Logout*.

En éste lo único que queremos conseguir es que los alumnos y profesores puedan suspender su sesión para poder salir de la aplicacion con éxito. 

```java
HttpSession session = request.getSession();
        session.removeAttribute("cookie");
        session.removeAttribute("key");
        session.invalidate();
        response.sendError(401);
```


Para llamar a este servlet, que lo llamamos desde loginalumno y profesor, hemos vuelto a utilizar AJAX. 
```java
out.println("<script>\n"
                + "function logout() {\n"
                + "    $.ajax({\n"
                + "        type: "GET",\n"
                + "        url: window.location,\n"
                + "        dataType: 'json',\n"
                + "        async: true,\n"
                + "        username:'00',\n"
                + "        password:'00',\n"
                + "        data: '{"logout"}'\n"
                + "    }).fail(function() {\n"
                + "        $(document).ready(function() {\n"
                + "            $.get('Logout', {}, function(responseText) {});\n"
                + "        });\n"
                + "        window.location.replace('index.html');\n"
                + "    });\n"
                + "}"
                + "</script>");
                
```

En la página se mostrara el boton "Logout" para poder llevar acabo esta acción.
```html
"<button type='button' onClick='logout()'>Logout</button>"
```

Valencia, 11/04/2022, Pablo López Honrubia




