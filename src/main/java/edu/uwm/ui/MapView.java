package edu.uwm.ui;

import java.io.Serializable;
import java.util.*;
//import java.util.function.BooleanSupplier;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vividsolutions.jts.geom.LineString;

import edu.uwm.data.HadoopData;
/*
Use to display the result on map
 */
public class MapView extends CssLayout implements LeafletClickListener{

    private LMap map = null;
	//private Label label = null;
	private TextArea label = null;
    //used to get all the data information in the point when it is clicked.
	private HashMap<String, ArrayList<ArrayList <String> > > point2data = new HashMap<String, ArrayList<ArrayList<String>>>();
    private int zoom_level;
    //color_panel is used to specify the density from blue to red.
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

    /*
    Build the Map view
     */
    private void buildView() {
        /*
        Initialize the Map
         */
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
        //set zoom level
        zoom_level = 1;
		map.setZoomLevel(zoom_level);
        //set center point
		map.setCenter(new Point(43.041809, -87.906837));
		map.addMoveEndListener(new LeafletMoveEndListener() {
            /*
            When the map is moved, it need to update the statistical information and Point Mark.
             */
			@Override
			public void onMoveEnd(LeafletMoveEndEvent event) {
                //Get boundary of map window
				double nelat = map.getBounds().getNorthEastLat();
				double nelon = map.getBounds().getNorthEastLon();
				double swlat = map.getBounds().getSouthWestLat();
				double swlon = map.getBounds().getSouthWestLon();
				//System.out.println(nelat);
				//System.out.println(nelon);
				//System.out.println(swlat);
				//System.out.println(swlon);

                //in different zoom level, set different radius for Point mark.
				int level = map.getZoomLevel();
				int r = 1;
				if (level <= 3) r = 1;
				else if (level <= 7) r = 2;
				else r = 5;

				ArrayList<ArrayList <String> > tmp_data = new ArrayList<ArrayList<String>>();

                /*
                Update the point mark, and also add the points to tmp_data while the points are inside the map windows
                 */
				Iterator<Component> iterator = map.iterator();
				while (iterator.hasNext()) {
					Component next = iterator.next();
					if (next instanceof LCircleMarker) {
						LCircleMarker tmp = (LCircleMarker) next;
						double lat = tmp.getPoint().getLat();
						double lon = tmp.getPoint().getLon();
						if (lat <= nelat && lat >= swlat && lon <= nelon && lon >= swlon)
							tmp_data.addAll(point2data.get((String)tmp.getData()));
						tmp.setRadius(r);
					}
				}
                /*
                Update the statistical information about the points inside the map window
                 */
				HashMap<String, Integer> name = new HashMap<String, Integer>();
				HashMap<String, HashSet<String> > trial = new HashMap<String, HashSet<String>>();
				HashMap<String, HashSet<String> > intervention = new HashMap<String, HashSet<String>>();
				int i, j;
				for (i=0; i<tmp_data.size(); i++) {
					String t_name = tmp_data.get(i).get(2);
					if (!name.containsKey(t_name) ) {
						name.put(t_name, new Integer(0));
						trial.put(t_name, new HashSet<String>());
						intervention.put(t_name, new HashSet<String>());
					}
					name.put(t_name, name.get(t_name) + 1);
					trial.get(t_name).add(tmp_data.get(i).get(3));
					for (j=4; j<tmp_data.get(i).size(); j++) {
						intervention.get(t_name).add(tmp_data.get(i).get(j));
					}
				}
				String label_text = "";
				for (Map.Entry<String, Integer> entry : name.entrySet()) {
					label_text = label_text + "Name:" + entry.getKey() + "\n" +
							"#Trials:" + Integer.toString(trial.get(entry.getKey()).size()) + "\n" +
							"#Sites:" + Integer.toString(entry.getValue()) + "\n" +
							"#Intervention:" + Integer.toString(intervention.get(entry.getKey()).size()) + "\n";
				}
				label.setValue(label_text);

			}
		});

        //add the statistical information to right top corner
		label = new TextArea();
		Page.Styles style = Page.getCurrent().getStyles();
		style.add(".flow-z {position:absolute; right:0px; z-index:1; text-align:right; background-color:transparent; border-style:none; resize:none; width:150px; height:200px;}");
		label.setPrimaryStyleName("flow-z");
		label.setEnabled(false);
		addComponent(label);
		addComponent(map);
    }

    /*
    Display the Clustering result in Map view.
     */
    public Boolean updateClusterMap(String dataset_key) {
    	if (map == null) {
    		buildView();
    	}
        /*
        Remove all the old point mark.
         */
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

        //set zoom level and center point
        zoom_level = 3;
        map.setZoomLevel(zoom_level);
        map.setCenter(new Point(43.041809, -87.906837));

        //get clustering result
        HadoopData hd = HadoopData.getInstance();
        ArrayList< ArrayList<String> > dataset = hd.getClusterDataSet(dataset_key);
        
        LCircleMarker cMarker = null;
        String color = null;
        HashMap<String, ArrayList< ArrayList<String> > > str2multi = new HashMap<String, ArrayList< ArrayList<String> > >();
        HashMap<String, Point > center_pos = new HashMap<String, Point>();
        TreeMap<String, Integer> group2num = new TreeMap<String, Integer>();

        //get the center points and points in different clusters by group id.
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

        //choose color by the clustering density from color_panel
        HashMap<String, String> id2color = new HashMap<String, String>();
        int i = 0;
        for (Map.Entry<String,Integer> entry : group2num.entrySet()) {
        	String key = entry.getKey();
        	String tmp = color_panel.get((int)((i*1.0/group2num.size())*color_panel.size()));
        	id2color.put(key, tmp);
        	i+=1;
        }

        //add point mark and line to the map view
        for (ArrayList<String> tmp : dataset) {
        	String mark_key = tmp.get(0) + tmp.get(1) + tmp.get(2) + tmp.get(3);
            //don't need to add mark if the point is already there.
        	if (str2multi.containsKey(mark_key)) {
        		ArrayList< ArrayList<String>> tmp_al = str2multi.get(mark_key);
        		tmp_al.add(tmp);
        		continue;
        	}
        	ArrayList< ArrayList<String> > tmp_al = new ArrayList< ArrayList<String> >();
        	tmp_al.add(tmp);
        	str2multi.put(mark_key, tmp_al);

            //set color value
        	if (id2color.containsKey(tmp.get(1))) {
        		color = id2color.get(tmp.get(1));
        	} else {
        		color = "#000000";
        	}

            //add origin point and center point separately.
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
	/*
	@Override
	public void onClick (final LeafletClickEvent event) {
		Object o = event.getSource();
		System.out.println(o.getClass().toString());
		if (o instanceof LCircleMarker) {
			popUp((String) ((LCircleMarker) o).getData());
		}
	}
	*/

    /*
    Popup detail information about the clicked point.
     */
    private void popUp(String key) {
        //initialize popover windows
    	Popover pover = new Popover();
    	pover.addStyleName("Detail");
        VerticalComponentGroup detailsGroup = new VerticalComponentGroup();
        Label title = new Label("Detail");
        detailsGroup.addComponent(title);

        //get the data points associated with the cilcked point.
		ArrayList<ArrayList <String> > tmp_data = point2data.get(key);
		HashMap<String, Integer> name = new HashMap<String, Integer>();
		HashMap<String, HashSet<String> > trial = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String> > intervention = new HashMap<String, HashSet<String>>();
		int i, j;
        //calculate the statistical information about the points.
		for (i=0; i<tmp_data.size(); i++) {
			String t_name = tmp_data.get(i).get(2);
			if (!name.containsKey(t_name) ) {
				name.put(t_name, new Integer(0));
				trial.put(t_name, new HashSet<String>());
				intervention.put(t_name, new HashSet<String>());
			}
			name.put(t_name, name.get(t_name) + 1);
			trial.get(t_name).add(tmp_data.get(i).get(3));
			for (j=4; j<tmp_data.get(i).size(); j++) {
				intervention.get(t_name).add(tmp_data.get(i).get(j));
			}
		}

        //add the information to popover window
		for (Map.Entry<String, Integer> entry : name.entrySet()) {
			Label lname = new Label("Name:" + entry.getKey());
			Label ltrial = new Label("#Trials:" + Integer.toString(trial.get(entry.getKey()).size()));
			Label lsites = new Label("#Sites:" + Integer.toString(entry.getValue()) );
			Label linter = new Label("#Interventions:" + Integer.toString(intervention.get(entry.getKey()).size()));
			detailsGroup.addComponent(lname);
			detailsGroup.addComponent(ltrial);
			detailsGroup.addComponent(lsites);
			detailsGroup.addComponent(linter);
		}
        //Display popup
        detailsGroup.setWidth(300, Unit.PIXELS);
        pover.setContent(detailsGroup);
        pover.setClosable(true);
        pover.showRelativeTo(this);
    }

    /*
    Display selected query results on map.
     */
	public Boolean updateQueryMap(HashMap<String, HashMap<String, String> >des, HashMap<String, ArrayList<ArrayList<String> > > dataset) {
		point2data.clear();
		if (map == null) {
			buildView();
		}
		//Initialize variables, and remove all the other marks firstly.
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

        //set zoom_level and center point.
		zoom_level = 1;
		map.setZoomLevel(zoom_level);
		map.setCenter(new Point(43.041809,-87.906837));
		int count = 0, site_count = 0;

		String label_text = "";

        //add point mark from the dataset to UI.
		for (Map.Entry<String, HashMap<String, String> > entry : des.entrySet()) {
            //For each dataset.
			site_count = 0;

			HashSet<String> trial_count = new HashSet<String>();
			HashSet<String> inter_count = new HashSet<String>();
			HashSet<String> visited = new HashSet<String>();

            //select color
			LCircleMarker cMarker = null;
			String color = "#" + entry.getValue().get("color");
			site_count = dataset.get(entry.getKey()).size();
			count += site_count;

            //add points in the dataset.
			for (ArrayList<String> tmp : dataset.get(entry.getKey())) {
				trial_count.add(tmp.get(3));
				for (int i = 4; i<tmp.size(); i++) {
					inter_count.add(tmp.get(i));
				}

                //Associate point information with point key.
				String key = tmp.get(0) + tmp.get(1);
				if (visited.contains(key)) {
					if (!point2data.containsKey(key))
						point2data.put(key, new ArrayList<ArrayList<String>>());
					point2data.get(key).add(tmp);
					continue;
				}
				visited.add(key);

                //add point mark to UI
				cMarker = new LCircleMarker(Double.parseDouble(tmp.get(0)), Double.parseDouble(tmp.get(1)), 1);
				cMarker.setData(key);
				if (!point2data.containsKey(key))
					point2data.put(key, new ArrayList<ArrayList<String>>());
				point2data.get(key).add(tmp);
				cMarker.setColor(color);
				cMarker.setFillColor(color);
				cMarker.setFillOpacity(0.5);
				cMarker.setOpacity(0.50);
				cMarker.addClickListener(this);
				map.addComponent(cMarker);
			}
            //Add statistical information
			label_text = label_text + "Name:" + entry.getKey() + "\n" +
					"#Trials:" + Integer.toString(trial_count.size()) + "\n" +
					"#Sites:" + Integer.toString(site_count) + "\n" +
					"#Intervention:" + Integer.toString(inter_count.size()) + "\n";
		}
        //set to UI
		label.setValue(label_text);
		return true;
	}
    
    /*
    Display original data points on map
     */
    public Boolean updateOriginalMap(String dataset_key) {
		point2data.clear();
    	if (map == null) {
    		buildView();
    	}
        //Initialize variables, and remove all the other marks firstly.
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

        //set zoom_level and center point.
        zoom_level = 1;
        map.setZoomLevel(zoom_level);
        map.setCenter(new Point(43.041809, -87.906837));
        
        //HadoopData hd = HadoopData.getInstance();
        //ArrayList< ArrayList<String> > dataset = hd.getOriginalDataSet(dataset_key);
        MongodbData md = MongodbData.getInstance();
		ArrayList< ArrayList<String> > dataset = md.getOriginalDataSet(dataset_key);

        //set statistical information
		label.setValue("#Sites:" + Integer.toString(dataset.size()));

        HashSet<String> visited = new HashSet<String>();

        //set color for point mark
        LCircleMarker cMarker = null;
        String color = "#000000";
        
        for (ArrayList<String> tmp : dataset) {
            String key = tmp.get(0) + tmp.get(1);
            //Associate point information with point key.
            if (visited.contains(key)) {
				if (!point2data.containsKey(key))
					point2data.put(key, new ArrayList<ArrayList<String>>());
				point2data.get(key).add(tmp);
                continue;
            }
            visited.add(key);
            //add point mark to UI
        	cMarker = new LCircleMarker(Double.parseDouble(tmp.get(0)), Double.parseDouble(tmp.get(1)), 1);
			cMarker.setData(key);
			cMarker.setColor(color);
			cMarker.setFillColor(color);
			cMarker.setFillOpacity(0.50);
    		cMarker.setOpacity(0.50);
			cMarker.addClickListener(this);
    		map.addComponent(cMarker);

            //Associate point information with point key.
			if (!point2data.containsKey(key))
				point2data.put(key, new ArrayList<ArrayList<String>>());
			point2data.get(key).add(tmp);
        }
        
        return true;
    }

    /*
    ClickListenser for Point Marker Click
     */
	@Override
	public void onClick(LeafletClickEvent event) {
		Object o = event.getSource();
		System.out.println(o.getClass().toString());
		if (o instanceof LCircleMarker) {
			popUp((String) ((LCircleMarker) o).getData());
		}
	}
}
