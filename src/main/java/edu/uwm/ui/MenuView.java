package edu.uwm.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

import edu.uwm.data.*;
import edu.uwm.ClinicalTrialsTouchKitUI;

@SuppressWarnings("serial")
public class MenuView extends NavigationView {
    private final VerticalComponentGroup content = new VerticalComponentGroup();
    private HashMap<String, NavigationButton> buttons = new HashMap<String, NavigationButton>();
    private HashMap<String, HashMap<String, String> > descriptions = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, ArrayList<ArrayList<String> > > datasets = new HashMap<String, ArrayList<ArrayList<String>>>();
    private Button load = null;
    public MenuView() {
        buildView();
    }
    public void buildView() {
    	setCaption("Query");
		MongodbData md = MongodbData.getInstance();

        Button btn = new Button();
        btn.setIcon(FontAwesome.PLUS);
        this.setRightComponent(btn);

        final Component co = this;
        btn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                QueryForm form = new QueryForm();
                form.setData(co);
                form.buildView();
                getNavigationManager().navigateTo(form);
                return;
            }
        });

        load = new Button("Show in Map", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ClinicalTrialsTouchKitUI app = ClinicalTrialsTouchKitUI.getApp();
                app.queryData(descriptions, datasets);
            }
        });
        setContent(new CssLayout(content, load));
    	//HadoopData hd = HadoopData.getInstance();
        //ArrayList<String> data_list = hd.getClusterDataList();

        /*
        ArrayList<String> data_list = md.getConditionsList();

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
        Button reload = new Button("Show in Map", new Button.ClickListener() {
        	@Override
        	public void buttonClick(ClickEvent event) {
        		ClinicalTrialsTouchKitUI app = ClinicalTrialsTouchKitUI.getApp();
        		app.reloadData();
        	}
        });
        setContent(new CssLayout(content, reload));
        */
    }
    public void addQuery(HashMap<String, String> query) {
        for(HashMap.Entry<String,String> entry : query.entrySet()) {
            System.out.println(entry.getKey() + '\t' + entry.getValue());
        }
        NavigationButton btn = new NavigationButton(query.get("name"));
        query.put("index", query.get("name"));

        this.addCss(query.get("color"));
        btn.addStyleName("color-" + query.get("color"));
        System.out.println(btn.getStyleName());
        HashMap<String, String> des = new HashMap<String, String>();
        ArrayList<ArrayList<String> > dataset = new ArrayList<ArrayList<String>>();
        MongodbData md = MongodbData.getInstance();
        md.getQueryData(query, des, dataset);
        des.putAll(query);

        descriptions.put(query.get("name"), des);
        datasets.put(query.get("name"), dataset);
        buttons.put(query.get("name"), btn);
        content.removeAllComponents();
        for (HashMap.Entry<String, NavigationButton> i : buttons.entrySet()) {
            content.addComponent(i.getValue());
        }

        btn.setData(des);
        btn.addClickListener(new NavigationButtonClickListener() {
            @Override
            public void buttonClick(NavigationButtonClickEvent event) {
                QueryForm form = new QueryForm();
                NavigationButton nb = (NavigationButton) event.getComponent();
                form.setData(nb.getData());
                form.buildView();
                getNavigationManager().navigateTo(form);
                return;
            }
        });

        setContent(new CssLayout(content, load));
    }
    public void removeQuery(String str) {
        buttons.remove(str);
        descriptions.remove(str);
        datasets.remove(str);
        content.removeAllComponents();
        for (HashMap.Entry<String, NavigationButton> i : buttons.entrySet()) {
            content.addComponent(i.getValue());
        }
        setContent(new CssLayout(content, load));
    }
    private Boolean addCss(String str) {
        Page.Styles style = Page.getCurrent().getStyles();
        style.add(".color-" + str + "{color:#" + str + ";}");
        return true;
    }
}
