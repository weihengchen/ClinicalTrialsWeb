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
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.parking.widgetset.client.model.Location;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import edu.uwm.data.HadoopData;

public class MapView extends CssLayout {

    private LMap map;

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
        map.setZoomLevel(3);
        addComponent(map);
        
        map.setCenter(new Point(43.041809,-87.906837));
    }
    
    public Boolean updateMap(String dataset_key) {
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
        HashMap<String, String> des;
        ArrayList< ArrayList<String> > dataset = hd.getClusterDataSet(dataset_key, des);
        
        LCircleMarker cMarker = null;
        for (ArrayList<String> tmp : dataset) {
        	if (tmp.get(0).equals("origin")) {
        		cMarker = new LCircleMarker(Double.parseDouble(tmp.get(3)), Double.parseDouble(tmp.get(2)), 10);
        		cMarker.setColor("#FF00FF");
        		cMarker.setWeight(3);
        		map.addComponent(cMarker);
        	} else if(tmp.get(0).equals("center")) {
        		cMarker = new LCircleMarker(Double.parseDouble(tmp.get(3)), Double.parseDouble(tmp.get(2)), 10);
        		cMarker.setColor("#FF0000");
        		cMarker.setFill(true);
        		cMarker.setWeight(10);
        		map.addComponent(cMarker);
        	}
        }
    	return true;
    }
}
