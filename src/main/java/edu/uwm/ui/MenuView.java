package edu.uwm.ui;

import java.util.ArrayList;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.ui.Notification;

import edu.uwm.data.*;
import edu.uwm.ClinicalTrialsTouchKitUI;

@SuppressWarnings("serial")
public class MenuView extends NavigationView {

    public MenuView() {
        setCaption("Data Set");
        
        HadoopData hd = HadoopData.getInstance();
        ArrayList<String> data_list = hd.getDataList();

        final VerticalComponentGroup content = new VerticalComponentGroup();
        for(String data_name : data_list) {
        	NavigationButton button = new NavigationButton(data_name);
        	button.addClickListener(new NavigationButtonClickListener() {
        		@Override
        		public void buttonClick(NavigationButtonClickEvent event) {
        			ClinicalTrialsTouchKitUI app = ClinicalTrialsTouchKitUI.getApp();
        			NavigationButton nb = (NavigationButton)event.getComponent();
        			Notification.show(nb.getCaption());
        			app.showDataSet(nb.getCaption());
        		}
        	});
        	content.addComponent(button);
        }
        /*
        NavigationButton button = new NavigationButton("Form");
        button.addClickListener(new NavigationButtonClickListener() {
            @Override
            public void buttonClick(NavigationButtonClickEvent event) {
                getNavigationManager().navigateTo(new FormView());
            }
        });
        content.addComponent(button);
        */
        setContent(content);
    };
}
