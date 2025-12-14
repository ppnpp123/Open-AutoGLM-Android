package com.aipaly.autoglm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * AutoGLM输入法服务
 * 用于处理键盘输入和文本输出
 */
public class AutoGLMInputMethodService extends InputMethodService {
    private static final String TAG = "AutoGLMInputMethod";

    // 广播Action：用于接收输入文本请求
    public static final String ACTION_INPUT_TEXT = "com.aipaly.autoglm.INPUT_TEXT";
    public static final String EXTRA_TEXT = "text";

    private static AutoGLMInputMethodService instance;
    private BroadcastReceiver inputReceiver;
    private KeyboardInputView keyboardInputView;

    public static AutoGLMInputMethodService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG, "AutoGLM输入法服务已创建");

        // 初始化词库
        BaiduDictHelper.init(this);

        // 注册广播接收器
        registerInputReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;

        // 注销广播接收器
        if (inputReceiver != null) {
            unregisterReceiver(inputReceiver);
            inputReceiver = null;
        }

        Log.d(TAG, "AutoGLM输入法服务已销毁");
    }

    private void registerInputReceiver() {
        inputReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ACTION_INPUT_TEXT.equals(intent.getAction())) {
                    String text = intent.getStringExtra(EXTRA_TEXT);
                    if (text != null && !text.isEmpty()) {
                        Log.d(TAG, "收到输入请求: " + text);
                        commitText(text);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(ACTION_INPUT_TEXT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(inputReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(inputReceiver, filter);
        }

        Log.d(TAG, "输入广播接收器已注册");
    }

    @Override
    public View onCreateInputView() {
        // 创建完整的键盘输入视图
        keyboardInputView = new KeyboardInputView(this);
        keyboardInputView.setInputMethodService(this);
        return keyboardInputView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        Log.d(TAG, "输入法视图启动");
        if (keyboardInputView != null) {
            keyboardInputView.clearInput(); // 清空之前输入的内容
        }
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        Log.d(TAG, "输入法视图结束");
    }

    /**
     * 提交文本到当前输入框
     */
    public void commitText(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            // 清空当前选中内容
            ic.commitText("", 0);
            // 输入新文本
            ic.commitText(text, 1);
            Log.d(TAG, "文本已提交: " + text);
        } else {
            Log.w(TAG, "InputConnection为null，无法提交文本");
        }
    }

    /**
     * 静态方法：发送输入文本广播
     */
    public static void sendInputText(Context context, String text) {
        Intent intent = new Intent(ACTION_INPUT_TEXT);
        intent.putExtra(EXTRA_TEXT, text);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
        Log.d(TAG, "已发送输入广播: " + text);
    }

    /**
     * 直接通过实例输入文本
     */
    public static boolean inputTextDirectly(String text) {
        if (instance != null) {
            instance.commitText(text);
            return true;
        }
        return false;
    }

    /**
     * 切换中英文输入模式
     */
    public void switchLanguage() {
        // 通过系统API切换输入法
        Intent intent = new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 切换输入法
     */
    public void switchInputMethod() {
        // 请求系统显示输入法选择器
        requestHideSelf(0);
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
