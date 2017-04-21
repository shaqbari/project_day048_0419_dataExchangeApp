package com.ss.solution;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.ss.util.file.FileUtil;

public class AppMain extends JFrame implements ActionListener{
	DBManager manager;
	Connection con;
	
	JPanel p_xml, p_json;
	JButton bt_xml_open, bt_xml_regist,  bt_json_export;
	JTable table;
	JTextArea  area;
	JScrollPane scroll_xml, scroll_json;
	File file;//�Ľ̴�� xml����
	String tableName; //����Ŭ �۾� ���̺�
	
	CarHandler carHandler;
	MyModel model;
		
	public AppMain() {
		manager=DBManager.getInstance();
		con=manager.getConnection();
		
		p_xml=new JPanel();
		p_json=new JPanel();
		bt_xml_open=new JButton("XML����");
		bt_xml_regist=new JButton("Oracle�� ����");
		bt_json_export=new JButton("JSON���� Export");
		table=new JTable(3,3);
		area=new JTextArea();
		scroll_xml=new JScrollPane(table);
		scroll_json=new JScrollPane(area);
		file=new File("E:/git/java_workspace3/project_day048_0419_DataExchangeApp/data/car.xml");
		
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
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				manager.disConnect(con); //db�ݱ�
				System.exit(0);//���μ��� ����
			}
		});
		
		setSize(900, 600);
		setVisible(true);
	}
	
	//xml������ ���� �޼ҵ�
	public void open(){
		SAXParserFactory factory=SAXParserFactory.newInstance();
		System.out.println(SAXParserFactory.newInstance());
		System.out.println(SAXParserFactory.newInstance());//�̱����̾ƴϳ�?

		try {
			SAXParser parser=factory.newSAXParser();
			try {
				parser.parse(file, carHandler=new CarHandler());
				
				//jtable�� xml�Ľ̰���� ����ϱ�!
				
				//hashmap�� vector�� �ٲ�� mymodel���� ����� �� �ִ�. //�÷��������ӿ����� ��ȯ�� �����ϴ�. values()�޼ҵ带 �̿��Ѵ�.								
				//Vector vec=new Vector(carHandler.colName.values());//���⼱ values�� ����
				Vector vec=new Vector(carHandler.colName.keySet());//���⼱ key�� ����. ������ ������ �ȸ´�. ������������ iterator�� ����Ѵ�.
				model=new MyModel(vec, carHandler.data);
				table.setModel(model);
				table.updateUI();
				
				//���̺��鶧  ���� ����ܰ迡�� �÷����� �־���ϰ� ���� �������� ��ȯ�����ʴ´�.
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	//open�� xml������ db�� �ִ� �޼ҵ�
	public void regist(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
				
		StringBuffer sql=new StringBuffer();
		
		//���翩�θ� �Ǵ��ϰ�, ���̺� ����
		//����Ŭ�� ��ųʴ�(�ý��� ���̺�)�� �̿��ؾ��Ѵ�.
		//���� ������� ���� �Ϲ�����(user_), ������(dba_), v$_�� ���� ���ξ �ٸ���.
		//desc�� table�̳� db�� ������ �� �� �ִ�.		
		tableName=FileUtil.getOnlyName(file.getName());
		sql.append("select table_name from user_tables");
		sql.append(" where table_name=?"); //�����ʹ� ��ҹ��ڸ� ������.
		
		
		
		try {
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setString(1, tableName.toUpperCase()); //�����ʹ� ��ҹ��ڸ� ������. �빮�ڷ� �ٲ���� �Ѵ�.
			rs=pstmt.executeQuery();
			
			//���̺��̸��� ���� 1���̹Ƿ� while�� �����ʿ䰡 ����.
			if(rs.next()){//���ڵ尡 �ִٸ�...(�ߺ��� ���̺��� ����)
				System.out.println("���̺��� �ֳ׿�");
				//�̹� �����ߴ� ���̺��� ���� (drop)
				
				/*sql.append("drop table"+tableName);
				System.out.println(sql.toString());�̹��ִ�sql���� �߰��ȴ�.*/
				
				sql.delete(0, sql.length());//�ʱ�ȭ
				sql.append("drop table "+tableName);
				pstmt=con.prepareStatement(sql.toString());
				pstmt.executeUpdate();//DDL�ΰ�� ��ȯ���� ����.
				JOptionPane.showMessageDialog(this, "�̹������ϴ� ���̺� ����");
								
			}
			
			//���̺����
			sql.delete(0, sql.length());
			//���ε庯���� �÷��� ���� ���� �������� ��ü�� ������δ� �� �� ����.
			sql.append("create table "+tableName+"(");
			sql.append(tableName+"_id number primary key");
			
			//���̺����� �÷�������ŭ �ݺ����� ������. ũ�ⱸ�Ϸ��� ���� ������ �ְ� �ؾ��Ѵ�.
			Set set=carHandler.colName.keySet();//���� ���� set���� �ٲ�� iterator�� �̿��� �� �ְ� key���� ������ �� �ִ�.
			Iterator it=set.iterator();
			while (it.hasNext()) {//���� ������ ����
				//brand, price, name���� Ű�� ����
				String key=(String)it.next();//key����
				String value=carHandler.colName.get(key);
				//sql.append(", �÷��� �ڷ��� ");
				sql.append(", "+key+" "+value);
			}
			sql.append(")");
			System.out.println(sql.toString());
			//�������� ����
			pstmt=con.prepareStatement(sql.toString());
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(this, tableName+"���̺� ����");
			
			//������ ����
			sql.delete(0, sql.length());
			sql.append("select sequence_name from user_sequences");
			sql.append(" where sequence_name=?");
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setString(1, ("seq_"+tableName).toUpperCase());
			rs=pstmt.executeQuery();
			if (!rs.next()) {//���ڵ尡 ���ٸ�
				//������ ����
				sql.delete(0, sql.length());
				sql.append("create sequence seq_"+tableName);
				sql.append(" increment by 1 start with 1");
				
				pstmt=con.prepareStatement(sql.toString());
				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(this, "������ ����");
			}
			
			//insert!!
			/*sql.delete(0, sql.length());
		sql.append("insert into "+tableName+"("+tableName+"_id");
		it=set.iterator();
		while (it.hasNext()) {
			String key=(String)it.next();			
			sql.append(", "+key);
		}
		//sql.append(") values(seq_+tableName")//�������� ������ �Ѵ�.;
			 �����ϴ� �ϵ��ڵ�����*/		
			sql.delete(0, sql.length());
			sql.append("insert into car(car_id, brand, name, price, color)");
			sql.append(" values(seq_car.nextval, ?, ?, ?, ?)");
			
			pstmt=con.prepareStatement(sql.toString());
			
			for(int i=0; i<table.getRowCount(); i++){
				for(int j=0; j<table.getColumnCount(); j++){
					String value=(String)table.getValueAt(i, j);
					pstmt.setString(j+1, value);	
				}
				pstmt.executeUpdate();
			}
			JOptionPane.showMessageDialog(this, "���ڵ��ϿϷ�");
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
	
	//db�� �����͸� json���� export
	public void export(){
		//����Ŭ�� ����ִ� ���̺� ���ڵ带 json���� ǥ���� ����
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		StringBuffer sql=new StringBuffer();
		sql.append("select * from "+tableName);
		try {
			pstmt=con.prepareStatement(sql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs=pstmt.executeQuery();
			
			rs.last();//Ŀ���� ���� ������ ���ڵ�� ������.			
			int total=rs.getRow();//row��ȣ�� �˾Ƴ���.
			rs.beforeFirst();//ùrow�������� ������.
						
			sql.delete(0, sql.length());
			sql.append("{\n");
			sql.append("\"cars\":[\n");
			while (rs.next()) {
				sql.append("{");
				/*for (int i = 0; i < carHandler.colName.size(); i++) {
					sql.append("\"brand\":\""+rs.getString("brand")+"\",\n");
				}*/
				sql.append("\"brand\":\""+rs.getString("brand")+"\",\n");
				sql.append("\"name\":\""+rs.getString("name")+"\",\n");
				sql.append("\"price\":"+rs.getString("price")+",\n");
				sql.append("\"color\":\""+rs.getString("color")+"\"\n");//��ǥ�� ����� �Ѵ�.
				if (total-2>=0) { //�迭n���߿��� index n-2�������� ��ǥ�� ���;� �Ѵ�.
					sql.append("},\n");
				}else{
					sql.append("}\n");
				}
				total--;
			}
			sql.append("]\n");
			sql.append("}");
			
			area.setText(sql.toString());
			
			//jsonparser�� put~�޼ҵ带 �̿��ؼ��� export�غ���
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
