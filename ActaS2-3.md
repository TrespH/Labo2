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

### 2. Filtro "Autentificador.java" :
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
    }
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


Valencia, 11/05/2022, Gabriel Sales García