/*tag�� ���� ������ �߽߰� �̺�Ʈ �߻���Ű�� ��ü*/

package com.ss.solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CarHandler extends DefaultHandler{
	/*�̺�Ʈ�� �߻��� ��, ������ ó���� �ؼ� ���������� xml�� �ؼ��� ����� 
	 * ���������¸� �����Ϳ� ��Ƶ���.(�÷��� �����ӿ� ����)*/
	Vector<Vector> data;
	Vector<String> vec; //VO, DTO����
	Map<String, String> colName; //�ڷ����� ������ �ְ� ���� �Ẹ��
	
	//���� �̺�Ʈ�� �߻���Ű�� ������� ��ġ�� �˱����� üũ����
	boolean cars;
	boolean car;
	boolean brand;
	boolean name;
	boolean price;
	boolean color;
	
	
	public void startDocument() throws SAXException {
		data=new Vector<Vector>();//�����ڿ��� �ص������� �̹��� ���⼭ �غ���
		
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//�ν��Ͻ��Ѱ��� ���� ���� �غ�����, DTO���		
		if (qName.equalsIgnoreCase("cars")) {
			cars=true;
		}else if(qName.equalsIgnoreCase("car")){
			vec=new Vector<String>();
			colName=new HashMap<String, String>(); //���������� �����ǹǷ� ���� �ѹ��������� if���� ��� �Ѵ�.
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
		//�� car ���� ������ ���Ϳ� ����!!
		if (qName.equalsIgnoreCase("car")) {
			data.add(vec);
		}
	}
	
	public void endDocument() throws SAXException {
		System.out.println("�Ѵ���� ����"+data.size());
	}
	
}
