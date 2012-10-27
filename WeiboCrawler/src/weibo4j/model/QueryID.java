package weibo4j.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weibo4j.http.Response;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class QueryID {
	private String id;
	private String value;
	
	public QueryID(JSONObject json) throws WeiboException, JSONException {
		if (!json.getString("id").isEmpty()) {
			id = json.getString("id"); 
		}
		if(!json.getString("value").isEmpty()) {
			value = json.getString("value");
		}else {
			Iterator<String> keys = json.sortedKeys();
			if (keys.hasNext()) {
				id = keys.next();
				value = json.getString(id);	
			}
		}
	}
	public static List<QueryID> constructQueryID(Response res) throws WeiboException{
		JSONArray list = res.asJSONArray();
		int size = list.length();
		List<QueryID> qIDs = new ArrayList<QueryID>();
			try {
				for(int i=0;i<size;i++){
				qIDs.add(new QueryID(list.getJSONObject(i)));
				}
				return qIDs;
			} catch (JSONException e) {
				throw new WeiboException(e);
			}
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "QueryID [id=" + id + ", value=" + value + "]";
	}
	
}
