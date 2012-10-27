package weibo4j.model;

import java.util.ArrayList;
import java.util.List;

import weibo4j.http.Response;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @author SinaWeibo
 *
 */
public class Emotion extends WeiboResponse{
	private static final long serialVersionUID = -4096813631691846494L;
	private String phrase;           //表情使用的替代文字
	private String type;             //表情类型，image为普通图片表情，magic为魔法表情
	private String url;              //表情图片存放的位置
	private boolean isHot;           //是否为热门表情
	private boolean isCommon;        //是否是常用表情
	private int orderNumber;         //该表情在系统中的排序号码
	private String category;         //表情分类

	public Emotion(Response res) throws WeiboException {
		super(res);
		JSONObject json = res.asJSONObject();
		try {
			phrase = json.getString("phrase");
			type = json.getString("type");
			url = json.getString("url");
			isHot= json.getBoolean("is_hot");
			orderNumber = json.getInt("order_number");
			category = json.getString("category");
			isCommon = json.getBoolean("is_common");
		} catch (JSONException je) {
			throw new WeiboException(je.getMessage() + ":" + json.toString(),
					je);
		}
	}
	public Emotion(JSONObject json) throws WeiboException {
		try {
			phrase = json.getString("phrase");
			type = json.getString("type");
			url = json.getString("url");
			isHot= json.getBoolean("is_hot");
			orderNumber = json.getInt("order_number");
			category = json.getString("category");
			isCommon = json.getBoolean("is_common");
		} catch (JSONException je) {
			throw new WeiboException(je.getMessage() + ":" + json.toString(),
					je);
		}
	}
	public static List<Emotion> constructEmotions(Response res) throws WeiboException {
		try {
			JSONArray list = res.asJSONArray();
			int size = list.length();
			List<Emotion> emotions = new ArrayList<Emotion>(size);
			for (int i = 0; i < size; i++) {
				emotions.add(new Emotion(list.getJSONObject(i)));
			}
			return emotions;
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		} catch (WeiboException te) {
			throw te;
		}

	}

	public Emotion() {
		super();
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isHot() {
		return isHot;
	}

	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}

	public boolean isCommon() {
		return isCommon;
	}

	public void setCommon(boolean isCommon) {
		this.isCommon = isCommon;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Emotion [" +
		"phrase=" + phrase + 
		", type=" + type + 
		", url=" + url + 
		", isHot=" + isHot + 
		", isCommon=" + isCommon + 
		", orderNumber=" + orderNumber + 
		", category=" + category + 
		"]";
	}



}
