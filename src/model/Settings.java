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
	private int rounds;
	private int questionsPerRound;
	private boolean consumeQuestions;
	public Settings(){
		setLocation("");
		setStrictTimeout(true);
		setTimeoutMs(20000);
		setQuestionsPerRound(3);
		setRounds(6);
		setConsumeQuestions(true);
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
		ret.put("consumeQuestions", c.encode(isConsumeQuestions()));
		ret.put("timeout", c.encode(getTimeoutMs()));
		ret.put("location", c.encode(getLocation()));
		ret.put("questionsPerRound", c.encode(getQuestionsPerRound()));
		ret.put("rounds", c.encode(getRounds()));
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
		Integer qprpt=codec.decode(m.get("questionsPerRound"), Integer.class);
		if(qprpt!=null)
			ret.setQuestionsPerRound(qprpt);
		Integer rounds=codec.decode(m.get("rounds"), Integer.class);
		if(rounds!=null)
			ret.setRounds(rounds);
		Boolean strictTimeout = codec.decode(m.get("strictTimeout"), Boolean.class);
		if(strictTimeout!=null)
			ret.setStrictTimeout(strictTimeout);
		Boolean consumeQuestions = codec.decode(m.get("consumeQuestions"), Boolean.class);
		if(consumeQuestions!=null)
			ret.setConsumeQuestions(consumeQuestions);
		return ret;
	}
	public int getRounds() {
		return rounds;
	}
	public void setRounds(int rounds) {
		this.rounds = rounds;
	}
	public int getQuestionsPerRound() {
		return questionsPerRound;
	}
	public void setQuestionsPerRound(int questionsPerRound) {
		this.questionsPerRound = questionsPerRound;
	}
	public boolean isConsumeQuestions() {
		return consumeQuestions;
	}
	public void setConsumeQuestions(boolean consumeQuestions) {
		this.consumeQuestions = consumeQuestions;
	}


}
