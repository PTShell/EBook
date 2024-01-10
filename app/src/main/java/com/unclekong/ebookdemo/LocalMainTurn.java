package com.unclekong.ebookdemo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalMainTurn extends Activity implements OnSeekBarChangeListener {

    public static int downStatus, useStatus, fileStatus = 0;
    /*-- MENU菜单选项下标 --*/
    private final int ITEM_HELP = 0;// 字体大小
    private final int ITEM_PRE = 1;// 上一张
    private final int ITEM_NEXT = 2;// 下一章
    private final int ITEM_SETTING = 3;// 模式切换
    private final int ITEM_SHUQIAN = 4;// 模式切换
    String str = null;
    int screenWidth;
    int screenHeight;
    int characterSize = 18;
    ProgressDialog mDialog;
    File file;
    SeekBar seek;
    Dialog menuDialog;// menu菜单Dialog
    View menuView;
    GridView menuGrid;
    StringBuffer sb = new StringBuffer();// 接受章节内容/
    String path;
    String filename;
    int selectposition;// 当前选择的位置
    int[] menu_image_array = {R.drawable.menu_help, R.drawable.menu_pre,
            R.drawable.menu_next, R.drawable.menu_setting, R.drawable.menu_shuqian,};
    int[] menu_image_array2 = {R.drawable.menu_help, R.drawable.menu_pre,
            R.drawable.menu_next, R.drawable.menu_setting, R.drawable.menu_shuqian,};
    String[] menu_name_array = {"字体大小", "上一章", "下一章", "模式切换", " 阅读记录",};
    String[] menu_name_array2 = {"字体大小", "上一章", "下一章", "模式切换", "阅读记录",};
    int sign = 0;
    private ViewFlipper viewFlipper;
    private String[] descriptionsArray;
    private int position;
    private TextView textViewContent;
    private FriendlyScrollView scroll;
    private LayoutInflater mInflater;
    private GestureDetector gestureDetector;
    private boolean flagSize = false;
    private Boolean flag = false;
    private Boolean mode = false;
    private Boolean flagfrist = true;
    private List<Integer> contentList = new ArrayList<Integer>();// 防止空指针
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {

            return gestureDetector.onTouchEvent(event);
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 获得屏幕的高度和宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        path = Environment.getExternalStorageDirectory().getPath();// 获得系统的路径
        // 获得书的内容
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        selectposition = bundle.getInt("selectedPosition");
        characterSize = bundle.getInt("characterSize");
        if (position < 0) {
            position = 0;
        }
        str = bundle.getString("content");// 得到内容
        filename = bundle.getString("filename");// 得到当前的章节名字
        contentList = bundle.getIntegerArrayList("list");// 得到章节列表
        position = 0;// 得到当前位置
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_info_list_view);
        //有米初始化开始
        AdManager.getInstance(this).init("d44ce4152d150f54", "308399787ce9716b", false);
        //有米初始化结束
        // /获得当前章节的位置////
        for (int i = 0; i < contentList.size(); i++) {
            if (selectposition == contentList.get(i)) {
                Log.i("selectedPosition", String.valueOf(selectposition));
                sign = i;
                break;
            }
        }
        seek = (SeekBar) findViewById(R.id.seek);
        seek.setVisibility(View.GONE);
        seek.setOnSeekBarChangeListener(LocalMainTurn.this);
        InitUI();
        super.onCreate(savedInstanceState);
        menuView = View.inflate(this, R.layout.gridview_menu, null);
        menuGrid = (GridView) menuView.findViewById(R.id.gridview);
        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (arg2) {
                    case ITEM_HELP:// 字体大小
                        flagSize = !flagSize;
                        if (flagSize) {
                            seek.setVisibility(View.VISIBLE);
                        } else {
                            seek.setVisibility(View.GONE);
                        }
                        menuDialog.dismiss();
                        break;
                    case ITEM_PRE:// 上一章
                        selectposition = selectposition - 1;
                        if (selectposition < 0) {
                            selectposition = 0;
                        }
                        Log.i("上一章11", "上一章");
                        Log.i("sign", String.valueOf(sign));
                        if (sign > 0) {
                            Log.i("上一章22", "上一章");
                            sign--;
                            flag = false;
                            int chid = contentList.get(selectposition);
                            InputStream is = null;
                            InputStreamReader isr = null;
                            BufferedReader br = null;
                            try {
                                is = getResources().openRawResource(
                                        Const.filesId[chid]); // 读取相应的章节
                                isr = new InputStreamReader(is, "GBK");// 这里添加了GBK，解决乱码问题
                                br = new BufferedReader(isr);
                                while ((str = br.readLine()) != null) {
                                    sb.append(str);
                                }
                                str = sb.toString().trim();
                                sb.delete(0, sb.length());
                            } catch (FileNotFoundException e3) {

                                e3.printStackTrace();
                            } catch (UnsupportedEncodingException e) {

                                e.printStackTrace();
                            } catch (IOException e) {

                                e.printStackTrace();
                            } finally {
                                try {
                                    br.close();
                                    isr.close();
                                    is.close();
                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(LocalMainTurn.this,
                                            "第" + (sign + 1) + "章", Toast.LENGTH_SHORT)
                                    .show();

                            fillDate();
                            viewFlipper.addView(getContentView());
                            viewFlipper.setInAnimation(AnimationControl
                                    .inFromRightAnimation());
                            viewFlipper.setOutAnimation(AnimationControl
                                    .outToLeftAnimation());
                            viewFlipper.showNext();
                            viewFlipper.removeViewAt(0);
                        } else if (sign >= contentList.size() - 1) {
                            Log.i("上一章33", "上一章");
                            Toast.makeText(LocalMainTurn.this, "没有数据啦",
                                    Toast.LENGTH_SHORT).show();
                        }
                        menuDialog.dismiss();
                        break;
                    case ITEM_NEXT:// 下一章
                        selectposition = selectposition + 1;
                        if (sign < contentList.size() - 1) {
                            sign++;
                            flag = true;
                            int chid = contentList.get(selectposition);

                            InputStream is = null;
                            InputStreamReader isr = null;
                            BufferedReader br = null;
                            try {
                                is = getResources().openRawResource(
                                        Const.filesId[chid]); // 读取相应的章节
                                isr = new InputStreamReader(is, "GBK");// 这里添加了GBK，解决乱码问题
                                br = new BufferedReader(isr);
                                while ((str = br.readLine()) != null) {
                                    sb.append(str);
                                }
                                str = sb.toString().trim();
                                sb.delete(0, sb.length());
                            } catch (FileNotFoundException e3) {

                                e3.printStackTrace();
                            } catch (UnsupportedEncodingException e) {

                                e.printStackTrace();
                            } catch (IOException e) {

                                e.printStackTrace();
                            } finally {
                                try {
                                    br.close();
                                    isr.close();
                                    is.close();
                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                            }

                            Toast.makeText(LocalMainTurn.this,
                                            "第" + (selectposition + 1) + "章", Toast.LENGTH_SHORT)
                                    .show();
                            fillDate();
                            viewFlipper.addView(getContentView());
                            viewFlipper.setInAnimation(AnimationControl
                                    .inFromRightAnimation());
                            viewFlipper.setOutAnimation(AnimationControl
                                    .outToLeftAnimation());
                            viewFlipper.showNext();
                            viewFlipper.removeViewAt(0);
                        } else if (sign >= contentList.size() - 1) {
                            Toast.makeText(LocalMainTurn.this, "没有数据啦",
                                    Toast.LENGTH_SHORT).show();
                        }
                        menuDialog.dismiss();
                        break;
                    case ITEM_SETTING:// 模式切换
                        mode = !mode;
                        getContentView();
                        viewFlipper.invalidate();
                        if (position - 1 >= 0) {
                            viewFlipper.addView(getContentView());
                            viewFlipper.setInAnimation(AnimationControl
                                    .inFromRightAnimation());
                            viewFlipper.setOutAnimation(AnimationControl
                                    .outToLeftAnimation());
                            viewFlipper.showPrevious();
                            viewFlipper.removeViewAt(0);
                            viewFlipper.addView(getContentView());
                            viewFlipper.setInAnimation(AnimationControl
                                    .inFromRightAnimation());
                            viewFlipper.setOutAnimation(AnimationControl
                                    .outToLeftAnimation());
                            viewFlipper.showNext();
                            viewFlipper.removeViewAt(0);
                        } else if (position + 1 < descriptionsArray.length) {
                            viewFlipper.addView(getContentView());
                            viewFlipper.setInAnimation(AnimationControl
                                    .inFromRightAnimation());
                            viewFlipper.setOutAnimation(AnimationControl
                                    .outToLeftAnimation());
                            viewFlipper.showNext();
                            viewFlipper.removeViewAt(0);
                            viewFlipper.addView(getContentView());
                            viewFlipper.setInAnimation(AnimationControl
                                    .inFromRightAnimation());
                            viewFlipper.setOutAnimation(AnimationControl
                                    .outToLeftAnimation());
                            viewFlipper.showPrevious();
                            viewFlipper.removeViewAt(0);
                        } else {
                            viewFlipper.addView(getContentView());
                            viewFlipper.setInAnimation(AnimationControl
                                    .inFromRightAnimation());
                            viewFlipper.setOutAnimation(AnimationControl
                                    .outToLeftAnimation());
                            viewFlipper.showNext();
                            viewFlipper.removeViewAt(0);
                            viewFlipper.addView(getContentView());
                            viewFlipper.setInAnimation(AnimationControl
                                    .inFromRightAnimation());
                            viewFlipper.setOutAnimation(AnimationControl
                                    .outToLeftAnimation());
                            viewFlipper.showPrevious();
                            viewFlipper.removeViewAt(0);
                        }
                        menuDialog.dismiss();
                        break;
                    case ITEM_SHUQIAN:// 模式切换
                        menuDialog.dismiss();
                        break;
                }
            }
        });

        menuDialog = new Dialog(LocalMainTurn.this, R.style.dialog);
        menuDialog.setContentView(menuView);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams lp = menuDialog.getWindow().getAttributes();
        lp.width = (int) (d.getWidth()); // 宽度设置为屏幕的0.8
        lp.alpha = 0.9f;
        menuDialog.getWindow().setAttributes(lp);
        menuDialog.getWindow().setGravity(Gravity.BOTTOM); // 设置靠右对齐
        menuDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
                    dialog.dismiss();
                return false;
            }
        });
    }

    /**
     * 返回监听
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LocalMainTurn.this, StarActivey.class);
        Bundle bundle = new Bundle();
        bundle.putInt("curTab", 1);
        intent.putExtra("date", bundle);
        startActivity(intent);
        finish();
    }

    private void InitUI() {
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper_data);
        mInflater = LayoutInflater.from(this);
        fillDate();
        viewFlipper.addView(getContentView());
    }

    @SuppressWarnings("deprecation")
    private void fillDate() {

        StringBuffer sb = new StringBuffer();
        List<String> list = new ArrayList<String>();
        int j = 0;//
        int k = 0;//
        int m = 0;
        /*
         * 先计算出文本的长度然后一张为单位遍历每张的字数，遇到\n算一行，满指定字数算一行 本例每24个字符一行，每张21行
         */
        if (!str.equals(null))
            for (int i = 0; i < str.length(); i++) {
                if (m == 0 || str.charAt(i) != '\n') {
                    sb.append(str.charAt(i));
                    if (m == 1) {
                        m = 0;
                    }
                }
                if (str.charAt(i) == '\n') {
                    j++;
                    k = 0;
                    m = 1;
                }
                k++;
                // 设定屏幕显示每行的字数
                if (k == screenWidth / characterSize) {
                    j++;
                    k = 0;
                }
                // 设定行数
                if (j == screenHeight / ((characterSize))) {
                    j = 0;

                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            }
        if (sb.length() != 0) {
            list.add(sb.toString());
            sb.delete(0, sb.length());
        }
        descriptionsArray = new String[list.size()];//
        for (int n = 0; n < list.size(); n++) {
            descriptionsArray[n] = list.get(n);
        }
        if (flagfrist) {
            flagfrist = !flagfrist;
        } else if (!flag) {
            position = 0;//确保回到章节首页
        } else {
            position = 0;//确保回到章节首页
        }
        gestureDetector = new GestureDetector(new CommonGestureListener());
    }

    private View getContentView() {
        // 将广告条adView添加到需要展示的layout控件中
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        AdView adView = new AdView(this, AdSize.SIZE_320x50);
        adLayout.addView(adView);
        //初始化广告视图，可以使用其他的构造函数设置广告视图的背景色、透明度及字体颜色

        View contentView = new View(this);
        contentView = mInflater.inflate(R.layout.common_info_item_view, null);
        textViewContent = (TextView) contentView.findViewById(R.id.text_detail);
        if (position < descriptionsArray.length && position >= 0) {
            textViewContent.setText(descriptionsArray[position]);
        }
        textViewContent.setPadding(30, 30, 30, 80);
        textViewContent.setTextSize(characterSize);
        Resources res = getResources();
        if (!mode) {
            Drawable drawable = res.getDrawable(R.drawable.bg2);
            contentView.setBackgroundDrawable(drawable);

        } else {
            Drawable drawable = res.getDrawable(R.drawable.bg);
            contentView.setBackgroundDrawable(drawable);
        }
        scroll = (FriendlyScrollView) contentView.findViewById(R.id.scroll);
        scroll.setOnTouchListener(onTouchListener);
        scroll.setGestureDetector(gestureDetector);
        scroll.setVerticalScrollBarEnabled(false);
        return contentView;


    }

    @Override
    /**
     * 创建MENU
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");// 必须创建一项
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**
     * 拦截MENU
     */
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menuDialog == null) {
            menuDialog = new Dialog(LocalMainTurn.this, R.style.dialog);
        } else {
            menuDialog.show();
        }
        return false;// 返回为true 则显示系统menu
    }

    /**
     * 构造菜单Adapter
     *
     * @param menuNameArray      名称
     * @param imageResourceArray 图片
     * @return SimpleAdapter
     */
    private SimpleAdapter getMenuAdapter(String[] menuNameArray,
                                         int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.item_menu, new String[]{"itemImage", "itemText"},
                new int[]{R.id.item_image, R.id.item_text});
        return simperAdapter;
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        characterSize = ((50 * progress) / seekBar.getWidth()) + 10;
        fillDate();
        getContentView();
        viewFlipper.addView(getContentView());
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.showPrevious();
        viewFlipper.removeViewAt(0);
        viewFlipper.addView(getContentView());
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.showNext();
        viewFlipper.removeViewAt(0);
    }

    // @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        getContentView();
        viewFlipper.addView(getContentView());
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.showPrevious();
        viewFlipper.removeViewAt(0);
        viewFlipper.addView(getContentView());
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.showNext();
        viewFlipper.removeViewAt(0);
    }

    // @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        getContentView();
        viewFlipper.addView(getContentView());
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.showPrevious();
        viewFlipper.removeViewAt(0);
        viewFlipper.addView(getContentView());
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.showNext();
        viewFlipper.removeViewAt(0);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onPause() {

        StoreageData sd = new StoreageData(
                LocalMainTurn.this.getApplicationContext());
        sd.setDateInt(filename + "0", contentList.get(sign));
        sd.setDateInt(filename + "1", position);
        sd.setDateInt(filename + "2", characterSize);
        super.onPause();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public class CommonGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

            super.onShowPress(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 50) {
                // 向左
                position = position + 1;
                if (sign < contentList.size() - 1
                        && !(position < descriptionsArray.length)) {
                    sign++;
                    flag = false;
                    int chid = contentList.get(sign);

                    InputStream is = null;
                    InputStreamReader isr = null;
                    BufferedReader br = null;
                    try {
                        is = getResources()
                                .openRawResource(Const.filesId[chid]); // 读取相应的章节
                        isr = new InputStreamReader(is, "GBK");// 这里添加了GBK，解决乱码问题
                        br = new BufferedReader(isr);
                        while ((str = br.readLine()) != null) {
                            sb.append(str);
                            sb.append('\n');
                        }
                        str = sb.toString().trim();
                        sb.delete(0, sb.length());

                    } catch (FileNotFoundException e3) {

                        e3.printStackTrace();
                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    } catch (IOException e) {

                        e.printStackTrace();
                    } finally {
                        try {
                            if (br != null) {
                                br.close();
                            }
                            if (isr != null) {
                                isr.close();
                            }
                            if (isr != null) {
                                is.close();
                            }

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(LocalMainTurn.this, "第 " + (sign + 1) + " 章",
                            Toast.LENGTH_SHORT).show();

                    fillDate();
                    viewFlipper.addView(getContentView());
                    viewFlipper.setInAnimation(AnimationControl
                            .inFromRightAnimation());
                    viewFlipper.setOutAnimation(AnimationControl
                            .outToLeftAnimation());
                    viewFlipper.showNext();
                    viewFlipper.removeViewAt(0);
                } else if (sign >= contentList.size() - 1
                        && position >= descriptionsArray.length) {
                    position = descriptionsArray.length - 1;
                    Toast.makeText(LocalMainTurn.this, "没有数据啦",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocalMainTurn.this,
                            "第 " + (sign + 1) + " 章" + "--" + "第 " + (position + 1) + " 页" + "--" + "本章共 " + (descriptionsArray.length) + " 页",
                            Toast.LENGTH_SHORT).show();
                    viewFlipper.addView(getContentView());
                    viewFlipper.setInAnimation(AnimationControl
                            .inFromRightAnimation());
                    viewFlipper.setOutAnimation(AnimationControl
                            .outToLeftAnimation());
                    viewFlipper.showNext();
                    viewFlipper.removeViewAt(0);
                }

            } else if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 50) {
                // 向右
                position = position - 1;
                if (sign > 0 && position < 0) {
                    sign--;
                    flag = true;
                    int chid = contentList.get(sign);
                    InputStream is = null;
                    InputStreamReader isr = null;
                    BufferedReader br = null;
                    try {
                        is = getResources()
                                .openRawResource(Const.filesId[chid]); // 读取相应的章节
                        isr = new InputStreamReader(is, "GBK");// 这里添加了GBK，解决乱码问题
                        br = new BufferedReader(isr);
                        while ((str = br.readLine()) != null) {
                            sb.append(str);
                            sb.append('\n');
                        }
                        str = sb.toString().trim();
                        sb.delete(0, sb.length());
                        sb.append('\n');
                    } catch (FileNotFoundException e3) {
                        e3.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {

                        e.printStackTrace();
                    } finally {
                        try {
                            br.close();
                            isr.close();
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(LocalMainTurn.this, "第 " + (sign + 1) + " 章 ",
                            Toast.LENGTH_SHORT).show();
                    fillDate();
                    viewFlipper.addView(getContentView());
                    viewFlipper.setInAnimation(AnimationControl
                            .inFromLeftAnimation());
                    viewFlipper.setOutAnimation(AnimationControl
                            .outToRightAnimation());
                    viewFlipper.showNext();
                    viewFlipper.removeViewAt(0);
                } else if (sign <= 0 && position < 0) {
                    position = 0;
                    Toast.makeText(LocalMainTurn.this, "没有数据啦",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocalMainTurn.this,
                            "第 " + (sign + 1) + " 章" + "--" + "第 " + (position + 1) + " 页" + "--" + "本章共 " + (descriptionsArray.length) + " 页",
                            Toast.LENGTH_SHORT).show();
                    viewFlipper.addView(getContentView());
                    viewFlipper.setInAnimation(AnimationControl
                            .inFromLeftAnimation());
                    viewFlipper.setOutAnimation(AnimationControl
                            .outToRightAnimation());
                    viewFlipper.showNext();
                    viewFlipper.removeViewAt(0);
                }
            }

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }

}
