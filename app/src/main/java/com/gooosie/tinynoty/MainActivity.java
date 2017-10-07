package com.gooosie.tinynoty;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private RelativeLayout mContainer;
    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private TextView mButtonClear;
    private TextView mButtonOK;
    private CheckBox mCheckBoxSelfStarting;

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private NotificationManager mNotificationManager;

    private long mExitTime;
    private long mSettingTime;
    private int mSettingCount;

    public static final String KEY_SETTING = "setting";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_SELF_STARTING = "self_starting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = (RelativeLayout) findViewById(R.id.rl_container);
        mEditTextTitle = (EditText) findViewById(R.id.edit_text_title);
        mEditTextContent = (EditText) findViewById(R.id.edit_text_content);
        mButtonClear = (TextView) findViewById(R.id.text_clear);
        mButtonOK = (TextView) findViewById(R.id.text_ok);
        mCheckBoxSelfStarting = (CheckBox) findViewById(R.id.check_box_self_starting);

        mContainer.setOnClickListener(this);
        mButtonOK.setOnClickListener(this);
        mButtonClear.setOnClickListener(this);
        mCheckBoxSelfStarting.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mEditTextContent.setSingleLine(false);
            mEditTextContent.setMaxLines(5);
        }

        mContext = getApplicationContext();
        mSharedPreferences = getSharedPreferences(KEY_SETTING, MODE_PRIVATE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mEditTextTitle.setText(mSharedPreferences.getString(KEY_TITLE, ""));
        mEditTextContent.setText(mSharedPreferences.getString(KEY_CONTENT, ""));
        mCheckBoxSelfStarting.setChecked(mSharedPreferences.getBoolean(KEY_SELF_STARTING, false));

        Intent intent = getIntent();
        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_TEXT)) {
                mEditTextContent.getText().append("\n").append(extras.getString(Intent.EXTRA_TEXT));
            }
        }
    }

    @Override
    protected void onPause() {
        if (mEditTextTitle.getText().length() != 0)  {
            mSharedPreferences.edit().putString(KEY_TITLE, mEditTextTitle.getText().toString()).apply();
        }
        if (mEditTextContent.getText().length() != 0) {
            mSharedPreferences.edit().putString(KEY_CONTENT, mEditTextContent.getText().toString()).apply();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitTime > 2000L) {
            Toast.makeText(this, R.string.toast_double_click_to_exit, Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.text_ok:
                showNoty(mEditTextTitle.getText().toString(), mEditTextContent.getText().toString());
                break;
            case R.id.text_clear:
                clearNoty();
                break;
            case R.id.check_box_self_starting:
                mSharedPreferences.edit().putBoolean(KEY_SELF_STARTING, ((CheckBox)v).isChecked()).apply();
                break;
            default:
                if ((System.currentTimeMillis() - mSettingTime) > 2000L && mSettingCount <= 2) {
                    mSettingTime = System.currentTimeMillis();
                    mSettingCount++;
                    break;
                }
                if (mSettingCount++ >= 2) {
                    mCheckBoxSelfStarting.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, R.string.show_setting, Toast.LENGTH_SHORT).show();
                    mSettingCount = 0;
                }
                break;
        }
    }

//    @SuppressLint("NewApi")
    private void showNoty(String title, String content) {
        if (title.length() <= 0 && content.length() <= 0) {
            return;
        }

        mSharedPreferences.edit().putString(KEY_TITLE, title).apply();
        mSharedPreferences.edit().putString(KEY_CONTENT, content).apply();

        Notification notification = null;

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            // API < 16.
            notification = new Notification(R.drawable.ic_noty, title, System.currentTimeMillis());
            notification.flags = Notification.FLAG_NO_CLEAR;
            // Remove by target API > 22
            notification.setLatestEventInfo(mContext, title, content, pendingIntent);
        } else {
            // API >= 16
            notification = new Notification.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_noty)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setStyle(new Notification.BigTextStyle().bigText(content))
                    .build();
            notification.flags = Notification.FLAG_NO_CLEAR;
        }

        mNotificationManager.notify(1, notification);
        finish();
    }

    private void clearNoty() {
        mNotificationManager.cancelAll();
        mEditTextTitle.setText("");
        mEditTextContent.setText("");
        mSharedPreferences.edit().putString(KEY_TITLE, "").apply();
        mSharedPreferences.edit().putString(KEY_CONTENT, "").apply();
        finish();
    }
}
