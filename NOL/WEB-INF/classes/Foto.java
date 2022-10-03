

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Foto
 */
public class Foto extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Foto() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String f = request.getParameter("dni"); 
        ServletContext context = getServletContext();
        String carpeta = context.getRealPath("/WEB-INF/imgs");	    
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        BufferedReader origen = new BufferedReader(new FileReader(carpeta + "/" + f +".pngb64"));
        PrintWriter out = response.getWriter();
        out.print("{\"dni\": \"" + f + "\", \"img\": \"");
        String linea = origen.readLine(); 
        out.print(linea);
        while ((linea = origen.readLine()) != null) {out.print("\n"+linea);}
        out.print("\"}");
        out.close(); 
        origen.close();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
