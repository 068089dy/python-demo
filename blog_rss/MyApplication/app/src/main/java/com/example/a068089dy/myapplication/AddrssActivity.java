package com.example.a068089dy.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a068089dy.myapplication.listview.friend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AddrssActivity extends AppCompatActivity {
    private EditText edit_rss;
    private Button btn_send;
    private String str_rss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrss);
        edit_rss = (EditText) findViewById(R.id.rss);
        btn_send = (Button) findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_rss = edit_rss.getText().toString();
                new NetThread("addrss:"+str_rss).start();
            }
        });
    }

    /*
    *
    * 网络线程内部类
    * */
    class NetThread extends Thread{
        String server_ip = "123.207.174.226";
        int server_port = 8000;
        String data;
        String send;
        public NetThread(String send){
            this.send = send;
        }

        public void run(){

            try {

                Socket socket = new Socket(server_ip, server_port);
                Log.d("server","start");
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(send);
                out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                data = in.readLine();
                if(data.equals("")){
                    Log.d("data", "nothing");
                }
                else
                {
                    //接受数据处理

                    //data = in.readLine();
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

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            Bundle b = msg.getData();
            String data = b.getString("data");
            Log.d("handledata",data);

            Toast.makeText(AddrssActivity.this,data,Toast.LENGTH_LONG).show();
        }
    };


    public void onBackPressed() {
        this.finish();
    }

}
