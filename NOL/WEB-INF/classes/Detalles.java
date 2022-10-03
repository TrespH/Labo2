

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.fluent.Executor;
import org.apache.hc.client5.http.fluent.Request;


/**
 * Servlet implementation class Detalles
 */
public class Detalles extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Detalles() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String titulo = "<!DOCTYPE html>" + 
                "<html lang=\"es-es\">" + 
                "<head>" + 
                "<meta charset=\"UTF-8\">" + 
                "<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p\" crossorigin=\"anonymous\"></script>" +
                "<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3\" crossorigin=\"anonymous\">" +
                "<title>Detalles</title>" +
                "<script src=\"https://code.jquery.com/jquery-3.6.0.min.js\"></script>" +
                "<link rel='stylesheet' type='text/css' href='Exped-Detal.css'>" +
                "</head>";
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(titulo);

        String acronimo = request.getParameter("asig");
        String dni = request.getParameter("dni");
        String nombre_alumno = request.getParameter("nombre");
        String apellido_alumno = request.getParameter("apellido");

        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<Cookie> cookie_list = (List<Cookie>) session.getAttribute("cookie");
        Executor executor = Executor.newInstance();
        BasicCookieStore cookies = new BasicCookieStore();
        cookies.addCookie(cookie_list.get(0));
        String url = request.getLocalName();
        String key = session.getAttribute("key").toString();
        
        String detalles_asignatura = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/asignaturas/" + acronimo + "/?key=" + key))
                .returnContent().toString();
        JSONObject detalles_json = new JSONObject(detalles_asignatura);
        
        out.println("<body>\r\n"
                + "    <div class='row' id='titulo'>\r\n");
        
        if (session.getAttribute("rol").toString().equals("rolalu"))
            out.println("        <div class='col-1'><a href='LoginAlumno'><img src='Flecha_atras.png' id='atras' width='30pt'/></a></div>\r\n");
        else
            out.println("        <div class='col-1'><a href='ListaAlumnos?acronimo=" + acronimo + "'><img src='Flecha_atras.png' id='atras' width='30pt'/></a></div>\r\n");
        
        out.println("        <div class='col-10'>\r\n"
                + "            <h1>"+ apellido_alumno + ", " + nombre_alumno + " (" + dni + ")</h1>\r\n"
                + "        </div>\r\n"
                + "    </div>\r\n"
                + "\r\n"
                + "    <hr>\r\n"
                + "    <div class='row'>\r\n"
                + "        <div class='col-3'><img class='fotos center' id='fotodni' src='img.png' alt='foto alumno'/></div>\r\n"
                + "        <div class='col-8'><h5>Detalle asignatura: " + detalles_json.getString("nombre") + " (" +  acronimo + ")</h5></div>\r\n"
                + "    </div>");
        
        String notas = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/alumnos/" + dni + "/asignaturas/?key=" + key))
                .returnContent().toString();

        JSONArray notas_json = new JSONArray(notas);

        String nota_asignatura = "0"; 
        for(int i = 0; i < notas_json.length(); i++) {
            JSONObject notas_obj = notas_json.getJSONObject(i);
            String nota = notas_obj.get("nota").toString();
            if(nota.length() == 0) {nota = "Sin calificacion";}
            if(notas_obj.get("asignatura").toString().equals(acronimo)) {
                nota_asignatura = nota;
            }
        }

        

        out.println("<div class='card text-center'>\r\n"
                + "    <div class='card-header'>Detalles del alumno</div>\r\n"
                + "    <div class='card-body'>\r\n"
                + "        <p><b>Nombre: </b>" + apellido_alumno + ", " + nombre_alumno + "</p>\r\n"
                + "        <p><b>DNI: </b>" + dni + "</p>\r\n"
                + "        <p id='calificacion'><b>" + nota_asignatura + "</b></p>\r\n"
                + "    </div>\r\n"
                + "</div>");

        if (session.getAttribute("rol").toString().equals("rolpro")) {
            out.println("<div class='card text-center'>\r\n"
                    + "    <div class='card-header'>" + "Modificar" + "</div>\r\n"
                    + "    <form action='ModificacionNotas' method='POST'>\r\n"
                    + "        <input type='hidden' name='dni' value='" + dni + "' />\r\n"
                    + "        <input type='hidden' name='acronimo' value='" + acronimo + "' />\r\n"
                    + "        <input type='hidden' name='nombre' value='" + nombre_alumno + "' />\r\n"
                    + "        <input type='hidden' name='apellido' value='" + apellido_alumno + "' />\r\n"
                    + "        <p><b>Modificar nota</b> (MAX 10, MIN 0) </p>\r\n"
                    + "        <input type='number' id='nota' name='nota' placeholder='" + nota_asignatura + "' min='1' max='10' />\r\n"
                    + "        <button type='submit' class='btn btn-primary'>Modificar</button>\r\n"
                    + "        <br><br></form>\r\n"
                    + "    </div>\r\n"
                    + "</div>");
            out.println("<script>\r\n"
                    + "$(document).ready(function() {\r\n"
                    + "    $('form').submit(function() {\r\n"
                    + "        $.ajax({\r\n"
                    + "            url: 'ModificacionNotas',\r\n"
                    + "            type: 'POST',\r\n"
                    + "            contentType: 'application/json',\r\n"
                    + "            data: \"dni=" + dni + "&acronimo=" + acronimo + "&nota=\" + $('#nota').attr('value')\",\r\n"
                    + "            success: function(responseText) {\r\n"
                    //+ "                    $(\"#calificacion\").text(\"Nota: \" + response);\r\n"
                    //+ "                    $(\"#nota\").text(response);\r\n"
                    + "                     alert(\"Has añadido la nota \" + $('#nota').attr('value')  + \"con exito! -- \" + responseText);\r\n"
                    + "                 },\r\n"
                    + "            error: function(responseText) {alert(\"No has añadido la nota \" + $('#nota').attr('value') + \"! -- \" + responseText);}\r\n"
                    + "        });\r\n"
                    + "        return false;\r\n"
                    + "    });\r\n"
                    + "});"
                    + "</script>");
        }

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
        
        out.println("</body></html>");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}

