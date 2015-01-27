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
	public ShellExec() {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		//Change the shell script permission to 777
		System.out.println(basepath);
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
	
	private  Boolean execCommand(String shell) {
		try {
			String args = "";
			for (String str : paras) {
				args += " " + str;
			}
			System.out.println("sh " + shell + args);
			Process p = Runtime.getRuntime().exec("sh " + shell + args);
			/*
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
