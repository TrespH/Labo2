# Acta sesión 3: Detalles de los alumnos 
## Información de la reunión.
**Fecha:** 18 de Mayo del 2022

**Hora:** 16:30h
47 103-118
38
**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** En vivo

**Lugar:** Aula studio de Arquitectura

**Asistentes:**
- Sales García, Gabriel
- Lopez Honrubia, Pablo
- Malerba, Alessio
- Pomplio, Matteo

## Resumen de la reunión:
En este encuentro, nos centramos en entender cómo obtener los datos del usuario que está entrando en la aplicación a través del login, pasando efectivamente a través de un enlace de LoginAlumno.java a Detalle.java. 

## 1. Conexión con el Servlet:
En primer lugar, teníamos que averiguar cómo conectar las peticiones de los usuarios con el servlet para que éste pudiera responder con los datos adecuados. Nos centramos en el método *doGet* porque aquí tendríamos que insertar el código html para 'imprimir' en la pantalla lo que el usuario está buscando:

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub 
        [...]

Tuvimos que decidir entre dos versiones que habíamos diseñado y escrito, cada una con problemas diferentes que no entendíamos del todo, porque la petición del hito incluía una versión con jquery.

## 2. Versión con jquery:
En la línea 47 importamos las librerias necesarias:

	<head>
	<script src="jquery-3.6.0.min.js"></script>
	</head>

A partir de aquí, creamos un formulario que permita la redirección a la hoja correcta, y mediante jquery gestionamos una función javascript dinámica que permita asociar un evento al clic de los botones, como se puede ver la línea: 

	/*out.println("<form> <input type ='button' data-toggle='modal' data-target='#detalles' value='" + 	 nombreD + "' id='vamos'/> </form>");

            out.println("<script>\n"
                    + "    $(document).ready(function() {\n"
                    + "        $('#vamos').click(function(event) {\n"
                    + "            $.get('Detalles', {\n"
                    + "              asig:'" + acronimo + "',"
                    + "              dni:'" + dni + "',"
                    + "              nombre:'" + nombre_alumno + "',"
                    + "              apellido:'" + apellido_alumno + "',"
                    + "            }, function(respuesta) {\n"
                    + "                $('#detalles').html(respuesta);\n"
                    + "            });\n"
                    + "        });\n"
                    + "    });\n"
                    + "</script>");*/



## 3.Versión directa:
La que hemos efectivamente usado, porque la con AJAX devolvió algunos errores anormales a veces. En esta versión, escribimos directamente a través de la función *out.println* en el servlet.

	out.println("<form action='Expediente' method='GET'><h2>" + 
            "<input type='hidden' name='dni' value='" + dni + "'/>" +
            "<input type='hidden' name='nombre' value='" + nombre_alumno + "'/>" +
            "<input type='hidden' name='apellido' value='" + apellido_alumno + "'/>" +
            "<input type='submit' id='expe' value='Imprimir Expediente'/>" + 
            "</h2></form>");
		out.println("   </div>" + 
				"   <div class=\"col-4\" id=\"nombres\">" + 
				"   <h3>Grupo 3TI12_G02</h3>" + 
				"   <ol>" + 
				"   <li>Sales Garcia, Gabriel</li>" + 
				"   <li>Malerba, Alessio</li>" + 
				"   <li>Pompilio, Matteo</li>" + 
				"   <li>Lopez, Pablo</li>" + 
				"   </ol></div>" +
				"<br>" +
				"<div id=\"abajo\">" + 
				"<p>Trabajo en grupo realizado por el grupo 3TI12_G2 en el curso DEW 2021/2022</p>" + 
				"</div>" + 
				"<div class='modal' id='detalles'></div>"
				+ "</body></html>");
*Valencia, 18/05/2022, Alessio Malerba.*


