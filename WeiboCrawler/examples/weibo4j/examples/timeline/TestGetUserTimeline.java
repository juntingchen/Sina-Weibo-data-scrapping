package weibo4j.examples.timeline;
import java.util.List;

import weibo4j.Timeline;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

public class TestGetUserTimeline {
	
	
	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		String access_token = "2.00btx2QCJNQayCde585a562di83J9E";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		Timeline tm = new Timeline();
		try {
			List<Status> status = tm.getUserTimeline(access_token);
			for(Status s : status){
				Log.logInfo(s.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}*/

	public static void main(String[] args) {
		String access_token = "2.00btx2QCJNQayCde585a562di83J9E";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		String uid = "2073922171";
		String screen_name = "DreamsRunner";
		Integer count = 50;
		Paging page=null;
		Integer base_app = 0;
		Integer feature = 0;
		Timeline tm = new Timeline();
		try {
			
		//	getUserTimeline(String uid, String screen_name,
		//			Integer count, Paging page, Integer base_app, Integer feature)
			List<Status> status = tm.getUserTimeline(uid, screen_name,
								count, page, base_app,feature);
			int total = status.size();
			
			for(Status s : status){
				Log.logInfo(s.toString());
			}
			String str;
			str = "" + total;
			System.out.println(str);
			
			
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
