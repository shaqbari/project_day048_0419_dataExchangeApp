/*tag나 각종 데이터 발견시 이벤트 발생시키는 객체*/

package com.ss.solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CarHandler extends DefaultHandler{
	/*이벤트가 발생할 때, 적절할 처리를 해서 최종적으로 xml의 해석된 결과를 
	 * 이차원형태를 데이터에 담아두자.(컬렉션 프레임웍 강추)*/
	Vector<Vector> data;
	Vector<String> vec; //VO, DTO역할
	Map<String, String> colName; //자료형도 담을수 있게 맵을 써보자
	
	//현재 이벤트를 발생시키는 실행부의 위치를 알기위한 체크변수
	boolean cars;
	boolean car;
	boolean brand;
	boolean name;
	boolean price;
	boolean color;
	
	
	public void startDocument() throws SAXException {
		data=new Vector<Vector>();//생성자에서 해도되지만 이번엔 여기서 해보자
		
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//인스턴스한개를 담을 벡터 준비하자, DTO대용		
		if (qName.equalsIgnoreCase("cars")) {
			cars=true;
		}else if(qName.equalsIgnoreCase("car")){
			vec=new Vector<String>();
			colName=new HashMap<String, String>(); //만날때마다 생성되므로 원래 한번만들어가도록 if문을 줘야 한다.
			car=true;
		}else if(qName.equalsIgnoreCase("brand")){
			colName.put(qName, "varchar2(20)");
			brand=true;
		}else if(qName.equalsIgnoreCase("name")){
			colName.put(qName, "varchar2(30)");
			name=true;
		}else if(qName.equalsIgnoreCase("price")){
			colName.put(qName, "number");
			price=true;
		}else if(qName.equalsIgnoreCase("color")){
			colName.put(qName, "varchar(20)");
			color=true;
		}		
		
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(brand){
			vec.add(new String(ch, start, length));
			brand=false;
		}else if(name){
			vec.add(new String(ch, start, length));
			name=false;
		}else if(price){
			vec.add(new String(ch, start, length));
			price=false;
		}else if(color){
			vec.add(new String(ch, start, length));
			color=false;
		}
		
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		//각 car 마다 이차원 벡터에 담자!!
		if (qName.equalsIgnoreCase("car")) {
			data.add(vec);
		}
	}
	
	public void endDocument() throws SAXException {
		System.out.println("총담겨진 차는"+data.size());
	}
	
}
