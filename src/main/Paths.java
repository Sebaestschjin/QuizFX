package main;

import java.io.File;

public class Paths {
	public static File resourcesDir=new File("res");
	public static File asRelativeTo(File path, File query){
		if(query.getAbsolutePath().startsWith(path.getAbsolutePath()))
			return new File(query.getAbsolutePath().substring(path.getAbsolutePath().length()+1));
		if(query.isAbsolute())
			return query.getAbsoluteFile();
		else
			return new File(path, query.toString());
	}
	public static File settingsFile(){
		return new File(new File(System.getProperty("user.home")), ".sexduell.json");
	}
	public static File relative(File ref, File path){
		if(path.isAbsolute())
			return path;
		return new File(ref, path.getPath());
	}
}
