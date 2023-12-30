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
    boolean isRead = false;
    int state;
    int scrWidth, scrHeight;
    int curFiles = 0;
    Splash splash;
    int curTab = 0;
    TabHost mTabHost;
    public List<ImageView> imageList = new ArrayList<ImageView>();
    ListView cagalotlist;
    Spinner sp_textColor, sp_backColor, sp_textSize, sp_textStyle;
    Button setButton;
    private List<Integer> contentList = new ArrayList<Integer>();// ��ֹ��ָ��,�½�id�б�//
    Menu menu;
    public TextView[] markTextViews = new TextView[5];
    private ImageButton[] markButtons = new ImageButton[5];
    Handler myHandler = new Handler() {// ��������UI�߳��еĿؼ�
        public void handleMessage(Message msg) {
            if (msg.what == 1) {//
                if (splash != null) {
                    splash.isLoop = false;
                    splash.exit();
                    //www.javaapk.com
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

    // �б��������Ӧ
    public void selectposition(int arg0, int page) {
        // �������
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = null;
        try {
            is = getResources().openRawResource(Const.filesId[arg0]); // ��ȡ��Ӧ���½�
            isr = new InputStreamReader(is, "GBK");// ���������GBK�������������
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
        // ���������½��õ�
        for (int j = 0; j < Const.filesId.length; j++) {
            int cateid = Integer.valueOf(j);
            contentList.add(cateid);
        }
        int child = contentList.get(0);
        Intent newIntent = new Intent();
        Bundle newbundle = new Bundle();
        newbundle.putString("content", sb.toString());// ��������
        newbundle.putIntegerArrayList("list",// �����½��б�
                (ArrayList<Integer>) contentList);
        newbundle.putInt("child", child);// �õ���һ��
        newbundle.putInt("characterSize", 18);// �����С
        newbundle.putInt("selectedPosition", arg0);// ѡ���λ��
        Log.i("����selectedPosition", String.valueOf(arg0));
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
        //ȡ��TabHost����    
        /* ΪTabHost��ӱ�ǩ */
        //�½�һ��newTabSpec(newTabSpec)    
        //�������ǩ��ͼ��(setIndicator)    
        //��������(setContent)   
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
        // ���õ�ǰ��ʾ��һ����ǩ
        mTabHost.setCurrentTab(0);
        imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.img01));

        // ��ǩ�л��¼�����setOnTabChangedListener
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            // TODO Auto-generated method stub
            public void onTabChanged(String tabId) {
                // ��������ѡ���ͼƬΪδѡ��ͼƬ
                imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.img1));
                imageList.get(1).setImageDrawable(getResources().getDrawable(R.drawable.img2));
                imageList.get(2).setImageDrawable(getResources().getDrawable(R.drawable.img3));

                if (tabId.equalsIgnoreCase("tab_test1")) {
                    imageList.get(0).setImageDrawable(getResources().getDrawable(R.drawable.img01));
                    // �ƶ��ײ�����ͼƬ
                    //moveTopSelect(0);
                } else if (tabId.equalsIgnoreCase("tab_test2")) {
                    imageList.get(1).setImageDrawable(getResources().getDrawable(R.drawable.img02));
                    // �ƶ��ײ�����ͼƬ
                    //moveTopSelect(1);
                } else if (tabId.equalsIgnoreCase("tab_test3")) {
                    imageList.get(2).setImageDrawable(getResources().getDrawable(R.drawable.img03));
                    // �ƶ��ײ�����ͼƬ
                    //moveTopSelect(2);
                }
            }
        });
    }

    /**
     * �������Tab��ǩ����Ĳ��֣���ҪTextView��ImageView�����غ� s:���ı���ʾ������ i:��ImageView��ͼƬλ��
     */
    public View composeLayout(String s, int i) {
        // ����һ��LinearLayout����   
        LinearLayout layout = new LinearLayout(this);
        // ���ò��ִ�ֱ��ʾ   
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView iv = new ImageView(this);
        imageList.add(iv);
        iv.setImageResource(i);
        //����ͼƬ����
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 40);
        lp.setMargins(0, 15, 0, 0);
        layout.addView(iv, lp);
        // ����TextView   
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setText(s);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(15);
        //����Text����
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
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                selectposition(arg2, 0);

            }
        });
    }

    // �����ǩû�д洢��Ϣ�����ԶԻ������ʽ��ʾ
    private void showDialog() {
        Dialog dialog = new AlertDialog.Builder(StarActivey.this).setTitle(
                        getString(R.string.toast1)).setMessage("��ǰ��ǩû�д浵��")
                .setPositiveButton(getString(R.string.ditemin),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                            }
                        }).create();// ������ť

        dialog.show();
    }

    public void displayToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    private void creatIsExit() {
        Dialog dialog = new AlertDialog.Builder(StarActivey.this)
                .setTitle(getString(R.string.toast1))
                .setMessage("�Ƿ�ȷ���˳����������˳�ǰ������ǩ�������´��Ķ���")
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
                        }).create();// ������ť

        dialog.show();
    }

    private void IsExit() {
        Dialog dialog = new AlertDialog.Builder(StarActivey.this)
                .setTitle(getString(R.string.toast1))
                .setMessage("�Ƿ�ȷ���˳���")
                .setPositiveButton(getString(R.string.ditemin),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                                finish();
                                android.os.Process
                                        .killProcess(android.os.Process.myPid()); // ��ȡPID
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        }).create();// ������ť

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