package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.machine;
import java.security.SecureRandom;



/**
 * Servlet implementation class GenerateKey
 */
@WebServlet("/GenerateKey")
public class GenerateKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GenerateKey() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// allow access only if session exists
		HttpSession session = request.getSession(false); 
		if (session == null) {
			response.setStatus(403);
			return; 
		}
			
		String key = RandomKeyGenerator.generateRandomKey(256);
		JSONObject obj = new JSONObject();
		
		DBConnection conn = DBConnectionFactory.getDBConnection();
		try {
			conn.updateKey(key);
			obj.put("keyGeneration", "SUCCESS");
	        obj.put("key", key);
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(obj.toString());
		}catch (Exception e ) {
			response.setStatus(500);
		}
	}
}

// helper class that generate secrete random key
class RandomKeyGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomKey(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(index));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        int keyLength = 16; // specify the desired length
        String randomKey = generateRandomKey(keyLength);
        System.out.println("Generated Random Key: " + randomKey);
    }
}
