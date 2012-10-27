package weibo4j.examples.friendships;

import java.util.List;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class GetFollowersById {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String access_token = args[0];
		String access_token = "2.00btx2QCJNQayCde585a562di83J9E";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		//String uid = args[1];
		String uid = "2073922171";
		Friendships fm = new Friendships();
		try {
			List<User> users = fm.getFollowersById(uid);
			for(User u : users){
				Log.logInfo(u.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
