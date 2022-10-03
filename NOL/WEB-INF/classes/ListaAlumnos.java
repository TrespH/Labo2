

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
 * Servlet implementation class ListaAlumnos
 */
public class ListaAlumnos extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListaAlumnos() {
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
                "<title>Alumnos</title>" +
                "<link rel='stylesheet' type='text/css' href='General.css'>" + 
                "</head>";
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(titulo);
        String acronimo = request.getParameter("acronimo");

        out.println ("<body>" + 
                "<div id='titulo' class='row'>" + 
                "   <div class='col-1'> <a href='LoginProf'><img src='Flecha_atras.png' id='atras' width='30pt'/></a></div>" + 
                "   <div class='col-10'><h1><b>Notas OnLine. </b>Alumnos de la Asignatura " + acronimo  + "</h1></div>" +
                "</div>");
        out.println("<div id='descripcion'>" + 
                "   <p><b>Lista de alumnos de la asignatura, al pulsar podrás ver más detalles:</b></p>" +
                "</div>");
        out.println("<br>" + 
                "<div class='row'>" + 
                "   <div class='col-8' id='inicios'>" + 
                "       <div class='row' id='asignaturas'>" + 
                "           <p><b>Alumnos y nota:</b></p>" +
                "       </div>");



        HttpSession sesion = request.getSession();
        //String dni_profesor = sesion.getAttribute("dni").toString();
        String key = sesion.getAttribute("key").toString();
        @SuppressWarnings("unchecked")
        List<Cookie> cookie_list = (List<Cookie>) sesion.getAttribute("cookie");
        BasicCookieStore cookies = new BasicCookieStore();
        cookies.addCookie(cookie_list.get(0));
        String url = request.getLocalName();

        Executor executor = Executor.newInstance();
        String lista_alumnos = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/asignaturas/" + acronimo + "/alumnos?key=" + key))
                .returnContent().toString();

        JSONArray lista_alumnos_array = new JSONArray(lista_alumnos);

        for (int i = 0; i < lista_alumnos_array.length(); i++) {
            JSONObject lista_alumnos_json = lista_alumnos_array.getJSONObject(i);
            String dni_alumno = lista_alumnos_json.getString("alumno");
            String nota = lista_alumnos_json.getString("nota");
            if(nota.length() == 0) {nota = "Sin calificar";}

            String t = executor.use(cookies)
                    .execute(Request.get("http://" + url + ":9090/CentroEducativo/alumnos/" + dni_alumno + "?key=" + key))
                    .returnContent().toString();

            JSONObject alumno = new JSONObject(t);
            String nombre_alumno = alumno.get("nombre").toString();
            String apellido_alumno = alumno.get("apellidos").toString();

            out.println("<form action='Detalles' method='GET'>" + 
                    "<input type='hidden' name='asig' value='" + acronimo + "'/>" +
                    "<input type='hidden' name='dni' value='" + dni_alumno + "'/>" +
                    "<input type='hidden' name='nombre' value='" + nombre_alumno + "'/>" +
                    "<input type='hidden' name='apellido' value='" + apellido_alumno + "'/>" +
                    "<ul>" + 
                    "   <li><p>" + dni_alumno + " - Nota: " + nota + "<button type='submit' class='btn btn-link'>Ver detalles</button></p></li>" +
                    "</ul>" + 
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
        out.println("<div><p>Trabajo en grupo realizado por el grupo 3TI12_G2 en el curso DEW 2021/2022</p></div>");
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
