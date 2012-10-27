package weibo4j.examples.user;

import weibo4j.Users;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class ShowUser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String access_token = args[0];
		String access_token = "2.00btx2QCJNQayC1f21dcfd000iLdVt"; 
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		//String uid = args[1];
		//String uid = "2073922171";
		String screen_name = "老挺Ah";
		Users um = new Users();
		try {
			//User user = um.showUserById(uid);
			User user = um.showUser(screen_name);
			Log.logInfo(user.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
