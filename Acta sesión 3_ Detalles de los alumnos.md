# Acta sesión 3: Detalles de los alumnos 
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


