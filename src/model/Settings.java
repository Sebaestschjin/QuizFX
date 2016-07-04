package model;

import java.util.HashMap;
import java.util.Map;

import json.JSOCodec;
import json.JSOEncodable;
import json.JSOWithPosition;

public class Settings implements JSOEncodable, Cloneable{
	private String location;
	private boolean strictTimeout;
	private int timeoutMs;
	public Settings(){
		setLocation("");
		setStrictTimeout(true);
		setTimeoutMs(20000);
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public boolean isStrictTimeout() {
		return strictTimeout;
	}
	public void setStrictTimeout(boolean strictTimeout) {
		this.strictTimeout = strictTimeout;
	}
	public int getTimeoutMs() {
		return timeoutMs;
	}
	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}
	public Settings clone(){
		try {
			return (Settings) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public JSOWithPosition encode(JSOCodec c) {
		Map<String, JSOWithPosition> ret=new HashMap<>();
		ret.put("strictTimeout", c.encode(isStrictTimeout()));
		ret.put("timeout", c.encode(getTimeoutMs()));
		ret.put("location", c.encode(getLocation()));
		return new JSOWithPosition(ret);
	}
	public static Settings decodeJSO(JSOWithPosition jso, JSOCodec codec){
		Map<String, JSOWithPosition> m=jso.getObjectNonNull();
		Settings ret=new Settings();
		String location=codec.decode(m.get("location"), String.class);
		if(location!=null)
			ret.setLocation(location);
		Integer timeout=codec.decode(m.get("timeout"), Integer.class);
		if(timeout!=null)
			ret.setTimeoutMs(timeout);
		Boolean strictTimeout = codec.decode(m.get("strictTimeout"), Boolean.class);
		if(strictTimeout!=null)
			ret.setStrictTimeout(strictTimeout);
		return ret;
	}

}
