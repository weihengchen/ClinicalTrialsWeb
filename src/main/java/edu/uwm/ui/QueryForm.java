package edu.uwm.ui;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import edu.uwm.ClinicalTrialsTouchKitUI;
import edu.uwm.data.HadoopData;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by along on 7/22/15.
 */
public class QueryForm extends NavigationView {
    public QueryForm() {
        buildView();
    }
    private static Integer no = 1;
    private String genColor() {
        Random rand = new Random();
        Integer red = rand.nextInt(256);
        Integer green = rand.nextInt(256);
        Integer blue = rand.nextInt(256);
        String ret = "";
        String tmp = Integer.toHexString(red);
        if (tmp.length() < 2) tmp = "0" + tmp;
        ret += tmp;
        tmp = Integer.toHexString(green);
        if (tmp.length() < 2) tmp = "0" + tmp;
        ret += tmp;
        tmp = Integer.toHexString(blue);
        if (tmp.length() < 2) tmp = "0" + tmp;
        ret += tmp;
        return ret;
    }
    public void buildView() {
        if (this.getData() == null) return;
        this.setCaption("Query");
        //HadoopData hd = HadoopData.getInstance();
        //HashMap<String, String> des = hd.getClusterDes((String)this.getData());

        final VerticalComponentGroup content = new VerticalComponentGroup();

        final TextField name = new TextField("Query Name:");
        name.setValue("#" + no.toString());
        name.setEnabled(true);
        content.addComponent(name);

        final TextField cond = new TextField("Condition:");
        cond.setEnabled(true);
        content.addComponent(cond);

        final TextField country = new TextField("Country:");
        country.setEnabled(true);
        content.addComponent(country);

        final TextField drug = new TextField("Drug:");
        drug.setEnabled(true);
        content.addComponent(drug);

        final TextField id = new TextField("NCT_ID:");
        id.setEnabled(true);
        content.addComponent(id);

        final TextField color = new TextField("COLOR:");
        color.setEnabled(true);
        content.addComponent(color);

        /*
        field = new TextField("Cost");
        field.setEnabled(false);
        field.setInputPrompt(des.get("cost"));
        content.addComponent(field);
        */

        final Object par = this.getData();
        if (par instanceof MenuView) {
            String str = genColor();
            color.setValue(str);
            this.addCss(str);
            color.addStyleName("color-" + str);

            no++;
            final Button submitButton = new Button("Query");
            submitButton.setIcon(FontAwesome.SEARCH_PLUS);
            submitButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    MenuView mv = (MenuView) par;
                    HashMap<String, String> paras = new HashMap<String, String>();
                    paras.put("name", name.getValue());
                    paras.put("condition", cond.getValue());
                    paras.put("country", country.getValue());
                    paras.put("drug", drug.getValue());
                    paras.put("id", id.getValue());
                    paras.put("color", color.getValue());
                    mv.addQuery(paras);
                    getNavigationManager().navigateBack();
                    return;
                }
            });

            setContent(new CssLayout(content, submitButton));
        } else if (par instanceof HashMap) {
            final HashMap<String, String> data = (HashMap<String, String>) par;
            name.setValue(data.get("name"));
            name.setEnabled(false);
            cond.setValue(data.get("condition"));
            cond.setEnabled(false);
            country.setValue(data.get("country"));
            country.setEnabled(false);
            drug.setValue(data.get("drug"));
            drug.setEnabled(false);
            id.setValue(data.get("id"));
            id.setEnabled(false);
            color.setValue(data.get("color"));
            this.addCss(data.get("color"));
            color.addStyleName("color-" + data.get("color"));
            color.setEnabled(false);

            TextField field = new TextField("#Trials");
            field.setEnabled(false);
            field.setInputPrompt(data.get("trial"));
            content.addComponent(field);

            field = new TextField("#Sites");
            field.setEnabled(false);
            field.setInputPrompt(data.get("site"));
            content.addComponent(field);

            field = new TextField("#Trials with result");
            field.setEnabled(false);
            field.setInputPrompt(data.get("result"));
            content.addComponent(field);

            field = new TextField("#Population with result");
            field.setEnabled(false);
            field.setInputPrompt(data.get("population"));
            content.addComponent(field);

            field = new TextField("#Sponsors");
            field.setEnabled(false);
            field.setInputPrompt(data.get("sponsors"));
            content.addComponent(field);

            final Button submitButton = new Button("Remove");
            submitButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    MenuView mv = (MenuView)getNavigationManager().getPreviousComponent();
                    mv.removeQuery(data.get("index"));
                    getNavigationManager().navigateBack();
                    return;
                }
            });

            setContent(new CssLayout(content, submitButton));
        }
    }
    private Boolean addCss(String str) {
        Page.Styles style = Page.getCurrent().getStyles();
        style.add(".color-" + str + "{color:#" + str + ";}");
        return true;
    }
}
