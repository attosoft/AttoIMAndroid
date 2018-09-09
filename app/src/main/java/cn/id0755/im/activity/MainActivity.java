package cn.id0755.im.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import cn.id0755.im.R;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private TextView mTextMessage;

    private Button btnLogout = null;

    private EditText editId = null;
    private EditText editContent = null;
    private TextView viewMyid = null;
    private Button btnSend = null;

    private ListView chatInfoListView;
    private MyAdapter chatInfoListAdapter;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    Executors.newSingleThreadExecutor().submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                new ConnectionManager().connect(Config.PORT, Config.HOST);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnLogout = (Button) this.findViewById(R.id.logout_btn);

        btnSend = (Button) this.findViewById(R.id.send_btn);
        editId = (EditText) this.findViewById(R.id.id_editText);
        editContent = (EditText) this.findViewById(R.id.content_editText);
        viewMyid = (TextView) this.findViewById(R.id.myid_view);

        chatInfoListView = (ListView) this.findViewById(R.id.demo_main_activity_layout_listView);
        chatInfoListAdapter = new MyAdapter(this);
        chatInfoListView.setAdapter(chatInfoListAdapter);

        this.setTitle("当前登陆：" + "");
        initListeners();
    }

    private void initListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出登陆
                doLogout();
                // 退出程序
                doExit();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSendMessage();
            }
        });
    }

    private void doSendMessage() {
        String msg = editContent.getText().toString().trim();
        String friendId = editId.getText().toString().trim();
        if (msg.length() > 0 && friendId.length() > 0) {
            showIMInfo_black("我对" + friendId + "说：" + msg);
        } else {
            showIMInfo_red("接收者id或发送内容为空，发送没有继续!");
            Log.e(MainActivity.class.getSimpleName(), "msg.len=" + msg.length() + ",friendId.len=" + friendId.length());
        }
    }

    private void doLogout() {
        // 发出退出登陆请求包（Android系统要求必须要在独立的线程中发送哦）
    }

    private void doExit() {
        finish();
        System.exit(0);
    }

    //--------------------------------------------------------------- 各种信息输出方法 START
    public void showIMInfo_black(String txt) {
        chatInfoListAdapter.addItem(txt, ChatInfoColorType.black);
    }

    public void showIMInfo_blue(String txt) {
        chatInfoListAdapter.addItem(txt, ChatInfoColorType.blue);
    }

    public void showIMInfo_brightred(String txt) {
        chatInfoListAdapter.addItem(txt, ChatInfoColorType.brightred);
    }

    public void showIMInfo_red(String txt) {
        chatInfoListAdapter.addItem(txt, ChatInfoColorType.red);
    }

    public void showIMInfo_green(String txt) {
        chatInfoListAdapter.addItem(txt, ChatInfoColorType.green);
    }
    //--------------------------------------------------------------- 各种信息输出方法 END

    //--------------------------------------------------------------- inner classes START

    /**
     * 各种显示列表Adapter实现类。
     */
    public class MyAdapter extends BaseAdapter {
        private List<Map<String, Object>> mData;
        private LayoutInflater mInflater;
        private SimpleDateFormat hhmmDataFormat = new SimpleDateFormat("HH:mm:ss");

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            mData = new ArrayList<Map<String, Object>>();
        }

        public void addItem(String content, ChatInfoColorType color) {
            Map<String, Object> it = new HashMap<String, Object>();
            it.put("__content__", "[" + hhmmDataFormat.format(new Date()) + "]" + content);
            it.put("__color__", color);
            mData.add(it);
            this.notifyDataSetChanged();
            chatInfoListView.setSelection(this.getCount());
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.demo_main_activity_list_item_layout, null);
                holder.content = (TextView) convertView.findViewById(R.id.demo_main_activity_list_item_layout_tvcontent);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.content.setText((String) mData.get(position).get("__content__"));
            ChatInfoColorType colorType = (ChatInfoColorType) mData.get(position).get("__color__");
            switch (colorType) {
                case blue:
                    holder.content.setTextColor(Color.rgb(0, 0, 255));
                    break;
                case brightred:
                    holder.content.setTextColor(Color.rgb(255, 0, 255));
                    break;
                case red:
                    holder.content.setTextColor(Color.rgb(255, 0, 0));
                    break;
                case green:
                    holder.content.setTextColor(Color.rgb(0, 128, 0));
                    break;
                case black:
                default:
                    holder.content.setTextColor(Color.rgb(0, 0, 0));
                    break;
            }

            return convertView;
        }

        public final class ViewHolder {
            public TextView content;
        }
    }

    /**
     * 信息颜色常量定义。
     */
    public enum ChatInfoColorType {
        black,
        blue,
        brightred,
        red,
        green,
    }
    //--------------------------------------------------------------- inner classes END
}
