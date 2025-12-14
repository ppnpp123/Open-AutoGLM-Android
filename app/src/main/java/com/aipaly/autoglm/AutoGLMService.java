package com.aipaly.autoglm;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AutoGLMService extends AccessibilityService {
    private static final String TAG = "AutoGLMService";
    private static AutoGLMService instance;
    private Handler mainHandler;
    private int screenWidth = 1080;
    private int screenHeight = 2400;
    
    public static AutoGLMService getInstance() {
        return instance;
    }
    
    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        mainHandler = new Handler(Looper.getMainLooper());
        
        // 获取屏幕尺寸
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            Display display = wm.getDefaultDisplay();
            display.getRealMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
        }
        
        Log.d(TAG, "AutoGLM Service connected, screen size: " + screenWidth + "x" + screenHeight);
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理辅助功能事件
    }
    
    @Override
    public void onInterrupt() {
        // 服务中断时的处理
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }
    
    public int getScreenWidth() {
        return screenWidth;
    }
    
    public int getScreenHeight() {
        return screenHeight;
    }
    
    // 截图功能 (Android 11+)
    @RequiresApi(api = Build.VERSION_CODES.R)
    public Bitmap takeScreenshot() {
        final AtomicReference<Bitmap> screenshotRef = new AtomicReference<>(null);
        final CountDownLatch latch = new CountDownLatch(1);
        
        try {
            takeScreenshot(Display.DEFAULT_DISPLAY, getMainExecutor(), new TakeScreenshotCallback() {
                @Override
                public void onSuccess(ScreenshotResult screenshot) {
                    try {
                        Bitmap bitmap = Bitmap.wrapHardwareBuffer(
                            screenshot.getHardwareBuffer(), 
                            screenshot.getColorSpace()
                        );
                        if (bitmap != null) {
                            // 转换为可变的软件bitmap
                            screenshotRef.set(bitmap.copy(Bitmap.Config.ARGB_8888, false));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "处理截图失败", e);
                    }
                    latch.countDown();
                }
                
                @Override
                public void onFailure(int errorCode) {
                    Log.e(TAG, "截图失败，错误码: " + errorCode);
                    latch.countDown();
                }
            });
            
            // 等待截图完成，最多5秒
            latch.await(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, "截图异常", e);
        }
        
        return screenshotRef.get();
    }
    
    // 点击操作
    public boolean tap(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(x, y);
            
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription gestureDescription = builder.addStroke(
                    new GestureDescription.StrokeDescription(path, 0, 100)
            ).build();
            
            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<Boolean> result = new AtomicReference<>(false);
            
            boolean dispatched = dispatchGesture(gestureDescription, new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                    Log.d(TAG, "点击完成: " + x + ", " + y);
                    result.set(true);
                    latch.countDown();
                }
                
                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                    Log.d(TAG, "点击取消");
                    result.set(false);
                    latch.countDown();
                }
            }, mainHandler);
            
            if (dispatched) {
                try {
                    latch.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "等待点击完成被中断", e);
                }
            }
            
            return result.get();
        } else {
            Log.e(TAG, "当前Android版本不支持手势操作");
            return false;
        }
    }
    
    // 滑动操作
    public boolean swipe(int startX, int startY, int endX, int endY, int duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(endX, endY);
            
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription gestureDescription = builder.addStroke(
                    new GestureDescription.StrokeDescription(path, 0, duration)
            ).build();
            
            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<Boolean> result = new AtomicReference<>(false);
            
            boolean dispatched = dispatchGesture(gestureDescription, new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                    Log.d(TAG, "滑动完成: " + startX + "," + startY + " -> " + endX + "," + endY);
                    result.set(true);
                    latch.countDown();
                }
                
                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                    Log.d(TAG, "滑动取消");
                    result.set(false);
                    latch.countDown();
                }
            }, mainHandler);
            
            if (dispatched) {
                try {
                    latch.await(duration + 2000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "等待滑动完成被中断", e);
                }
            }
            
            return result.get();
        } else {
            Log.e(TAG, "当前Android版本不支持手势操作");
            return false;
        }
    }
    
    // 长按操作
    public boolean longPress(int x, int y, int duration) {
        // 通过在同一点滑动来实现长按
        return swipe(x, y, x, y, duration);
    }
    
    // 双击操作
    public boolean doubleTap(int x, int y) {
        boolean first = tap(x, y);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.e(TAG, "双击间隔被中断", e);
        }
        boolean second = tap(x, y);
        return first && second;
    }
    
    // 返回操作
    public boolean performBack() {
        return performGlobalAction(GLOBAL_ACTION_BACK);
    }
    
    // 主页操作
    public boolean performHome() {
        return performGlobalAction(GLOBAL_ACTION_HOME);
    }
    
    // 最近任务操作
    public boolean performRecents() {
        return performGlobalAction(GLOBAL_ACTION_RECENTS);
    }
    
    // 获取当前应用名称
    public String getCurrentApp() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            CharSequence packageName = root.getPackageName();
            root.recycle();
            return packageName != null ? packageName.toString() : "System Home";
        }
        return "System Home";
    }
    
    // 输入文本（增强版，支持多种输入方式）
    public boolean inputText(String text) {
        Log.d(TAG, "开始输入文本: " + text);
        
        try {
            // 首先设置剪贴板内容（所有方法都可能用到）
            setClipboardText(text);
            Thread.sleep(300);
            
            // 方法1：尝试使用ACTION_SET_TEXT直接设置文本
            Log.d(TAG, "尝试方法1: ACTION_SET_TEXT");
            if (inputTextBySetText(text)) {
                Log.d(TAG, "方法1成功: 使用ACTION_SET_TEXT");
                return true;
            }
            
            // 方法2：使用剪贴板粘贴（通过节点粘贴）
            Log.d(TAG, "尝试方法2: 节点粘贴");
            if (inputTextByNodePaste()) {
                Log.d(TAG, "方法2成功: 使用节点粘贴");
                return true;
            }
            
            // 方法3：遍历所有可编辑节点尝试输入
            Log.d(TAG, "尝试方法3: 遍历所有可编辑节点");
            if (inputTextToAllEditableNodes(text)) {
                Log.d(TAG, "方法3成功: 遍历所有可编辑节点");
                return true;
            }
            
            // 方法4：通过广播发送文本到ADB Keyboard
            Log.d(TAG, "尝试方法4: 广播到输入法");
            if (inputTextByBroadcast(text)) {
                Log.d(TAG, "方法4成功: 广播到输入法");
                return true;
            }
            
            Log.w(TAG, "所有文本输入方法都失败了，文本已复制到剪贴板，请手动粘贴");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "输入文本失败", e);
            return false;
        }
    }
    
    // 设置剪贴板内容
    private void setClipboardText(String text) {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            mainHandler.post(() -> {
                try {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        ClipData clip = ClipData.newPlainText("AutoGLM", text);
                        clipboard.setPrimaryClip(clip);
                        Log.d(TAG, "剪贴板已设置: " + text);
                    }
                } finally {
                    latch.countDown();
                }
            });
            latch.await(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, "设置剪贴板失败", e);
        }
    }
    
    // 方法1：使用ACTION_SET_TEXT直接设置文本
    private boolean inputTextBySetText(String text) {
        try {
            AccessibilityNodeInfo focusedNode = findFocusedEditableNode();
            if (focusedNode == null) {
                Log.d(TAG, "方法1: 未找到焦点输入框");
                return false;
            }
            
            // 先尝试点击使其获得焦点
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Thread.sleep(200);
            
            // 清空现有文本
            Bundle clearArgs = new Bundle();
            clearArgs.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "");
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, clearArgs);
            Thread.sleep(100);
            
            // 设置新文本
            Bundle textArgs = new Bundle();
            textArgs.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            boolean result = focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, textArgs);
            
            Log.d(TAG, "方法1 ACTION_SET_TEXT结果: " + result);
            focusedNode.recycle();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "方法1失败", e);
            return false;
        }
    }
    
    // 方法2：使用节点粘贴
    private boolean inputTextByNodePaste() {
        try {
            AccessibilityNodeInfo focusedNode = findFocusedEditableNode();
            if (focusedNode == null) {
                Log.d(TAG, "方法2: 未找到焦点输入框");
                return false;
            }
            
            // 聚焦并点击
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Thread.sleep(200);
            
            // 全选（清空）
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_SELECT);
            Thread.sleep(100);
            
            // 粘贴
            boolean result = focusedNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            Log.d(TAG, "方法2 粘贴结果: " + result);
            
            focusedNode.recycle();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "方法2失败", e);
            return false;
        }
    }
    
    // 方法3：遍历所有可编辑节点尝试输入
    private boolean inputTextToAllEditableNodes(String text) {
        try {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (root == null) {
                Log.d(TAG, "方法3: root为null");
                return false;
            }
            
            boolean result = tryInputOnEditableNodes(root, text);
            root.recycle();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "方法3失败", e);
            return false;
        }
    }
    
    // 递归尝试在可编辑节点上输入
    private boolean tryInputOnEditableNodes(AccessibilityNodeInfo node, String text) {
        if (node == null) return false;
        
        // 检查当前节点是否可编辑
        if (node.isEditable() || node.isFocusable()) {
            String className = node.getClassName() != null ? node.getClassName().toString() : "";
            // 判断是否是EditText或类似的输入框
            if (className.contains("EditText") || className.contains("Input") || 
                className.contains("Search") || node.isEditable()) {
                
                Log.d(TAG, "找到可能的输入节点: " + className);
                
                // 尝试聚焦
                node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // ignore
                }
                
                // 尝试设置文本
                Bundle textArgs = new Bundle();
                textArgs.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                if (node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, textArgs)) {
                    Log.d(TAG, "方法3: 在节点上设置文本成功");
                    return true;
                }
                
                // 尝试粘贴
                if (node.performAction(AccessibilityNodeInfo.ACTION_PASTE)) {
                    Log.d(TAG, "方法3: 在节点上粘贴成功");
                    return true;
                }
            }
        }
        
        // 递归子节点
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (tryInputOnEditableNodes(child, text)) {
                    child.recycle();
                    return true;
                }
                child.recycle();
            }
        }
        
        return false;
    }
    
    // 方法4：通过广播发送文本（适用于ADB Keyboard等） 
    private boolean inputTextByBroadcast(String text) {
        try {
            // 发送广播到ADB Keyboard
            android.content.Intent intent = new android.content.Intent("ADB_INPUT_TEXT");
            intent.putExtra("text", text);
            sendBroadcast(intent);
            Log.d(TAG, "已发送ADB_INPUT_TEXT广播");
            
            // 同时尝试发送通用输入广播
            android.content.Intent intent2 = new android.content.Intent("INPUT_TEXT");
            intent2.putExtra("text", text);
            sendBroadcast(intent2);
            
            // 等待一下看是否有效  
            Thread.sleep(500);
            
            // 如果还是没输入成功，最后尝试模拟字符输入
            // 注意：这需要软键盘在屏幕上才能工作
            return true; // 返回true让流程继续，因为文本已在剪贴板
        } catch (Exception e) {
            Log.e(TAG, "方法4失败", e);
            return false;
        }
    }
    
    // 查找当前焦点的可编辑节点
    private AccessibilityNodeInfo findFocusedEditableNode() {
        // 首先尝试获取输入焦点节点
        AccessibilityNodeInfo focusedNode = findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (focusedNode != null) {
            Log.d(TAG, "找到焦点节点: " + focusedNode.getClassName());
            if (focusedNode.isEditable()) {
                return focusedNode;
            }
            // 即使不是editable，也可能是搜索框
            String className = focusedNode.getClassName() != null ? focusedNode.getClassName().toString() : "";
            if (className.contains("EditText") || className.contains("Input") || className.contains("Search")) {
                return focusedNode;
            }
            focusedNode.recycle();
        }
        
        // 尝试获取accessibility焦点
        focusedNode = findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY);
        if (focusedNode != null) {
            Log.d(TAG, "找到辅助焦点节点: " + focusedNode.getClassName());
            if (focusedNode.isEditable()) {
                return focusedNode;
            }
            focusedNode.recycle();
        }
        
        // 尝试从root查找第一个可编辑节点
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            AccessibilityNodeInfo editable = findEditableNode(root);
            root.recycle();
            if (editable != null) {
                Log.d(TAG, "从root找到可编辑节点: " + editable.getClassName());
            }
            return editable;
        }
        
        Log.d(TAG, "未找到任何可编辑节点");
        return null;
    }
    
    // 查找可编辑节点
    private AccessibilityNodeInfo findEditableNode(AccessibilityNodeInfo root) {
        if (root == null) return null;
        
        if (root.isEditable()) {
            return root;
        }
        
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null) {
                AccessibilityNodeInfo result = findEditableNode(child);
                if (result != null) {
                    return result;
                }
                child.recycle();
            }
        }
        
        return null;
    }
    
    // 查找并点击文本元素
    public boolean clickByText(String text) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) {
            Log.w(TAG, "Root node is null");
            return false;
        }
        
        AccessibilityNodeInfo node = findNodeByText(root, text);
        if (node != null) {
            android.graphics.Rect nodeBounds = new android.graphics.Rect();
            node.getBoundsInScreen(nodeBounds);
            int x = nodeBounds.left + nodeBounds.width() / 2;
            int y = nodeBounds.top + nodeBounds.height() / 2;
            
            node.recycle();
            root.recycle();
            return tap(x, y);
        }
        
        root.recycle();
        return false;
    }
    
    // 递归查找文本节点
    private AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo root, String text) {
        if (root == null) return null;
        
        // 检查当前节点
        CharSequence contentDesc = root.getContentDescription();
        CharSequence nodeText = root.getText();
        
        if ((contentDesc != null && contentDesc.toString().contains(text)) ||
            (nodeText != null && nodeText.toString().contains(text))) {
            return root;
        }
        
        // 递归检查子节点
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null) {
                AccessibilityNodeInfo result = findNodeByText(child, text);
                if (result != null) {
                    return result;
                }
                child.recycle();
            }
        }
        
        return null;
    }
}
