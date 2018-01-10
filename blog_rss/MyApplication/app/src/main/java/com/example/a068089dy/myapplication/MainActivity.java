package com.example.a068089dy.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a068089dy.myapplication.listview.friend;
import com.example.a068089dy.myapplication.listview.friendAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SwipeRefreshLayout.OnRefreshListener{
    private List<friend> friendList = new ArrayList<friend>();
    private SwipeRefreshLayout swipeLayout;
    private friendAdapter adapter;
    private boolean isrefreshfinish  = false;
    private String server_ip = "123.207.174.226";
    private int server_port = 8887;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String data = b.getString("data");
            Log.d("handledata",data);

            if (!data.equals("")) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
                friend newuser = new friend(data,data);
                friendList.add(newuser);
            }else{
                Toast.makeText(MainActivity.this, "没有数据更新！", Toast.LENGTH_LONG).show();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initdata();
        adapter = new friendAdapter(MainActivity.this,R.layout.friend_item,friendList);
        final ListView friendListView = (ListView) findViewById(R.id.friend_listview);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,friendstr);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(MainActivity.this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
        friendListView.setAdapter(adapter);
        //注册监听器
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){

                friend frid = friendList.get(position);
                Toast.makeText(MainActivity.this,frid.getName(),Toast.LENGTH_LONG).show();

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initdata(){
        friend user1 = new friend("user1","artical");
        friendList.add(user1);
        friend user2 = new friend("user1","artical");
        friendList.add(user2);
    }
    /*
    * 下拉刷新
    * */
    public void onRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                new NetThread().start();
                if(isrefreshfinish) {
                    Thread.interrupted();
                    Log.d("refresh","interrupted");
                }
            }
        },0);
    }

    /*
    *
    * 网络线程内部类
    * */
   class NetThread extends Thread{
        String server_ip = "123.207.174.226";
        int server_port = 8888;
        String data = "asd";
        public NetThread(){

        }

        public void run(){

            try {

                Socket socket = new Socket(server_ip, server_port);
                Log.d("server","start");
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("refresh");
                out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if(data.equals("")){
                    Log.d("data", "nothing");
                }
                else
                {
                    //接受数据处理

                    data = in.readLine();
                    Log.d("data", data);
                    //Toast.makeText(MainActivity.this,data,Toast.LENGTH_LONG).show();
                    //从消息池中取出一个message
                    Message msg = handler.obtainMessage();
                    //Bundle是message中的数据
                    Bundle b = new Bundle();
                    b.putString("data",data);
                    msg.setData(b);
                    //传递数据
                    handler.sendMessage(msg); // 向Handler发送消息,更新UI
                    Log.d("msgsend",msg.toString());
                }
                in.close();
                socket.close();
                out.close();
            }
            catch(Exception e){
                Log.d("error",e.toString());
            }
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /*
    * 主菜单
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
    *
    * 菜单
    * */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*
    * 侧滑菜单
    * */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this,settingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this,AddrssActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_web) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://blog.yoitsu.moe/"));
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
