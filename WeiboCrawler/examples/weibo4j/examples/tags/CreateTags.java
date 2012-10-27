package weibo4j.examples.tags;

import java.util.List;

import weibo4j.Tags;
import weibo4j.Weibo;
import weibo4j.examples.Log;
import weibo4j.model.Tag;
import weibo4j.model.WeiboException;

public class CreateTags {

	/**
	 * @param args
	 * @throws WeiboException 
	 */
	public static void main(String[] args){
		String access_token = args[0];
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		String tag = args[1];
		Tags tm  = new Tags();
		List<Tag> tags= null;
		try {
			tags = tm.createTags(tag);
			for(Tag t : tags){
				Log.logInfo(t.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
