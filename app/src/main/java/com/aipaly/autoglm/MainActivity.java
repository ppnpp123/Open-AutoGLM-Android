package com.aipaly.autoglm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ACCESSIBILITY_PERMISSION = 1;
    private EditText editApiKey;
    private EditText editTask;
    private Button btnStartTask;
    private Button btnStopTask;
    private Button btnSaveApiKey;
    private TextView tvStatus;
    private TextView tvLog;
    private TaskExecutor currentTaskExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        loadApiKey();
        checkAccessibilityService();
    }

    private void initViews() {
        editApiKey = findViewById(R.id.edit_api_key);
        editTask = findViewById(R.id.edit_task);
        btnStartTask = findViewById(R.id.btn_start_task);
        btnStopTask = findViewById(R.id.btn_stop_task);
        btnSaveApiKey = findViewById(R.id.btn_save_api_key);
        tvStatus = findViewById(R.id.tv_status);
        tvLog = findViewById(R.id.tv_log);
        
        // 设置日志框可滚动
        tvLog.setMovementMethod(new ScrollingMovementMethod());

        btnSaveApiKey.setOnClickListener(v -> saveApiKey());
        btnStartTask.setOnClickListener(v -> startTask());
        
        // 初始化停止按钮
        if (btnStopTask != null) {
            btnStopTask.setOnClickListener(v -> stopTask());
            btnStopTask.setEnabled(false);
        }
    }

    private void checkAccessibilityService() {
        if (!isAccessibilityServiceEnabled()) {
            showAccessibilityDialog();
        } else {
            tvStatus.setText("状态: 无障碍服务已启用，等待输入");
        }
    }

    private void showAccessibilityDialog() {
        new AlertDialog.Builder(this)
            .setTitle("需要无障碍权限")
            .setMessage("AutoGLM需要无障碍权限来控制手机屏幕。请在设置中启用AutoGLM无障碍服务。")
            .setPositiveButton("去设置", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, REQUEST_ACCESSIBILITY_PERMISSION);
            })
            .setNegativeButton("稍后", (dialog, which) -> {
                tvStatus.setText("状态: 需要启用无障碍服务");
            })
            .setCancelable(false)
            .show();
    }

    private void saveApiKey() {
        String apiKey = editApiKey.getText().toString().trim();
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "请输入API密钥", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AutoGLM", MODE_PRIVATE);
        prefs.edit().putString("api_key", apiKey).apply();
        Toast.makeText(this, "API密钥已保存", Toast.LENGTH_SHORT).show();
    }

    private void loadApiKey() {
        SharedPreferences prefs = getSharedPreferences("AutoGLM", MODE_PRIVATE);
        String apiKey = prefs.getString("api_key", "");
        editApiKey.setText(apiKey);
    }

    private void startTask() {
        // 检查无障碍服务
        if (!isAccessibilityServiceEnabled()) {
            showAccessibilityDialog();
            return;
        }
        
        String task = editTask.getText().toString().trim();
        if (task.isEmpty()) {
            Toast.makeText(this, "请输入任务描述", Toast.LENGTH_SHORT).show();
            return;
        }

        String apiKey = editApiKey.getText().toString().trim();
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "请先保存API密钥", Toast.LENGTH_SHORT).show();
            return;
        }

        tvStatus.setText("状态: 任务执行中...");
        
        // 清空之前的日志并添加新的任务开始信息
        tvLog.setText("");
        appendToLog("========== 新任务开始 ==========");
        appendToLog("任务: " + task);
        appendToLog("================================\n");
        
        // 设置按钮状态
        btnStartTask.setEnabled(false);
        if (btnStopTask != null) {
            btnStopTask.setEnabled(true);
        }
        
        // 启动任务处理
        currentTaskExecutor = new TaskExecutor(this, apiKey);
        currentTaskExecutor.executeTask(task);
    }
    
    private void stopTask() {
        if (currentTaskExecutor != null) {
            currentTaskExecutor.stopTask();
            currentTaskExecutor = null;
        }
        
        btnStartTask.setEnabled(true);
        if (btnStopTask != null) {
            btnStopTask.setEnabled(false);
        }
        tvStatus.setText("状态: 任务已停止");
    }
    
    public void appendToLog(String message) {
        runOnUiThread(() -> {
            String currentLog = tvLog.getText().toString();
            String timestamp = new java.text.SimpleDateFormat("HH:mm:ss", 
                java.util.Locale.getDefault()).format(new java.util.Date());
            String newLog = currentLog + "\n[" + timestamp + "] " + message;
            tvLog.setText(newLog);
            
            // 滚动到底部
            final int scrollAmount = tvLog.getLayout() != null ? 
                tvLog.getLayout().getLineTop(tvLog.getLineCount()) - tvLog.getHeight() : 0;
            if (scrollAmount > 0) {
                tvLog.scrollTo(0, scrollAmount);
            } else {
                tvLog.scrollTo(0, 0);
            }
        });
    }
    
    public void onTaskComplete() {
        runOnUiThread(() -> {
            btnStartTask.setEnabled(true);
            if (btnStopTask != null) {
                btnStopTask.setEnabled(false);
            }
            currentTaskExecutor = null;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 检查无障碍服务状态
        if (isAccessibilityServiceEnabled()) {
            tvStatus.setText("状态: 无障碍服务已启用，等待输入");
        } else {
            tvStatus.setText("状态: 需要启用无障碍服务");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCESSIBILITY_PERMISSION) {
            if (isAccessibilityServiceEnabled()) {
                Toast.makeText(this, "无障碍服务已启用", Toast.LENGTH_SHORT).show();
                tvStatus.setText("状态: 无障碍服务已启用，等待输入");
            } else {
                tvStatus.setText("状态: 需要启用无障碍服务");
            }
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        String service = getPackageName() + "/" + AutoGLMService.class.getCanonicalName();
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(), 
                    Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getContentResolver(), 
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            if (settingValue != null) {
                return settingValue.contains(service);
            }
        }
        return false;
    }
}
