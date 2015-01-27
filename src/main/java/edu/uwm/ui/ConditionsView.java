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
public class ConditionsView extends NavigationView {
	 public ConditionsView() {
	        buildView();
	 }
	 public void buildView() {
		 setCaption("Conditions");
		 HadoopData hd = HadoopData.getInstance();
		 ArrayList<String> data_list = hd.getConditionsList();

		 final VerticalComponentGroup content = new VerticalComponentGroup();
		 for(String data_name : data_list) {
			 NavigationButton button = new NavigationButton(data_name);
			 button.setData(data_name);
			 button.addClickListener(new NavigationButtonClickListener() {
				 @Override
				 public void buttonClick(NavigationButtonClickEvent event) {
					 OriginalDataView originalview = new OriginalDataView();
					 NavigationButton nb = (NavigationButton)event.getComponent();
					 originalview.setData(nb.getData());
					 originalview.buildView();
					
					 getNavigationManager().navigateTo(originalview);
				 }
			 });
			 content.addComponent(button);
		 }
		 setContent(content);
	 }

}
