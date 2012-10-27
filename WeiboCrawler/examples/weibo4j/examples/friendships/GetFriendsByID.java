package weibo4j.examples.friendships;

import java.util.List;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class GetFriendsByID {

	public static void main(String[] args) {
		String access_token = "2.00btx2QCJNQayCde585a562di83J9E";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		String id = "1883283154";
		Friendships fm = new Friendships();
		try {
			List<User> users = fm.getFriendsByID(id, 10, 2);
			for(User user : users){
				System.out.println(user.toString());
			}
			
			System.out.println("Count: " + users.size());
			
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	

	}

}
