package edu.uwm.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.uwm.data.*;
import edu.uwm.ClinicalTrialsTouchKitUI;

@SuppressWarnings("serial")
public class MenuView extends NavigationView {
    private final VerticalComponentGroup content = new VerticalComponentGroup();
    private HashMap<String, NavigationButton> buttons = new HashMap<String, NavigationButton>();
    private HashMap<String, HashMap<String, String> > descriptions = null;
    private HashMap<String, ArrayList<ArrayList<String> > > datasets = null;
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

        descriptions = md.getQueryDes();
        datasets = md.getQueryDatasets();

        for (Map.Entry<String, HashMap<String, String>> i : descriptions.entrySet()) {
            HashMap<String, String> query = i.getValue();
            NavigationButton tmp_btn = new NavigationButton(query.get("name"));
            this.addCss(query.get("color"));
            tmp_btn.addStyleName("color-" + query.get("color"));
            buttons.put(query.get("name"), tmp_btn);
            for (Map.Entry<String, NavigationButton> j : buttons.entrySet()) {
                content.addComponent(j.getValue());
            }
            tmp_btn.setData(query);
            tmp_btn.addClickListener(new NavigationButtonClickListener() {
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
        }

        load = new Button("Show in Map", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ClinicalTrialsTouchKitUI app = ClinicalTrialsTouchKitUI.getApp();
                app.queryData(descriptions, datasets);
            }
        });
        setContent(new CssLayout(content, load));
    }
    public void addQuery(HashMap<String, String> query) {
        NavigationButton btn = new NavigationButton(query.get("name"));
        this.addCss(query.get("color"));
        btn.addStyleName("color-" + query.get("color"));

        MongodbData md = MongodbData.getInstance();
        md.getQueryData(query);

        buttons.put(query.get("name"), btn);
        content.removeAllComponents();
        for (Map.Entry<String, NavigationButton> i : buttons.entrySet()) {
            content.addComponent(i.getValue());
        }

        btn.setData(descriptions.get(query.get("name")));
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
        for (Map.Entry<String, NavigationButton> i : buttons.entrySet()) {
            content.addComponent(i.getValue());
        }
        setContent(new CssLayout(content, load));
    }

    private boolean addCss(String str) {
        Page.Styles style = Page.getCurrent().getStyles();
        style.add(".color-" + str + "{color:#" + str + ";}");
        return true;
    }
}
