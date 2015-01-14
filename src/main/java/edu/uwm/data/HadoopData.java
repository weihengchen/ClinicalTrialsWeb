package edu.uwm.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;

public class HadoopData {
	private static HadoopData instance = null;
	
	private ArrayList<String> data_list = null;
	private ArrayList<String> file_name = null;
	private HashMap<String, String> name2full = null;
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
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		Configuration conf = new Configuration();
    	conf.addResource(new Path(basepath+"/config/core-site.xml"));
    	conf.addResource(new Path(basepath+"/config/hdfs-site.xml"));
    	conf.addResource(new Path(basepath+"/config/mpred-site.xml"));
    	
    	String dir = "/user/projectcluster_result";
    	Path path = new Path(dir);
    	
    	try {
    		fileSystem = FileSystem.get(conf);
    		if (fileSystem.exists(path) && fileSystem.isDirectory(path)) {
    			FileStatus []fstatus = fileSystem.listStatus(path);
    			for (FileStatus f : fstatus) {
    				String str = f.getPath().toString();
    				int st = str.lastIndexOf('/');
    				int en = str.indexOf("_cluster.txt", st);
    				if (en == -1) continue;
    				String cond = str.substring(st,en);
    				name2full.put(cond, str);
    				file_name.add(str);
    				data_list.add(cond);
    			}
    		}
    	} catch (IOException e) {
    		return false;
    	}
		return true;
	}
	public ArrayList<String> getClusterDataList() {
		return data_list;
	}
	public ArrayList< ArrayList<String> > getClusterDataSet(String key) {
		//mark\tcluster_num\tlatitude\tlongitude\tname\tAddress
		return new ArrayList< ArrayList<String>>();
	}
}
