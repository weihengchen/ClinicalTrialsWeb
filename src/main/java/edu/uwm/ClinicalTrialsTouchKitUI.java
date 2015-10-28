package edu.uwm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.vaadin.addon.leaflet.LCircleMarker;

import edu.uwm.ui.*;
import edu.uwm.data.*;
import edu.uwm.gwt.client.*;

import com.vaadin.addon.touchkit.annotations.CacheManifestEnabled;
import com.vaadin.addon.touchkit.annotations.OfflineModeEnabled;
import com.vaadin.addon.touchkit.extensions.OfflineMode;
import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;

/**
 * The UI's "main" class
 *
 */
@SuppressWarnings("serial")
@Widgetset("edu.uwm.gwt.ClinicalTrialsWidgetSet")
@Theme("touchkit")
// Cache static application files so as the application can be started
// and run even when the network is down.
@CacheManifestEnabled
// Switch to the OfflineMode client UI when the server is unreachable
@OfflineModeEnabled
// Make the server retain UI state whenever the browser reloads the app
@PreserveOnRefresh
public class ClinicalTrialsTouchKitUI extends UI {
	private TabBarView tabBarView = null;
	private MapView mapview = null;
	//private HadoopData hd = null;
    private MongodbData md = null;
	private MenuView menuview = null;

    private final ClinicalTrialsPersistToServerRpc serverRpc = new ClinicalTrialsPersistToServerRpc() {
        @Override
        public void persistToServer() {
            // TODO this method is called from client side to store offline data
        }
    };

    @Override
    protected void init(VaadinRequest request) {
    	buildView();

        // Use of the OfflineMode connector is optional.
        OfflineMode offlineMode = new OfflineMode();
        offlineMode.extend(this);
        // Maintain the session when the browser app closes.
        offlineMode.setPersistentSessionCookie(true);
        // Define the timeout in secs to wait when a server request is sent
        // before falling back to offline mode.
        offlineMode.setOfflineModeTimeout(60);
    }
    /*
    1. Initialize the MongoDB singleton Instance
    2. Create the TabBarView, and add three views(query, condition, map).
     */
    private void buildView() {
    	//hd = HadoopData.getInstance();
    	md = MongodbData.getInstance();

        tabBarView = new TabBarView();
        final NavigationManager navigationManager = new NavigationManager();
        navigationManager.setCaption("Query");
        navigationManager.setCurrentComponent(new MenuView());
        Tab tab;
        tab = tabBarView.addTab(navigationManager);
        tab.setCaption("Query");
        tab.setIcon(FontAwesome.SEARCH);
        
        final NavigationManager condManager = new NavigationManager();
        //navigationManager.setCaption("Data");
        condManager.setCurrentComponent(new ConditionsView());
        tab = tabBarView.addTab(condManager);
        tab.setCaption("Conditions");
        tab.setIcon(FontAwesome.LIST);
        
        mapview = new MapView();
        tab = tabBarView.addTab(mapview);
        tab.setIcon(FontAwesome.MAP_MARKER);
        tab.setCaption("Map");
        setContent(tabBarView);
    }

    /*
    Get the instance of Current UI object to manage different views.
     */
    public static ClinicalTrialsTouchKitUI getApp() {
    	return (ClinicalTrialsTouchKitUI) UI.getCurrent();
    }

    /*
    Change view to map view to display the clustering result.
    data_set:String, the name of condition
     */
    public void showDataSet(String data_set) {
    	mapview.updateClusterMap(data_set);
    	tabBarView.setSelectedTab(mapview);
    	return;
    }

    /*
    Change view to map view to display the original recruitment sites position
    data_set:String, the name of condition
     */
    public void showOriginalDataSet(String data_set) {
    	mapview.updateOriginalMap(data_set);
    	tabBarView.setSelectedTab(mapview);
    	return;
    }

    /*
    Change view to map view to display the original recruitment sites position
    des:HashMap<String, HashMap<String, String> >, conditions -> {description_title:description}
    dataset:HashMap<String, ArrayList<ArrayList<String> > >, condition -> [ [pos_x, pos_y, count,...], ...]
     */
    public void queryData(HashMap<String, HashMap<String, String> >des, HashMap<String, ArrayList<ArrayList<String> > > dataset) {
    	//hd.reloadData();
    	//buildView();
        mapview.updateQueryMap(des, dataset);
        tabBarView.setSelectedTab(mapview);
        return;
    }
}

