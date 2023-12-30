package com.unclekong.ebookdemo;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * @param  �Լ�ֵ�Եķ�ʽ�洢�Ͷ�ȡ�����������͡���ʵ��Activity������ݴ��ݺ����ݹ���
 */
public class SaveTools {
    //www.javaapk.com
    public SharedPreferences sharepre;

    public SaveTools(Activity activity) {
        sharepre = activity.getSharedPreferences("textReader", Activity.MODE_PRIVATE);

    }

    public void saveInt(String name, int value) {
        SharedPreferences.Editor editor = sharepre.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public void saveString(String name, String value) {
        SharedPreferences.Editor editor = sharepre.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public void saveBoolean(String name, boolean value) {
        SharedPreferences.Editor editor = sharepre.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public int readInt(String name, int defaultValue) {
        return sharepre.getInt(name, defaultValue);
    }

    public Boolean readBoolean(String name, Boolean defaultValue) {
        return sharepre.getBoolean(name, defaultValue);
    }

    public String readString(String name, String defaultValue) {
        return sharepre.getString(name, defaultValue);
    }
}
