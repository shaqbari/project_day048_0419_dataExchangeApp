package com.ss.solution;

import java.sql.Connection;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class MyModel extends AbstractTableModel{
	Vector<String> colName=new Vector<String>();
	Vector<Vector> data=new Vector<Vector>();
	
	public MyModel(AppMain main) {
		
		
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
