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

public class HadoopData implements Serializable{
	private static HadoopData instance = null;
	
	private ArrayList<String> data_list = null;
	private ArrayList<String> file_name = null;
	private HashMap<String, String> name2full = null;
	private HashMap<String, ArrayList< ArrayList<String> > > name2dataset;
	private HashMap<String, HashMap<String, String> > name2des;
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
		data_list = new ArrayList<String>();
		file_name = new ArrayList<String>();
		name2full = new HashMap<String, String>();
		name2des = new HashMap<String, HashMap<String, String> >();
		name2dataset = new HashMap<String, ArrayList< ArrayList<String> > >();
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		Configuration conf = new Configuration();
    	conf.addResource(new Path(basepath+"/config/core-site.xml"));
    	conf.addResource(new Path(basepath+"/config/hdfs-site.xml"));
    	conf.addResource(new Path(basepath+"/config/mpred-site.xml"));
    	
    	String dir = "/user/project/cluster_result";
    	Path path = new Path(dir);
    	
    	//System.out.println("===="+dir);
    	
    	try {
    		fileSystem = FileSystem.get(conf);
    		if (fileSystem.exists(path) && fileSystem.isDirectory(path)) {
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
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
		return true;
	}
	public ArrayList<String> getClusterDataList() {
		return data_list;
	}
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
    				if (sep[0].equals("#name")) de.put("name", sep[1]);
    				else if (sep[0].equals("#cnum")) de.put("cnum", sep[1]);
    				else if (sep[0].equals("#onum")) de.put("onum", sep[1]);
    				else if (sep[0].equals("#cost")) de.put("cost", sep[1]);
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
	public HashMap<String, String> getClusterDes(String key) {
		if (!name2full.containsKey(key)) return null;
		if (!name2des.containsKey(key)) readData(key);
		return name2des.get(key);
	}
	public ArrayList< ArrayList<String> > getClusterDataSet(String key) {
		//mark\tcluster_num\tlatitude\tlongitude\tname\tAddress
		
		if (!name2full.containsKey(key)) return null;
		if (!name2dataset.containsKey(key)) readData(key);
		return name2dataset.get(key);
	}
	public void reloadData() {
		instance = null;
		fileSystem = null;
		data_list = null;
		file_name = null;
		name2full = null;
		name2dataset = null;
		name2des = null;
	}
}
