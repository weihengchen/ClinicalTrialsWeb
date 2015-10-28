package edu.uwm.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.client.ui.Icon;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.uwm.data.*;
import edu.uwm.ClinicalTrialsTouchKitUI;
/*
Collection of different queries, multi selection and display on map.
 */
@SuppressWarnings("serial")
public class MenuView extends NavigationView {
    private final VerticalComponentGroup content = new VerticalComponentGroup();
    private HashMap<String, CssLayout> cells = new HashMap<String, CssLayout>();
    private HashMap<String, HashMap<String, String> > descriptions = null;
    private HashMap<String, ArrayList<ArrayList<String> > > datasets = null;
    private HashMap<String, Boolean> mark = null;
    private Button load = null;
    public MenuView() {
        buildView();
    }
    /*
    Build Menu View
     */
    public void buildView() {
        //Initialize
    	setCaption("Query");
		MongodbData md = MongodbData.getInstance();

        //Add right top corner button "new query" and clicklistener
        Button btn = new Button();
        btn.setIcon(FontAwesome.PLUS);
        btn.setCaption("New Query");
        this.setRightComponent(btn);

        final Component co = this;
        btn.addClickListener(new Button.ClickListener() {
            //click to create a new query by using query form
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                QueryForm form = new QueryForm();
                form.setData(co);
                form.buildView();
                getNavigationManager().navigateTo(form);
                return;
            }
        });

        //Add CSS style
        Page.Styles style = Page.getCurrent().getStyles();
        style.add(".float-left{float:left;vertical-align:middle; text-align:left; left:3px; z-index:1; position:absolute;}");
        style.add(".float-right{float:right;vertical-align:middle;}");
        //hl.addStyleName("horizontal v-horizontallayout v-layout v-horizontal v-widget horizontal v-horizontallayout-horizontal v-has-width v-has-height");

        //Get existed query results
        descriptions = md.getQueryDes();
        datasets = md.getQueryDatasets();
        mark = new HashMap<String, Boolean>();

        //add the query results to UI one by one.
        for (Map.Entry<String, HashMap<String, String>> i : descriptions.entrySet()) {
            //set the description of the query
            HashMap<String, String> query = i.getValue();
            NavigationButton tmp_btn = new NavigationButton(query.get("name"));
            this.addCss(query.get("color"));
            tmp_btn.addStyleName("color-" + query.get("color"));
            tmp_btn.setWidth("95%");
            tmp_btn.addStyleName("float-right");

            //add clicklistener to redirect to query detail page
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

            //Add multi selection function
            Button t_btn = new Button();
            t_btn.setWidth("10%");
            t_btn.setStyleName("float-left");
            t_btn.setIcon(FontAwesome.SQUARE_O);
            t_btn.setData(i.getKey());
            t_btn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent clickEvent) {
                    Button btn = clickEvent.getButton();
                    String key = (String) btn.getData();
                    if (mark.get(key)) {
                        mark.put(key, false);
                        btn.setIcon(FontAwesome.SQUARE_O);
                    } else {
                        mark.put(key, true);
                        btn.setIcon(FontAwesome.CHECK_SQUARE_O);
                    }
                }
            });
            mark.put(i.getKey(), false);

            //add to query result component collection
            cells.put(query.get("name"), new CssLayout(t_btn, tmp_btn));
        }

        //Add query result cell to UI
        for (Map.Entry<String, CssLayout> i : cells.entrySet()) {
            content.addComponent(i.getValue());
        }

        //Add a button to redirect to MapView
        load = new Button("Show in Map", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ClinicalTrialsTouchKitUI app = ClinicalTrialsTouchKitUI.getApp();
                HashMap<String, HashMap<String, String> > des = new HashMap<String, HashMap<String, String>>();
                HashMap<String, ArrayList<ArrayList<String> > > dataset = new HashMap<String, ArrayList<ArrayList<String>>>();
                for (Map.Entry<String, Boolean> entry : mark.entrySet()) {
                    if (entry.getValue()) {
                        String key = entry.getKey();
                        des.put(key, descriptions.get(key));
                        dataset.put(key, datasets.get(key));
                    }
                }
                app.queryData(des, dataset);
            }
        });
        //set UI
        setContent(new CssLayout(content, load));
    }
    /*
    add query result to Menuview
     */
    public void addQuery(HashMap<String, String> query) {
        //Initialize
        MongodbData md = MongodbData.getInstance();
        md.getQueryData(query);

        //add item in view, set the color, css style, click listenser
        NavigationButton btn = new NavigationButton(query.get("name"));
        this.addCss(query.get("color"));
        btn.addStyleName("color-" + query.get("color"));
        btn.setWidth("95%");
        btn.addStyleName("float-right");
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

        //add multi selection option
        Button t_btn = new Button();
        t_btn.setWidth("10%");
        t_btn.setStyleName("float-left");
        t_btn.setIcon(FontAwesome.SQUARE_O);
        t_btn.setData(query.get("name"));
        t_btn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                Button btn = clickEvent.getButton();
                String key = (String) btn.getData();
                if (mark.get(key)) {
                    mark.put(key, false);
                    btn.setIcon(FontAwesome.SQUARE_O);
                } else {
                    mark.put(key, true);
                    btn.setIcon(FontAwesome.CHECK_SQUARE_O);
                }
            }
        });
        mark.put(query.get("name"), false);

        //add to component group
        cells.put(query.get("name"), new CssLayout(t_btn, btn));
        content.removeAllComponents();
        for (Map.Entry<String, CssLayout> i : cells.entrySet()) {
            content.addComponent(i.getValue());
        }

        //set content
        setContent(new CssLayout(content, load));
    }

    /*
    remove the query result
     */
    public void removeQuery(String str) {
        cells.remove(str);
        descriptions.remove(str);
        datasets.remove(str);
        mark.remove(str);
        content.removeAllComponents();
        for (Map.Entry<String, CssLayout> i : cells.entrySet()) {
            content.addComponent(i.getValue());
        }
        setContent(new CssLayout(content, load));
    }

    /*
    Add CSS style to current page.
     */
    private boolean addCss(String str) {
        Page.Styles style = Page.getCurrent().getStyles();
        style.add(".color-" + str + "{color:#" + str + ";}");
        return true;
    }
}
