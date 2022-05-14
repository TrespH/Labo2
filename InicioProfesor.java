

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
import org.json.JSONArray;
import org.json.JSONObject;

public class InicioProf extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

	public InicioProf() {
		super();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String tit = "<!DOCTYPE html>\n" + 
				"<html lang=\"es\">\n" + 
				"<head>\n" + 
				"    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" + 
				"    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" + 
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + 
				"    <title>NOL</title>\n" + 
				"\n" + 
				"        <!--JQUERY-->\n" + 
				"        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" + 
				"            \n" + 
				"        <!-- FRAMEWORK BOOTSTRAP para el estilo de la pagina-->\n" + 
				"        <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4\" crossorigin=\"anonymous\"></script>\n" + 
				"\n" + 
				"        <!-- BOOTSTRAP css-->\n" + 
				"        <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x\" crossorigin=\"anonymous\">\n" + 
				"\n" + 
				"		<style>\n" + 
				"			#intro{\n" + 
				"			    color: white;\n" + 
				"			    background-color: grey;\n" + 
				"			    text-align: center;\n" + 
				"			    border-radius: 15px;\n" + 
				"			    margin-left: 10px;\n" + 
				"			    margin-right: 10px;\n" + 
				"			    margin-top: 10px;\n" + 
				"			   \n" + 
				"			}\n" + 
				"			\n" + 
				"			#inicios{\n" + 
				"			    margin-left: 2%;\n" + 
				"			}\n" + 
				"			\n" + 
				"			#nombres{\n" + 
				"			    background-color: rgb(204, 237, 241);\n" + 
				"			    border-radius: 15px;\n" + 
				"			}\n" + 
				"			\n" + 
				"			#pie{margin-left: 2%;}\n" + 
				"		</style>\n" + 
				"</head>\n" + 
				"\n"; 
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(titulo);
		HttpSession sesionpro = request.getSession();
		String dni = sesionpro.getAttribute("dni").toString();
		String key = sesionpro.getAttribute("key").toString();
		List<Cookie> cookies = (List<Cookie>) sesion.getAttribute("cookies");
		String url = request.getLocalName();


		BasicCookieStore cookiez = new BasicCookieStore();
		BasicCookieStore.addCookie(cookies.get(0));
		Executor executor = Executor.newInstance();
		String t = executor.use(cookies)
				.execute(Request.get("http://"+url+":9090/CentroEducativo/profesores/" + dni + "?key=" + key))
				.returnContent().toString();

		JSONObject profesor = new JSONObject(t);
		out.println("<body>\n" + 
				"        <div class=\"row\" id=\"intro\">\n" + 
				"            <h1><b>Notas OnLine. </b>Asignaturas del/la profesor@ " + profesor.get("nombre").toString() + " " + profesor.get("apellidos").toString()+"</h1>\n" + 
				"            <p><b>Aqu&iacute tienes las asignaturas que impartes.</b></p>\n" + 
				"            <p><b>Aqu&iacute tienes la lista de tus alumnos.</b></p>\n" + 
				"        </div>\n" + 
				"        <br>  \n" + 
				"        <div class=\"row\">\n" + 
				"            <div class=\"col-7\" id=\"inicios\">\n" + 
				"                <div class=\"row\" id=\"asignaturas\">");

		sesion.setAttribute("rol", "rolpro");
		String asigNom = "acronimo";
		String asignaturas = executor.use(cookie)
				.execute(Request.get("http://"+url+":9090/CentroEducativo/profesores/" + dni + "/asignaturas/?key=" + key))
				.returnContent().toString();	


		JSONArray array =new JSONArray(asignaturas);
		for(int i=0; i<array.length() ;i++) {
			JSONObject asig = array.getJSONObject(i);
			String acronimo = asig.getString(asigNom);
			String nombre = asig.getString("nombre");
			
			out.println("<form  action='Asignaturas' method='GET'> "
					+ "<h2><b>" + "<input type ='submit' style='border: 0ch;' value = '" + nombre + "' name='" + acronimo + "'></form>" + "</b></h2>");
		}
		out.println("</div>\n" + 
				"            </div>\n" + 
				"            <div class=\"col-4\" id=\"nombres\">\n" + 
				"                <h3>Grupo 3TI12_g04</h3>\n" + 
				"                <ol>\n" + 
				"                    <li>Sales Garcia, Gabriel</li>\n" + 
				"                    <li>Malerba, Alessio</li>\n" + 
				"                    <li>Pomplio, Matteo</li>\n" + 
				"                    <li>Lopez, Pablo</li>\n" +  
				"                </ol>\n" + 
				"            </div>\n" + 
				"        </div>\n" + 
				"        <br>\n" + 
				"        <hr>\n" + 
				"        <br>\n" + 
				"        <div id=\"pie\">\n" + 
				"           <p>Trabajo en grupo realizado para la asignatura Desarrollo Web. Curso 2021-2022</p>\n" + 
				"        </div>  \n" + 
				"       \n" + 
				"</body>\n" + 
				"</html> \n" + 
				"");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
