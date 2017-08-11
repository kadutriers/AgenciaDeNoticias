package com.wordpress.agenciadenoticiasblog.adn;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnNoticiaListener {

    static final String URL = "https://public-api.wordpress.com/rest/v1.1/sites/agenciadenoticiasblog.wordpress.com/posts/";
    ListView listView;
    NoticiaAdapter adapter;
    ArrayList<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new NoticiaAdapter(getApplicationContext(),R.layout.layout_post_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post =  posts.get(position);

                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("content", post.content);

                startActivity(intent);
            }
        });
        NoticiaTask task = new NoticiaTask(this);
        task.execute(URL);
    }

    @Override
    public void OnNoticia(JSONArray array) {

        posts = Post.parse(array);
        adapter.addAll(posts);
        adapter.notifyDataSetChanged();
    }

    public class NoticiaTask extends AsyncTask<String,Void, JSONArray>{

        private OnNoticiaListener listener;

        public NoticiaTask(OnNoticiaListener listener){
            this.listener = listener;
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String url = params[0];
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(url).build();

            try {
                Response response = client.newCall(request).execute();
                String json = response.body().string();

                try {
                    JSONObject object = new JSONObject(json);
                    JSONArray array = object.optJSONArray("posts");
                    return array;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            super.onPostExecute(array);
            if (null == array)
                return;

            if (null != listener)
                listener.OnNoticia(array);
        }
    }

    public class NoticiaAdapter extends ArrayAdapter<Post>{

        private int resource;

        public NoticiaAdapter(Context context, int resource) {
            super(context, resource);
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (null == convertView){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(resource, null);
            }

            ViewHolder vh;
            if(null != convertView.getTag()){
                vh = (ViewHolder) convertView.getTag();
            }
            else {
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }

            Post post = getItem(position);
            vh.title.setText(post.title);
            vh.desc.setText(post.description);
            String url = post.thumbnail;
            Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(vh.thumbnail);
            return convertView;
        }

        private class ViewHolder{

            public TextView title;
            public TextView desc;
            public ImageView thumbnail;

            public ViewHolder(View view){

                title = (TextView) view.findViewById(R.id.title);
                desc = (TextView) view.findViewById(R.id.description);
                thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            }
        }
    }
}
