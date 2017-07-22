/**
 *
 */
package dao;

import java.util.AbstractMap;
import java.io.IOException;
import java.lang.Thread;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Uniform;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;
import org.restlet.representation.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Cyclops-THSS
 *
 */
@SuppressWarnings("deprecation")
public class HttpClient {
	// rstrip slash
	private static final String UrlPrefix = "http://166.111.17.69:8080";
	private static Series<CookieSetting> cookies = null;

	public static class MapEntryConverter implements Converter {

		public boolean canConvert(Class clazz) {
			return AbstractMap.class.isAssignableFrom(clazz);
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			AbstractMap map = (AbstractMap) value;
			for (Object obj : map.entrySet()) {
				Map.Entry entry = (Map.Entry) obj;
				writer.startNode(entry.getKey().toString());
				Object val = entry.getValue();
				if (null != val) {
					writer.setValue(val.toString());
				}
				writer.endNode();
			}
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			Map<String, Object> map = new HashMap<String, Object>();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				if (reader.hasMoreChildren()) {
					Map<String, Object> childMap = (Map<String, Object>) context
							.convertAnother(new HashMap<String, Object>(), Map.class);
					map.put(reader.getNodeName(), childMap);
				} else {
					if (map.containsKey(reader.getNodeName())) {
						if (map.get(reader.getNodeName()) instanceof Vector<?>) {
							((Vector) map.get(reader.getNodeName())).add(reader.getValue());
						} else {
							Vector ls = new Vector();
							ls.add((String) map.get(reader.getNodeName()));
							ls.add(reader.getValue());
							map.put(reader.getNodeName(), ls);
						}
					} else {
						map.put(reader.getNodeName(), reader.getValue());
					}
				}
				reader.moveUp();
			}
			return map;
		}
	}

	public boolean get(String uri, Map<String, String> payload, Callback callback) {
		XStream xs = new XStream(new DomDriver());
		xs.registerConverter(new MapEntryConverter());
		xs.alias("root", Map.class);
		try {
			System.out.println("try get start");
            System.out.println("进入client resource");

			ClientResource cr = new ClientResource(Method.GET, UrlPrefix + uri);
            if (payload != null) {
                for (Map.Entry<String, String> entry: payload.entrySet()) {
                    String name = entry.getKey(), value = entry.getValue();
                    cr.addQueryParameter(name, value);
                }
            }

			//cr.getRequest().setEntity(xs.toXML(payload), MediaType.APPLICATION_XML);
			System.out.println("getRequest()过后");
			if (cookies != null) {
				Series<Cookie> co = new Series<Cookie>(Cookie.class);
				for (CookieSetting cs : cookies) {
					co.add(cs);
				}
				cr.getRequest().setCookies(co);
			}
			System.out.println("try get start --> end");
			new Thread(() -> {
				System.out.println("new Thread get");
				cr.get();
				Response resp = cr.getResponse();
				System.out.println("new Thread ");
				if (callback != null) {
					Object data = null;
					System.out.println("Call back is not null ");
					if (resp.getEntityAsText() != null) {
						Map response = (Map) xs.fromXML(resp.getEntityAsText());

                        System.out.println("response是啥"+resp.getEntityAsText());

						if (response == null || !response.get("code").equals("0")) {
							System.out.println("response code equals 0 ");
							callback.error((String) response.getOrDefault("msg", "Unknown Error"));
							return;
						}
						if (!response.get("data").toString().isEmpty()) {
							System.out.println("get response data");
							data = response.get("data");
						}
					}
					callback.run(data);
					try {
						resp.getEntity().exhaust();
						resp.getEntity().release();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (ResourceException e) {
			if (callback != null) {
				callback.error(e.getMessage());
			}
			return false;
		}
		return true;
	}

	public boolean post(String uri, Map<String, String> payload, Callback callback) {
		XStream xs = new XStream(new DomDriver());
		xs.registerConverter(new MapEntryConverter());
		xs.alias("root", Map.class);
		try {
			ClientResource cr = new ClientResource(Method.POST, UrlPrefix + uri);
			if (cookies != null) {
				Series<Cookie> co = new Series<Cookie>(Cookie.class);
				for (CookieSetting cs : cookies) {
					co.add(cs);
				}
				cr.getRequest().setCookies(co);
			}
			new Thread(() -> {
				cr.post(xs.toXML(payload), MediaType.APPLICATION_XML);
				Response resp = cr.getResponse();
				if (null != resp.getCookieSettings()) {
					cookies = resp.getCookieSettings();
				}
				if (callback != null) {
					Object data = null;
					if (resp.getEntityAsText() != null) {
						Map response = (Map) xs.fromXML(resp.getEntityAsText());
						if (response == null || !response.get("code").equals("0")) {
							callback.error((String) response.getOrDefault("msg", "Unknown Error"));
							return;
						}
						if (!response.get("data").toString().isEmpty()) {
							data = response.get("data");
						}
					}
					callback.run(data);
					try {
						resp.getEntity().exhaust();
						resp.getEntity().release();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (ResourceException e) {
			if (callback != null) {
				callback.error(e.getMessage());
			}
			return false;
		}
		return true;
	}
}
