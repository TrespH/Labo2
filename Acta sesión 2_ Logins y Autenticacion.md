# Acta sesión 2 : Login y Autenticacion
### Información de la reunión:
**Fecha:**  11/05/2022

**Hora:**  16:30 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Presencial

**Asistentes:**
- Sales García, Gabriel
- Malerba, Alessio
- Pomplio, Matteo
- Lopez, Pablo
- 

### 1. Resumen de la reunión:

En esta sesion lo que hemos hecho es crear un proyecto con la única funcion de autenticar a través de CentroEducativo y asi autenticar a los usuarios. Despues tenemos un servlet de codigo de alumno y profesor donde el alumno pueden consultar y ver las notas y sus asignaturas. Para conseguir la contraseña

 

### 2. Auntenticación web:
Para la autenticacion web hemos creado un proyecto cuya única función es autenticar donde accederemos a CentroEducativo con Basic en nuestro servidor tomcat donde ya hay alumnos y profesores guardados. Guardaremos a los usuarios que accedan al servicio creando un objeto de tipo map; y asi si necesitamos acceder al CentroEducativo indexaremos el map.

Para poder obtener el usuario utilizamos la funcion getRemoteUser() y si queremos acceder a la contraseña, pensamos que la forma óptima es, gracias al map que se va generando segun los usuarios que van entrando, decodificaremos el mensaje gracias a:
````
String pass = request.getHeader("Authorization").substring(6); 
sun.misc.BASE64Decoder dec = new ssun.misc.BASE64Decoder();  
String passDecoded= new String(dec.decodeBuffer(pass)).substring(10);

````

Ya realizado este paso comprobaremos si tenemos una sesion creado ya con nuestro usario, contraseña y la key con : 
``` 
if(session.isNew() || session.getAttribute("key") == null) {
			session.setAttribute("dni", esteUser);
			session.setAttribute("pass", estaPass);
			JSONObject id = new JSONObject();
			id.put("dni", esteUser);
			id.put("password", estaPass);
			StringEntity entity = new StringEntity(id.toString());
			String url = request.getLocalName();
		
```	
		
Una vez esto tenemos que enviar un mensaje http a CentroEducativo donde nos generaran una llave. Hemos utilizado gson para el los objetos json y la biblioteca httpComponents de apache para comunicarnos con REST.

````
HttpPost post = new HttpPost("http://"+ url +":9090/CentroEducativo/login");
				post.setEntity(entity);
				post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				CloseableHttpResponse resp = httpclient.execute(post);
				session.setAttribute("cookie", cookie.getCookies());
				String t = EntityUtils.toString(resp.getEntity());        		        		

				httpclient.close();

				session.setAttribute("key", t);
````

### LoginAlumno

En este caso tenemos otro servlet para loginAlumno

En el LoginAlumno lo que intentamos conseguir autenticarnos como alumno y acceder a las asignaturas que estas matriculado y sus respectivas notas.

Primero si se desea iniciar sesion como alumno se procedera a la autenticacion BASIC. Cuando hayamos pasado ésta, obtendremos la llave con la petición a CentroEducativo. Hemos creado un filtro para que que se conserve la llave de la sesion y también guarde  los cookies de esta. Necesitaremos la libreria Classic de HttpComponents.

Una vez se ha autenticado y ha pasado el filtro, entrara en la pagina de inicio
````
HttpSession sesionalu = request.getSession();
		String dni = sesionalu.getAttribute("dni").toString();
		String key = sesionalu.getAttribute("key").toString();
		List<Cookie> cookies = (List<Cookie>) sesionalu.getAttribute("cookies");
		String url = request.getLocalName();

		
		BasicCookieStore cookies = new BasicCookieStore();
		BasicCookieStore.addCookie(cookies.get(0));
		Executor executor = Executor.newInstance();
		String t = executor.use(cookies)
				.execute(Request.get("http://"+url+":9090/CentroEducativo/alumnos/" + dni + "?key=" + key))
				.returnContent().toString();
				
				
````

Despues recibiremos un objeto JSON que incluira las asignaturas, hemos utilizado la biblioteca java-json.

### 4. LoginProfesor

Con LoginProfesor hemos seguido los mismos pasos que para LoginAlumno, con la diferencia del rol asignado a estos, que en este caso es rolpro, y los objetos JSON creados. 


### 5. Objetivos para la próxima reunión:

Crear los servlets de alumno y de las asignaturas

Valencia, 11/04/2022, Gabriel Sales García