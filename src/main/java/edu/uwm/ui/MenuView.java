package edu.uwm.ui;

import java.util.ArrayList;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

import edu.uwm.data.*;
import edu.uwm.ClinicalTrialsTouchKitUI;

@SuppressWarnings("serial")
public class MenuView extends NavigationView {

    public MenuView() {
        buildView();
    }
    public void buildView() {
    	setCaption("Data Set");
    	HadoopData hd = HadoopData.getInstance();
        ArrayList<String> data_list = hd.getClusterDataList();

        final VerticalComponentGroup content = new VerticalComponentGroup();
        for(String data_name : data_list) {
        	NavigationButton button = new NavigationButton(data_name);
        	button.setData(data_name);
        	button.addClickListener(new NavigationButtonClickListener() {
        		@Override
        		public void buttonClick(NavigationButtonClickEvent event) {
        			FormView fv = new FormView();
        			NavigationButton nb = (NavigationButton)event.getComponent();
        			fv.setData(nb.getData());
        			fv.buildView();
        			getNavigationManager().navigateTo(fv);
        		}
        	});
        	content.addComponent(button);
        }
        Button reload = new Button("Reload Data", new Button.ClickListener() {
        	@Override
        	public void buttonClick(ClickEvent event) {
        		ClinicalTrialsTouchKitUI app = ClinicalTrialsTouchKitUI.getApp();
        		app.reloadData();
        	}
        });
        setContent(new CssLayout(content, reload));
    }
}
