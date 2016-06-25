package json.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import json.JSOWithPosition;

public class JSONParser {
	enum TT{
		OPEN2("'['"), OPEN3("'{'"), CLOS2("']'"), CLOS3("'}'"), COMMA("','"), COLON("':'"),
		NUMBER("NUMBER"), BOOL("BOOELAN"), NULL("'null'"), STRING("STRING"), EOF("end of input");
		final String name;
		TT(String name){
			this.name=name;
		}
		public String getName(){
			return name;
		}
	}
	TT tokenType;
	Object tokenValue;

	PosBuffer oldPos;
	PosBuffer pos;
	PushbackReader input;

	public JSOWithPosition parse(Reader r, String srcName) throws IOException{
		input=new PushbackReader(r, 10);
		oldPos=null;
		pos=new PosBuffer(srcName);
		return parseValue();
	}

	public JSOWithPosition parse(String s, String srcName) throws IOException{
		return parse(new StringReader(s), srcName);
	}
	public JSOWithPosition parse(File s, String srcName) throws IOException{
		try(BufferedReader br=new BufferedReader(
				new InputStreamReader(new FileInputStream(s), "UTF-8"));
				)
		{
			return parse(br, srcName==null?s.getName():srcName);
		}		
	}
	public JSOWithPosition parse(File s) throws IOException{
		return parse(s, null);
	}
	private JSOWithPosition parseValue() throws IOException {
		TT tt=getToken();
		switch(tt){
		case NULL:
			consumeToken();
			return null;
		case BOOL:
			consumeToken();
			return new JSOWithPosition(tokenValue, oldPos);
		case NUMBER:
			consumeToken();
			return new JSOWithPosition(tokenValue, oldPos);
		case STRING:
			consumeToken();
			return new JSOWithPosition(tokenValue, oldPos);
		case OPEN2:{
			consumeToken();
			PosBuffer p=pos.clone();
			List<JSOWithPosition> ret=new ArrayList<>();
			if(getToken()!=TT.CLOS2){
				while(true){
					JSOWithPosition elem=parseValue();
					ret.add(elem);
					if(getToken()==TT.CLOS2)
						break;
					if(getToken()!=TT.COMMA)
						throw new ParserException("Unexpected token "+getToken().getName(), oldPos);
					consumeToken();
				}
			}
			assert(getToken()==TT.CLOS2);
			consumeToken();
			return new JSOWithPosition(ret, p);
		}
		case OPEN3:{
			consumeToken();
			PosBuffer p=pos.clone();
			Map<String, JSOWithPosition> ret=new HashMap<>();
			if(getToken()!=TT.CLOS3){
				while(true){
					if(getToken()!=TT.STRING)
						throw new ParserException("Expected "+TT.STRING.getName(), oldPos);
					String key=(String)tokenValue;
					consumeToken();
					if(getToken()!=TT.COLON)
						throw new ParserException("Expected "+TT.COLON.getName(), oldPos);
					consumeToken();
					
					JSOWithPosition value=parseValue();
					ret.put(key, value);
					if(getToken()==TT.CLOS3)
						break;
					if(getToken()!=TT.COMMA)
						throw new ParserException("Unexpected token "+getToken().getName(), oldPos);
					consumeToken();
				}
			}
			assert(getToken()==TT.CLOS3);
			consumeToken();
			return new JSOWithPosition(ret, p);
		}
		default:
			throw new ParserException("Unexpected token "+getToken().getName(), oldPos);
		}
	}



	private TT getToken() throws IOException{
		if(tokenType==null){
			lex();
			assert(tokenType!=null);
		}
		return tokenType;
	}
	@SuppressWarnings("unused")
	private TT nextToken() throws IOException{
		consumeToken();
		return getToken();
	}
	private void consumeToken(){
		tokenType=null;
	}




	private void lex() throws IOException{
		int ic;
		char c;
		tokenValue=null;
		while(true){

			ic=input.read();
			if(ic==-1){
				tokenType=TT.EOF;
				return;
			}
			c = (char)ic;
			if(" \t\n\r".indexOf(c)<0)
				break;
			pos.advance(c);
		}
		oldPos=pos.clone();
		switch(c){
		case '[':
			pos.advance(c);
			tokenType=TT.OPEN2;
			return;
		case '{':
			pos.advance(c);
			tokenType=TT.OPEN3;
			return;
		case ']':
			pos.advance(c);
			tokenType=TT.CLOS2;
			return;
		case '}':
			pos.advance(c);
			tokenType=TT.CLOS3;
			return;
		case ',':
			pos.advance(c);
			tokenType=TT.COMMA;
			return;
		case ':':
			pos.advance(c);
			tokenType=TT.COLON;
			return;
		case '"':{
			pos.advance(c);
			StringBuilder sb=new StringBuilder();
			while(true){
				ic=input.read();
				if(ic==-1){
					throw new ParserException("End of input in string literal", oldPos);
				}
				c = (char)ic;
				pos.advance(c);
				if(c=='"')
					break;
				if(c=='\n')
					throw new ParserException("Line break in string literal", pos);
				if(c=='\r')
					throw new ParserException("Carriage return in string literal", pos);
				//TODO other control characters
				if(c=='\\'){
					if(ic==-1){
						throw new ParserException("End of input in string literal", oldPos);
					}
					c = (char)ic;
					pos.advance(c);
					switch(c){
					case '\\': sb.append('\\'); break;
					case '\t': sb.append('\t'); break;
					case '\r': sb.append('\r'); break;
					case '\n': sb.append('\n'); break;
					case '\b': sb.append('\b'); break;
					case '\f': sb.append('\f'); break;
					case '/': sb.append('/'); break;
					case '"': sb.append('"'); break;
					case 'u':{
						int charValue=0;
						for(int i=0; i<4; ++i){
							ic=input.read();
							if(ic==-1){
								throw new ParserException("End of input in string literal", oldPos);
							}
							c = (char)ic;
							pos.advance(c);
							int digitValue;
							if(c>='0' && c <='9')
								digitValue=c-'0';
							else if(c>='a' && c <='f')
								digitValue=c-'a' + 0xA;
							else if(c>='A' && c <='F')
								digitValue=c-'A' + 0xA;
							else
								throw new ParserException("Expected hexadecimal digit", pos);
							charValue = charValue*16 + digitValue;
						}
						sb.append((char)charValue);
					}
					default:
						throw new ParserException("Illegal escape character", oldPos);
					}
				}else{
					sb.append(c);
				}
			}
			tokenType=TT.STRING;
			tokenValue=sb.toString();
			return;
		}


		}
		if(c>='0' && c<='9' || c=='-'){
			tokenType=TT.NUMBER;
			double num=lexNumber(c);
			if(num==(int)Math.floor(num))
				tokenValue=(int)Math.floor(num);
			else
				tokenValue=num;
			return;
		}
		
		if(c>='a' && c<='z'){
			StringBuilder sb=new StringBuilder();
			all:{
				while(c>='a' && c<='z'){
					sb.append(c);
					pos.advance(c);
					ic=input.read();
					if(ic==-1){
						break all;
					}
					c=(char)ic;			
				}
				input.unread(c);
			}
			String s = sb.toString();
			if(s.equals("true")){
				tokenType=TT.BOOL;
				tokenValue=true;
			}else if(s.equals("false")){
				tokenType=TT.BOOL;
				tokenValue=false;
			}else if(s.equals("null")){
				tokenType=TT.NULL;
				tokenValue=null;
			}else{
				throw new ParserException("Unexpected identifier '"+s+"'", pos);
			}
			return;

		}
		throw new ParserException("Unexpected character", pos);
	}


	private double lexNumber(char c) throws IOException {
		int ic;
		StringBuilder sb=new StringBuilder();
		all:{
			if(c=='-'){
				sb.append(c);
				pos.advance(c);
				ic=input.read();
				if(ic==-1){
					break all;
				}
				c=(char)ic;
			}
			if(c=='0'){
				sb.append(c);
				pos.advance(c);
				ic=input.read();
				if(ic==-1){
					break all;
				}
				c=(char)ic;				
			}else if(c>='1' && c<='9'){
				sb.append(c);
				pos.advance(c);
				ic=input.read();
				if(ic==-1){
					break all;
				}
				c=(char)ic;	
				while(c>='0' && c<='9'){
					sb.append(c);
					pos.advance(c);
					ic=input.read();
					if(ic==-1){
						break all;
					}
					c=(char)ic;						
				}
			}
			if(c=='.'){
				sb.append(c);
				pos.advance(c);
				ic=input.read();
				if(ic==-1){
					throw new ParserException("End of file in number literal", pos);
				}
				c=(char)ic;	
				while(c>='0' && c<='9'){
					sb.append(c);
					pos.advance(c);
					ic=input.read();
					if(ic==-1){
						break all;
					}
					c=(char)ic;						
				}				
			}
			if(c=='e' || c=='E'){
				sb.append(c);
				pos.advance(c);
				ic=input.read();
				if(ic==-1){
					throw new ParserException("End of file in number literal", pos);
				}
				c=(char)ic;	
				if(c=='+' || c=='-'){
					sb.append(c);
					pos.advance(c);
					ic=input.read();
					if(ic==-1){
						throw new ParserException("End of file in number literal", pos);
					}
					c=(char)ic;	
				}
				int expDigits=0;
				while(c>='0' && c<='9'){
					expDigits++;
					sb.append(c);
					pos.advance(c);
					ic=input.read();
					if(ic==-1){
						if(expDigits==0)
							throw new ParserException("End of file in number literal", pos);
						break all;
					}
					c=(char)ic;						
				}	
				if(expDigits==0)
					throw new ParserException("End of file in number literal", pos);
			}
			input.unread(c);
		}
		try{
			return Double.parseDouble(sb.toString());
		}catch(NumberFormatException x){
			throw new ParserException("Malformed number literal", pos);
		}
	}
}
