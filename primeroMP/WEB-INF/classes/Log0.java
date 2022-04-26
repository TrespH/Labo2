

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Log0
 */
public class Log0 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Log0() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String fechaHora =  LocalDateTime.now().toString();
		String user = request.getParameter("user");
		String password = request.getParameter("password");
		String URI = request.getRequestURL() + request.getContextPath() + request.getServletPath() + request.getPathInfo();
		out.println("<!DOCTYPE html>\n<html lang='es-es'><head><title>Log0</title></head><body><table>");
		out.println("<tr><td colspan=2><b>Info</b></td><tr>");
		out.println("<tr><td>Usuario</td><td>"+user+"</td><tr>");
		out.println("<tr><td>Contraseña</td><td>"+password+"</td><tr>");
		out.println("<tr><td>Fecha y Hora</td><td>"+fechaHora+"</td><tr>");
		out.println("<tr><td>Request URI</td><td>"+URI+"</td><tr>");
		out.println("<tr><td colspan=2><b>Protocolo HTTP</b></td><tr>");
		out.println("<tr><td>Metodo</td><td>"+request.getMethod()+"</td><tr>");			
		out.println("</table></body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}