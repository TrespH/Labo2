# Acta sesión 4: Modificar Nota
### Información de la reunión:
**Fecha:**  01/06/2022

**Hora:**  16:30 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Presencial

**Asistentes:**
- Sales García, Gabriel
- Malerba, Alessio
- Pomplio, Matteo
- Lopez, Pablo



### 1. Resumen de la reunión:

En esta sesion lo que hemos hecho es seguir con las funciones que debe tener el profesor, que son ver las asignaturas que imparte, ver sus alumnos de cada asignatura, y poder añadir notas y mofidificarlas de sus alumnos.
 

### 2. Ver asignaturas que imparte

Una vez pasado el filtro de autenticación, ya estamos en la página donde el profesor podra ver las asignaturas que imparte con sus respectivas notas. 

Cuando clique en una de las asignaturas que imparte, podra visualizar los alumnos que tiene en cada asignatura. 
````
  out.println("<body>" + 
                "<div class=\"row\">" + 
                "<h1><b>Notas OnLine. </b>Alumnos de la asignatura " + acronimo + "</h1>" + 
                "<p><b>Aqu� tienes sus alumnos</b></p>" + 
                "</div><br><div>");

````


Ésto lo consigue accediendo al servlet de ListaAlumnos; lo mostrará indicando su DNI, y abajo pondra la última nota que el profesor le ha puesto. Si no le ha modificado aún la nota mostrara un mensaje de "Sin calificar", y en cuanto la modifique, se actualizará con la nota deseada.

````
 for (int i = 0; i < lista_alumnos_array.length(); i++) {
            JSONObject lista_alumnos_json = lista_alumnos_array.getJSONObject(i);
            String dni_alumno = lista_alumnos_json.getString("alumno");
            String nota = lista_alumnos_json.getString("nota");
            if(nota.length() == 0) {nota = "Sin calificar";}
            
````



### 3. ModificarNota

Cuando el profesor acceda a una asignatura, verá los alumnos matriculados a esa asignatura como hemos dicho en el punto anterior. El profesor tiene el "poder" de poder modificar la nota a sus alumnos. Por lo tanto cuando entre en una asignatura
clicara en el boton de "VAMOS!" con el que accedera al servlet Detalles para poder modificarlas.

A continuación, se mostrará una página muy parecida a la mostrada al alumno, con la foto de la alumno, la información de éste y la información de la asignatura, pero además incluira el formulario donde podra actualizar la nota del alumno: 

````
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

Valencia, 11/04/2022, Gabriel Sales García