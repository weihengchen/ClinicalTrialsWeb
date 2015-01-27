
package edu.uwm.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.vaadin.addon.touchkit.ui.DatePicker;
import com.vaadin.addon.touchkit.ui.EmailField;
import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import edu.uwm.ClinicalTrialsTouchKitUI;
import edu.uwm.data.HadoopData;
import edu.uwm.data.ShellExec;

@SuppressWarnings("serial")
public class OriginalDataView extends NavigationView {

    public OriginalDataView() {
        buildView();
    }
    public void buildView() {
    	if (this.getData() == null) return;
    	setCaption((String)this.getData());
    	HadoopData hd = HadoopData.getInstance();
    	HashMap<String, String> des = hd.getOriginalDes((String)this.getData());
    	
        final VerticalComponentGroup content = new VerticalComponentGroup();
        
        final Button submitButton = new Button("Cluster");
        submitButton.setData(this.getData());
        submitButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
    			ShellExec shell = new ShellExec();		
    			Button nb = (Button)event.getComponent();
    			ArrayList<String> tmp = new ArrayList<String>();
    			tmp.add((String)nb.getData());
    			shell.setParas(tmp);
    			shell.execCluster();
            }
        });
        
        if (des != null) {
        	TextField field = new TextField("Name");
        	field.setEnabled(false);
        	field.setInputPrompt(des.get("name"));
        	content.addComponent(field);

        	field = new TextField("Description");
        	field.setEnabled(false);
        	field.setInputPrompt(des.get("des"));

        	content.addComponent(field);
        	field = new TextField("Number");
        	field.setEnabled(false);
        	field.setInputPrompt(des.get("num"));
        	content.addComponent(field);

        	final Button showButton = new Button("Show in Map");
        	showButton.setData(this.getData());
        	showButton.addClickListener(new ClickListener() {
        		@Override
        		public void buttonClick(ClickEvent event) {
        			ClinicalTrialsTouchKitUI app = ClinicalTrialsTouchKitUI.getApp();
        			Button btn = (Button)event.getComponent();
        			app.showOriginalDataSet((String)btn.getData());    			
        		}
        	});
        	
        	setContent(new CssLayout(content, showButton, submitButton));
        } else {
        	
        	ShellExec shell = new ShellExec();
    		ArrayList<String> tmp = new ArrayList<String>();
			tmp.add((String)this.getData());
			shell.setParas(tmp);
			shell.execOriginal();
			
        	setContent(new CssLayout(content, submitButton));
        }
        
    }

}
