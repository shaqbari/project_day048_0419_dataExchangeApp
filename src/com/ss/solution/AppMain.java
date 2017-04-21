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
	File file;//파싱대상 xml파일
	String tableName; //오라클 작업 테이블
	
	CarHandler carHandler;
	MyModel model;
		
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
				manager.disConnect(con); //db닫기
				System.exit(0);//프로세스 종료
			}
		});
		
		setSize(900, 600);
		setVisible(true);
	}
	
	//xml파일을 여는 메소드
	public void open(){
		SAXParserFactory factory=SAXParserFactory.newInstance();
		System.out.println(SAXParserFactory.newInstance());
		System.out.println(SAXParserFactory.newInstance());//싱글톤이아니네?

		try {
			SAXParser parser=factory.newSAXParser();
			try {
				parser.parse(file, carHandler=new CarHandler());
				
				//jtable에 xml파싱결과를 출력하기!
				
				//hashmap을 vector로 바꿔야 mymodel에서 써먹을 수 있다. //컬렉션프레임웍간에 변환이 가능하다. values()메소드를 이용한다.								
				//Vector vec=new Vector(carHandler.colName.values());//여기선 values가 들어간다
				Vector vec=new Vector(carHandler.colName.keySet());//여기선 key가 들어간다. 하지만 순서가 안맞다. 순서맞으려면 iterator를 써야한다.
				model=new MyModel(vec, carHandler.data);
				table.setModel(model);
				table.updateUI();
				
				//테이블만들때  원래 설계단계에서 컬럼명이 있어야하고 보통 동적으로 변환하지않는다.
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	//open한 xml파일을 db에 넣는 메소드
	public void regist(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
				
		StringBuffer sql=new StringBuffer();
		
		//존재여부를 판단하고, 테이블 생성
		//오라클의 딕셔너니(시스템 테이블)를 이용해야한다.
		//현재 사용중인 계정 일반유저(user_), 관리자(dba_), v$_등등에 따라 접두어가 다르다.
		//desc는 table이나 db의 구조를 알 수 있다.		
		tableName=FileUtil.getOnlyName(file.getName());
		sql.append("select table_name from user_tables");
		sql.append(" where table_name=?"); //데이터는 대소문자를 가린다.
		
		
		
		try {
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setString(1, tableName.toUpperCase()); //데이터는 대소문자를 가린다. 대문자로 바꿔줘야 한다.
			rs=pstmt.executeQuery();
			
			//테이블이름은 오직 1개이므로 while문 돌릴필요가 없다.
			if(rs.next()){//레코드가 있다면...(중복된 테이블이 존재)
				System.out.println("테이블이 있네요");
				//이미 존재했던 테이블을 제거 (drop)
				
				/*sql.append("drop table"+tableName);
				System.out.println(sql.toString());이미있던sql문에 추가된다.*/
				
				sql.delete(0, sql.length());//초기화
				sql.append("drop table "+tableName);
				pstmt=con.prepareStatement(sql.toString());
				pstmt.executeUpdate();//DDL인경우 반환값이 없다.
				JOptionPane.showMessageDialog(this, "이미존재하는 테이블 삭제");
								
			}
			
			//테이블생성
			sql.delete(0, sql.length());
			//바인드변수는 컬럼에 들어가는 값만 가능하지 객체를 대상으로는 할 수 없다.
			sql.append("create table "+tableName+"(");
			sql.append(tableName+"_id number primary key");
			
			//맵이보유한 컬럼갯수만큼 반복문을 돌린다. 크기구하려면 먼저 순서가 있게 해야한다.
			Set set=carHandler.colName.keySet();//맵은 먼저 set으로 바꿔야 iterator를 이용할 수 있고 key값을 추출할 수 있다.
			Iterator it=set.iterator();
			while (it.hasNext()) {//값이 있을때 까지
				//brand, price, name값이 키로 존재
				String key=(String)it.next();//key추출
				String value=carHandler.colName.get(key);
				//sql.append(", 컬럼명 자료형 ");
				sql.append(", "+key+" "+value);
			}
			sql.append(")");
			System.out.println(sql.toString());
			//생성쿼리 수행
			pstmt=con.prepareStatement(sql.toString());
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(this, tableName+"테이블 생성");
			
			//시퀀스 생성
			sql.delete(0, sql.length());
			sql.append("select sequence_name from user_sequences");
			sql.append(" where sequence_name=?");
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setString(1, ("seq_"+tableName).toUpperCase());
			rs=pstmt.executeQuery();
			if (!rs.next()) {//레코드가 없다면
				//시퀀스 생성
				sql.delete(0, sql.length());
				sql.append("create sequence seq_"+tableName);
				sql.append(" increment by 1 start with 1");
				
				pstmt=con.prepareStatement(sql.toString());
				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(this, "시퀀스 생성");
			}
			
			//insert!!
			/*sql.delete(0, sql.length());
		sql.append("insert into "+tableName+"("+tableName+"_id");
		it=set.iterator();
		while (it.hasNext()) {
			String key=(String)it.next();			
			sql.append(", "+key);
		}
		//sql.append(") values(seq_+tableName")//시퀀스도 만들어야 한다.;
			 복잡하니 하드코딩하자*/		
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
			JOptionPane.showMessageDialog(this, "레코드등록완료");
			
			
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
	
	//db의 데이터를 json으로 export
	public void export(){
		//오라클에 들어있는 테이블 레코드를 json으로 표현해 보기
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		StringBuffer sql=new StringBuffer();
		sql.append("select * from "+tableName);
		try {
			pstmt=con.prepareStatement(sql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs=pstmt.executeQuery();
			
			rs.last();//커서를 제일 마지막 레코드로 보낸다.			
			int total=rs.getRow();//row번호를 알아낸다.
			rs.beforeFirst();//첫row이전으로 보낸다.
						
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
				sql.append("\"color\":\""+rs.getString("color")+"\"\n");//쉼표가 없어야 한다.
				if (total-2>=0) { //배열n개중에서 index n-2번까지만 쉼표가 나와야 한다.
					sql.append("},\n");
				}else{
					sql.append("}\n");
				}
				total--;
			}
			sql.append("]\n");
			sql.append("}");
			
			area.setText(sql.toString());
			
			//jsonparser의 put~메소드를 이용해서도 export해보자
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
