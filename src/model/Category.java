package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import javafx.scene.paint.Color;
import json.DataFormatException;
import json.JSOCodec;
import json.JSOEncodable;
import json.JSOWithPosition;
import main.Paths;
import util.Colors;
import util.Weighted;

public class Category implements Weighted, JSOEncodable{
	String title;
	List<Question> questions;
	Color color;
	File imageFile;
	BufferedImage image;
	double weight;

	public Category(String title, Color c, File imageFile, double weight, Collection<Question> questions){
		this.title=title;
		this.questions=new ArrayList<>(questions);
		this.color=c;
		this.imageFile=imageFile;
		this.weight=weight;
	}
	public Category(String title, java.awt.Color c, File imageFile, double weight, Collection<Question> questions){
		this(title,Colors.toFx(c), imageFile, weight, questions);
	}
	public Category withTitle(String title){
		return new Category(title, color, imageFile, weight, questions);
	}
	public Category withColor(Color color){
		return new Category(title, color, imageFile, weight, questions);
	}
	public Category withImageFile(File imageFile){
		return new Category(title, color, imageFile, weight, questions);
	}
	public Category withWeight(double weight){
		return new Category(title, color, imageFile, weight, questions);
	}
	public Category withQuestions(Collection<Question> questions){
		return new Category(title, color, imageFile, weight, questions);
	}
	public String getTitle(){
		return title;
	}
	public File getImageFile(){
		return imageFile;
	}
	public BufferedImage getImage() throws IOException{
		if(image==null){
			if(imageFile!=null){
				File abs = Paths.relative(Paths.resourcesDir, imageFile);
				image=ImageIO.read(abs);
			}
		}
		return image;
	}
	@Override
	public double getWeight() {
		return weight;
	}
	public List<Question> getQuestions(){
		return Collections.unmodifiableList(questions);
	}
	public Color getColor() {
		return color;
	}
	public java.awt.Color getAwtColor() {
		return Colors.toAwt(color);
	}
	@Override
	public JSOWithPosition encode(JSOCodec c) {
		Map<String, JSOWithPosition> ret=new HashMap<>();
		ret.put("name", c.encode(title));
		ret.put("bild", c.encode(imageFile==null?null:imageFile.getPath()));
		ret.put("farbe", c.encode(Colors.toString(color)));
		ret.put("gewicht", c.encode(weight));
		ret.put("fragen", c.encode(questions));
		return new JSOWithPosition(ret);
	}
	public static Category decodeJSO(JSOWithPosition jso, JSOCodec codec){
		Map<String, JSOWithPosition> m=jso.getObjectNonNull();
		String title=codec.decode(m.get("name"), String.class);
		if(title==null)
			throw new DataFormatException("\"name\" is required", jso.getPosition());
		String iFile=codec.decode(m.get("bild"), String.class);
		String colorString=codec.decode(m.get("farbe"), String.class);
		if(colorString==null)
			throw new DataFormatException("\"farbe\" is required", jso.getPosition());
		Color color=Colors.parseColor(colorString, m.get("farbe").getPosition());
		Double weight=codec.decode(m.get("gewicht"), Double.class);
		if(weight==null)
			weight=1.0;
		List<Question> questions=codec.decodeList(m.get("fragen"), Question.class);
		if(questions==null)
			throw new DataFormatException("\"fragen\" is required", jso.getPosition());
		return new Category(title, color, iFile==null?null:new File(iFile), weight, questions);
	}

	@Override
	public String toString() {
		return title;
	}
}
