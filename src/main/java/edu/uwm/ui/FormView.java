package edu.uwm.ui;

import java.util.HashMap;

import com.vaadin.addon.touchkit.ui.DatePicker;
import com.vaadin.addon.touchkit.ui.EmailField;
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

/*
FormView to display the details of clustering result
 */
@SuppressWarnings("serial")
public class FormView extends NavigationView {

    public FormView() {
        buildView();
    }
    public void buildView() {
    	if (this.getData() == null) return;
        /*
        Get result from HadoopData by cluster name
         */
    	setCaption((String)this.getData());
    	HadoopData hd = HadoopData.getInstance();
    	HashMap<String, String> des = hd.getClusterDes((String)this.getData());
    	
        final VerticalComponentGroup content = new VerticalComponentGroup();

        /*
        Add details component to UI
         */
        TextField field = new TextField("Name");
        field.setEnabled(false);
        field.setInputPrompt(des.get("name"));
        content.addComponent(field);
        
        field = new TextField("Center Number");
        field.setEnabled(false);
        field.setInputPrompt(des.get("cnum"));
        content.addComponent(field);
        
        field = new TextField("Original Number");
        field.setEnabled(false);
        field.setInputPrompt(des.get("onum"));
        content.addComponent(field);
        
        field = new TextField("Iteration Number");
        field.setEnabled(false);
        field.setInputPrompt(des.get("inum"));
        content.addComponent(field);
        
        field = new TextField("Cost");
        field.setEnabled(false);
        field.setInputPrompt(des.get("cost"));
        content.addComponent(field);

        //Add Map show button, and ClickListener
        final Button submitButton = new Button("Show in Map");
        submitButton.setData(this.getData());
        submitButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ClinicalTrialsTouchKitUI app = ClinicalTrialsTouchKitUI.getApp();
            	Button btn = (Button)event.getComponent();
    			app.showDataSet((String)btn.getData());
            }
        });

        setContent(new CssLayout(content, submitButton));
    }

}
