package edu.uwm.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;

/*
Singleton implement of HadoopData
 */

public class HadoopData implements Serializable{
	//instance
	private static HadoopData instance = null;

	//dataset name collection
	private ArrayList<String> data_list = null;
	//dataset filename collection
	private ArrayList<String> file_name = null;
	//map from name to full path of dataset
	private HashMap<String, String> name2full = null;
	//map from name to dataset content list
	private HashMap<String, ArrayList< ArrayList<String> > > name2dataset;
	//map from name to description of dataset
	private HashMap<String, HashMap<String, String> > name2des;

	//conditions list
	private ArrayList<String> conditions_list = null;
	//map from original condition name to full path
	private HashMap<String, String> original_name2full = null;
	//map from original name to dataset content list
	private HashMap<String, ArrayList< ArrayList<String> > > original_name2dataset;
	//map from original name to description of dataset
	private HashMap<String, HashMap<String, String> > original_name2des;
	//Hadoop file system
	FileSystem fileSystem = null;
	private HadoopData() {
		if (false == init()) {
			instance = null;
			return;
		}
	}
	public static HadoopData getInstance() {
		if (instance == null) {
			instance = new HadoopData();
		}
		return instance;
	}
	private Boolean init() {
		//initialize variables
		conditions_list = new ArrayList<String>();
		data_list = new ArrayList<String>();
		file_name = new ArrayList<String>();
		name2full = new HashMap<String, String>();
		name2des = new HashMap<String, HashMap<String, String> >();
		name2dataset = new HashMap<String, ArrayList< ArrayList<String> > >();
		original_name2des = new HashMap<String, HashMap<String, String> >();
		original_name2dataset = new HashMap<String, ArrayList< ArrayList<String> > >();

		//initialize the configuration files in Hadoop, set up for HDFS
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		Configuration conf = new Configuration();
    	conf.addResource(new Path(basepath+"/config/hadoop/core-site.xml"));
    	conf.addResource(new Path(basepath+"/config/hadoop/hdfs-site.xml"));
    	conf.addResource(new Path(basepath+"/config/hadoop/mpred-site.xml"));
    	
    	String dir = "/user/project/cluster_result";
    	Path path = new Path(dir);
    	
    	//System.out.println("===="+dir);
    	//get data from HDFS
    	try {
    		fileSystem = FileSystem.get(conf);
    		if (fileSystem.exists(path) && fileSystem.isDirectory(path)) {
				//get file path for cluster results
    			FileStatus []fstatus = fileSystem.listStatus(path);
    			for (FileStatus f : fstatus) {
    				String str = f.getPath().toString();
    				//System.out.println("===="+str);
    				int st = str.lastIndexOf('/') + 1;
    				int en = str.indexOf("_cluster.txt", st);
    				if (en == -1) continue;
    				String cond = str.substring(st,en);
    				name2full.put(cond, str);
    				file_name.add(str);
    				data_list.add(cond);
    			}
    		}

			//read conditions from file
    		Path cond = new Path("/user/project/result/conditions.txt");
    		if (fileSystem.exists(cond) && fileSystem.isFile(cond)) {
    			FSDataInputStream fin = fileSystem.open(cond);
        		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        		String line;
        		
        		while ((line = reader.readLine()) != null) {
        			conditions_list.add(line);
        		}
        		fin.close();
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    	
    	
    	
		return true;
	}
    //get cluster results
	public ArrayList<String> getClusterDataList() {
		return data_list;
	}
    //Read data from HDFS files
	private void readData(String key) {
		ArrayList< ArrayList<String> > re = new ArrayList< ArrayList<String> >();
		HashMap<String, String> de = new HashMap<String, String>();
		Path file = new Path(name2full.get(key));
		try {
    		FSDataInputStream fin = fileSystem.open(file);
    		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
    		String line;
    		
    		while ((line = reader.readLine()) != null) {
    			//System.err.println((new Exception().getStackTrace()[0].getFileName()) + (new Exception().getStackTrace()[0].getLineNumber()) + line);
    			String []sep = line.split("\t");
    			if (sep[0].charAt(0) == '#') {
    				de.put(sep[0].substring(1), sep[1]);
    			} else {
    				ArrayList<String> tmp = new ArrayList<String>();
    				for (String str : sep) {
    					tmp.add(str);
    				}
    				re.add(tmp);
    			}
    		}
    		reader.close();
    		fin.close();
		} catch (IOException e) {
    		e.printStackTrace();
    	}
		name2dataset.put(key, re);
		name2des.put(key, de);
	}

    //read original dataset from HDFS
	private Boolean readOriginalData(String key) {
		ArrayList< ArrayList<String> > re = new ArrayList< ArrayList<String> >();
		HashMap<String, String> de = new HashMap<String, String>();

        //original condition file
		String dir = "/user/project/original_data/" + key.replace(' ', '_') + "_original.txt";
    	Path path = new Path(dir);
    	System.out.println(dir);
    	
    	try {
    		if (fileSystem.exists(path) && fileSystem.isFile(path)) {
    			System.out.println("Original File exist!");
    			FSDataInputStream fin = fileSystem.open(path);
        		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        		String line;
        		
        		while ((line = reader.readLine()) != null) {
        			//System.err.println((new Exception().getStackTrace()[0].getFileName()) + (new Exception().getStackTrace()[0].getLineNumber()) + line);
        			String []sep = line.split("\t");
        			if (sep[0].charAt(0) == '#') {
                        //description of dataset
        				de.put(sep[0].substring(1), sep[1]);
        			} else {
                        //content of dataset
        				ArrayList<String> tmp = new ArrayList<String>();
        				for (String str : sep) {
        					tmp.add(str);
        				}
        				re.add(tmp);
        			}
        		}
        		reader.close();
        		fin.close();
    			
    		} else {
    			return false;
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}

        //add to HashMap, don't need to run again
		original_name2dataset.put(key, re);
		original_name2des.put(key, de);
		return true;
	}

    //get conditions list
	public ArrayList<String> getConditionsList() {
		if (conditions_list.size() > 100) {
			ArrayList<String> tmp = new ArrayList<String>();
			for (String str : conditions_list.subList(0,100)) {
				tmp.add(str);
			}
			return tmp;
		}
		return conditions_list;
	}
    //get cluster description
	public HashMap<String, String> getClusterDes(String key) {
        //return nul, if not exist
		if (!name2full.containsKey(key)) return null;
        //read the data, if not read
		if (!name2des.containsKey(key)) readData(key);
        //return the stored data
		return name2des.get(key);
	}
	// get cluster dataset
	public ArrayList< ArrayList<String> > getClusterDataSet(String key) {
		//mark\tcluster_num\tlatitude\tlongitude\tname\tAddress
        //return nul, if not exist
		if (!name2full.containsKey(key)) return null;
		if (!name2dataset.containsKey(key)) readData(key);
        //return the stored data
		return name2dataset.get(key);
	}
	
	public HashMap<String, String> getOriginalDes(String key) {
        //read the data, if not read
        if (!original_name2des.containsKey(key)) {
			Boolean ret = readOriginalData(key);
			if (!ret) {
				return null;
			}
		}
        //return the stored data
		return original_name2des.get(key);
	}
	public ArrayList< ArrayList<String> > getOriginalDataSet(String key) {
		//mark\tcluster_num\tlatitude\tlongitude\tname\tAddress
        //read the data, if not read
		if (!original_name2dataset.containsKey(key)) {
			Boolean ret = readOriginalData(key);
			if (!ret) {
				return null;
			}
		}
        //return the stored data
		return original_name2dataset.get(key);
	}
	public void reloadData() {
        //clear the variables, and reload data
		instance = null;
		fileSystem = null;
		conditions_list = null;
		data_list = null;
		file_name = null;
		name2full = null;
		name2dataset = null;
		name2des = null;
	}
}
