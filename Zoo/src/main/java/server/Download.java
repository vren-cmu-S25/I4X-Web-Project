package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.logRecord;
import entity.logRecord.LogRecordComparator;

/**
 * Servlet implementation class Download
 */
@WebServlet("/Download")
public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Download() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// allow access only if session exists
		HttpSession session = request.getSession(false); 
		if (session == null) {
			response.setStatus(403);
			return; 
		}
				
		DBConnection connection = DBConnectionFactory.getDBConnection(); 
		
		try {
			String inputDateString = request.getParameter("date");
			String[] dateComponents = inputDateString.split("-"); 

			String year = dateComponents[0];
			String month = dateComponents[1];
			String date = dateComponents[2];

			String tableName = "log_" + year + "_" + month + "_" + date;
        
			Set<logRecord> records = connection.getLog(tableName);	
			
			if (records == null) {
			    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			    return;
			}
			
			List<logRecord> sortedRecords = new ArrayList<>(records);
			Collections.sort(sortedRecords, new LogRecordComparator());
			
			// Set response content type to plain text
	        response.setContentType("text/plain");

	        // Set response headers for file download
	        response.setHeader("Content-Disposition", "attachment; filename=logs_" + date + ".txt");
			
	        PrintWriter out = response.getWriter();
	        String prevHour = null;
	        String prevMin = null;

	        for (logRecord record : sortedRecords) {
	            String currentHour = record.getHr();
	            String currentMin = record.getMin();

	            // Check if hour or minute has changed
	            if (!currentHour.equals(prevHour) || !currentMin.equals(prevMin)) {
	                // Print header row for new hour and minute combination
	                out.print("====================");
	                out.print(currentHour + ":" + currentMin);
	                out.println("====================");
	            }

	            // Print record data with fixed column width
	            out.printf("%-14s%-16s%s%n", record.getName(), record.getIp(), record.getStatus());

	            // Update previous hour and minute
	            prevHour = currentHour;
	            prevMin = currentMin;
	        }

	        // Flush and close the PrintWriter
	        out.flush();
	        out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
