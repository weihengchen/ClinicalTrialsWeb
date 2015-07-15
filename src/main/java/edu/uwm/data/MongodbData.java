package edu.uwm.data;

import java.io.Serializable;
import java.util.*;

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
 */
public class MongodbData implements Serializable {
    private static MongodbData instance = null;

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
        //TODO: set up authentication in MongoDB, put parameters in config file
        System.out.println("MongoDB initialize start!");
        mongoClient = new MongoClient( "localhost" , 27017 );
        db = mongoClient.getDatabase("clinicaltrial");

        original_name2des = new HashMap<String, HashMap<String, String> >();
        original_name2dataset = new HashMap<String, ArrayList< ArrayList<String> > >();

        return true;
    }

    private ArrayList<String> conditions = null;
    public ArrayList<String> getConditionsList() {
        conditions = new ArrayList<String>();
        MongoCollection<Document> mc = db.getCollection("trials");
        MongoCursor<Document> cursor = mc.find(exists("clinical_study.condition")).projection(fields(include("clinical_study.condition"), excludeId())).iterator();
        TreeMap<String, Integer> cond2count = new TreeMap<String, Integer>();

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

        for (Map.Entry<String, Integer> entry : entriesSortedByValues(cond2count)) {
            conditions.add(entry.getKey());
        }
        return conditions;
    }

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
    private Boolean readOriginalData(String key) {
        MongoCollection<Document> mc = db.getCollection("trials");
        MongoCursor<Document> cursor = mc.find(and(exists("clinical_study.location"), or(eq("clinical_study.condition",key), in("clinical_study.condition", key))))
                .projection(fields(include("clinical_study.location", "clinical_study.clinical_results.baseline.population"), excludeId()))
                .iterator();
        //description
        String name = key;
        Integer trial = 0, sites = 0, result = 0, result_pop = 0;
        ArrayList<ArrayList <String> > points = new ArrayList<ArrayList<String>>();
        try {
            while(cursor.hasNext()) {
                Document doc = cursor.next();
                trial++;
                try {
                    JSONObject json = new JSONObject(doc.toJson());
                    JSONObject study = json.optJSONObject("clinical_study");
                    if (study.optJSONArray("location") != null) {
                       JSONArray jr = study.optJSONArray("location");
                        int i;
                        for (i=0; i<jr.length(); i++) {
                            sites++;
                            JSONObject fac = jr.optJSONObject(i).optJSONObject("facility");
                            if (fac != null) {
                                ArrayList<String> tmp = new ArrayList<String>();
                                tmp.add(Double.toString(fac.optDouble("latitude")));
                                tmp.add(Double.toString(fac.optDouble("longitude")));
                                points.add(tmp);
                            }
                        }
                    } else {
                        JSONObject js = study.optJSONObject("location");
                        sites++;
                        JSONObject fac = js.optJSONObject("facility");
                        if (fac != null) {
                            ArrayList<String> tmp = new ArrayList<String>();
                            tmp.add(Double.toString(fac.optDouble("latitude")));
                            tmp.add(Double.toString(fac.optDouble("longitude")));
                            points.add(tmp);
                        }
                    }
                    JSONObject res = study.optJSONObject("clinical_results");
                    if (res != null) {
                        result++;
                        JSONObject bl = res.optJSONObject("baseline");
                        if (bl != null) {
                            int pop = bl.optInt("population");
                            result_pop += pop;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            HashMap<String, String> dat = new HashMap<String, String>();
            dat.put("name", name);
            dat.put("trial", trial.toString());
            dat.put("site", sites.toString());
            dat.put("result", result.toString());
            dat.put("population", result_pop.toString());
            original_name2des.put(key, dat);
            original_name2dataset.put(key, points);
        } finally {
            cursor.close();
        }
        //geographical information
        return true;
    }

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
}
