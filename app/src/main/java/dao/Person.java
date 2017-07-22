/**
 *
 */
package dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import dao.Book;

/**
 * @author Cyclops-THSS
 *
 */
public class Person {
    final private HttpClient client;

    public String id;
    public String password;
    public String email;
    public String userName;

    private Vector<String> followerIds = null;
    private Vector<String> followingIds = null;
    private Vector<String> likeBookIds = null;

    public boolean isLoggedin = false;
    public boolean isRegistered = false;

    final private Map<String, Book> bookCache;

    public static void main(String[] args) {
        Person p = new Person("aaaaa");
        p.register("pass", "a@b.com", new Callback<Person>() {

            @Override
            public void handle(Person val) {
                if (val.isRegistered) {
                    val.login(new Callback<Person>() {

                        @Override
                        public void handle(Person val) {
                            if (val.isLoggedin) {
                                val.getFollowers(new Callback<Vector>() {

                                    @Override
                                    public void empty(Vector val) {
                                        System.out.println("hit");
                                    }

                                });
                            }
                        }

                    });
                }
            }

        });
    }

    public Person(String username) {
        client = new HttpClient();
        userName = username;
        bookCache = new HashMap<String, Book>();
    }

    public Person(Map map) {
        client = null;
        bookCache = null;
        this.loadMap(map);
    }

    private void loadMap(Map map) {
        if (map.isEmpty()) {
            return;
        }
        // Followers
        Map followersMap = null, itemMap = null;
        if (!map.get("followers").equals("")) {
            followersMap = (Map) map.get("followers");
            if (null != followersMap.getOrDefault("item", null)) {
                itemMap = (Map) followersMap.get("item");
                if (itemMap.get("follower__id") instanceof String) {
                    this.followerIds = new Vector();
                    this.followerIds.add((String) itemMap.get("follower__id"));
                } else {
                    this.followerIds = (Vector) itemMap.get("follower__id");
                }
            }
        }
        // Followings
        Map followingsMap = null;
        if (!map.get("followings").equals("")) {
            followingsMap = (Map) map.get("followings");
            if (null != followingsMap.getOrDefault("item", null)) {
                itemMap = (Map) followingsMap.get("item");
                if (itemMap.get("following__id") instanceof String) {
                    this.followingIds = new Vector();
                    this.followingIds.add((String) itemMap.get("following__id"));
                } else {
                    this.followingIds = (Vector) itemMap.get("following__id");
                }
            }
        }
        // Likes
        Map likesMap = null;
        if (!map.get("likes").equals("")) {
            likesMap = (Map) map.get("likes");
            if (null != (Map) likesMap.getOrDefault("item", null)) {
                itemMap = (Map) likesMap.get("item");
                if (itemMap.get("book__id") instanceof String) {
                    likeBookIds = new Vector();
                    likeBookIds.add((String) itemMap.get("book__id"));
                } else {
                    likeBookIds = (Vector) itemMap.get("book__id");
                }
            }
        }
        // Basics
        this.userName = (String) map.get("username");
        this.id = (String) map.get("id");
        this.email = (String) map.get("email");
    }

    private Person getThis() {
        return this;
    }

    public void register(String _password, String _email, Callback<Person> _callback) {
        if (this.isRegistered) {
            return;
        }
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("email", _email);
        payload.put("username", this.userName);
        payload.put("password", _password);
        this.client.post("/api/u/register", payload, new Callback<Object>() {
            @Override
            public void empty(Object nul) {
                password = _password;
                email = _email;
                isRegistered = true;
                Callback.call(_callback, getThis());
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void resetPassword(String _password, String new_password, Callback<Person> _callback) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("username", this.userName);
        payload.put("password", _password);
        payload.put("new_password", new_password);
        this.client.post("/api/u/reset", payload, new Callback<Object>() {
            @Override
            public void empty(Object nul) {
                password = new_password;
                Callback.call(_callback, getThis());
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void canRegister(Callback<Boolean> _callback) {
        if (this.isLoggedin || this.isRegistered) {
            Callback.call(_callback, true);
        }
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("username", this.userName);
        this.client.get("/api/u/check", payload, new Callback<Object>() {
            @Override
            public void empty(Object val) {
                Callback.call(_callback, false);
            }

            @Override
            public void error(String msg) {
                Callback.call(_callback, true);
            }
        });
    }

    public void checkLoggedin(Callback<Boolean> _callback) {
        this.client.get("/api/u/", null, new Callback<Object>() {
            @Override
            public void handle(Object val) {
                getThis().isLoggedin = true;
                Callback.call(_callback, true);
            }

            @Override
            public void error(String msg) {
                getThis().isLoggedin = false;
                Callback.call(_callback, false);
            }
        });
    }

    public void login(Callback<Person> _callback) {
        if (this.isLoggedin) {
            return;
        }
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("username", this.userName);
        payload.put("password", this.password);
        //this.client.get("/api/b/detail", payload, new Callback<Object>() {
        this.client.post("/api/u/login", payload, new Callback<Object>() {
            @Override
            public void handle(Object nul){
                System.out.println("log in handle");
            }
            @Override
            public void empty(Object nul) {
                System.out.println("log in successfully");
                getThis().isLoggedin = true;
                getThis().isRegistered = true;
                Callback.call(_callback, getThis());
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void logout(Callback<Person> _callback) {
        if (!this.isLoggedin) {
            return;
        }
        this.client.post("/api/u/logout", null, new Callback<Object>() {
            @Override
            public void empty(Object nul) {
                getThis().isLoggedin = false;
                Callback.call(_callback, getThis());
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void follow(String id, Callback<Person> _callback) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("id", id);
        this.client.post("/api/u/follow", payload, new Callback<Object>() {
            @Override
            public void empty(Object nul) {
                Callback.call(_callback, getThis());
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void likeBook(Book b, Callback _callback) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("id", b.id);
        this.client.post("/api/b/like", payload, new Callback<Object>() {
            @Override
            public void empty(Object nul) {
                Callback.call(_callback, null);
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void getFollowers(Callback<Vector> _callback) {
        this.client.get("/api/u/", null, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                Vector followers = null;
                if (!val.isEmpty() && !val.get("followers").equals("")) {
                    Map followersMap = null, itemMap = null;
                    followersMap = (Map) val.get("followers");
                    if (null != followersMap.getOrDefault("item", null)) {
                        itemMap = (Map) followersMap.get("item");
                        if (itemMap.get("follower__id") instanceof String) {
                            followers = new Vector();
                            followers.add(itemMap.get("follower__id"));
                        } else {
                            followers = (Vector) itemMap.get("follower__id");
                        }
                    }
                }
                Callback.call(_callback, followers);
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void getFollowings(Callback<Vector> _callback) {
        this.client.get("/api/u/", null, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                Vector followings = null;
                if (!val.isEmpty() && !val.get("followings").equals("")) {
                    System.out.println("following非空");
                    Map followingsMap = null, itemMap = null;
                    followingsMap = (Map) val.get("followings");
                    if (followingsMap.getOrDefault("item", null)!= null) {
                        itemMap = (Map) followingsMap.get("item");
                        System.out.println("给itemMap的"+followingsMap.get("item"));
                        if (itemMap.get("following_id") instanceof String) {
                            followings = new Vector();
                            followings.add(itemMap.get("following_id"));
                            System.out.println("添加到返回的vector的following的id"+itemMap.get("following_id"));
                        } else {
                            followings = (Vector) itemMap.get("following_id");
                            System.out.println("不是string的时候添加到返回的vector的following的id"+itemMap.get("following_id"));
                        }
                    }
                }
                Callback.call(_callback, followings);
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void getLikeBooks(Callback<Vector> _callback) {
        this.client.get("/api/u/", null, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                Vector likes = null;
                if (!val.isEmpty() && !val.get("likes").equals("")) {
                    Map likesMap = null, itemMap = null;
                    likesMap = (Map) val.get("likes");
                    if (null != (Map) likesMap.getOrDefault("item", null)) {
                        itemMap = (Map) likesMap.get("item");

                        if (itemMap.get("book__id") instanceof String) {
                            likes = new Vector();
                            likes.add(itemMap.get("book__id"));
                        } else {
                            likes = new Vector();
                            likes = (Vector) itemMap.get("book__id");
                        }
                    }
                }
                Callback.call(_callback, likes);
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void getPersonByName(String name, Callback<Vector> _callback) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("q", name);
        this.client.get("/api/u/search", payload, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                if (val.isEmpty()) {
                    return;
                }
                Vector<Person> persons = null;
                Map itemMap = null;
                if (null != val.getOrDefault("item", null)) {
                    itemMap = (Map) val.get("item");
                    persons = new Vector<Person>();
                    if (itemMap instanceof Map) {
                        persons.add(new Person(itemMap));
                    } else {
                        for (Map map : (Vector<Map>) itemMap) {
                            persons.add(new Person(map));
                        }
                    }
                }
                Callback.call(_callback, persons);
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void getPersonById(String id, Callback<Person> _callback) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("id", id);
        this.client.get("/api/u/get", payload, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                Callback.call(_callback, new Person(val));
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void getFollowCount(Callback<Map> _callback) {
        this.client.get("/api/u/count", null, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                Callback.call(_callback, val);
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void getBookById(String id, Callback<Book> _callback) {
        if (bookCache.entrySet().contains(id)) {
            Book b = bookCache.get(id);
            System.out.println("cache有"+id);
            Callback.call(_callback, bookCache.get(id));
        } else {
            Map<String, String> payload = new HashMap<String, String>();
            payload.put("id", id);
            System.out.println("cache没有"+id);
            this.client.get("/api/b/detail", payload, new Callback<Map>() {
                @Override
                public void handle(Map val) {
                    Book b = new Book(val);
                    bookCache.put(id, b);
                    Callback.call(_callback, b);
                }
                @Override
                public void error(String msg) {
                    System.out.println("errrrrrrrrrrrr");
                    _callback.error(msg);
                }
            });
        }
    }

    public void getBooksOfRank(String web, int type, int freq, Callback<Map> _callback) {
        Map payload = new HashMap<String, String>();
        payload.put("rankWeb", web);
        payload.put("rankType", String.valueOf(type));
        payload.put("rankFreq", String.valueOf(freq));
        this.client.get("/api/b/rankitems", payload, new Callback<Object>() {
            @Override
            public void handle(Object val) {
                Map idRank = new HashMap<Integer, String>();
                if (val instanceof Map) {
                    System.out.println("val is map");
                    if(((Map) val).get("value")!=null&&((Map) val).get("book__id")!=null)
                        idRank.put( ((Map) val).get("value"), ((Map) val).get("book__id"));
                    else if (((Map) val).get("value")==null)
                        System.out.println("mmp value");
                    else if(((Map) val).get("book__id")==null)
                        System.out.println("mmp bookid");
                } else {
                    for (Map map : (Vector<Map>) val) {
                        idRank.put(((Map) map).get("value"), ((Map) map).get("book__id"));
                        System.out.println("val is notmap or null");
                    }
                }
                for(Object tempid : idRank.values()) {
                    if(tempid!=null){
                    String id = tempid.toString();
                    System.out.println("tempid in person = "+id);}
                    else
                        System.out.println("fuck");}
                Callback.call(_callback, idRank);
            }

            @Override
            public void empty(Object val) {
                Callback.call(_callback, null);
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

    public void getBookByName(String _name, Callback<Vector> _callback) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("q", _name);
        this.client.get("/api/b/search", payload, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                Vector result = new Vector<String>();
                if (val instanceof Map) {
                    result.add(val.get("id"));
                } else {
                    for (Map map : (Vector<Map>) val) {
                        result.add(val.get("id"));
                    }
                }
                Callback.call(_callback, result);
            }

            @Override
            public void empty(Map val) {
                Callback.call(_callback, null);
            }

            @Override
            public void error(String msg) {
                _callback.error(msg);
            }
        });
    }

}