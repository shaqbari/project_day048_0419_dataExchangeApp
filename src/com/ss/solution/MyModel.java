package com.ss.solution;

import java.sql.Connection;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class MyModel extends AbstractTableModel{
	Vector<String> colName;
	Vector<Vector> data;
	
	//xml파싱한 결과를 아래의 인수로 넘겨받자
	public MyModel(Vector colName, Vector<Vector> data) {
		this.colName=colName;
		this.data=data;
		
	}
	
	public String getColumnName(int column) {
		return colName.get(column);
	}
	
	public int getColumnCount() {
		return colName.size();
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex).get(columnIndex);
	}

}
