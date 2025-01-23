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
import entity.machine;
import entity.machine.machineBuilder;

/**
 * Servlet implementation class Add
 */
@WebServlet("/Add")
public class Add extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Add() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false); 
		if (session == null) {
			response.setStatus(403);
			return; 
		}
		
		DBConnection conn = DBConnectionFactory.getDBConnection();
		try {
			machineBuilder mcbuilder = new machineBuilder();
			mcbuilder.setName(request.getParameter("name"));
			mcbuilder.setIp(request.getParameter("ip"));
			mcbuilder.setHr(request.getParameter("hr"));
			mcbuilder.setMin(request.getParameter("min"));
			mcbuilder.setStatus(true);
			
			JSONObject obj = new JSONObject();
			
			if (conn.registerMachine(mcbuilder.build())) {
				obj.put("register", "SUCCESS");
			}
			else {
				response.setStatus(401);
			}
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(obj.toString());
		}catch (Exception e ) {
			response.setStatus(500);
		}
	}

}
