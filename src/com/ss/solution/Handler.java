/*tag�� ���� ������ �߽߰� �̺�Ʈ �߻���Ű�� ��ü*/

package com.ss.solution;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Handler extends DefaultHandler{
	MyModel model;
	
	boolean member;
	boolean name;
	boolean age;
	boolean gender;
	
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
		
		if (qName.equalsIgnoreCase("name")) {
			name=true;
		}
		if (qName.equalsIgnoreCase("age")) {
			age=true;
		}
		if (qName.equalsIgnoreCase("gender")) {
			gender=true;
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
		}else if (gender) {
			vec.add(new String(ch, start, length));			
			gender=false;
		}
		
	}
	
	//�ݴ� �±װ� �߰ߵǸ� ȣ��Ǵ� �޼ҵ�
	public void endElement(String uri, String localName, String qName) throws SAXException {		
		if (qName.equalsIgnoreCase("member")) {
			model.data.addElement(vec);
		}
		
	}
	
	public void endDocument() throws SAXException {
		System.out.println("����� �Ѹ����"+model.data.size());
	}
	
}
