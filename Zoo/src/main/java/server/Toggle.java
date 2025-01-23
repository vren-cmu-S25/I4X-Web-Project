package server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Toggle
 */
@WebServlet("/Toggle")
public class Toggle extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Toggle() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// allow access only if session exists
		HttpSession session = request.getSession(false); 
		if (session == null) {
			response.setStatus(403);
			return; 
		}
		
		DBConnection conn = DBConnectionFactory.getDBConnection();
		try {
			String ip = request.getParameter("ip");
			JSONObject obj = new JSONObject();
			
			conn.toggleMachine(ip);
			obj.put("Toggle", "SUCCESS");

	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(obj.toString());
		}catch (Exception e ) {
			response.setStatus(500);
		}
	}

}
