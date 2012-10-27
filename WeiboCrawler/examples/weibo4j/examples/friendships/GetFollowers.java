package weibo4j.examples.friendships;

import java.util.List;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class GetFollowers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = args[0];
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		Friendships fm = new Friendships();
		String screen_name = args[1];
		try {
			List<User> users = fm.getFollowersByName(screen_name);
			for(User u : users){
				Log.logInfo(u.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
