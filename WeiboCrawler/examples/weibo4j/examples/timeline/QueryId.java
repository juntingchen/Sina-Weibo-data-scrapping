package weibo4j.examples.timeline;

import weibo4j.Timeline;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.WeiboException;

public class QueryId {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = args[0];
		String mid = args[1];
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		Timeline tm = new Timeline();		
		try {
			String id = tm.QueryId( mid, 1);
				Log.logInfo(String.valueOf(id));			
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
