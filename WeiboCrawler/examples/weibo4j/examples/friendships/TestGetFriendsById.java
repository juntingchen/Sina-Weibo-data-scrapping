package weibo4j.examples.friendships;

import java.util.List;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.model.Paging;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class TestGetFriendsById {
	
	
	public static void main(String[] args) {
		String access_token = "2.00btx2QCJNQayCde585a562di83J9E";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		String id = "2073922171";
		Friendships fm = new Friendships();
		try {
			
			//getFriendsIds(String uid, Integer count, Integer cursor)
			//getFriendsByID(String id, Integer count, Integer cursor)
			/*String[] users = fm.getFriendsIds(id, 200, 0);
			for(String user : users){
				System.out.println(user.toString());
			}*/
			List<User> users = fm.getFriendsByID(id, 200, 0);
			for(User user : users){
				System.out.println(user.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
