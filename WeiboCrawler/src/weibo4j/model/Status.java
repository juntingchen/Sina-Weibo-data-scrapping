/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package weibo4j.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import weibo4j.http.Response;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * A data class representing one single status of a user.
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class Status extends WeiboResponse implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8795691786466526420L;

	private User user = null;                            //作者信息
	private long idstr;                                  //保留字段，请勿使用                     
	private Date createdAt;                              //status创建时间
	private String id;                                     //status id
	private String text;                                 //微博内容
	private Source source;                               //微博来源
	private boolean favorited;                           //是否已收藏
	private long inReplyToStatusId;                      //回复ID
	private long inReplyToUserId;                        //回复人ID
	private String inReplyToScreenName;                  //回复人昵称
	private String thumbnailPic;                        //微博内容中的图片的缩略地址
	private String bmiddlePic;                          //中型图片
	private String originalPic;                         //原始图片
	private Status retweetedStatus = null;              //转发的博文，内容为status，如果不是转发，则没有此字段
	private String geo;                                  //地理信息，保存经纬度，没有时不返回此字段
	private double latitude = -1;                        //纬度
	private double longitude = -1;                       //经度
	private int repostsCount;                           //转发数
	private int commentsCount;                          //评论数
	private String mid;                                  //微博MID
	private String annotations;                          //元数据，没有时不返回此字段
	private String reposts;
	private String statuses;
	public Status()
	{

	}
	public Status(Response res)throws WeiboException{
		super(res);
		JSONObject json=res.asJSONObject();
		constructJson(json);
	}

	private void constructJson(JSONObject json) throws WeiboException {
		try {
			idstr = json.getLong("idstr");
			id = json.getString("id");
			text = json.getString("text");
			source = new Source(json.getString("source"));
			createdAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
			inReplyToStatusId = getLong("in_reply_to_status_id", json);
			inReplyToUserId = getLong("in_reply_to_user_id", json);
			favorited = getBoolean("favorited", json);
			thumbnailPic = json.getString("thumbnail_pic");
			bmiddlePic = json.getString("bmiddle_pic");
			originalPic = json.getString("original_pic");
			repostsCount = json.getInt("reposts_count");
			commentsCount = json.getInt("comments_count");
			annotations = json.getString("annotations");
			if(!json.isNull("user"))
				user = new User(json.getJSONObject("user"));
			inReplyToScreenName=json.getString("inReplyToScreenName");
			if(!json.isNull("retweeted_status")){
				retweetedStatus= new Status(json.getJSONObject("retweeted_status"));
			}
			mid=json.getString("mid");
			geo= json.getString("geo");
			if(geo!=null &&!"".equals(geo) &&!"null".equals(geo)){
				getGeoInfo(geo);
			}
		} catch (JSONException je) {
			throw new WeiboException(je.getMessage() + ":" + json.toString(), je);
		}
	}

	private void getGeoInfo(String geo) {
		StringBuffer value= new StringBuffer();
		for(char c:geo.toCharArray()){
			if(c>45&&c<58){
				value.append(c);
			}
			if(c==44){
				if(value.length()>0){
					latitude=Double.parseDouble(value.toString());
					value.delete(0, value.length());
				}
			}
		}
		longitude=Double.parseDouble(value.toString());
	}


	public Status(JSONObject json)throws WeiboException, JSONException{
		constructJson(json);
	}
	public Status(String str) throws WeiboException, JSONException {
		// StatusStream uses this constructor
		super();
		JSONObject json = new JSONObject(str);
		constructJson(json);
	}

	public User getUser() {
		return user;
	}
	public long getIdstr() {
		return idstr;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public String getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public Source getSource() {
		return source;
	}
	public boolean isFavorited() {
		return favorited;
	}
	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}
	public long getInReplyToUserId() {
		return inReplyToUserId;
	}
	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}
	public String getThumbnailPic() {
		return thumbnailPic;
	}
	public String getBmiddlePic() {
		return bmiddlePic;
	}
	public String getOriginalPic() {
		return originalPic;
	}
	public Status getRetweetedStatus() {
		return retweetedStatus;
	}
	public String getGeo() {
		return geo;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public int getRepostsCount() {
		return repostsCount;
	}
	public int getCommentsCount() {
		return commentsCount;
	}
	public String getMid() {
		return mid;
	}
	public String getAnnotations() {
		return annotations;
	}


	public String getReposts() {
		return reposts;
	}

	public void setReposts(String reposts) {
		this.reposts = reposts;
	}


	public String getStatuses() {
		return statuses;
	}

	public void setStatuses(String statuses) {
		this.statuses = statuses;
	}

	/*package*/
	public static List<Status> constructStatuses(Response res) throws WeiboException {
		try {
			JSONObject json=res.asJSONObject();
			JSONArray list = null;
			if(!json.isNull("reposts")){
				list = res.asJSONObject().getJSONArray("reposts");
			}
			else if(!json.isNull("statuses")){
				list = res.asJSONObject().getJSONArray("statuses");
			}else{				
				list = res.asJSONArray();
			}
			int size = list.length();
			List<Status> statuses = new ArrayList<Status>(size);

			for (int i = 0; i < size; i++) {
				statuses.add(new Status(list.getJSONObject(i)));
			}
			return statuses;
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		} catch (WeiboException te) {
			throw te;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Status other = (Status) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Status [user=" + user + 
		", idstr=" + idstr + 
		", createdAt=" + createdAt + 
		", id=" + id + 
		", text=" + text + 
		", source=" + source + 
		", favorited=" + favorited + 
		", inReplyToStatusId=" + inReplyToStatusId + 
		", inReplyToUserId=" + inReplyToUserId + 
		", inReplyToScreenName=" + inReplyToScreenName + 
		", thumbnail_pic=" + thumbnailPic + 
		", bmiddle_pic=" + bmiddlePic + 
		", original_pic=" + originalPic + 
		", retweeted_status=" + retweetedStatus + 
		", geo=" + geo + 
		", latitude=" + latitude + 
		", longitude=" + longitude + 
		", reposts_count=" + repostsCount + 
		", comments_count=" + commentsCount + 
		", mid=" + mid + 
		", annotations=" + annotations + 
		"]";
	}




}
