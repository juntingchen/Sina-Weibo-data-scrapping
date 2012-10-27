package weibo4j.examples.friendships;

import java.util.List;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class GetFollowsActive {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = args[0];
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		String uid = args[1];
		Friendships fm = new Friendships();
		try {
			List<User> users = fm.getFollowersActive(uid);
			for(User u : users){
				Log.logInfo(u.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
