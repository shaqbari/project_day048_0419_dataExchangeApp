package com.ss.solution;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class AppMain extends JFrame implements ActionListener{
	DBManager manager;
	Connection con;
	
	JPanel p_xml, p_json;
	JButton bt_xml_open, bt_xml_regist,  bt_json_export;
	JTable table;
	JTextArea  area;
	JScrollPane scroll_xml, scroll_json;
	JFileChooser chooser;
	File file;
	
	
	public AppMain() {
		manager=DBManager.getInstance();
		con=manager.getConnection();
		
		p_xml=new JPanel();
		p_json=new JPanel();
		bt_xml_open=new JButton("XML열기");
		bt_xml_regist=new JButton("Oracle에 저장");
		bt_json_export=new JButton("JSON으로 Export");
		table=new JTable(3,3);
		area=new JTextArea();
		scroll_xml=new JScrollPane(table);
		scroll_json=new JScrollPane(area);
		
		setLayout(new GridLayout(1, 2));
		
		scroll_xml.setPreferredSize(new Dimension(400, 500));
		scroll_json.setPreferredSize(new Dimension(400, 500));
		
		p_xml.add(bt_xml_open);
		p_xml.add(bt_xml_regist);
		p_xml.add(scroll_xml);
		
		p_json.add(bt_json_export);
		p_json.add(scroll_json);
		
		add(p_xml);
		add(p_json);		
		
		bt_xml_open.addActionListener(this);
		bt_xml_regist.addActionListener(this);
		bt_json_export.addActionListener(this);
		
		setSize(900, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//xml파일을 여는 메소드
	public void open(){
		
	}
	
	//open한 xml파일을 db에 넣는 메소드
	public void regist(){
		
	}
	
	//db의 데이터를 json으로 export
	public void export(){
		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj=(Object)e.getSource();
		if (obj==bt_xml_open) {
			open();
		}else if (obj==bt_xml_regist) {
			regist();
		}else if (obj==bt_json_export) {
			export();
		}
	}
	
	public static void main(String[] args) {
		new AppMain();
	}

}
