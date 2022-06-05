# Acta sesión 5: Cerrar Sesion
### Información de la reunión:
**Fecha:**  05/06/2022

**Hora:**  11:00 h

**Identificador del grupo:** 3ti21_G2

**Tipo de reunión:** Online, via Discord

**Asistentes:**
- Sales García, Gabriel
- Malerba, Alessio
- Pomplio, Matteo
- Lopez, Pablo



### 1. Resumen de la reunión:

En esta sesion lo que hemos hecho es crear un servlet final para poder cerrar sesion con alumno y también como profesor. 

 

### 2. LogOut

El Servlet que hemos creado para esta función lo hemos llamado *Logout*.

En éste lo único que queremos conseguir es que los alumnos y profesores puedan suspender su sesión para poder salir de la aplicacion con éxito. 

````
HttpSession session = request.getSession();
        session.removeAttribute("cookie");
        session.removeAttribute("key");
        session.invalidate();
        response.sendError(401);
	}
````


Para llamar a este servlet, que lo llamamos desde loginalumno y profesor, hemos vuelto a utilizar AJAX. 
````
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
                
````

En la página se mostrara el boton "Logout" para poder llevar acabo esta acción.
````
"<button type='button' onClick='logout()'>Logout</button>"
````

Valencia, 11/04/2022, Pablo López Honrubia