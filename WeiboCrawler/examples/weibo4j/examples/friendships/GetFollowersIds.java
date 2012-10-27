package weibo4j.examples.friendships;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.WeiboException;

public class GetFollowersIds {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = "2.00btx2QCJNQayCde585a562di83J9E";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		String uid = "2073922171";
		Friendships fm = new Friendships();
		try {
			String[] ids = fm.getFollowersIdsById(uid);
			for(String u : ids){
				Log.logInfo(u.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
