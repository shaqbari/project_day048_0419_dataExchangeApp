/*tag�� ���� ������ �߽߰� �̺�Ʈ �߻���Ű�� ��ü*/

package com.ss.xml_json;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Handler extends DefaultHandler{
	MyModel model;
	
	boolean member;
	boolean car;
	boolean pet;
	
	boolean name;
	boolean age;
	boolean phone;
	boolean gender;
	
	boolean brand;
	boolean price;
	boolean color;
	
	boolean type;
	
	Vector<String> vec;
	
	public Handler(MyModel model) {
		this.model=model;
		
	}

	//<�����±�>�� �߰ߵǸ� ȣ��Ǵ� �޼ҵ�
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		/*�����±��� <member>�� �߰ߵǸ� Vector�� ��������*/
		if (qName.equalsIgnoreCase("member")) {//��ҹ��ڱ����� ���Ѵ�.
			vec=new Vector<String>();
			member=true;
		}
		if (qName.equalsIgnoreCase("car")) {//��ҹ��ڱ����� ���Ѵ�.
			vec=new Vector<String>();
			car=true;
		}
		if (qName.equalsIgnoreCase("pet")) {//��ҹ��ڱ����� ���Ѵ�.
			vec=new Vector<String>();
			pet=true;
		}
		
		if (qName.equalsIgnoreCase("name")) {
			name=true;
		}
		if (qName.equalsIgnoreCase("age")) {
			age=true;
		}
		if (qName.equalsIgnoreCase("phone")) {
			phone=true;
		}
		if (qName.equalsIgnoreCase("gender")) {
			gender=true;
		}
		
		if (qName.equalsIgnoreCase("brand")) {
			brand=true;
		}
		if (qName.equalsIgnoreCase("color")) {
			color=true;
		}
		if (qName.equalsIgnoreCase("price")) {
			price=true;
		}
		
		if (qName.equalsIgnoreCase("type")) {
			type=true;
		}		
		
	}
	
	//�ؽ�Ʈ�� �߰ߵǸ� ȣ��Ǵ� �޼ҵ�	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (name) {
			vec.add(new String(ch, start, length));	
			name=false;
		}else if (age) {
			vec.add(new String(ch, start, length));
			age=false;
		}else if(phone){
			vec.add(new String(ch, start, length));
			phone=false;
		}else if (gender) {
			vec.add(new String(ch, start, length));			
			gender=false;
		}else if (brand) {
			vec.add(new String(ch, start, length));			
			brand=false;
		}else if (color) {
			vec.add(new String(ch, start, length));			
			color=false;
		}else if (price) {
			vec.add(new String(ch, start, length));			
			price=false;
		}else if(type){
			vec.add(new String(ch, start, length));			
			type=false;
		}
		
	}
	
	//�ݴ� �±װ� �߰ߵǸ� ȣ��Ǵ� �޼ҵ�
	public void endElement(String uri, String localName, String qName) throws SAXException {		
		if (qName.equalsIgnoreCase("member")) {
			model.data.addElement(vec);
		}
		if (qName.equalsIgnoreCase("car")) {//��ҹ��ڱ����� ���Ѵ�.
			model.data.addElement(vec);
		}
		if (qName.equalsIgnoreCase("pet")) {//��ҹ��ڱ����� ���Ѵ�.
			model.data.addElement(vec);
		}
		
	}
	
	public void endDocument() throws SAXException {
		System.out.println("����� �Ѹ����"+model.data.size());
	}
	
}
