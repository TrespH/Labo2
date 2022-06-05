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
