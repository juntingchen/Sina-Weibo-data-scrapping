package weibo4j.examples.trends;

import weibo4j.Trend;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.WeiboException;

public class TrendDestroy {

	/**
	 * @param args
	 * @throws WeiboException 
	 */
	public static void main(String[] args){
		String access_token = args[0];
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		Trend tm = new Trend();
		int trendId = 7256671;
		try {
			boolean result = tm.trendsDestroy(trendId);
			Log.logInfo(String.valueOf(result));
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
