

import java.io.IOException;
import java.io.PrintWriter;
//import java.time.LocalTime;
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
 * Servlet implementation class LoginAlumno
 */
public class LoginAlumno extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginAlumno() {
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
                "<title>NOL</title>" +
                "<script src=\"https://code.jquery.com/jquery-3.6.0.min.js\"></script>" +
                "<link rel='stylesheet' type='text/css' href='General.css'> " + 
                "</head>";
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(titulo);

        HttpSession session = request.getSession();
        String dni = session.getAttribute("dni").toString();
        String key = session.getAttribute("key").toString();
        @SuppressWarnings("unchecked")
        List<Cookie> cookie_list = (List<Cookie>) session.getAttribute("cookie");
        String url = request.getLocalName();


        BasicCookieStore cookies = new BasicCookieStore();
        cookies.addCookie(cookie_list.get(0));
        Executor executor = Executor.newInstance();
        String t = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/alumnos/" + dni + "?key=" + key))
                .returnContent().toString();

        JSONObject alumno = new JSONObject(t);
        String nombre_alumno = alumno.get("nombre").toString();
        String apellido_alumno = alumno.get("apellidos").toString();
        out.println("<body>" + 
                "   <div class='row' id='titulo'>" + 
                "       <h1><b>Notas OnLine. </b>Asignaturas de " + nombre_alumno + " " + apellido_alumno + "</h1> </div>" + 
                "       <div class='row' id='CS'><button type='button' onClick='logout()' class='btn btn-link' style='color: red;'>Cerrar sesión</button></div>" + 
                "       <div id='descripcion'>" + 
                "           <p><b>En esta página se muestran las asignaturas en las que estás matriculad@.</b></p>" + 
                "           <p><b>Al pulsar en una podrás acceder a tu calificación</b></p>" + 
                "       </div><br>");

        out.println("<div class='row'>" + 
                "   <div class='col-8' id='inicios'>" + 
                "       <div class='row' id='asignaturas'>" + 
                "           <p><b>Asignaturas:</b></p>" +
                "       </div>");

        session.setAttribute("rol", "rolalu");
        String parametro_asignatura = "asignatura";
        String asignaturas = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/alumnos/" + dni + "/asignaturas/?key=" + key))
                .returnContent().toString();

        JSONArray array = new JSONArray(asignaturas);
        for (int i = 0; i < array.length(); i++) {
            JSONObject asignatura_json = array.getJSONObject(i);
            String acronimo = asignatura_json.getString(parametro_asignatura);

            String detallesAsignaturas = executor.use(cookies)
                    .execute(Request.get("http://" + url + ":9090/CentroEducativo/asignaturas/" + acronimo + "/?key=" + key))
                    .returnContent().toString();
            JSONObject detalles = new JSONObject(detallesAsignaturas);
            String nombreD = detalles.getString("nombre");

            out.println("<form action='Detalles' method='GET'>" + 
                    "<input type='hidden' name='asig' value='" + acronimo + "'/>" +
                    "<input type='hidden' name='dni' value='" + dni + "'/>" +
                    "<input type='hidden' name='nombre' value='" + nombre_alumno + "'/>" +
                    "<input type='hidden' name='apellido' value='" + apellido_alumno + "'/>" +
                    "<button type='submit' class='btn btn-link'>" + nombreD + "</button> " + 
                    "</form>");
        }

        out.println("</div>");
        out.println("<div class='card col-3' style='width: 18rem;' id='nombres'>" + 
                "   <div class='card-body'>" +
                "       <h5 class='card-title'>Grupo 3TI12_G2</h5>" + 
                "       <h6 class='card-subtitle mb-2 text-muted'>Conoces los miembros del nuestro grupo!</h6>" + 
                "       <ol>" + 
                "           <li>Lopez Honrubia, Pablo</li>" +
                "           <li>Malerba, Alessio</li>" +
                "           <li>Pompilio, Matteo</li>" +
                "           <li>Sales García, Gabriel</li>" +
                "       </ol>" + 
                "   </div>" +
                "</div>" + 
                "</div>");

        out.println("<div>"+
                "   <form action='Expediente' method='GET'>" + 
                "   <input type='hidden' name='dni' value='" + dni + "'/>" +
                "   <input type='hidden' name='nombre' value='" + nombre_alumno + "'/>" +
                "   <input type='hidden' name='apellido' value='" + apellido_alumno + "'/>" +
                "   <button type='submit' class='btn btn-outline-secondary'> <img src='impresora.png' width='30pt'/> Imprimir expediente </button>" +
                "   </form>" + 
                "</div>");
        out.println("<div><p>Trabajo en grupo realizado por el grupo 3TI12_G2 en el curso DEW 2021/2022</p></div>");

        out.println("<script>\n"
                + "function logout() {\n"
                + "    $.ajax({\n"
                + "        type: \"GET\",\n"
                + "        url: window.location,\n"
                + "        dataType: 'json',\n"
                + "        async: true,\n"
                + "        username:'00',\n"
                + "        password:'00',\n"
                + "        data: '{\"logout\"}'\n"
                + "    }).fail(function() {\n"
                + "        $(document).ready(function() {\n"
                + "            $.get('Logout', {}, function(responseText) {});\n"
                + "        });\n"
                + "        window.location.replace('index.html');\n"
                + "    });\n"
                + "}"
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