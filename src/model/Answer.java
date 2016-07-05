package model;

import json.JSOCodec;
import json.JSOEncodable;
import json.JSOWithPosition;

public class Answer implements JSOEncodable{
	int id;
	boolean correct;
	String text;
	public Answer(String text, int id){
		this.id=id;
		this.text=text;
		correct=id==0;
	}
	public String getText(){
		return text;
	}
	public boolean isCorrect(){
		return correct;
	}
	@Override
	public JSOWithPosition encode(JSOCodec c) {
		return new JSOWithPosition(text);
	}
}
