package edu.uwm.ui;

import java.io.Serializable;
import java.util.*;
//import java.util.function.BooleanSupplier;

import edu.uwm.data.MongodbData;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.control.LZoom;
import org.vaadin.addon.leaflet.control.LScale;
import org.vaadin.addon.leaflet.shared.Point;

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
import com.vividsolutions.jts.geom.LineString;

import edu.uwm.data.HadoopData;

public class MapView extends CssLayout implements LeafletClickListener{

    private LMap map = null;
    private int zoom_level;
    private ArrayList<String> color_panel = new ArrayList<String>(Arrays.asList("#801FEF","#7C1DEF","#771CF0","#721AF2",
    		"#6B1AF2","#6718F2","#6016F4","#5B15F4","#5613F4","#5011F6",
    		"#4911F6","#420FF6","#3D0EF7","#360CF9","#2F0AF9","#2A08F9",
    		"#2307FB","#1C05FB","#1503FB","#0E03FD","#0701FD","#0000FF",
    		"#0305FD","#050CFB","#0A11F9","#0C18F9","#111DF6","#1323F6",
    		"#162AF4","#1A2FF2","#1C34F2","#2138EF","#233DEF","#2642ED",
    		"#2A48EB","#2D4BEB","#2F50E8","#3354E8","#3459E6","#385DE4",
    		"#3B60E2","#3D64E2","#4169E1","#3F69E2","#3D6BE2","#3B6BE4",
    		"#3B6EE6","#386EE8","#3870E8","#3672EB","#3474EB","#3375ED",
    		"#3177EF","#2F79EF","#2D7BF2","#2B7CF2","#2A7EF4","#2882F6",
    		"#2683F6","#2485F9","#2387F9","#218AFB","#1F8CFD","#1C90FF",
    		"#1C90FF","#1A93FF","#1895FF","#1897FF","#1699FF","#159AFF",
    		"#139EFF","#11A0FF","#11A1FF","#0FA3FF","#0EA5FF","#0CA8FF",
    		"#0AAAFF","#08ACFF","#08AFFF","#07B1FF","#05B3FF","#03B6FF",
    		"#01B8FF","#01BCFF","#00BFFF","#07BFFD","#0EBFFD","#15BFFB",
    		"#1CBFFB","#23BFFB","#28BFF9","#2FC1F9","#36C1F7","#3DC1F7",
    		"#44C1F6","#4BC3F6","#52C3F4","#57C3F4","#5EC4F4","#64C6F2",
    		"#6BC6F2","#72C8F2","#79C8F0","#7ECAEF","#85CAEF","#8CCDEF",
    		"#8ECDEF","#91CFEF","#95D1EF","#97D3EF","#9AD4F0","#9ED6F0",
    		"#A1D6F2","#A3D8F2","#A8DAF2","#AADBF2","#AEDDF4","#B1DDF4",
    		"#B3DFF4","#B8E1F4","#BAE2F6","#BDE4F6","#C1E6F6","#C4E6F6",
    		"#C8E8F7","#CAE9F7","#CFEBF9","#FFFFC8","#FFFDBF","#FFFDB8",
    		"#FFFDB1","#FFFBAA","#FFFBA3","#FFF99C","#FFF995","#FFF78E",
    		"#FFF687","#FFF680","#FFF479","#FFF272","#FFF06B","#FFEF62",
    		"#FFED5B","#FFEB54","#FFE94D","#FFE646","#FFE43F","#FFE238",
    		"#FFE131","#FFDD2F","#FFDB2D","#FFDA2A","#FFD628","#FFD426",
    		"#FFD323","#FFCF21","#FFCD1D","#FFCA1C","#FFC81A","#FFC616",
    		"#FFC315","#FFC113","#FFBD0F","#FFBA0E","#FFB80A","#FFB508",
    		"#FFB307","#FFAF03","#FFAC01","#FFAA00","#FFA700","#FFA300",
    		"#FFA100","#FF9E00","#FF9A00","#FF9700","#FF9500","#FF9300",
    		"#FF9000","#FF8C00","#FF8900","#FF8700","#FF8300","#FF8000",
    		"#FF7E00","#FF7C00","#FF7900","#FF7500","#FF7200","#FF7000",
    		"#FF6E00","#FF6700","#FF6200","#FF5D00","#FF5900","#FF5200",
    		"#FF4D00","#FF4900","#FF4400","#FF3D00","#FF3800","#FF3400",
    		"#FF2F00","#FF2800","#FF2300","#FF1F00","#FF1A00","#FF1300",
    		"#FF0F00","#FF0A00","#FF0500","#FF0000","#FB0000","#F90000",
    		"#F60000","#F40000","#F00000","#EF0000","#EB0000","#E90000",
    		"#E60000","#E40000","#E20000","#DF0000","#DB0000","#DA0000",
    		"#D60000","#D40000","#D10000","#CF0000","#CD0000","#CA0000",
    		"#C80000","#CA0505","#CD0C0C","#CF1313","#D11A1A","#D42323",
    		"#D62A2A","#DA3131","#DB3A3A","#DF4242","#E24B4B","#E45252",
    		"#E65B5B","#E96464","#EB6E6E","#EF7777","#F08080","#F48989",
    		"#F69393","#F99C9C","#FBA7A7","#FFB1B1"));
    
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
        		//"https://a.tiles.mapbox.com/v4/weiheng.kp1naddf/page.html?access_token=pk.eyJ1Ijoid2VpaGVuZyIsImEiOiJxRXhVT2pVIn0.NLDHWGkfoNQRysu3wKBoiA#4/43.04/-87.91");
        		"http://a.tiles.mapbox.com/v4/weiheng.kp1naddf/{z}/{x}/{y}.png?access_token=pk.eyJ1Ijoid2VpaGVuZyIsImEiOiJxRXhVT2pVIn0.NLDHWGkfoNQRysu3wKBoiA");
        		//"http://{s}.tiles.mapbox.com/v3/vaadin.i1pikm9o/{z}/{x}/{y}.png?access_token=pk.eyJ1Ijoid2VpaGVuZyIsImEiOiJxRXhVT2pVIn0.NLDHWGkfoNQRysu3wKBoiA");
        mapBoxTiles.setDetectRetina(true);
        map.addLayer(mapBoxTiles);

        map.setAttributionPrefix("Powered by <a href=\"leafletjs.com\">Leaflet</a> — &copy; <a href='http://osm.org/copyright'>OpenStreetMap</a> contributors");

        map.setImmediate(true);

        map.setSizeFull();
        zoom_level = 3;
        map.setZoomLevel(zoom_level);
        map.setCenter(new Point(43.041809,-87.906837));
        
        addComponent(map);
    }
    
    public Boolean updateClusterMap(String dataset_key) {
    	if (map == null) {
    		buildView();
    	}
    	Iterator<Component> iterator = map.iterator();
        Collection<Component> remove = new ArrayList<Component>();
        while (iterator.hasNext()) {
            Component next = iterator.next();
            if (next instanceof LCircleMarker || next instanceof LPolyline) {
                remove.add(next);
            }
        }
        for (Component component : remove) {
            map.removeComponent(component);
        }
        
        zoom_level = 3;
        map.setZoomLevel(zoom_level);
        map.setCenter(new Point(43.041809, -87.906837));
        
        HadoopData hd = HadoopData.getInstance();
        ArrayList< ArrayList<String> > dataset = hd.getClusterDataSet(dataset_key);
        
        
        LCircleMarker cMarker = null;
        String color = null;
        HashMap<String, ArrayList< ArrayList<String> > > str2multi = new HashMap<String, ArrayList< ArrayList<String> > >();
        HashMap<String, Point > center_pos = new HashMap<String, Point>();
        TreeMap<String, Integer> group2num = new TreeMap<String, Integer>();
        
        for (ArrayList<String> tmp : dataset) {
        	if(tmp.get(0).equals("center")) {
        		Point p = new Point(Double.parseDouble(tmp.get(3)), Double.parseDouble(tmp.get(2)));
        		center_pos.put(tmp.get(1), p);
        	} else {
        		if (group2num.containsKey(tmp.get(1))) {
        			Integer g = group2num.get(tmp.get(1));
        			group2num.put(tmp.get(1), g + 1);
        		} else {
        			group2num.put(tmp.get(1), 1);
        		}
        	}
        }
        HashMap<String, String> id2color = new HashMap<String, String>();
        int i = 0;
        for (Map.Entry<String,Integer> entry : group2num.entrySet()) {
        	String key = entry.getKey();
        	String tmp = color_panel.get((int)((i*1.0/group2num.size())*color_panel.size()));
        	id2color.put(key, tmp);
        	i+=1;
        }
        
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
        		color = "#000000";
        	}

        	if (tmp.get(0).equals("origin")) {
        		if (center_pos.containsKey(tmp.get(1))) {
        			LPolyline line = new LPolyline();
        			line.setPoints(center_pos.get(tmp.get(1)), 
        					new Point(Double.parseDouble(tmp.get(3)), Double.parseDouble(tmp.get(2))));
        			line.setColor(color);
        			map.addComponent(line);
        		}
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

    private void popUp(Object para) {
    	ArrayList< ArrayList<String>> data = null;
    	if (!(para instanceof ArrayList<?>)) {
    		return;
    	}
    	data = (ArrayList<ArrayList<String> >) para;
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

	public Boolean updateQueryMap(HashMap<String, HashMap<String, String> >des, HashMap<String, ArrayList<ArrayList<String> > > dataset) {
		if (map == null) {
			buildView();
		}
		Iterator<Component> iterator = map.iterator();
		Collection<Component> remove = new ArrayList<Component>();
		while (iterator.hasNext()) {
			Component next = iterator.next();
			if (next instanceof LCircleMarker || next instanceof LPolyline) {
				remove.add(next);
			}
		}
		for (Component component : remove) {
			map.removeComponent(component);
		}

		zoom_level = 3;
		map.setZoomLevel(zoom_level);
		map.setCenter(new Point(43.041809,-87.906837));

		for (Map.Entry<String, HashMap<String, String> > entry : des.entrySet()) {
			HashSet<String> visited = new HashSet<String>();

			LCircleMarker cMarker = null;
			String color = "#" + entry.getValue().get("color");

			for (ArrayList<String> tmp : dataset.get(entry.getKey())) {
				String key = tmp.get(0) + tmp.get(1);
				if (visited.contains(key)) {
					continue;
				}
				visited.add(key);
				cMarker = new LCircleMarker(Double.parseDouble(tmp.get(0)), Double.parseDouble(tmp.get(1)), 2);
				cMarker.setColor(color);
				//cMarker.setOpacity(0.90);
				map.addComponent(cMarker);
			}
		}

		return true;
	}
    

    public Boolean updateOriginalMap(String dataset_key) {
    	if (map == null) {
    		buildView();
    	}
    	Iterator<Component> iterator = map.iterator();
        Collection<Component> remove = new ArrayList<Component>();
        while (iterator.hasNext()) {
            Component next = iterator.next();
            if (next instanceof LCircleMarker || next instanceof LPolyline) {
                remove.add(next);
            }
        }
        for (Component component : remove) {
            map.removeComponent(component);
        }
        
        zoom_level = 3;
        map.setZoomLevel(zoom_level);
        map.setCenter(new Point(43.041809,-87.906837));
        
        //HadoopData hd = HadoopData.getInstance();
        //ArrayList< ArrayList<String> > dataset = hd.getOriginalDataSet(dataset_key);
        MongodbData md = MongodbData.getInstance();
		ArrayList< ArrayList<String> > dataset = md.getOriginalDataSet(dataset_key);

        HashSet<String> visited = new HashSet<String>();
        
        LCircleMarker cMarker = null;
        String color = "#FFFFFF";
        
        for (ArrayList<String> tmp : dataset) {
            String key = tmp.get(0) + tmp.get(1);
            if (visited.contains(key)) {
                continue;
            }
            visited.add(key);
        	cMarker = new LCircleMarker(Double.parseDouble(tmp.get(0)), Double.parseDouble(tmp.get(1)), 2);
    		cMarker.setColor(color);
    		//cMarker.setOpacity(0.90);
    		map.addComponent(cMarker);
        }
        
        return true;
    }
}
