package edu.uwm.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

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
}
