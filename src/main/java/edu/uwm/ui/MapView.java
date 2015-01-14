package edu.uwm.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.addon.leaflet.LCircleMarker;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.control.LZoom;
import org.vaadin.addon.leaflet.control.LScale;
import org.vaadin.addon.leaflet.shared.Point;

import java.util.Random;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import edu.uwm.data.HadoopData;

public class MapView extends CssLayout implements LeafletClickListener{

    private LMap map;
    private HashMap<String, String> id2color = new HashMap<String, String>();
    private int zoom_level;

    @Override
    public void attach() {
        if (map == null) {
            buildView();
        }
        super.attach();
    };

    private void buildView() {
        setCaption("Map");
        addStyleName("mapview");
        setSizeFull();

        map = new LMap();

        // Note, if you wish to use Mapbox base maps, get your own API key.
        LTileLayer mapBoxTiles = new LTileLayer(
                "http://{s}.tiles.mapbox.com/v3/vaadin.i1pikm9o/{z}/{x}/{y}.png");
        mapBoxTiles.setDetectRetina(true);
        map.addLayer(mapBoxTiles);

        map.setAttributionPrefix("Powered by <a href=\"leafletjs.com\">Leaflet</a> — &copy; <a href='http://osm.org/copyright'>OpenStreetMap</a> contributors");

        map.setImmediate(true);

        map.setSizeFull();
        zoom_level = 3;
        map.setZoomLevel(zoom_level);
        
        addComponent(map);
        
        map.setCenter(new Point(43.041809,-87.906837));
    }
    
    public Boolean updateClusterMap(String dataset_key) {
    	Iterator<Component> iterator = map.iterator();
        Collection<Component> remove = new ArrayList<Component>();
        while (iterator.hasNext()) {
            Component next = iterator.next();
            if (next instanceof LMarker) {
                remove.add(next);
            }
        }
        for (Component component : remove) {
            map.removeComponent(component);
        }
        
        HadoopData hd = HadoopData.getInstance();
        ArrayList< ArrayList<String> > dataset = hd.getClusterDataSet(dataset_key);
        
        
        LCircleMarker cMarker = null;
        String color = null;
        HashMap<String, ArrayList< ArrayList<String> > > str2multi = new HashMap<String, ArrayList< ArrayList<String> > >();
        for (ArrayList<String> tmp : dataset) {
        	String mark_key = tmp.get(0) + tmp.get(1) + tmp.get(2) + tmp.get(3);
        	if (str2multi.containsKey(mark_key)) {
        		ArrayList< ArrayList<String>> tmp_al = str2multi.get(mark_key);
        		tmp_al.add(tmp);
        		continue;
        	}
        	ArrayList< ArrayList<String> > tmp_al = new ArrayList< ArrayList<String> >();
        	tmp_al.add(tmp);
        	str2multi.put(mark_key, tmp_al);
        	if (id2color.containsKey(tmp.get(1))) {
        		color = id2color.get(tmp.get(1));
        	} else {
        		color = genColor();
        		id2color.put(tmp.get(1), color);
        	}
        	if (tmp.get(0).equals("origin")) {
        		cMarker = new LCircleMarker(Double.parseDouble(tmp.get(3)), Double.parseDouble(tmp.get(2)), 2);
        		cMarker.setColor(color);
        		cMarker.setOpacity(0.50);
        		cMarker.setData(tmp_al);
        		cMarker.addClickListener(this);
        		map.addComponent(cMarker);
        	} else if(tmp.get(0).equals("center")) {
        		cMarker = new LCircleMarker(Double.parseDouble(tmp.get(3)), Double.parseDouble(tmp.get(2)), 6);
        		cMarker.setColor(color);
        		cMarker.setFillColor(color);
        		cMarker.setFill(true);
        		cMarker.setFillOpacity(0.70);
        		cMarker.setOpacity(0.70);
        		cMarker.setData(tmp_al);
        		cMarker.addClickListener(this);
        		map.addComponent(cMarker);
        	}
        }
    	return true;
    }
    private String genColor() {
    	String[] panel = {"00", "11", "22", "33", "44", "55",
    			"66", "77", "88", "99", "AA", "BB", "CC", "DD", "EE", "FF"
    	};
    	Random gen = new Random();
    	String color = "#";
    	color += panel[gen.nextInt(16)];
    	color += panel[gen.nextInt(16)];
    	color += panel[gen.nextInt(16)];
    	return color;
    }
    private void popUp(Object para) {
    	ArrayList< ArrayList<String>> data = null;
    	if (!(para instanceof ArrayList<?>)) {
    		return;
    	}
    	data =(ArrayList<ArrayList<String> >) para;
    	Popover pover = new Popover();
    	pover.addStyleName("Detail");

        VerticalComponentGroup detailsGroup = new VerticalComponentGroup();
        Label title = new Label(Integer.toString(data.size()));
        detailsGroup.addComponent(title);

        //detailsGroup.addComponent(buildTicketLayout(ticket));
        /*
        Label closeLabel = new Label("Close");
        closeLabel.addStyleName("blue");
        closeLabel.addStyleName("textcentered");
        closeLabel.addStyleName("closelabel");

        closeLabel.setHeight(30.0f, Unit.PIXELS);

        CssLayout wrapper = new CssLayout(closeLabel);
        wrapper.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                close();
            }
        });
        detailsGroup.addComponent(wrapper);
        */
        detailsGroup.setWidth(300, Unit.PIXELS);
        pover.setContent(detailsGroup);
        pover.setClosable(true);
        pover.showRelativeTo(this);
    }
    
    @Override
    public void onClick(final LeafletClickEvent event) {
    	Object o = event.getSource();
        if (o instanceof LCircleMarker) {
        	popUp(((LCircleMarker) o).getData());
        }
    }
}
