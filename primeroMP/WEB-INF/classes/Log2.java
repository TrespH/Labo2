

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Log2
 */
public class Log2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Log2() {
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
		String output = "";
		String path = this.getServletContext().getInitParameter("Path") + "LOG2.txt";
		PrintWriter out = response.getWriter();
		String fechaHora =  LocalDateTime.now().toString();
		String user = request.getParameter("user");
		String password = request.getParameter("password");
		String URI = request.getRequestURL() + request.getContextPath() + request.getServletPath() + request.getPathInfo();
		output += "<!DOCTYPE html>\n<html lang='es-es'><head><title>Log1</title></head><body><table>\n";
		output += "<tr><td colspan=2><b>Info</b></td><tr>\n";
		output += "<tr><td>Usuario</td><td>"+user+"</td><tr>\n";
		output += "<tr><td>Contraseña</td><td>"+password+"</td><tr>\n";
		output += "<tr><td>Fecha y Hora</td><td>"+fechaHora+"</td><tr>\n";
		output += "<tr><td>Request URI</td><td>"+URI+"</td><tr>\n";
		output += "<tr><td>Path</td><td>"+path+"</td><tr>\n";
		output += "<tr><td colspan=2><b>Protocolo HTTP</b></td><tr>\n";
		output += "<tr><td>Metodo</td><td>"+request.getMethod()+"</td><tr>\n";			
		output += "</table></body></html>";
		out.println(output);
		try {
	      File myObj = new File(path);
	      if (myObj.createNewFile()) {
	        System.out.println("File created: " + myObj.getName());
	        FileWriter textFile = new FileWriter(myObj);
	        String res = path + "\n" + fechaHora + "\n" + "\n" + user + "\n" + password + "\n" + URI;
	        textFile.write(res);
	        textFile.close();
	      } else {
	        System.out.println("File already exists.");
	      }
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
