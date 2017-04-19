package com.ss.xml_json;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.simple.parser.JSONParser;
import org.xml.sax.SAXException;

public class AppMain extends JFrame implements ActionListener {
	DBManager manager;
	Connection con;

	JPanel p_xml, p_json;
	JButton bt_xml_open, bt_xml_regist, bt_json_export;
	JTable table;
	JTextArea area;
	JScrollPane scroll_xml, scroll_json;

	JFileChooser chooser;
	File file;
	String fileId;
	
	SAXParserFactory factory;
	SAXParser parser;
	Handler handler;
	MyModel model;
	
	String[] member={"name", "age", "phone", "gender"};
	String[] car={"brand", "name", "price", "color"};
	String[] pet={"type", "name", "age", "gender"};

	public AppMain() {
		manager = DBManager.getInstance();
		con = manager.getConnection();

		p_xml = new JPanel();
		p_json = new JPanel();
		bt_xml_open = new JButton("XML����");
		bt_xml_regist = new JButton("Oracle�� ����");
		bt_json_export = new JButton("JSON���� Export");
		table = new JTable();
		area = new JTextArea();
		scroll_xml = new JScrollPane(table);
		scroll_json = new JScrollPane(area);

		chooser = new JFileChooser("E:/git/java_workspace3/project_day048_0419_DataExchangeApp/data");

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

	// xml������ ���� ���̺�� �����ִ� �޼ҵ�
	public void openXML() {
		int result = chooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			
			String fileName=file.getName();
			String[] fileNameArr=fileName.split("\\.");
			fileId=fileNameArr[0];
			String fileExt=fileNameArr[1];
			
			int ext=fileName.indexOf(".", fileNameArr.length-1);
			String ext2=fileName.substring(ext);
			System.out.println(ext2);
			
			if (fileExt.equalsIgnoreCase("xml")==false) {
				JOptionPane.showMessageDialog(this, "xml���ϸ� �������ּ���");
				return;
			}			

			factory = SAXParserFactory.newInstance();
			try {
				parser = factory.newSAXParser();
				parser.parse(file, handler = new Handler(model = new MyModel(fileId)));
				
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		table.setModel(model);
		table.updateUI();
	}
	
	
	public boolean selectTableName(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String dropTableSql = "select table_name from user_tables";
		
		try {
			pstmt=con.prepareStatement(dropTableSql);
			rs=pstmt.executeQuery();
			
			Vector<String> vec=new Vector<String>();
			while(rs.next()){
				vec.add(rs.getString("table_name"));
			}
			
			for (int i = 0; i < vec.size(); i++) {
				if (fileId.equalsIgnoreCase(vec.get(i))) {
					return true;					
				}
			}			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	finally {
			if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public void dropTable(){
		PreparedStatement pstmt=null;
		String dropTableSql = "drop table "+fileId;
		
		try {
			pstmt=con.prepareStatement(dropTableSql);
			int result=pstmt.executeUpdate();
			if (result==1) {
				System.out.println("drop����");				
			}else{
				System.out.println("drop����");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	finally {
			if (pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	public void createTable(){
		PreparedStatement pstmt=null;

		StringBuffer createTableSql = new StringBuffer();
		createTableSql.append("create table "+fileId+"(");
		createTableSql.append(" name varchar(20), ");
		createTableSql.append(" age varchar(20), ");
		createTableSql.append(" phone varchar(20), ");
		createTableSql.append(" gender varchar(20)");
		createTableSql.append(" )");
				
		try {
			pstmt=con.prepareStatement(createTableSql.toString());
			int result=pstmt.executeUpdate();
			if (result==1) {
				System.out.println("create ����");
			}else{
				System.out.println("create ����");				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	finally {
			if (pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	// open�� xml������ db�� �ִ� �޼ҵ�, ���̺��� ������ �����, ������ ����� �ٽû���
	public void registXML() {
		if (selectTableName()) {//��ġ�ϴ� ���̺��� �ִٸ� drop�ϰ� �ٽû���, ���ٸ� �׳� ����
			dropTable();			
			createTable();
		}else{
			createTable();
		}
		
		PreparedStatement pstmt = null;
		String insertSql = "insert into "+fileId+" values(?, ?, ?, ?)";

		for (int i = 0; i < table.getRowCount(); i++) {
			try {
				pstmt = con.prepareStatement(insertSql);
				
				//���ε� ������ ��´�.
				for (int j = 0; j < table.getColumnCount(); j++) {
					pstmt.setString(j+1, (String) table.getValueAt(i, j)); //?�� ��°� 1���� ����				
				}

				int result = pstmt.executeUpdate();
				if (result == 1) {
					System.out.println("insert ����");
				} else {
					System.out.println("insert ����");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	// db�� �����͸� json���� export
	public void exportJson() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from member";
		String json="";
		
		Vector<Vector> data=new Vector<>();
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			ResultSetMetaData meta=rs.getMetaData();
			Vector<String> colName=new Vector<String>();			
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				colName.add(meta.getColumnName(i));
			}
			
			while(rs.next()){
				Vector<String> vec=new Vector<String>();
				vec.add(rs.getString("name"));
				vec.add(rs.getString("age"));
				vec.add(rs.getString("phone"));
				vec.add(rs.getString("gender"));
				
				data.add(vec);
			}
			
			
			area.setText("");
			area.append("{\n");
			area.append("\"member\":[\n");
			for (int i = 0; i < data.size(); i++) {
				area.append("{\n");
				for (int j = 0; j < data.get(i).size(); j++) {					
					if (j==data.get(i).size()-1) {//�������϶��� ,�� ������ �ʴ´�.
						area.append("\""+colName.get(j)+"\":\""+data.get(i).get(j).toString()+"\"\n");
					}else{
						area.append("\""+colName.get(j)+"\":\""+data.get(i).get(j).toString()+"\",\n");						
					}
				}
				if (i==data.size()-1) {
					area.append("}\n");
				}else {
					area.append("},\n");					
				}
			}
			
			area.append("]\n");
			area.append("}\n");
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}			
		}
		
		
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = (Object) e.getSource();
		if (obj == bt_xml_open) {
			openXML();
		} else if (obj == bt_xml_regist) {
			registXML();			
		} else if (obj == bt_json_export) {
			exportJson();
		}
	}

	public static void main(String[] args) {
		new AppMain();
	}

}
