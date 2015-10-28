package edu.uwm.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.vaadin.server.VaadinService;

public class ShellExec {
	private String cluster_shell;
	private String original_shell;
	private ArrayList<String> paras;
	//initialize
	public ShellExec() {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		//Change the shell script permission to 777
		System.out.println(basepath);
        //the path of cluster and original
		cluster_shell = basepath + "/config/script/spark_cluster.sh";
		original_shell = basepath + "/config/script/spark_original.sh";
	}
	
	public Boolean setParas(ArrayList<String> p) {
		paras = p;
		return true;
	}
	
	public Boolean execCluster() {
		return execCommand(cluster_shell);
	}
	
	public Boolean execOriginal() {
		return execCommand(original_shell);
	}

    //execute command
	private  Boolean execCommand(String shell) {
		try {
			ArrayList<String> execShell = new ArrayList<String>();
			execShell.add("sh");
			execShell.add(shell);
			execShell.addAll(paras);
			String[] command = new String[execShell.size()];
			command = execShell.toArray(command);
			Process p = Runtime.getRuntime().exec(command);
			/*
			String args = "";
			for (String str : paras) {
				args += " " + str.replace(" ", "\" \"");
			}
			String[] arr = new String[paras.size()+2];
			System.out.println("sh " + shell + args);
			Process p = Runtime.getRuntime().exec("sh " + shell + args);
			
			p.waitFor();
			BufferedReader reader = 
			         new BufferedReader(new InputStreamReader(p.getInputStream()));
			 
			String line = "";			
			while ((line = reader.readLine())!= null) {
				System.out.println(line);
			}
			*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
