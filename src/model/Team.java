package model;

import java.util.HashMap;
import java.util.Map;

import json.DataFormatException;
import json.JSOCodec;
import json.JSOEncodable;
import json.JSOWithPosition;

public class Team implements JSOEncodable{
	String name;
	int id;
	static int idCounter;
	
	public Team(String name){
		this.name=name;
		id=idCounter++;
	}
	public Team(String name, int id){
		this.name=name;
		this.id=id;
	}
	public String getName(){
		return name;
	}
	public int getId(){
		return id;
	}
	public static int getIdCounter(){
		return idCounter;
	}
	public static void setIdCounter(int newValue){
		idCounter=newValue;
	}
	@Override
	public JSOWithPosition encode(JSOCodec c) {
		Map<String, JSOWithPosition> ret=new HashMap<>();
		ret.put("name", c.encode(name));
		ret.put("id", c.encode(id));
		return new JSOWithPosition(ret);
	}
	public static Team decodeJSO(JSOWithPosition jso, JSOCodec codec){
		Map<String, JSOWithPosition> m=jso.getObjectNonNull();
		String name=codec.decode(m.get("name"), String.class);
		if(name==null)
			throw new DataFormatException("\"name\" is required", jso.getPosition());
		Integer id=codec.decode(m.get("id"), Integer.class);
		if(id==null)
			throw new DataFormatException("\"id\" is required", jso.getPosition());
		return new Team(name, id);
	}
	
	
}
