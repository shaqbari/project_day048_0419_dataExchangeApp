package com.ss.xml_json;

import java.sql.Connection;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class MyModel extends AbstractTableModel{
	Vector<String> colName=new Vector<String>();
	Vector<Vector> data=new Vector<Vector>();
	
	public MyModel(String fileId) {
		
		colName.removeAll(colName);
		data.removeAll(data);
		
		
		if (fileId.equalsIgnoreCase("member")) {
			colName.addElement("name");
			colName.addElement("age");
			colName.addElement("phone");
			colName.addElement("gender");			
		}else if (fileId.equalsIgnoreCase("pet")) {
			colName.addElement("type");
			colName.addElement("name");
			colName.addElement("age");
			colName.addElement("gender");	
		}else if (fileId.equalsIgnoreCase("car")) {
			colName.addElement("brand");
			colName.addElement("name");
			colName.addElement("price");
			colName.addElement("color");	
		}
	}
	
	
	public String getColumnName(int column) {
		return colName.get(column);
	}
	
	public int getColumnCount() {
		return data.get(0).size();
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex).get(columnIndex);
	}

}
