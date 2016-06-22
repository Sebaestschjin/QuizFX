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
import json.parser.PosBuffer;
import main.Paths;
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
	@Override
	public JSOWithPosition encode(JSOCodec c) {
		Map<String, JSOWithPosition> ret=new HashMap<>();
		ret.put("name", c.encode(title));
		ret.put("bild", c.encode(imageFile==null?null:imageFile.getPath()));
		ret.put("farbe", c.encode(((int)color.getRed()*0xFF)+"/"+((int)color.getGreen()*0xFF)+"/"+((int)color.getBlue()*0xFF)));
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
		Color color=parseColor(colorString, m.get("farbe").getPosition());
		Double weight=codec.decode(m.get("gewicht"), Double.class);
		if(weight==null)
			weight=1.0;
		List<Question> questions=codec.decodeList(m.get("fragen"), Question.class);
		if(questions==null)
			throw new DataFormatException("\"fragen\" is required", jso.getPosition());
		return new Category(title, color, iFile==null?null:new File(iFile), weight, questions);
	}
	private static Color parseColor(String s, PosBuffer pos) {
		int firstSlash=s.indexOf('/');
		int lastSlash=s.lastIndexOf('/');
		if(firstSlash!=lastSlash && firstSlash>=0 && lastSlash>=0){
			try{
				int r = Integer.parseInt(s.substring(0, firstSlash));
				int g = Integer.parseInt(s.substring(firstSlash+1, lastSlash));
				int b = Integer.parseInt(s.substring(lastSlash+1));
				if(r<0 || g<0 || b<0 || r>0xFF || g>0xFF || b>0xFF)
					throw new DataFormatException("invalid color string: Number out of range", pos);
				double factor=1.0/0xFF;
				return new Color(r*factor, g*factor, b*factor, 1);
			}catch(NumberFormatException x){
				throw new DataFormatException("invalid color string", pos);
			}

		}
		if(s.length()==6 && s.matches("[0-9a-fA-F]*")){
			int r = Integer.parseInt(s.substring(0, 2), 16);
			int g = Integer.parseInt(s.substring(2, 4), 16);
			int b = Integer.parseInt(s.substring(4, 6), 16);
			double factor=1.0/0xFF;
			return new Color(r*factor, g*factor, b*factor, 1);

		}
		throw new DataFormatException("invalid color string", pos);
	}
}
