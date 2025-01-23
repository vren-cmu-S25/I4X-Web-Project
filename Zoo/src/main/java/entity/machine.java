package entity;

import java.util.Set;
import org.json.JSONArray; 
import org.json.JSONException; 
import org.json.JSONObject;

public class machine {
	private String name;
	private String ip;
	private String hr;
	private String min;
	private boolean status;
	
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
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
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
	public static class machineBuilder {
		private String name;
		private String ip;
		private String hr;
		private String min;
		private boolean status;
		
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
		public void setStatus(boolean status) {
			this.status = status;
		}
		
		public machine build() {
			return new machine(this);
		}
	}	
	
	private machine(machineBuilder builder) {
		this.name = builder.name;
		this.ip = builder.ip;
		this.hr = builder.hr;
		this.min = builder.min;
		this.status = builder.status;
	}
}
