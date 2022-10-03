

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.fluent.Executor;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.hc.core5.http.HttpHeaders;


/**
 * Servlet implementation class ModificacionNotas
 */
public class ModificacionNotas extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModificacionNotas() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String titulo = "<!DOCTYPE html>" + 
                "<html lang=\"es-es\">" + 
                "<head>" + 
                "<meta charset=\"UTF-8\">" + 
                "<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p\" crossorigin=\"anonymous\"></script>" +
                "<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3\" crossorigin=\"anonymous\">" +
                "<title>Nota</title>" +
                "</head>";
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(titulo);

        String dni = request.getParameter("dni").toString();
        String acronimo = request.getParameter("acronimo").toString();
        String nota_param = request.getParameter("nota").toString();

        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<Cookie> cookie_list = (List<Cookie>) session.getAttribute("cookie");
        Executor executor = Executor.newInstance();
        BasicCookieStore cookies = new BasicCookieStore();
        cookies.addCookie(cookie_list.get(0));
        String url = request.getLocalName();
        String key = session.getAttribute("key").toString();

        StringEntity nota_entity = new StringEntity(nota_param);
        executor.use(cookies).execute(Request.put("http://" + url + ":9090/CentroEducativo/alumnos/" + dni + "/asignaturas/" + acronimo + "?key=" + key)
                .body(nota_entity)
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

        String notas = executor.use(cookies)
                .execute(Request.get("http://" + url + ":9090/CentroEducativo/alumnos/" + dni + "/asignaturas/?key=" + key))
                .returnContent().toString();

        JSONArray notas_json = new JSONArray(notas);
        String nota_asignatura = "0"; 
        for (int i = 0; i < notas_json.length(); i++) {
            JSONObject notas_obj = notas_json.getJSONObject(i);
            String nota = notas_obj.get("nota").toString();
            if(notas_obj.get("asignatura").toString().equals(acronimo)) {nota_asignatura = nota;}
        }
        out.println("<body><br><h3 style='text-align: center;'> Has añadido la nota (" + nota_asignatura + ") con exito! </h3>");
        out.println("<form style='text-align: center;' action='Detalles' method='GET'>" + 
                "<input type='hidden' name='asig' value='" + acronimo + "'/>" +
                "<input type='hidden' name='dni' value='" + dni + "'/>" +
                "<input type='hidden' name='nombre' value='" + request.getParameter("nombre") + "'/>" +
                "<input type='hidden' name='apellido' value='" + request.getParameter("apellido") + "'/>" +
                "<button type='submit' class='btn'>Volver</button>" + 
                "</form>");
        out.println("</body></html>");
    }

}
