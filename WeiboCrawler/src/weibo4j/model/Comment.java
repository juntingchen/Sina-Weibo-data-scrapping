package weibo4j.model;

import weibo4j.http.Response;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A data class representing one single status of a user.
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class Comment extends WeiboResponse implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1272011191310628589L;
	private Date createdAt;                    //评论时间
	private long id;                           //评论id
	private String text;                       //评论内容
	private String source;                     //内容来源
	private Comment replycomment = null;       //回复的评论内容
	private User user = null;                  //User对象
	private Status status = null;              //Status对象
	private Comment comments =null;            //评论对象

	/*package*/public Comment(Response res) throws WeiboException {
		super(res);
		JSONObject json =res.asJSONObject();
		try {
			id = json.getLong("id");
			text = json.getString("text");
			source = json.getString("source");
			createdAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
			if(!json.isNull("comments"))
				comments = new Comment(json.getJSONObject("comments"));
			if(!json.isNull("user"))
				user = new User(json.getJSONObject("user"));
			if(!json.isNull("status"))
				status = new Status(json.getJSONObject("status"));
			if(!json.isNull("reply_comment"))
				replycomment = (new Comment(json.getJSONObject("reply_comment")));
		} catch (JSONException je) {
			throw new WeiboException(je.getMessage() + ":" + json.toString(), je);
		}
	}

	public Comment(JSONObject json)throws WeiboException, JSONException{
		id = json.getLong("id");
		text = json.getString("text");
		source = json.getString("source");
		createdAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
		if(!json.isNull("comments"))
			comments = new Comment(json.getJSONObject("comments"));
		if(!json.isNull("user"))
			user = new User(json.getJSONObject("user"));
		if(!json.isNull("status"))
			status = new Status(json.getJSONObject("status"));	
	}

	public Comment(String str) throws WeiboException, JSONException {
		// StatusStream uses this constructor
		super();
		JSONObject json = new JSONObject(str);
		id = json.getLong("id");
		text = json.getString("text");
		source = json.getString("source");
		createdAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
		comments = new Comment(json.getJSONObject("comments"));
		status = new Status(json.getJSONObject("status"));
		user = new User(json.getJSONObject("user"));

	}

	public static List<Comment> constructComments(Response res) throws WeiboException {
		JSONObject json = res.asJSONObject();
		JSONArray list = null;
		try {
			if(!json.isNull("comments")){				
				list = res.asJSONObject().getJSONArray("comments");
			}
			int size = list.length();
			List<Comment> comments = new ArrayList<Comment>(size);
			for (int i = 0; i < size; i++) {
				comments.add(new Comment(list.getJSONObject(i)));
			}
			return comments;
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		} catch (WeiboException te) {
			throw te;
		}

	}
	public static List<Comment> constructComment(Response res) throws WeiboException {
		try {
			JSONArray list = res.asJSONArray();
			int size = list.length();
			List<Comment> comments = new ArrayList<Comment>(size);
			for (int i = 0; i < size; i++) {
				comments.add(new Comment(list.getJSONObject(i)));
			}
			return comments;
		}catch (JSONException jsone) {
			throw new WeiboException(jsone);
		} catch (WeiboException te) {
			throw te;
		}
	}
	public Date getCreatedAt() {
		return createdAt;
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getSource() {
		return source;
	}

	public Comment getReplycomment() {
		return replycomment;
	}

	public User getUser() {
		return user;
	}

	public Status getStatus() {
		return status;
	}

	public Comment getComments() {
		return comments;
	}

	public void setComments(Comment comments) {
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Comment{" +
		"createdAt=" + createdAt +
		", id=" + id +
		", text='" + text + '\'' +
		", source='" + source + '\'' +
		", user=" + user +
		", status=" + status +
		'}';
	}
}
