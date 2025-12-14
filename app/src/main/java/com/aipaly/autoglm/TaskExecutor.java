package com.aipaly.autoglm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskExecutor {
    private static final String TAG = "TaskExecutor";
    private static final int MAX_STEPS = 50;
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String MODEL_NAME = "autoglm-phone";
    
    private Context context;
    private String apiKey;
    private TextView tvStatus;
    private MainActivity mainActivity;
    private OkHttpClient client;
    private JSONArray conversationHistory;
    private int stepCount;
    private boolean isRunning;
    private String currentTask;

    public TaskExecutor(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.conversationHistory = new JSONArray();
        this.stepCount = 0;
        this.isRunning = false;
        
        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            this.tvStatus = activity.findViewById(R.id.tv_status);
            this.mainActivity = activity;
        }
    }

    public void executeTask(String task) {
        if (isRunning) {
            updateStatus("任务正在执行中，请等待");
            return;
        }
        
        this.currentTask = task;
        this.conversationHistory = new JSONArray();
        this.stepCount = 0;
        this.isRunning = true;
        
        new Thread(() -> {
            try {
                // 初始化对话，添加系统消息
                initConversation();
                
                // 执行第一步（包含用户任务）
                executeStep(true);
                
            } catch (Exception e) {
                Log.e(TAG, "执行任务时出错", e);
                updateStatus("执行任务时出错: " + e.getMessage());
                isRunning = false;
            }
        }).start();
    }
    
    private void initConversation() throws JSONException {
        // 添加系统消息
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", getSystemPrompt());
        conversationHistory.put(systemMessage);
    }
    
    private String getSystemPrompt() {
        // 获取当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
        String today = sdf.format(new Date());
        String[] weekdays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String weekday = weekdays[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1];
        
        return "今天的日期是: " + today + " " + weekday + "\n" +
                "你是一个智能体分析专家，可以根据操作历史和当前状态图执行一系列操作来完成任务。\n" +
                "你必须严格按照要求输出以下格式：\n" +
                "<think>{think}</think>\n" +
                "<answer>{action}</answer>\n\n" +
                "其中：\n" +
                "- {think} 是对你为什么选择这个操作的简短推理说明。\n" +
                "- {action} 是本次执行的具体操作指令，必须严格遵循下方定义的指令格式。\n\n" +
                "操作指令及其作用如下：\n" +
                "- do(action=\"Launch\", app=\"xxx\") - 启动目标app\n" +
                "- do(action=\"Tap\", element=[x,y]) - 点击操作，坐标从(0,0)到(999,999)\n" +
                "- do(action=\"Type\", text=\"xxx\") - 输入文本（注意：输入文本时系统会自动将文本复制到剪贴板，请一定要看下方说明）\n" +
                    "【重要！！！一定要看】文本输入说明：\n" +
                    "当需要在输入框中输入文本时，请按以下步骤操作：\n" +
                    "1. 先使用 do(action=\"Type\", text=\"要输入的文本\") 指令，系统会自动将文本复制到剪贴板\n" +
                    "2. 然后使用 do(action=\"Long Press\", element=[x,y]) 长按输入框\n" +
                    "3. 等待弹出菜单后，点击\"粘贴\"按钮完成输入\n" +
                    "这是因为当前运行环境无法使用键盘直接输入，必须通过剪贴板粘贴方式输入文本。\n\n" +
                "- do(action=\"Swipe\", start=[x1,y1], end=[x2,y2]) - 滑动操作\n" +
                "- do(action=\"Long Press\", element=[x,y]) - 长按操作\n" +
                "- do(action=\"Double Tap\", element=[x,y]) - 双击操作\n" +
                "- do(action=\"Back\") - 返回上一页面\n" +
                "- do(action=\"Home\") - 返回主页\n" +
                "- do(action=\"Wait\", duration=\"x seconds\") - 等待\n" +
                "- finish(message=\"xxx\") - 完成任务\n\n" +
                "必须遵循的规则：\n" +
                "1. 在执行任何操作前，先检查当前app是否是目标app。\n" +
                "2. 如果进入到无关页面，先执行Back。\n" +
                "3. 如果页面未加载出内容，最多连续Wait三次。\n" +
                "4. 在输入搜索关键词或内容时，必须使用Type+Long Press+点击粘贴的组合操作。\n" +
                "5. 在结束任务前请仔细检查任务是否完成。";
    }
    
    private void executeStep(boolean isFirstStep) {
        if (!isRunning) return;
        
        stepCount++;
        if (stepCount > MAX_STEPS) {
            updateStatus("已达到最大步数限制");
            appendLog("任务执行达到最大步数限制: " + MAX_STEPS);
            isRunning = false;
            return;
        }
        
        updateStatus("步骤 " + stepCount + ": 正在截图...");
        
        try {
            // 获取截图
            Bitmap screenshot = ScreenshotUtil.takeScreenshot(context);
            if (screenshot == null) {
                updateStatus("错误：无法获取屏幕截图");
                appendLog("截图失败");
                isRunning = false;
                return;
            }
            
            String base64Image = bitmapToBase64(screenshot);
            String currentApp = getCurrentApp();
            
            // 构建用户消息
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            
            JSONArray userContent = new JSONArray();
            
            // 添加图像
            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", "data:image/png;base64," + base64Image);
            imageContent.put("image_url", imageUrl);
            userContent.put(imageContent);
            
            // 添加文本
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            
            String textMessage;
            if (isFirstStep) {
                // 第一步包含任务描述
                JSONObject screenInfo = new JSONObject();
                screenInfo.put("current_app", currentApp);
                textMessage = currentTask + "\n\n" + screenInfo.toString();
            } else {
                // 后续步骤只包含屏幕信息
                JSONObject screenInfo = new JSONObject();
                screenInfo.put("current_app", currentApp);
                textMessage = "** Screen Info **\n\n" + screenInfo.toString();
            }
            textContent.put("text", textMessage);
            userContent.put(textContent);
            
            userMessage.put("content", userContent);
            conversationHistory.put(userMessage);
            
            // 调用API
            callModelAPI();
            
        } catch (Exception e) {
            Log.e(TAG, "执行步骤时出错", e);
            updateStatus("执行步骤时出错: " + e.getMessage());
            appendLog("步骤执行错误: " + e.getMessage());
            isRunning = false;
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 使用JPEG格式和适中的质量压缩以减小图片大小
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    private void callModelAPI() {
        updateStatus("步骤 " + stepCount + ": 正在调用AI模型...");
        
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("messages", conversationHistory);
            requestBody.put("model", MODEL_NAME);
            requestBody.put("max_tokens", 3000);
            requestBody.put("temperature", 0.0);
            requestBody.put("top_p", 0.85);
            requestBody.put("frequency_penalty", 0.2);
            requestBody.put("stream", false);  // 关键：禁用流式响应
            
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            
            appendLog("步骤 " + stepCount + " - API调用中...");
            
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API调用失败", e);
                    updateStatus("API调用失败: " + e.getMessage());
                    appendLog("API调用失败: " + e.getMessage());
                    isRunning = false;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "";
                            updateStatus("API响应失败: " + response.code());
                            appendLog("API响应失败: " + response.code() + " - " + errorBody);
                            isRunning = false;
                            return;
                        }
                        
                        String responseBody = response.body().string();
                        processApiResponse(responseBody);
                        
                    } catch (Exception e) {
                        Log.e(TAG, "处理API响应失败", e);
                        updateStatus("处理响应失败: " + e.getMessage());
                        appendLog("处理响应失败: " + e.getMessage());
                        isRunning = false;
                    } finally {
                        response.close();
                    }
                }
            });
            
        } catch (JSONException e) {
            Log.e(TAG, "构建请求失败", e);
            updateStatus("构建请求失败: " + e.getMessage());
            appendLog("构建请求失败: " + e.getMessage());
            isRunning = false;
        }
    }
    
    private void processApiResponse(String responseBody) throws JSONException {
        appendLog("原始响应: " + responseBody.substring(0, Math.min(responseBody.length(), 500)));
        
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray choices = jsonResponse.getJSONArray("choices");
        
        if (choices.length() == 0) {
            updateStatus("API响应中没有找到内容");
            appendLog("API响应为空");
            isRunning = false;
            return;
        }
        
        String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
        
        if (content == null || content.trim().isEmpty()) {
            appendLog("AI返回内容为空");
            updateStatus("AI返回内容为空");
            isRunning = false;
            return;
        }
        
        appendLog("AI响应内容:\n" + content);
        
        // 解析thinking和action
        String thinking = "";
        String action = "";
        
        // 方法1: 解析 <think> 和 <answer> 标签
        if (content.contains("<think>") && content.contains("</think>")) {
            int thinkStart = content.indexOf("<think>") + 7;
            int thinkEnd = content.indexOf("</think>");
            if (thinkEnd > thinkStart) {
                thinking = content.substring(thinkStart, thinkEnd).trim();
            }
        }
        
        if (content.contains("<answer>") && content.contains("</answer>")) {
            int answerStart = content.indexOf("<answer>") + 8;
            int answerEnd = content.indexOf("</answer>");
            if (answerEnd > answerStart) {
                action = content.substring(answerStart, answerEnd).trim();
            }
        }
        
        // 方法2: 如果没有标签，尝试直接解析 do() 或 finish() 格式
        if (action.isEmpty()) {
            if (content.contains("finish(message=")) {
                int finishStart = content.indexOf("finish(message=");
                int finishEnd = content.indexOf(")", finishStart);
                if (finishEnd > finishStart) {
                    action = content.substring(finishStart, finishEnd + 1).trim();
                } else {
                    action = content.substring(finishStart).trim();
                }
                // thinking是finish之前的文本
                if (finishStart > 0) {
                    thinking = content.substring(0, finishStart).trim();
                }
            } else if (content.contains("do(action=")) {
                int doStart = content.indexOf("do(action=");
                int doEnd = content.indexOf(")", doStart);
                if (doEnd > doStart) {
                    action = content.substring(doStart, doEnd + 1).trim();
                } else {
                    action = content.substring(doStart).trim();
                }
                // thinking是do之前的文本
                if (doStart > 0) {
                    thinking = content.substring(0, doStart).trim();
                }
            }
        }
        
        // 方法3: 如果还是空的，尝试按换行分割
        if (action.isEmpty()) {
            String[] lines = content.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("do(") || line.startsWith("finish(")) {
                    action = line;
                    break;
                }
            }
            // 如果没找到，整个内容作为action
            if (action.isEmpty()) {
                action = content.trim();
            }
        }
        
        appendLog("解析结果 - 思考: " + (thinking.isEmpty() ? "(空)" : thinking.substring(0, Math.min(thinking.length(), 100))));
        appendLog("解析结果 - 操作: " + action);
        
        // 添加助手响应到对话历史（移除图片以节省空间）
        JSONObject assistantMessage = new JSONObject();
        assistantMessage.put("role", "assistant");
        assistantMessage.put("content", "<think>" + thinking + "</think><answer>" + action + "</answer>");
        conversationHistory.put(assistantMessage);
        
        // 移除上一条用户消息中的图片内容以节省空间
        removeImageFromLastUserMessage();
        
        // 执行操作
        executeAction(action);
    }
    
    private void removeImageFromLastUserMessage() {
        try {
            for (int i = conversationHistory.length() - 1; i >= 0; i--) {
                JSONObject msg = conversationHistory.getJSONObject(i);
                if ("user".equals(msg.getString("role"))) {
                    Object contentObj = msg.get("content");
                    if (contentObj instanceof JSONArray) {
                        JSONArray contentArr = (JSONArray) contentObj;
                        JSONArray newContent = new JSONArray();
                        for (int j = 0; j < contentArr.length(); j++) {
                            JSONObject item = contentArr.getJSONObject(j);
                            if ("text".equals(item.optString("type"))) {
                                newContent.put(item);
                            }
                        }
                        msg.put("content", newContent);
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "移除图片内容失败", e);
        }
    }
    
    private void executeAction(String action) {
        updateStatus("步骤 " + stepCount + ": 执行操作...");
        
        try {
            // 检查是否为finish操作
            if (action.startsWith("finish(")) {
                String message = extractFinishMessage(action);
                updateStatus("任务完成: " + message);
                appendLog("✅ 任务完成: " + message);
                isRunning = false;
                return;
            }
            
            // 解析do()操作
            if (action.startsWith("do(")) {
                parseDo(action);
            } else {
                updateStatus("无法解析的操作: " + action);
                appendLog("无法解析的操作格式");
                isRunning = false;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "执行操作失败", e);
            updateStatus("执行操作失败: " + e.getMessage());
            appendLog("执行操作失败: " + e.getMessage());
            isRunning = false;
        }
    }
    
    private String extractFinishMessage(String action) {
        // 从 finish(message="xxx") 中提取消息
        try {
            int start = action.indexOf("message=") + 8;
            if (action.charAt(start) == '"') start++;
            int end = action.lastIndexOf(")");
            if (action.charAt(end - 1) == '"') end--;
            return action.substring(start, end);
        } catch (Exception e) {
            return action;
        }
    }
    
    private void parseDo(String action) {
        try {
            AutoGLMService service = AutoGLMService.getInstance();
            if (service == null) {
                updateStatus("无障碍服务不可用");
                appendLog("无障碍服务未连接");
                isRunning = false;
                return;
            }
            
            int screenWidth = ScreenshotUtil.getScreenWidth();
            int screenHeight = ScreenshotUtil.getScreenHeight();
            
            // 提取action类型
            String actionType = extractParameter(action, "action");
            appendLog("执行操作类型: " + actionType);
            
            boolean success = false;
            
            switch (actionType) {
                case "Tap": {
                    int[] coords = extractCoordinates(action, "element");
                    if (coords != null) {
                        int x = (int) (coords[0] / 1000.0 * screenWidth);
                        int y = (int) (coords[1] / 1000.0 * screenHeight);
                        success = service.tap(x, y);
                        appendLog("点击: (" + x + ", " + y + ")");
                    }
                    break;
                }
                
                case "Type":
                case "Type_Name": {
                    String text = extractParameter(action, "text");
                    if (text != null) {
                        appendLog("输入文本: " + text);
                        // 新逻辑：复制到剪贴板 → 长按输入框 → 点击粘贴
                        success = typeByClipboardPaste(service, text, screenWidth, screenHeight);
                    }
                    break;
                }
                
                case "Swipe": {
                    int[] start = extractCoordinates(action, "start");
                    int[] end = extractCoordinates(action, "end");
                    if (start != null && end != null) {
                        int startX = (int) (start[0] / 1000.0 * screenWidth);
                        int startY = (int) (start[1] / 1000.0 * screenHeight);
                        int endX = (int) (end[0] / 1000.0 * screenWidth);
                        int endY = (int) (end[1] / 1000.0 * screenHeight);
                        success = service.swipe(startX, startY, endX, endY, 500);
                        appendLog("滑动: (" + startX + "," + startY + ") -> (" + endX + "," + endY + ")");
                    }
                    break;
                }
                
                case "Long Press": {
                    int[] coords = extractCoordinates(action, "element");
                    if (coords != null) {
                        int x = (int) (coords[0] / 1000.0 * screenWidth);
                        int y = (int) (coords[1] / 1000.0 * screenHeight);
                        success = service.longPress(x, y, 1000);
                        appendLog("长按: (" + x + ", " + y + ")");
                    }
                    break;
                }
                
                case "Double Tap": {
                    int[] coords = extractCoordinates(action, "element");
                    if (coords != null) {
                        int x = (int) (coords[0] / 1000.0 * screenWidth);
                        int y = (int) (coords[1] / 1000.0 * screenHeight);
                        success = service.doubleTap(x, y);
                        appendLog("双击: (" + x + ", " + y + ")");
                    }
                    break;
                }
                
                case "Back": {
                    success = service.performBack();
                    appendLog("返回");
                    break;
                }
                
                case "Home": {
                    success = service.performHome();
                    appendLog("主页");
                    break;
                }
                
                case "Wait": {
                    String duration = extractParameter(action, "duration");
                    int seconds = 1;
                    if (duration != null) {
                        try {
                            seconds = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
                        } catch (NumberFormatException e) {
                            seconds = 1;
                        }
                    }
                    Thread.sleep(seconds * 1000L);
                    success = true;
                    appendLog("等待: " + seconds + "秒");
                    break;
                }
                
                case "Launch": {
                    String app = extractParameter(action, "app");
                    if (app != null) {
                        success = launchApp(app);
                        appendLog("启动应用: " + app);
                    }
                    break;
                }
                
                default:
                    appendLog("未知操作类型: " + actionType);
                    success = false;
            }
            
            // 等待操作生效
            Thread.sleep(1500);
            
            // 继续下一步
            if (isRunning) {
                executeStep(false);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "解析操作失败", e);
            updateStatus("解析操作失败: " + e.getMessage());
            appendLog("解析操作失败: " + e.getMessage());
            isRunning = false;
        }
    }
    
    private String extractParameter(String action, String param) {
        try {
            String pattern = param + "=\"";
            int start = action.indexOf(pattern);
            if (start >= 0) {
                start += pattern.length();
                int end = action.indexOf("\"", start);
                if (end > start) {
                    return action.substring(start, end);
                }
            }
            // 尝试不带引号的格式
            pattern = param + "=";
            start = action.indexOf(pattern);
            if (start >= 0) {
                start += pattern.length();
                int end = action.indexOf(",", start);
                if (end < 0) end = action.indexOf(")", start);
                if (end > start) {
                    String value = action.substring(start, end).trim();
                    return value.replace("\"", "");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "提取参数失败: " + param, e);
        }
        return null;
    }
    
    private int[] extractCoordinates(String action, String param) {
        try {
            String pattern = param + "=[";
            int start = action.indexOf(pattern);
            if (start >= 0) {
                start += pattern.length();
                int end = action.indexOf("]", start);
                if (end > start) {
                    String coordStr = action.substring(start, end);
                    String[] parts = coordStr.split(",");
                    if (parts.length >= 2) {
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        return new int[]{x, y};
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "提取坐标失败: " + param, e);
        }
        return null;
    }
    
    private boolean launchApp(String appName) {
        String packageName = getAppPackageName(appName);
        if (packageName == null) {
            appendLog("未找到应用: " + appName);
            return false;
        }
        
        try {
            PackageManager pm = context.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "启动应用失败", e);
        }
        return false;
    }
    
    private String getAppPackageName(String appName) {
        switch (appName) {
            case "微信": return "com.tencent.mm";
            case "QQ": return "com.tencent.mobileqq";
            case "微博": return "com.sina.weibo";
            case "淘宝": return "com.taobao.taobao";
            case "京东": return "com.jingdong.app.mall";
            case "美团": return "com.sankuai.meituan";
            case "小红书": return "com.xingin.xhs";
            case "抖音": return "com.ss.android.ugc.aweme";
            case "支付宝": return "com.eg.android.AlipayGphone";
            case "百度": return "com.baidu.searchbox";
            case "高德地图": return "com.autonavi.minimap";
            case "知乎": return "com.zhihu.android";
            case "哔哩哔哩":
            case "bilibili": return "tv.danmaku.bili";
            case "网易云音乐": return "com.netease.cloudmusic";
            case "饿了么": return "me.ele";
            case "设置": return "com.android.settings";
            case "相机": return "com.android.camera";
            case "浏览器": return "com.android.browser";
            default: return appName; // 假设传入的就是包名
        }
    }

    // 通过剪贴板粘贴方式输入文本（先尝试输入法，再尝试剪贴板粘贴）
    private boolean typeByClipboardPaste(AutoGLMService service, String text, int screenWidth, int screenHeight) {
        try {
            appendLog("开始执行文本输入流程...");
            
            // 方法1：尝试通过输入法服务直接输入
            appendLog("尝试方法1: 通过输入法服务输入");
            if (AutoGLMInputMethodService.inputTextDirectly(text)) {
                appendLog("输入法服务输入成功！");
                Thread.sleep(300);
                return true;
            }
            
            // 方法2：发送输入法广播
            appendLog("尝试方法2: 发送输入法广播");
            AutoGLMInputMethodService.sendInputText(context, text);
            Thread.sleep(500);
            
            // 方法3：通过无障碍服务的inputText方法（会设置剪贴板并尝试粘贴）
            appendLog("尝试方法3: 通过无障碍服务输入");
            boolean inputSuccess = service.inputText(text);
            if (inputSuccess) {
                appendLog("无障碍服务输入成功！");
                return true;
            }
            
            // 方法4：手动复制到剪贴板并执行粘贴操作
            appendLog("尝试方法4: 手动复制粘贴流程");
            
            // 步骤1：文本已经在剪贴板中（inputText会设置）
            Thread.sleep(300);
            
            // 步骤2：尝试直接粘贴（通过无障碍服务查找输入框并粘贴）
            appendLog("尝试直接在输入框执行粘贴...");
            
            // 尝试点击"粘贴"文字按钮（如果有弹出菜单的话）
            boolean pasteClicked = service.clickByText("粘贴");
            if (!pasteClicked) {
                pasteClicked = service.clickByText("Paste");
            }
            
            if (pasteClicked) {
                appendLog("点击粘贴按钮成功！");
                Thread.sleep(300);
                return true;
            }
            
            // 步骤3：如果上面都失败，执行长按+粘贴流程
            appendLog("执行长按粘贴流程...");
            int centerX = screenWidth / 2;
            int inputY = screenHeight / 4;
            
            service.longPress(centerX, inputY, 1000);
            Thread.sleep(800);
            
            pasteClicked = service.clickByText("粘贴");
            if (!pasteClicked) {
                pasteClicked = service.clickByText("Paste");
            }
            if (!pasteClicked) {
                service.tap(centerX, inputY - 100);
            }
            
            Thread.sleep(500);
            appendLog("文本输入流程完成（文本已在剪贴板）");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "文本输入失败", e);
            appendLog("文本输入失败: " + e.getMessage());
            return false;
        }
    }

    private String getCurrentApp() {
        AutoGLMService service = AutoGLMService.getInstance();
        if (service != null) {
            return service.getCurrentApp();
        }
        return "Unknown";
    }

    private void updateStatus(String status) {
        if (mainActivity != null && tvStatus != null) {
            mainActivity.runOnUiThread(() -> tvStatus.setText("状态: " + status));
        }
    }
    
    private void appendLog(String message) {
        if (mainActivity != null) {
            mainActivity.appendToLog(message);
        }
    }
    
    public void stopTask() {
        isRunning = false;
        updateStatus("任务已停止");
        appendLog("任务被用户停止");
    }
}
