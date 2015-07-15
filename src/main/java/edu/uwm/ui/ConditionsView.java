package edu.uwm.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

import com.vaadin.ui.TextField;
import edu.uwm.data.*;
import edu.uwm.ClinicalTrialsTouchKitUI;

@SuppressWarnings("serial")
public class ConditionsView extends NavigationView {
	public ConditionsView() {
	        buildView();
	 }
	public void buildView() {
		 setCaption("Conditions");
		 //HadoopData hd = HadoopData.getInstance();
		 //ArrayList<String> data_list = hd.getConditionsList();
		 MongodbData md = MongodbData.getInstance();
		 ArrayList<String> data_list = md.getConditionsList();

		 btns = new ArrayList<NavigationButton>();
		 content = new VerticalComponentGroup();

		 filter = new TextField();

        filter.setInputPrompt("Search...");

        filter.addTextChangeListener(new TextChangeListener() {
			 @Override
			 public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {
				 String text = textChangeEvent.getText();
				 refreshConditions(text);
			 }
		 });

		 content.addComponent(filter);

		 for(String data_name : data_list) {
			 NavigationButton button = new NavigationButton(data_name);
			 button.setData(data_name);
             button.addClickListener(new NavigationButtonClickListener() {
                 @Override
                 public void buttonClick(NavigationButtonClickEvent event) {
                     OriginalDataView originalview = new OriginalDataView();
                     NavigationButton nb = (NavigationButton) event.getComponent();
                     originalview.setData(nb.getData());
                     originalview.buildView();

                     getNavigationManager().navigateTo(originalview);
                 }
             });
             btns.add(button);
			 content.addComponent(button);
		 }
		 setContent(content);
	}

	private VerticalComponentGroup content;
	private TextField filter;
	private ArrayList<NavigationButton> btns;
	private void refreshConditions(String txt) {
		txt = txt.toLowerCase();
		content.removeAllComponents();
		content.addComponent(filter);
		Iterator<NavigationButton> comp = btns.iterator();
		while (comp.hasNext()) {
			NavigationButton tmp = (NavigationButton)comp.next();
			String str = ((String)tmp.getData()).toLowerCase();
			if (str.indexOf(txt) != -1) {
				content.addComponent(tmp);
			}
		}
		filter.setCursorPosition(txt.length());
		setContent(content);
	}

}
