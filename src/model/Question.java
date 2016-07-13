package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import json.DataFormatException;
import json.JSOCodec;
import json.JSOEncodable;
import json.JSOWithPosition;
import main.Paths;
import util.Weighted;

public class Question implements Weighted, JSOEncodable{
	String questionText;
	File questionImageFile;
	BufferedImage questionImage;
	String answerText;
	String answerSource;
	File answerImageFile;
	BufferedImage answerImage;
	double weight;
	List<Answer> answers=new ArrayList<Answer>(4);
	public Question(String qText, File qFile, String aText, File aFile, String aSource, double weight, Answer...answers){
		this(qText, qFile, aText, aFile, aSource, weight, Arrays.asList(answers));
	}
	public Question(String qText, File qFile, String aText, File aFile, String aSource, double weight, Collection<Answer> answers){
		questionText=qText;
		questionImageFile=qFile;
		answerText=aText;
		answerImageFile=aFile;
		answerSource=aSource;
		this.weight=weight;
		this.answers.addAll(answers);
	}
	public Question withQuestionText(String questionText){
		return new Question(questionText, questionImageFile, answerText, answerImageFile, answerSource, weight, answers);
	}
	public Question withAnswerText(String answerText){
		return new Question(questionText, questionImageFile, answerText, answerImageFile, answerSource, weight, answers);
	}
	public Question withAnswerSource(String answerSource){
		return new Question(questionText, questionImageFile, answerText, answerImageFile, answerSource, weight, answers);
	}
	public Question withQuestionImageFile(File questionImageFile){
		return new Question(questionText, questionImageFile, answerText, answerImageFile, answerSource, weight, answers);
	}
	public Question withAnswerImageFile(File answerImageFile){
		return new Question(questionText, questionImageFile, answerText, answerImageFile, answerSource, weight, answers);
	}
	public Question withWeight(double weight){
		return new Question(questionText, questionImageFile, answerText, answerImageFile, answerSource, weight, answers);
	}
	public Question withAnswers(Answer... answers){
		return new Question(questionText, questionImageFile, answerText, answerImageFile, answerSource, weight, answers);
	}
	public Question withAnswers(Collection<Answer> answers){
		return new Question(questionText, questionImageFile, answerText, answerImageFile, answerSource, weight, answers);
	}
	public Question withAnswer(Answer a){
		Answer[] as=(Answer[]) answers.toArray(new Answer[answers.size()]);
		as[a.id]=a;
		return withAnswers(as);
	}
	public String getQuestionText(){
		return questionText;
	}
	public File getQuestionImageFile(){
		return questionImageFile;
	}
	public BufferedImage getQuestionImage() throws IOException{
		if(questionImage==null){
			if(questionImageFile!=null){
				File abs = Paths.relative(Paths.resourcesDir, questionImageFile);
				questionImage=ImageIO.read(abs);
			}
		}
		return questionImage;
	}
	public String getAnswerText(){
		return answerText;
	}
	public File getAnswerImageFile(){
		return answerImageFile;
	}
	public BufferedImage getAnswerImage() throws IOException{
		if(answerImage==null){
			if(answerImageFile!=null){
				File abs = Paths.relative(Paths.resourcesDir, answerImageFile);
				answerImage=ImageIO.read(abs);
			}
		}
		return answerImage;
	}
	public double getWeight(){
		return weight;
	}
	public List<Answer> getAnswers(){
		return Collections.unmodifiableList(answers);
	}
	public List<Answer> getShuffledAnswers(){
		ArrayList<Answer> r=new ArrayList<>(answers);
		Collections.shuffle(r);
		return r;
	}
	@Override
	public JSOWithPosition encode(JSOCodec c) {
		Map<String, JSOWithPosition> ret=new HashMap<>();
		ret.put("fragetext", c.encode(questionText));
		ret.put("fragebild", c.encode(questionImageFile==null?null:questionImageFile.getPath()));
		ret.put("antworttext", c.encode(answerText));
		ret.put("antwortbild", c.encode(answerImageFile==null?null:answerImageFile.getPath()));
		ret.put("antwortquulle", c.encode(answerSource));
		ret.put("gewicht", c.encode(weight));
		ret.put("antworten", c.encode(answers));
		return new JSOWithPosition(ret);
	}
	public static Question decodeJSO(JSOWithPosition jso, JSOCodec codec){
		Map<String, JSOWithPosition> m=jso.getObjectNonNull();
		String qText=codec.decode(m.get("fragetext"), String.class);
		if(qText==null)
			throw new DataFormatException("\"fragetext\" is required", jso.getPosition());
		String qFile=codec.decode(m.get("fragebild"), String.class);
		String aText=codec.decode(m.get("antworttext"), String.class);
		if(aText==null)
			throw new DataFormatException("\"antworttext\" is required", jso.getPosition());
		String aFile=codec.decode(m.get("antwortbild"), String.class);
		String aSource=codec.decode(m.get("antwortquelle"), String.class);
		Double weight=codec.decode(m.get("gewicht"), Double.class);
		if(weight==null)
			weight=1.0;
		List<String> preAnswers=codec.decodeList(m.get("antworten"), String.class);
		if(preAnswers==null)
			throw new DataFormatException("\"antworten\" is required", jso.getPosition());
		int i=0;
		List<Answer> answers=new ArrayList<>(preAnswers.size());
		for(String pa: preAnswers){
			answers.add(new Answer(pa, i++));
		}
		return new Question(qText, qFile==null?null:new File(qFile), aText, aFile==null?null:new File(aFile), aSource, weight, answers);
	}
	public String getAnswerSource() {
		return answerSource;
	}
	@Override
	public String toString() {
		return questionText.length()==0?" ":questionText;
	}
}
