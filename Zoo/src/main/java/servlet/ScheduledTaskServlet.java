package servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletContextEvent;
import entity.machine;
import db.DBConnection;
import db.DBConnectionFactory;

public class ScheduledTaskServlet {
	private Timer timer;

    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new Task(), 0, 60 * 1000); // run every minute
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if (timer != null) {
            timer.cancel();
        }
    }

    private class Task extends TimerTask {
        @Override
        public void run() {
        	DBConnection conn = DBConnectionFactory.getDBConnection(); 
    		Set<machine> items = conn.getMachines();
    		
    		for (machine item : items) {
                String hr = item.getHr();
                String min = item.getMin();
                String ip = item.getIp();
                boolean isOn = item.isStatus();

                String currentHour = getCurrentHour();
                String currentMinute = getCurrentMinute();
                
                if (hr.equals(currentHour) && min.equals(currentMinute)) {
                    String status;
                	if (!isOn) status = "OFF";
                	else {	
                		try {
                        	int responseCode = sendGetRequest(ip);
                        	status = (responseCode == HttpURLConnection.HTTP_OK) ? "SUCCESS" : "FAIL";
                    	} catch (Exception e) {
                    		status = "FAIL";
                    	}
                	}
                    conn.writeLog(item, status);
                }
            }
        }

        private String getCurrentHour() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            return sdf.format(new Date());
        }

        private String getCurrentMinute() {
            SimpleDateFormat sdf = new SimpleDateFormat("mm");
            return sdf.format(new Date());
        }

        private int sendGetRequest(String ip) throws Exception {
            int responseCode;
            try {
                String urlString = "http://" + ip + "/H";
                URL url = new URI(urlString).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    in.readLine(); // Read the response (not used)
                    in.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e; // Re-throw the exception to indicate failure
            }
            return responseCode;
        }
    }
}
