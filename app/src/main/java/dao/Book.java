
package dao;

/**
 * @author thuqianwei
 * 1212
 *
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.List;
import dao.Callback;

public class Book {

	public class Spec {
		public float price;
		public String web;
		public String url;
		public String intro;
		public int star;

		public Spec(Map map) {
			price = Float.parseFloat((String) map.get("price"));
			web = (String) map.get("web");
			url = (String) map.get("url");
			intro = (String) map.get("intro");
			star = Integer.parseInt((String) map.get("star"));
		}
	}

	public class Tag {
		public String name;
		public int level;

		public Tag(Map map) {
			name = (String) map.get("tag__name");
			level = Integer.parseInt((String) map.get("level"));
		}
	}

	public class Rank {
		public String web;
		public String type;
		public String freq;
		public int value;

		public Rank(Map map) {
			web = (String) map.get("web");
			type = (String) map.get("type");
			freq = (String) map.get("freq");
			value = Integer.parseInt((String) map.get("value"));
		}
	}
	final private HttpClient client;

	public String id;
	public String isbn;
	public String imageUrl;
	public String title;
	public String author;
	public int likeCount;
	public List<Spec> specs = null;
	public List<Tag> tags = null;
	public List<String> likes = null;
	public List<Rank> ranks = null;

	public Book(Map map) {
		client = new HttpClient();
		id = (String) map.get("id");
		isbn = (String) map.get("ISBN");
		imageUrl = (String) map.get("image");
		title = (String) map.get("title");
		author = (String) map.get("author");
		//likeCount = Integer.parseInt((String) map.get("likes_count"));
        likeCount = Integer.parseInt((String) map.get("likedby"));

		if (null != map.getOrDefault("specs", null)) {
			this.loadSpecs((Map) map.get("specs"));
		}
		if (null != map.getOrDefault("tags", null)) {
			//this.loadTags((Map) map.get("tags"));
		}
		if (null != map.getOrDefault("likes", null)) {
			//this.loadLikes((Map) map.get("likes"));
		}
		if (null != map.getOrDefault("ranks", null)) {
			this.loadRanks((Map) map.get("ranks"));
		}
	}

	private void loadTags(Map obj) {
		if (null == obj || null == obj.getOrDefault("item", null))
			return;
		Object itemMap = obj.get("item");
		this.tags = new Vector<Tag>();
		if (itemMap instanceof Map) {
			this.tags.add(new Tag((Map) itemMap));
		} else {
			for (Map map : (Vector<Map>) itemMap) {
				this.tags.add(new Tag(map));
			}
		}
	}

	private void loadSpecs(Map obj) {
		if (null == obj || null == obj.getOrDefault("item", null))
			return;
		Object itemMap = obj.get("item");
		this.specs = new Vector<Spec>();
		if (itemMap instanceof Map) {
			this.specs.add(new Spec((Map) itemMap));
		} else {
			for (Map map : (Vector<Map>) itemMap) {
				this.specs.add(new Spec(map));
			}
		}
	}

	private void loadLikes(Map obj) {
		if (null == obj || null == obj.getOrDefault("item", null)) {
			return;
		}
		Object itemMap = obj.get("item");
		this.likes = new Vector<String>();
		if (itemMap instanceof Map) {
			this.likes.add((String) ((Map) itemMap).get("user__id"));
		} else {
			for (Map map : (Vector<Map>) itemMap) {
				this.likes.add((String) ((Map) itemMap).get("user__id"));
			}
		}
	}

	private void loadRanks(Map obj) {
		if (null == obj || null == obj.getOrDefault("item", null)) {
			return;
		}
		Object itemMap = obj.get("item");
		this.ranks = new Vector<Rank>();
		if (itemMap instanceof Map) {
			this.ranks.add(new Rank((Map) itemMap));
		} else {
			for (Map map : (Vector<Map>) itemMap) {
				this.ranks.add(new Rank(map));
			}
		}
	}

	public void getLikeCount(Callback<Integer> _callback) {
		//this.client.get("/api/b/count", null, new Callback<String>() {
		//@Override
		//public void handle(String val) {
		Callback.call(_callback, this.likeCount);
		//}

		//@Override
		//public void error(String msg) {
		//	_callback.error(msg);
		//}
		//});
	}

	public void getWebUrls(Callback<Map> _callback) {
		Map<String, String> urls = new HashMap<String, String>();
		for(int i=0;i<this.specs.size();i++){
			urls.put(specs.get(i).web, specs.get(i).url);
		}
	}

	public void getPrices(Callback<Map> _callback) {
		Map<String, Float> prices = new HashMap<String, Float>();
		for(int i=0;i<this.specs.size();i++){
			prices.put(specs.get(i).web, specs.get(i).price);
		}
	}

	public void getTags(Callback<List> _callback) {
		Callback.call(_callback, this.tags);
	}

}