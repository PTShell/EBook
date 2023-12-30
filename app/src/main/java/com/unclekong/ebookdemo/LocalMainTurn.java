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

    private ViewFlipper viewFlipper;
    private String[] descriptionsArray;
    private int position;
    private TextView textViewContent;
    private FriendlyScrollView scroll;
    private LayoutInflater mInflater;
    //www.javaapk.com
    private GestureDetector gestureDetector;
    String str = null;
    int screenWidth;
    int screenHeight;
    int characterSize = 18;

    ProgressDialog mDialog;
    File file;
    public static int downStatus, useStatus, fileStatus = 0;
    private boolean flagSize = false;
    SeekBar seek;
    Dialog menuDialog;// menu�˵�Dialog
    View menuView;
    GridView menuGrid;
    StringBuffer sb = new StringBuffer();// �����½�����/
    String path;
    String filename;
    int selectposition;// ��ǰѡ���λ��
    /*-- MENU�˵�ѡ���±� --*/
    private final int ITEM_HELP = 0;// �����С
    private final int ITEM_PRE = 1;// ��һ��
    private final int ITEM_NEXT = 2;// ��һ��
    private final int ITEM_SETTING = 3;// ģʽ�л�
    private final int ITEM_SHUQIAN = 4;// ģʽ�л�
    private Boolean flag = false;
    private Boolean mode = false;
    private Boolean flagfrist = true;
    int[] menu_image_array = {R.drawable.menu_help, R.drawable.menu_pre,
            R.drawable.menu_next, R.drawable.menu_setting, R.drawable.menu_shuqian,};
    int[] menu_image_array2 = {R.drawable.menu_help, R.drawable.menu_pre,
            R.drawable.menu_next, R.drawable.menu_setting, R.drawable.menu_shuqian,};

    String[] menu_name_array = {"�����С", "��һ��", "��һ��", "ģʽ�л�", " �Ķ���¼",};
    String[] menu_name_array2 = {"�����С", "��һ��", "��һ��", "ģʽ�л�", "�Ķ���¼",};
    private List<Integer> contentList = new ArrayList<Integer>();// ��ֹ��ָ��

    int sign = 0;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // �����Ļ�ĸ߶ȺͿ��
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        path = Environment.getExternalStorageDirectory().getPath();// ���ϵͳ��·��
        // ����������
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        selectposition = bundle.getInt("selectedPosition");
        characterSize = bundle.getInt("characterSize");
        if (position < 0) {
            position = 0;
        }
        str = bundle.getString("content");// �õ�����
        filename = bundle.getString("filename");// �õ���ǰ���½�����
        contentList = bundle.getIntegerArrayList("list");// �õ��½��б�
        position = 0;// �õ���ǰλ��
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_info_list_view);
        //���׳�ʼ����ʼ
        AdManager.getInstance(this).init("d44ce4152d150f54", "308399787ce9716b", false);
        //���׳�ʼ������
        // /��õ�ǰ�½ڵ�λ��////
        for (int i = 0; i < contentList.size(); i++) {
            if (selectposition == contentList.get(i)) {
                Log.i("selectedPosition", String.valueOf(selectposition));
                sign = i;
                break;
            }
        }
        seek = (SeekBar) findViewById(R.id.seek);
        seek.setVisibility(8);
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
                    case ITEM_HELP:// �����С
                        flagSize = !flagSize;
                        if (flagSize) {
                            seek.setVisibility(View.VISIBLE);
                        } else {
                            seek.setVisibility(8);
                        }
                        menuDialog.dismiss();
                        break;
                    case ITEM_PRE:// ��һ��
                        selectposition = selectposition - 1;
                        if (selectposition < 0) {
                            selectposition = 0;
                        }
                        Log.i("��һ��11", "��һ��");
                        Log.i("sign", String.valueOf(sign));
                        if (sign > 0) {
                            Log.i("��һ��22", "��һ��");
                            sign--;
                            flag = false;
                            int chid = contentList.get(selectposition);
                            InputStream is = null;
                            InputStreamReader isr = null;
                            BufferedReader br = null;
                            try {
                                is = getResources().openRawResource(
                                        Const.filesId[chid]); // ��ȡ��Ӧ���½�
                                isr = new InputStreamReader(is, "GBK");// ���������GBK�������������
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
                                            "��" + (sign + 1) + "��", Toast.LENGTH_SHORT)
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
                            Log.i("��һ��33", "��һ��");
                            Toast.makeText(LocalMainTurn.this, "û��������",
                                    Toast.LENGTH_SHORT).show();
                        }
                        menuDialog.dismiss();
                        break;
                    case ITEM_NEXT:// ��һ��
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
                                        Const.filesId[chid]); // ��ȡ��Ӧ���½�
                                isr = new InputStreamReader(is, "GBK");// ���������GBK�������������
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
                                            "��" + (selectposition + 1) + "��", Toast.LENGTH_SHORT)
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
                            Toast.makeText(LocalMainTurn.this, "û��������",
                                    Toast.LENGTH_SHORT).show();
                        }
                        menuDialog.dismiss();
                        break;
                    case ITEM_SETTING:// ģʽ�л�
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
                    case ITEM_SHUQIAN:// ģʽ�л�
                        menuDialog.dismiss();
                        break;
                }
            }
        });

        menuDialog = new Dialog(LocalMainTurn.this, R.style.dialog);
        menuDialog.setContentView(menuView);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // Ϊ��ȡ��Ļ����
        WindowManager.LayoutParams lp = menuDialog.getWindow().getAttributes();
        lp.width = (int) (d.getWidth()); // �������Ϊ��Ļ��0.8
        lp.alpha = 0.9f;
        menuDialog.getWindow().setAttributes(lp);
        menuDialog.getWindow().setGravity(Gravity.BOTTOM); // ���ÿ��Ҷ���
        menuDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU)// ��������
                    dialog.dismiss();
                return false;
            }
        });
    }

    /**
     * ���ؼ���
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
         * �ȼ�����ı��ĳ���Ȼ��һ��Ϊ��λ����ÿ�ŵ�����������\n��һ�У���ָ��������һ�� ����ÿ24���ַ�һ�У�ÿ��21��
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
                // �趨��Ļ��ʾÿ�е�����
                if (k == screenWidth / characterSize) {
                    j++;
                    k = 0;
                }
                // �趨����
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
            position = 0;//ȷ���ص��½���ҳ
        } else {
            position = 0;//ȷ���ص��½���ҳ
        }
        gestureDetector = new GestureDetector(new CommonGestureListener());
    }

    private View getContentView() {
        // �������adView��ӵ���Ҫչʾ��layout�ؼ���
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        AdView adView = new AdView(this, AdSize.SIZE_320x50);
        adLayout.addView(adView);
        //��ʼ�������ͼ������ʹ�������Ĺ��캯�����ù����ͼ�ı���ɫ��͸���ȼ�������ɫ

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

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {

            return gestureDetector.onTouchEvent(event);
        }
    };

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
                // ����
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
                                .openRawResource(Const.filesId[chid]); // ��ȡ��Ӧ���½�
                        isr = new InputStreamReader(is, "GBK");// ���������GBK�������������
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

                    Toast.makeText(LocalMainTurn.this, "�� " + (sign + 1) + " ��",
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
                    Toast.makeText(LocalMainTurn.this, "û��������",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocalMainTurn.this,
                            "�� " + (sign + 1) + " ��" + "--" + "�� " + (position + 1) + " ҳ" + "--" + "���¹� " + (descriptionsArray.length) + " ҳ",
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
                // ����
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
                                .openRawResource(Const.filesId[chid]); // ��ȡ��Ӧ���½�
                        isr = new InputStreamReader(is, "GBK");// ���������GBK�������������
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
                    Toast.makeText(LocalMainTurn.this, "�� " + (sign + 1) + " �� ",
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
                    Toast.makeText(LocalMainTurn.this, "û��������",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocalMainTurn.this,
                            "�� " + (sign + 1) + " ��" + "--" + "�� " + (position + 1) + " ҳ" + "--" + "���¹� " + (descriptionsArray.length) + " ҳ",
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

    @Override
    /**
     * ����MENU
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");// ���봴��һ��
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**
     * ����MENU
     */
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menuDialog == null) {
            menuDialog = new Dialog(LocalMainTurn.this, R.style.dialog);
        } else {
            menuDialog.show();
        }
        return false;// ����Ϊtrue ����ʾϵͳmenu
    }

    /**
     * ����˵�Adapter
     *
     * @param menuNameArray      ����
     * @param imageResourceArray ͼƬ
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

}
