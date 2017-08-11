package com.wordpress.agenciadenoticiasblog.adn;

import android.text.Html;
import android.text.Spanned;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class Post{
    public String title;
    public String description;
    public String thumbnail;
    public String content;

    public static Post parse(JSONObject object){

        Post post = new Post();
        post.title = object.optString("title");
        post.description = Html.fromHtml(object.optString("excerpt")).toString();
        post.thumbnail = object.optString("featured_image");
        post.content = object.optString("content");

        return post;
    }

    public static ArrayList<Post> parse(JSONArray array){

        ArrayList<Post> posts = new ArrayList<>();

        int length = array.length();
        for (int i = 0; i < length; i++){
            JSONObject object = array.optJSONObject(i);
            Post post = Post.parse(object);
            posts.add(post);
        }
        return posts;
    }
}