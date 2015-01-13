package edu.uwm.data;

import java.util.ArrayList;

import com.vaadin.ui.Notification;

public class HadoopData {
	private static HadoopData instance = null;
	
	private ArrayList<String> data_list = null;
	private HadoopData() {
		init();
	}
	public static HadoopData getInstance() {
		if (instance == null) {
			instance = new HadoopData();
		}
		return instance;
	}
	private void init() {
		data_list = new ArrayList<String>();
		
		//For Test
		data_list.add("HIV");
		data_list.add("Breast");
	}
	public ArrayList<String> getDataList() {
		return data_list;
	}
}
