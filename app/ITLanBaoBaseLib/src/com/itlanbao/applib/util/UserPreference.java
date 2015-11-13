package com.itlanbao.applib.util;

import com.itlanbao.applib.ItLanbaoLibApplication;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
 

public class UserPreference {
    private static SharedPreferences mUserPreferences;
    private static final String USER_PREFERENCE = "user_preference";
    
    // 是否开启轮询
    private static final String TAG_IS_OPEN_LOOP_PUSH = "isOpenLoopPush";
    // Notification通知铃声
    private static final String TAG_NOTIFICATION_RING_TONE = "ringTone";
    // Notification通知震动
    private static final String TAG_NOTIFICATION_NEW_MAIL_VIBRATE = "newMailVibrate";
 
    public static void clearData(){
		final Editor editor = ensureIntializePreference().edit();
		if(editor != null) {
		    editor.clear();
		}
		mUserPreferences = null; 
    }
    
    public static SharedPreferences ensureIntializePreference() {
        if (mUserPreferences == null) { 
            mUserPreferences = ItLanbaoLibApplication.getInstance().getSharedPreferences(USER_PREFERENCE, 0);
        }
        return mUserPreferences;
    }

    public static void save(String key, String value) {
        Editor editor = ensureIntializePreference().edit();
        editor.putString(key, value);
        editor.commit(); 
    }
    
    public static void save(String key, int value) {
        Editor editor = ensureIntializePreference().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void save(String key, boolean value) {
        Editor editor = ensureIntializePreference().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void save(String key, long value) {
        Editor editor = ensureIntializePreference().edit();
        editor.putLong(key, value);
        editor.commit();
    }
    
    public static void save(String key, Float value) {
        Editor editor = ensureIntializePreference().edit();
        editor.putFloat(key, value);
        editor.commit();
    }
 
    public static String read(String key, String defaultvalue) {
        return ensureIntializePreference().getString(key, defaultvalue);
    }

    public static int read(String key, int defaultvalue) {
        return ensureIntializePreference().getInt(key, defaultvalue);
    }

    public static long read(String key, long defaultvalue) {
        return ensureIntializePreference().getLong(key, defaultvalue);
    }
    
    public static float read(String key, float defaultvalue) {
        return ensureIntializePreference().getFloat(key, defaultvalue);
    }


    public static boolean read(String key, boolean defaultvalue) {
        return ensureIntializePreference().getBoolean(key, defaultvalue);
    }

    /**
     * 是否开启轮询推送
     * @return
     */
    public static boolean isOpenLoopPush() {
        return read(TAG_IS_OPEN_LOOP_PUSH, true);
    }
    
    public static void setOpenLoopPush(boolean isOpenNewMessagePush) {
        save(TAG_IS_OPEN_LOOP_PUSH, isOpenNewMessagePush);
    }
    
    /**
     * 是否开启通知铃声
     * 
     * @return
     */
    public static boolean isRingTone() {
        return read(TAG_NOTIFICATION_RING_TONE, true);
    }
    
    public static void setRingTone(boolean isRingTone) {
        save(TAG_NOTIFICATION_RING_TONE, isRingTone);
    }
    
    /**
     * 是否开启震动
     * 
     * @return
     */
    public static boolean isNewMailVibrate() {
        return read(TAG_NOTIFICATION_NEW_MAIL_VIBRATE, true);
    }
    
    public static void setNewMailVibrate(boolean isNewMailVibrate) {
        save(TAG_NOTIFICATION_NEW_MAIL_VIBRATE, isNewMailVibrate);
    }
}
