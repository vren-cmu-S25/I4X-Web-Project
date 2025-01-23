package server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getDBConnection(); 
		try {
			JSONObject obj = new JSONObject(); 
			HttpSession session = request.getSession(false); 
			if (session == null) {
				response.setStatus(403);
				obj.put("status", "Session Invalid"); 
			} 
			else {
				String username = (String) session.getAttribute("username"); 
				String name = conn.getFullname(username); 
				obj.put("status", "OK");
				obj.put("username", username);
				obj.put("name", name);
		}
		AppHelper.writeJsonObject(response, obj); } 
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    DBConnection conn = DBConnectionFactory.getDBConnection(); 
	    try {
	        // Get username and password from the request parameters
	        String username = request.getParameter("username");
	        String pwd = request.getParameter("password");
	        JSONObject obj = new JSONObject();
	        
	        if (conn.verifyLogin(username, pwd)) {
	            HttpSession session = request.getSession(); 
	            session.setAttribute("username", username);
	            // setting session to expire in 10 minutes 
	            session.setMaxInactiveInterval(10 * 60);
	            // Get user name
	            String name = conn.getFullname(username); 
	            obj.put("status", "OK");
	            obj.put("name", name);
	            obj.put("username", username);
	        } else {
	            response.setStatus(401);
	        }
	        // Write response object as JSON
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(obj.toString());
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Handle any exceptions here
	        response.setStatus(500); // Internal server error
	    }
	}


}
