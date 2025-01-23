package entity;

import java.util.Set;
import org.json.JSONArray; 
import org.json.JSONException; 
import org.json.JSONObject;
import java.util.Comparator;

public class logRecord {
	private String name;
	private String ip;
	private String hr;
	private String min;
	private String status;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHr() {
		return hr;
	}
	public void setHr(String hr) {
		this.hr = hr;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("name", name);
			obj.put("ip", ip);
			obj.put("hr", hr);
			obj.put("min", min);
			obj.put("status", status);
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	// builder pattern
	public static class logRecordBuilder {
		private String name;
		private String ip;
		private String hr;
		private String min;
		private String status;
		
		public void setName(String name) {
			this.name = name;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public void setHr(String hr) {
			this.hr = hr;
		}
		public void setMin(String min) {
			this.min = min;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
		public logRecord build() {
			return new logRecord(this);
		}
	}	
	
	private logRecord(logRecordBuilder builder) {
		this.name = builder.name;
		this.ip = builder.ip;
		this.hr = builder.hr;
		this.min = builder.min;
		this.status = builder.status;
	}
	
	public static class LogRecordComparator implements Comparator<logRecord> {
	    @Override
	    public int compare(logRecord r1, logRecord r2) {
	        // Convert hour and minute strings to integers for comparison
	        int hour1 = Integer.parseInt(r1.getHr());
	        int min1 = Integer.parseInt(r1.getMin());
	        int hour2 = Integer.parseInt(r2.getHr());
	        int min2 = Integer.parseInt(r2.getMin());

	        // Compare hours
	        if (hour1 != hour2) {
	            return Integer.compare(hour1, hour2);
	        } else {
	            // If hours are equal, compare minutes
	            return Integer.compare(min1, min2);
	        }
	    }
	}
}
