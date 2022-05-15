# Acta sesión 2: Pruebas previas S3
## Información de la reunión.
**Fecha:** 7 de Mayo del 2022

**Hora:** 16:00h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Online

**Plataforma:** Discord

**Asistentes:**
- Sales García, Gabriel
- López, Pablo
- Pompilio, Mateo

## Resumen de la reunión:
Hemos probado las funcionalidades del plugin RESTED para Firefox y en segundo lugar hemos creado un servlet para explorar la API *TheCatAPI*. También hemos estudiado los puntos necesarios para la autentificación.

## 1. El complemento DESCANSADO
Como en la última sesión hemos interactuado con el CentroEducativo a través de la herramienta *curl*, ahora utilizamos un soporte gráfico, el complemento *RESTED* (versión 2.5.0 para Firefox).
La primera operación a testar es el login del administrador, con metodo POST al URL http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/login, especificando el Context-Type en el header de la petición HTTP y los dos parametros del login:![Screenshot_29.png](https://www.dropbox.com/s/5zb2f4oid1vac97/Screenshot_29.png?dl=0&raw=1)
Después de haber recibido la respuesta con el código 200 y con la clave de sesión *935d6i9m0inial2amd12habsgh*, solicitamos una segunda petición para añadir un estudiante:![Screenshot_30.png](https://www.dropbox.com/s/eabxhwfyegswqaw/Screenshot_30 .png?dl=0&raw=1)
Y al final, con metodo GET y al URL http://dew-mpompil-2122.dsicv.upv.es:9090/CentroEducativo/alumnosyasignaturas?key=935d6i9m0inial2amd12habsgh, el CentroEducativo nos devolverá en formato JSON una lista de los alumnos, entre los cuales el nuevo, Alessio.

## 2. El API TheCatAPI
En este apartado vamos a crear un servlet *CatApi* en el cual, trás de recibir un envío desde la pagina HTML:
```html
<!DOCTYPEhtml>
<html>
    <head><title>gatos</title></head>
    <cuerpo>
        <forma acción="CatApi" método="obtener">
            <input type="enviar" value="Enviar"/>
        </formulario>
    </cuerpo>
</html>
```
... el servlet solicitará una petición HTTP a través de la librería *java.net.HttpURLConnection* al servicio web TheCatAPI (https://api.thecatapi.com/v1/images/search), el cual responderá con un documento contenido una matriz de estructuras JSON.
A través de la función *url.openStream()* y de la librería *java.util.Scanner* leeremos el stream recibido y lo convertiremos en formato JSON: la conversión será posible gracias a una librería importada (https://mvnrepository. com/artifact/org.json/json/20180813) importando el archivo .jar en la carpeta del proyecto Eclipse *src/main/webapp/WEB-INF/lib*.
En este caso el tamaño del array será 1, así que accederemos al primer elemento y extraeremos el atributo *url*: la url será al final printado en la estructura html de salida:
```java
importar java.io.*;
importar java.net.HttpURLConnection;
importar java.net.URL;
importar java.util.Scanner;
importar javax.servlet.*;
importar org.json.JSONArray;
importar org.json.JSONException;
importar org.json.JSONObject;

[...]

//doGet:
URL url = nueva URL ("https://api.thecatapi.com/v1/images/search");
Conexión HttpURLConnection = (HttpURLConnection) url.openConnection();
conexión.setRequestProperty("obtener", "aplicación/json");
conexión.conectar();
Escáner sc = nuevo Escáner (url.openStream());
Cadena respuesta = "";
while(sc.hasNext()) respuesta += sc.nextLine();

respuesta.setContentType("texto/html");
PrintWriter out = respuesta.getWriter();
Salida de cadena = "<!DOCTYPE html>\n<html>\n<cabeza>\n"
    + "<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\"/><title>Foto gato'</title></head>";
tratar {
    JSONArray ja;
    ja = new JSONArray(respuesta);
    JSONObject json = ja.getJSONObject(0);
    String fotoUrl = json.get("url").toString();
    salida += "<body><h3>Te devuelvo la foto</h3><img src=" + fotoUrl + "></body></html>";
    out.println(salida);            
} captura (JSONException e) {
    // TODO Bloque catch generado automáticamente
    e.printStackTrace();
}
```
## 3. Autentificación:
Proceso que deberemos utilizar en la siguiente sesión. Nos hemos dedicado a estudiar que debemos hacer para poder ponerlo en práctica más adelante. Veremos algunas líneas de código de interés.
Accederemos a CentroEducativo a través de una autenticación *BASIC* en nuestro servidor tomcat que y una vez hecho esto, crearemos un objeto del tipo map para guardar e indexar todos los usuarios que acceden al servicio desde el servlet, de esta manera, si necesitamos acceder a CentroEducativo podemos hacerlo indexando el map. Para la obtención del usuario utilizamos ```request.getRemoteUser()```
Para conseguir la contraseña hemos optado por una forma más sencilla. Como hemos creado un Map, este se irá generando a medida que los ususarios se vayan logando. Nos parecía demasiado tener que introducir toda la información en el map en un momento inicial.
Sabemos (porque lo hemos estudiado) el mensaje http *BASIC* solo tiene una codificación en BASE64. Java nos permite decodificarlo.
Mostramos el código: 
```
String user = req.getRemoteUser();
		String pass = req.getHeader("Authorization").substring(6);	
		
		byte[] decodedBytes = Base64.getDecoder().decode(pass);
		String passDecoded = new String(decodedBytes).substring(10);
```
Posteriormente comprobamos de que exista ya una sesión iniciada para estos valores. Si existe, genial ya hemos acabado. Pero si no... debemos ennviar una petición a la API centro educativo para que nos cree una key. Para poder comunicarnos con el centro educativo no nos funciona la forma que hemos utilizado para conectarnos a la CatApi. Hemos tenido que bucar otra opción. Utilizamos la biblioteca fluent. Nos permite abrir conexiones y nos funciona para este caso. Creamos un objeto JSON para poder tratar los datos.
```java
JSONObject json = new JSONObject();
            json.put("dni", user_value);
            json.put("password", pass_value);
            StringEntity entity = new StringEntity(json.toString()); //Ponemos JSON en String
            String t = Request.post("http://dew-milogin-2122.dsicv.upv.es:9090/CentroEducativo/login") .body(entity) .setHeader(HttpHeaders.CONTENT_TYPE, "application/json") .execute().returnContent().toString(); //Petición
            sesion.setAttribute("key", t); // Añadimos key devuelta.
```
Ya habríamos hecho la autentificación.

## 2. Tareas próximas:
-   Preparar todo para el estudio y dearrollo del hito 2.

Valencia, 05/09/2022, Matteo Pompilio
