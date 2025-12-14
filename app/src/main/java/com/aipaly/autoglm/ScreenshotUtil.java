package com.aipaly.autoglm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;

public class ScreenshotUtil {
    private static final String TAG = "ScreenshotUtil";
    
    /**
     * 获取当前屏幕截图
     * Android 11及以上使用无障碍服务截图
     * 低版本使用模拟截图
     */
    public static Bitmap takeScreenshot(Context context) {
        AutoGLMService service = AutoGLMService.getInstance();
        
        if (service != null) {
            // Android 11+ 使用无障碍服务截图
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Log.d(TAG, "使用无障碍服务截图 (Android 11+)");
                Bitmap screenshot = service.takeScreenshot();
                if (screenshot != null) {
                    Log.d(TAG, "截图成功: " + screenshot.getWidth() + "x" + screenshot.getHeight());
                    return screenshot;
                } else {
                    Log.w(TAG, "无障碍服务截图失败，使用模拟截图");
                }
            } else {
                Log.d(TAG, "Android版本低于11，使用模拟截图");
            }
            
            // 低版本或截图失败时使用模拟截图
            return createMockScreenshot(service.getScreenWidth(), service.getScreenHeight());
        } else {
            Log.e(TAG, "无障碍服务未连接");
            // 返回默认尺寸的模拟截图
            return createMockScreenshot(1080, 2400);
        }
    }
    
    /**
     * 检查是否可以截图
     */
    public static boolean canTakeScreenshot() {
        AutoGLMService service = AutoGLMService.getInstance();
        if (service != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return true;
        }
        return false;
    }
    
    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth() {
        AutoGLMService service = AutoGLMService.getInstance();
        if (service != null) {
            return service.getScreenWidth();
        }
        return 1080;
    }
    
    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight() {
        AutoGLMService service = AutoGLMService.getInstance();
        if (service != null) {
            return service.getScreenHeight();
        }
        return 2400;
    }
    
    /**
     * 创建模拟截图（用于测试或低版本Android）
     */
    private static Bitmap createMockScreenshot(int width, int height) {
        Log.d(TAG, "创建模拟截图: " + width + "x" + height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // 填充浅灰色背景
        canvas.drawARGB(255, 240, 240, 240);
        return bitmap;
    }
}
