package com.unclekong.ebookdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class StarActivey extends Activity {
    public List<ImageView> imageList = new ArrayList<ImageView>();
    public TextView[] markTextViews = new TextView[5];
    boolean isRead = false;
    int state;
    int scrWidth, scrHeight;
    int curFiles = 0;
    Splash splash;
    int curTab = 0;
    TabHost mTabHost;
    ListView cagalotlist;
    Spinner sp_textColor, sp_backColor, sp_textSize, sp_textStyle;
    Button setButton;
    Menu menu;
    private List<Integer> contentList = new ArrayList<Integer>();// 防止空指针,章节id列表//
    Handler myHandler = new Handler() {// 用来更新UI线程中的控件
        public void handleMessage(Message msg) {
            if (msg.what == 1) {//
                if (splash != null) {
                    splash.isLoop = false;
                    splash.exit();

                    splash = null;
                }
                curTab = 0;
                creatTableHost();
            } else if (msg.what == 2) {
                creatTableHost();
            } else if (msg.what == 4) {
                finish();
            }

        }
    };
    private ImageButton[] markButtons = new ImageButton[5];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        scrWidth = dm.widthPixels;
        scrHeight = dm.heightPixels;
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getBundleExtra("date");
            if (bundle.getInt("curTab") == 1) {
                creatTableHost();
            } else {

                setSplash();
            }
        } catch (Exception e) {
            // TODO: handle exception
            setSplash();
        }


    }

    // 列表点击后的响应
    public void selectposition(int arg0, int page) {
        // 读书操作
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = null;
        try {
            is = getResources().openRawResource(Const.filesId[arg0]); // 读取相应的章节
            isr = new InputStreamReader(is, "GBK");// 这里添加了GBK，解决乱码问题
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append('\n');
            }
            br.close();
            isr.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 用来计算章节用的
        for (int j = 0; j < Const.filesId.length; j++) {
            int cateid = Integer.valueOf(j);
            contentList.add(cateid);
        }
        int child = contentList.get(0);
        Intent newIntent = new Intent();
        Bundle newbundle = new Bundle();
        newbundle.putString("content", sb.toString());// 传递内容
        newbundle.putIntegerArrayList("list",// 传递章节列表
                (ArrayList<Integer>) contentList);
        newbundle.putInt("child", child);// 得到第一个
        newbundle.putInt("characterSize", 18);// 字体大小
        newbundle.putInt("selectedPosition", arg0);// 选择的位置
        Log.i("传递selectedPosition", String.valueOf(arg0));
        newIntent.setClass(StarActivey.this, LocalMainTurn.class);
        newIntent.putExtras(newbundle);
        startActivity(newIntent);
        finish();
    }

    public void setSplash() {
        if (splash == null) {
            splash = new Splash(this);
        }
        setContentView(splash);
        state = Const.STATE_SPLASH;
    }

    public void creatTableHost() {
        isRead = false;
        state = Const.STATE_TABHOST;
        setContentView(R.layout.bujv_tabhost);
        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();
        //取得TabHost对象    
        /* 为TabHost添加标签 */
        //新建一个newTabSpec(newTabSpec)    
        //设置其标签和图标(setIndicator)    
        //设置内容(setContent)   
        cagalotlist = (ListView) findViewById(R.id.tab_listview);

        CreatListView();
        mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
                .setIndicator(composeLayout(getString(R.string.calalog), R.drawable.img1))
                .setContent(R.id.tab_listview));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
                .setIndicator(composeLayout(getString(R.string.mark), R.drawable.img2))
                .setContent(R.id.mark));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
                .setIndicator(composeLayout(getString(R.string.about), R.drawable.img3))
                .setContent(R.id.about));
        // 设置当前显示哪一个标签
        mTabHost.setCurrentTab(0);

        imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.img01));

        // 标签切换事件处理，setOnTabChangedListener
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            // TODO Auto-generated method stub
            public void onTabChanged(String tabId) {
                // 设置所有选项卡的图片为未选中图片   
                imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.img1));
                imageList.get(1).setImageDrawable(getResources().getDrawable(R.drawable.img2));
                imageList.get(2).setImageDrawable(getResources().getDrawable(R.drawable.img3));

                if (tabId.equalsIgnoreCase("tab_test1")) {
                    imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.img01));
                    // 移动底部背景图片   
                    //moveTopSelect(0);  
                } else if (tabId.equalsIgnoreCase("tab_test2")) {
                    imageList.get(1).setImageDrawable(getResources().getDrawable(R.drawable.img02));
                    // 移动底部背景图片   
                    //moveTopSelect(1);  
                } else if (tabId.equalsIgnoreCase("tab_test3")) {
                    imageList.get(2).setImageDrawable(getResources().getDrawable(R.drawable.img03));
                    // 移动底部背景图片   
                    //moveTopSelect(2);  
                }
            }
        });

    }

    /**
     * 这个设置Tab标签本身的布局，需要TextView和ImageView不能重合 s:是文本显示的内容 i:是ImageView的图片位置
     */
    public View composeLayout(String s, int i) {
        // 定义一个LinearLayout布局   
        LinearLayout layout = new LinearLayout(this);
        // 设置布局垂直显示   
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView iv = new ImageView(this);
        imageList.add(iv);
        iv.setImageResource(i);
        //设置图片布局
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 40);
        lp.setMargins(0, 15, 0, 0);
        layout.addView(iv, lp);
        // 定义TextView   
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setText(s);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(15);
        //设置Text布局
        layout.addView(tv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return layout;
    }

    private void CreatListView() {

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < Const.cagalog.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("title", Const.cagalog[i]);

            map.put("image", R.drawable.cagalog);
            listItem.add(map);
        }
        SimpleAdapter myAdapter = new SimpleAdapter(this, listItem,
                R.layout.fav_item, new String[]{"title", "image"},
                new int[]{R.id.textcalalog, R.id.image});
        cagalotlist.setAdapter(myAdapter);
        cagalotlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectposition(arg2, 0);
            }
        });
    }


    // 如果书签没有存储信息，则以对话框的形式提示
    private void showDialog() {
        Dialog dialog = new AlertDialog.Builder(StarActivey.this).setTitle(
                        getString(R.string.toast1)).setMessage("当前标签没有存档！")
                .setPositiveButton(getString(R.string.ditemin),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                            }
                        }).create();// 创建按钮
        dialog.show();
    }

    public void displayToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    private void creatIsExit() {
        Dialog dialog = new AlertDialog.Builder(StarActivey.this)
                .setTitle(getString(R.string.toast1))
                .setMessage("是否确认退出？建议在退出前保存书签，方便下次阅读！")
                .setPositiveButton(getString(R.string.ditemin),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {


                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();

                            }
                        }).create();// 创建按钮

        dialog.show();
    }

    private void IsExit() {
        Dialog dialog = new AlertDialog.Builder(StarActivey.this)
                .setTitle(getString(R.string.toast1))
                .setMessage("是否确认退出？")
                .setPositiveButton(getString(R.string.ditemin), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        finish();
                        android.os.Process
                                .killProcess(android.os.Process.myPid()); // 获取PID
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();// 创建按钮
        dialog.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        System.out.println("----------------" + state);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (state == Const.STATE_READER) {
                creatIsExit();
            } else {
                IsExit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}