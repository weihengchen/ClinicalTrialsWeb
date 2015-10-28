package edu.uwm.data;

import java.io.Serializable;
import java.util.*;
import static java.util.Arrays.asList;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.apache.xpath.operations.Bool;
import org.bson.BSONObject;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;

/**
 * Created by along on 7/14/15.
 * singleton implement of MongoDB data
 */
public class MongodbData implements Serializable {
    //instance
    private static MongodbData instance = null;

    //MongoDB variables
    private MongoClient mongoClient = null;
    private MongoDatabase db = null;

    private MongodbData() {
        if (false == init()) {
            instance = null;
            return;
        }
    }
    public static MongodbData getInstance() {
        if (instance == null) {
            instance = new MongodbData();
        }
        return instance;
    }
    private Boolean init() {
        //Initialize parameters in MongoDB
        //TODO: set up authentication in MongoDB, put parameters in config file
        System.out.println("MongoDB initialize start!");
        mongoClient = new MongoClient( "localhost" , 27017 );
        db = mongoClient.getDatabase("clinicaltrial");

        original_name2des = new HashMap<String, HashMap<String, String> >();
        original_name2dataset = new HashMap<String, ArrayList< ArrayList<String> > >();

        return true;
    }

    //condition list
    private ArrayList<String> conditions = null;
    //get Conditions List
    public ArrayList<String> getConditionsList() {
        conditions = new ArrayList<String>();
        //Initialize collection
        MongoCollection<Document> mc = db.getCollection("trials");
        //Initialize query
        MongoCursor<Document> cursor = mc.find(exists("clinical_study.condition")).projection(fields(include("clinical_study.condition"), excludeId())).iterator();
        TreeMap<String, Integer> cond2count = new TreeMap<String, Integer>();

        //read data
        try {
            while(cursor.hasNext()) {
                Document doc = cursor.next();
                Document study = (Document)doc.get("clinical_study");
                Object obj = study.get("condition");
                if (obj instanceof String) {
                    String key = (String)obj;
                    Integer val = 0;
                    if (cond2count.containsKey(key)) {
                        val = cond2count.get(key);
                    }
                    val += 1;
                    cond2count.put(key, val);
                } else if (obj instanceof ArrayList) {
                    for (String key: (ArrayList<String>)obj) {
                        Integer val = 0;
                        if (cond2count.containsKey(key)) {
                            val = cond2count.get(key);
                        }
                        val += 1;
                        cond2count.put(key, val);
                    }
                }
            }
        } finally {
            cursor.close();
        }

        //add to list
        for (Map.Entry<String, Integer> entry : entriesSortedByValues(cond2count)) {
            conditions.add(entry.getKey());
        }
        return conditions;
    }

    //sort Hashmap
    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    //get Description, Geoloation of a query
    private boolean getGeoLocations(Document query, HashMap<String, String> des, ArrayList< ArrayList<String>> dataset, String country) {
        MongoCollection<Document> mc = db.getCollection("trials");
        //construct query
        MongoCursor<Document> cursor = mc.find(query)
                .projection(fields(include("clinical_study.location", "clinical_study.clinical_results.baseline.measure_list"
                        , "clinical_study.intervention.intervention_name", "clinical_study.sponsors.collaborator", "clinical_study.sponsors.lead_sponsor", "clinical_study.id_info.nct_id"), excludeId()))
                .iterator();

        HashSet<String> sponsors = new HashSet<String>();
        //description
        Integer trial = 0, sites = 0, result = 0, result_pop = 0;
        ArrayList<ArrayList <String> > points = dataset;
        //read data
        try {
            while(cursor.hasNext()) {
                Document doc = cursor.next();
                trial++;
                try {
                    ArrayList<String> intervention = new ArrayList<String>();
                    JSONObject json = new JSONObject(doc.toJson());
                    //read basic information: nct_id, intervention, sponsors
                    JSONObject study = json.optJSONObject("clinical_study");
                    String nct = study.optJSONObject("id_info").optString("nct_id");
                    if (study.optJSONArray("intervention") != null) {
                        JSONArray arr_intervention = study.optJSONArray("intervention");
                        int i;
                        for (i=0; i<arr_intervention.length(); i++) {
                            intervention.add(arr_intervention.getJSONObject(i).getString("intervention_name"));
                        }
                    } else if (study.optJSONObject("intervention") != null) {
                        intervention.add(study.optJSONObject("intervention").getString("intervention_name"));
                    }
                    JSONObject sp = study.optJSONObject("sponsors");
                    if (sp != null) {
                        JSONObject leader = sp.optJSONObject("lead_sponsor");
                        if (leader != null) {
                            sponsors.add(leader.optString("agency"));
                        }
                        JSONArray coll = sp.optJSONArray("collaborator");
                        if (coll != null) {
                            for (int i = 0; i < coll.length(); i++) {
                                sponsors.add(coll.getJSONObject(i).optString("agency"));
                            }
                        } else {
                            JSONObject c = sp.optJSONObject("collaborator");
                            if (c != null) {
                                sponsors.add(c.optString("agency"));
                            }
                        }
                    }
                    //read locations
                    if (study.optJSONArray("location") != null) {
                        //single location
                        JSONArray jr = study.optJSONArray("location");
                        int i;
                        for (i=0; i<jr.length(); i++) {
                            JSONObject fac = jr.optJSONObject(i).optJSONObject("facility");
                            if ( fac != null && fac.optJSONObject("address")!=null && (!country.isEmpty())) {
                                String c = fac.optJSONObject("address").optString("country");
                                if (c.toLowerCase().indexOf(country.toLowerCase()) == -1)
                                    continue;
                            }
                            sites++;
                            if (fac != null) {
                                ArrayList<String> tmp = new ArrayList<String>();
                                tmp.add(Double.toString(fac.optDouble("latitude")));
                                tmp.add(Double.toString(fac.optDouble("longitude")));
                                tmp.add(des.get("name"));
                                tmp.add(nct);
                                for (int j=0; j<intervention.size(); j++) {
                                    tmp.add(intervention.get(j));
                                }
                                points.add(tmp);
                            }
                        }
                    } else {
                        //multi locations
                        JSONObject js = study.optJSONObject("location");
                        if (js != null) {
                            JSONObject fac = js.optJSONObject("facility");
                            if (fac != null && fac.optJSONObject("address") != null) {
                                String c = fac.optJSONObject("address").optString("country");
                                if (country.isEmpty() || c.toLowerCase().indexOf(country.toLowerCase()) != -1) {
                                    sites++;
                                    if (fac != null) {
                                        ArrayList<String> tmp = new ArrayList<String>();
                                        tmp.add(Double.toString(fac.optDouble("latitude")));
                                        tmp.add(Double.toString(fac.optDouble("longitude")));
                                        tmp.add(des.get("name"));
                                        tmp.add(nct);
                                        for (int j = 0; j < intervention.size(); j++) {
                                            tmp.add(intervention.get(j));
                                        }
                                        points.add(tmp);
                                    }
                                }
                            }
                        }
                    }
                    //results
                    if (study.optJSONObject("clinical_results") != null) result++;
                    JSONObject tmp;
                    JSONArray res;
                    int pop = 0;
                    while (true) {
                        tmp = study.optJSONObject("clinical_results");
                        if (tmp == null) break;
                        tmp = tmp.optJSONObject("baseline");
                        if (tmp == null) break;
                        tmp = tmp.optJSONObject("measure_list");
                        if (tmp == null) break;
                        res = tmp.optJSONArray("measure");
                        if (res == null) break;
                        tmp = res.getJSONObject(0).optJSONObject("category_list");
                        if (tmp == null) break;
                        tmp = tmp.optJSONObject("category");
                        if (tmp == null) break;
                        tmp = tmp.optJSONObject("measurement_list");
                        if (tmp == null) break;
                        res = tmp.optJSONArray("measurement");
                        if (res == null) {
                            tmp = tmp.optJSONObject("measurement");
                            if (tmp == null)break;
                            //System.out.println(nct + tmp.toString());
                            pop = tmp.optInt("value", 0);
                        } else {
                            //System.out.println(nct + res.toString());
                            pop = res.getJSONObject(res.length()-1).optInt("value", 0);
                        }
                        result_pop += pop;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //put to collection
            des.put("trial", trial.toString());
            des.put("site", sites.toString());
            des.put("result", result.toString());
            des.put("population", result_pop.toString());
            des.put("sponsors", Integer.toString(sponsors.size()));
        } finally {
            cursor.close();
        }
        return true;
    }

    //origianl dataset name to description
    private HashMap<String, HashMap<String,String> > original_name2des = null;
    public HashMap<String, String> getOriginalDes(String key) {
        if (!original_name2des.containsKey(key)) {
            Boolean ret = readOriginalData(key);
            if (!ret) {
                return null;
            }
        }
        return original_name2des.get(key);
    }
    //read original dataset
    private Boolean readOriginalData(String key) {
        //construct query
        Document query = new Document("clinical_study.condition", key);
        HashMap<String, String> dat = new HashMap<String, String>();
        ArrayList<ArrayList <String> > points = new ArrayList<ArrayList<String>>();
        dat.put("name", key);
        //get information
        getGeoLocations(query, dat, points, "");
        original_name2des.put(key, dat);
        original_name2dataset.put(key, points);
        //geographical information
        return true;
    }

    //map from name to dataset content
    private HashMap<String, ArrayList< ArrayList<String> > > original_name2dataset;
    public ArrayList< ArrayList<String> > getOriginalDataSet(String key) {
        if (!original_name2dataset.containsKey(key)) {
            Boolean ret = readOriginalData(key);
            if (!ret) {
                return null;
            }
        }
        return original_name2dataset.get(key);
    }

    //query results
    //map from name to description
    private HashMap<String, HashMap<String, String> > descriptions = new HashMap<String, HashMap<String, String>>();
    //map from name to dataset
    private HashMap<String, ArrayList<ArrayList<String> > > datasets = new HashMap<String, ArrayList<ArrayList<String>>>();
    //get description
    public HashMap<String, HashMap<String, String> > getQueryDes() {
        return descriptions;
    }
    //get datasets
    public HashMap<String, ArrayList<ArrayList<String> > > getQueryDatasets() {
        return datasets;
    }

    //new query
    public boolean getQueryData(HashMap<String, String> key) {
        //construct new query
        Document query = new Document();
        if (!key.get("id").isEmpty()) {
            System.out.println(key.get("id"));
            query = new Document("$and", asList(query, new Document("clinical_study.id_info.nct_id", key.get("id"))));
        }

        if (!key.get("condition").isEmpty()) {
            query = new Document("$and", asList(query, new Document("clinical_study.condition", new Document("$regex", key.get("condition")))));
        }

        if (!key.get("country").isEmpty()) {
            query = new Document("$and", asList(query, new Document("clinical_study.location_countries.country", new Document("$regex", key.get("country")))));
        }

        if (!key.get("intervention").isEmpty()) {
            query = new Document("$and", asList(query, new Document("clinical_study.intervention.intervention_name", new Document("$regex", key.get("intervention")))));
        }
        HashMap<String, String> des = new HashMap<String, String>();
        ArrayList< ArrayList<String>> dataset = new ArrayList<ArrayList<String>>();

        des.putAll(key);
        getGeoLocations(query, des, dataset, key.get("country"));
        descriptions.put(key.get("name"), des);
        datasets.put(key.get("name"), dataset);
        return true;
    }

    //check same query name
    public boolean sameQueryName(String key) {
        if (descriptions.containsKey(key)) {
            return true;
        }
        return false;
    }
}
