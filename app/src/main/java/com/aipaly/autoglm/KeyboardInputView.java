package com.aipaly.autoglm;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

public class KeyboardInputView extends LinearLayout implements View.OnClickListener {
    private Context context;
    private AutoGLMInputMethodService inputMethodService;
    private StringBuilder currentInput = new StringBuilder(); // 当前输入的拼音
    private List<String> candidates; // 候选词列表
    private List<String> displayedCandidates; // 当前显示的候选词列表
    private LinearLayout candidateContainer; // 候选词容器
    private TextView pinyinDisplay; // 拼音显示文本框
    private Button pageUpBtn, pageDownBtn; // 上一页和下一页按钮
    private TextView pageInfo; // 页码信息

    // 每页显示的候选词数量
    private static final int CANDIDATES_PER_PAGE = 10;
    // 当前页码（从0开始）
    private int currentPage = 0;
    // 总页数
    private int totalPages = 0;

    // 按钮引用
    private Button keyQ, keyW, keyE, keyR, keyT, keyY, keyU, keyI, keyO, keyP;
    private Button keyA, keyS, keyD, keyF, keyG, keyH, keyJ, keyK, keyL;
    private Button keyZ, keyX, keyC, keyV, keyB, keyN, keyM, keyBackspace;
    // 数字键
    private Button key1, key2, key3, key4, key5, key6, key7, key8, key9, key0;
    // 隐藏输入法按钮
    private Button keyHide;
    // 用于处理退格键长按删除
    private Handler backspaceHandler = new Handler(Looper.getMainLooper());
    private Runnable backspaceRunnable;
    private Button keySpace, keyComma, keyPeriod, keySwitch, keyEnter;
    private Button keySegment; // 分词按钮
    private Button keyToggleNum; // 数字键盘切换按钮
    private LinearLayout numericContainer; // 数字键盘容器

    // 添加一个变量来保存当前的分词结果
    private List<String> currentSegmentation = null;

    public KeyboardInputView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public KeyboardInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public KeyboardInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.keyboard_view, this, true);

        // 初始化按钮
        initViews();

        // 设置按钮点击监听器
        setClickListeners();

        // 初始化候选词容器
        candidateContainer = findViewById(R.id.candidate_container);
        
        // 初始化键盘布局（设置为中文模式）
        isEnglishMode = false;
        updateKeyboardLayout();
    }

    @Override
    public void onClick(View v) {
        if (inputMethodService == null) return;

        if (v == keyQ) appendToInput("q");
        else if (v == keyW) appendToInput("w");
        else if (v == keyE) appendToInput("e");
        else if (v == keyR) appendToInput("r");
        else if (v == keyT) appendToInput("t");
        else if (v == keyY) appendToInput("y");
        else if (v == keyU) appendToInput("u");
        else if (v == keyI) appendToInput("i");
        else if (v == keyO) appendToInput("o");
        else if (v == keyP) appendToInput("p");
        else if (v == keyA) appendToInput("a");
        else if (v == keyS) appendToInput("s");
        else if (v == keyD) appendToInput("d");
        else if (v == keyF) appendToInput("f");
        else if (v == keyG) appendToInput("g");
        else if (v == keyH) appendToInput("h");
        else if (v == keyJ) appendToInput("j");
        else if (v == keyK) appendToInput("k");
        else if (v == keyL) appendToInput("l");
        else if (v == keyZ) appendToInput("z");
        else if (v == keyX) appendToInput("x");
        else if (v == keyC) appendToInput("c");
        else if (v == keyV) appendToInput("v");
        else if (v == keyB) appendToInput("b");
        else if (v == keyN) appendToInput("n");
        else if (v == keyM) appendToInput("m");
        else if (v == keyBackspace) handleBackspace();
        else if (v == keySpace) handleSpace();
        else if (v == keyComma) handleSymbol(",");
        else if (v == keyPeriod) handleSymbol(".");
        else if (v == keySwitch) handleSwitch();
        else if (v == keyEnter) handleEnter();
        else if (v == keySegment) handleSegment(); // 分词按钮
        else if (v == keyHide) handleHide(); // 隐藏输入法按钮
        else if (v == keyToggleNum) handleToggleNumeric(); // 数字键盘切换按钮
        else if (v == pageUpBtn) handlePageUp();
        else if (v == pageDownBtn) handlePageDown();
    }

    private void initViews() {
        keyQ = findViewById(R.id.key_q);
        keyW = findViewById(R.id.key_w);
        keyE = findViewById(R.id.key_e);
        keyR = findViewById(R.id.key_r);
        keyT = findViewById(R.id.key_t);
        keyY = findViewById(R.id.key_y);
        keyU = findViewById(R.id.key_u);
        keyI = findViewById(R.id.key_i);
        keyO = findViewById(R.id.key_o);
        keyP = findViewById(R.id.key_p);
        keyA = findViewById(R.id.key_a);
        keyS = findViewById(R.id.key_s);
        keyD = findViewById(R.id.key_d);
        keyF = findViewById(R.id.key_f);
        keyG = findViewById(R.id.key_g);
        keyH = findViewById(R.id.key_h);
        keyJ = findViewById(R.id.key_j);
        keyK = findViewById(R.id.key_k);
        keyL = findViewById(R.id.key_l);
        keyZ = findViewById(R.id.key_z);
        keyX = findViewById(R.id.key_x);
        keyC = findViewById(R.id.key_c);
        keyV = findViewById(R.id.key_v);
        keyB = findViewById(R.id.key_b);
        keyN = findViewById(R.id.key_n);
        keyM = findViewById(R.id.key_m);
        keyBackspace = findViewById(R.id.key_backspace);
        // 数字键
        key1 = findViewById(R.id.key_1);
        key2 = findViewById(R.id.key_2);
        key3 = findViewById(R.id.key_3);
        key4 = findViewById(R.id.key_4);
        key5 = findViewById(R.id.key_5);
        key6 = findViewById(R.id.key_6);
        key7 = findViewById(R.id.key_7);
        key8 = findViewById(R.id.key_8);
        key9 = findViewById(R.id.key_9);
        key0 = findViewById(R.id.key_0);
        // 隐藏输入法按钮
        keyHide = findViewById(R.id.key_hide);
        keySpace = findViewById(R.id.key_space);
        keyComma = findViewById(R.id.key_comma);
        keyPeriod = findViewById(R.id.key_period);
        keySwitch = findViewById(R.id.key_switch);
        keyEnter = findViewById(R.id.key_enter);
        keySegment = findViewById(R.id.key_segment); // 分词按钮
        keyToggleNum = findViewById(R.id.key_toggle_num);
        numericContainer = findViewById(R.id.numeric_container);
        pinyinDisplay = findViewById(R.id.pinyin_display);
        pageUpBtn = findViewById(R.id.page_up_btn);
        pageDownBtn = findViewById(R.id.page_down_btn);
        pageInfo = findViewById(R.id.page_info);
    }

    private void setClickListeners() {
        keyQ.setOnClickListener(this);
        keyW.setOnClickListener(this);
        keyE.setOnClickListener(this);
        keyR.setOnClickListener(this);
        keyT.setOnClickListener(this);
        keyY.setOnClickListener(this);
        keyU.setOnClickListener(this);
        keyI.setOnClickListener(this);
        keyO.setOnClickListener(this);
        keyP.setOnClickListener(this);
        keyA.setOnClickListener(this);
        keyS.setOnClickListener(this);
        keyD.setOnClickListener(this);
        keyF.setOnClickListener(this);
        keyG.setOnClickListener(this);
        keyH.setOnClickListener(this);
        keyJ.setOnClickListener(this);
        keyK.setOnClickListener(this);
        keyL.setOnClickListener(this);
        keyZ.setOnClickListener(this);
        keyX.setOnClickListener(this);
        keyC.setOnClickListener(this);
        keyV.setOnClickListener(this);
        keyB.setOnClickListener(this);
        keyN.setOnClickListener(this);
        keyM.setOnClickListener(this);
        keyBackspace.setOnClickListener(this);
        // 数字键
        key1.setOnClickListener(this);
        key2.setOnClickListener(this);
        key3.setOnClickListener(this);
        key4.setOnClickListener(this);
        key5.setOnClickListener(this);
        key6.setOnClickListener(this);
        key7.setOnClickListener(this);
        key8.setOnClickListener(this);
        key9.setOnClickListener(this);
        key0.setOnClickListener(this);
        // 隐藏输入法按钮
        keyHide.setOnClickListener(this);
        keySpace.setOnClickListener(this);
        keyComma.setOnClickListener(this);
        keyPeriod.setOnClickListener(this);
        keySwitch.setOnClickListener(this);
        keyEnter.setOnClickListener(this);
        keySegment.setOnClickListener(this); // 分词按钮
        keyToggleNum.setOnClickListener(this);
        pageUpBtn.setOnClickListener(this);
        pageDownBtn.setOnClickListener(this);

        // 长按退格键连续删除
        keyBackspace.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 立即执行一次删除
                        handleBackspace();
                        // 启动循环删除
                        backspaceRunnable = new Runnable() {
                            @Override
                            public void run() {
                                handleBackspace();
                                backspaceHandler.postDelayed(this, 100); // 100ms 间隔
                            }
                        };
                        backspaceHandler.postDelayed(backspaceRunnable, 300); // 按住 300ms 后开始循环
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // 停止循环删除
                        if (backspaceRunnable != null) {
                            backspaceHandler.removeCallbacks(backspaceRunnable);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    public void setInputMethodService(AutoGLMInputMethodService service) {
        this.inputMethodService = service;
    }


    private void appendToInput(String letter) {
        if (isEnglishMode) {
            // 英文模式下直接输入字符
            inputMethodService.commitText(letter);
        } else {
            // 中文模式下将字母添加到拼音输入
            currentInput.append(letter.toLowerCase());
            // 清除分词状态，因为输入发生了变化
            currentSegmentation = null;
            updateCandidates();
        }
    }

    private void handleBackspace() {
        if (currentInput.length() > 0) {
            // 删除当前拼音输入的最后一个字符
            currentInput.deleteCharAt(currentInput.length() - 1);
            // 清除分词状态，因为输入发生了变化
            currentSegmentation = null;
            // 更新候选词显示
            updateCandidates();
        } else {
            // 删除原输入框中的文字或选中文本
            InputConnection ic = inputMethodService.getCurrentInputConnection();
            if (ic != null) {
                CharSequence selected = ic.getSelectedText(0);
                if (selected != null && selected.length() > 0) {
                    // 删除选中的文本
                    ic.commitText("", 1);
                } else {
                    // 删除光标前的一个字符
                    ic.deleteSurroundingText(1, 0);
                }
            }
        }
    }

    /**
     * 提交选中的候选词，并处理剩余拼音
     */
    private void commitCandidate(String candidate) {
        if (inputMethodService == null) return;

        // 将候选词提交到输入框
        inputMethodService.commitText(candidate);

        String input = currentInput.toString();

        // 处理包含分词分隔符（单引号）的情况
        if (input.contains("'")) {
            // 假设当前候选词来源于分隔符前的音节
            int delimiterIdx = input.indexOf('\'');
            // 若候选词匹配整个输入（完整词组），则清空所有输入
            List<String> fullMatches = BaiduDictHelper.getChineseByPinyin(input);
            if (fullMatches != null && fullMatches.contains(candidate)) {
                currentInput.setLength(0);
            } else {
                // 移除已选音节及其分隔符，仅保留分隔符后的后缀
                String remaining = input.substring(delimiterIdx + 1);
                currentInput.setLength(0);
                currentInput.append(remaining);
                // 若剩余部分仍然包含分隔符，需要保留其位置
                // 此处不再自动添加额外分隔符，保持用户手动分词的状态
            }
        } else {
            // 原有逻辑：无分隔符时按普通拼音处理
            List<String> segments = PinyinHelper.segmentPinyin(input);
            // 构造全拼音key用于完整匹配检查
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < segments.size(); i++) {
                if (i > 0) sb.append("'");
                sb.append(segments.get(i));
            }
            String key = sb.toString();
            List<String> fullMatches = BaiduDictHelper.getChineseByPinyin(key);
            if (fullMatches != null && fullMatches.contains(candidate)) {
                // 完全匹配，清空所有输入
                currentInput.setLength(0);
            } else {
                // 只删除已使用的首个音节
                if (!segments.isEmpty()) {
                    String firstSeg = segments.get(0);
                    int len = firstSeg.length();
                    if (len <= currentInput.length()) {
                        currentInput.delete(0, len);
                    } else {
                        currentInput.setLength(0);
                    }
                } else {
                    currentInput.setLength(0);
                }
            }
        }

        // 清除分词状态并刷新候选词
        currentSegmentation = null;
        // 选词后重置分页到首页
        currentPage = 0;
        updateCandidates();
    }

    private void handleSpace() {
        if (currentInput.length() > 0) {
            // 如果有输入的拼音，先确认选择
            if (candidates != null && !candidates.isEmpty()) {
                // 默认选择第一个候选词
                commitCandidate(candidates.get(0));
            } else {
                // 如果没有候选词，直接输入空格
                inputMethodService.commitText(" ");
            }
        } else {
            // 如果没有输入拼音，直接输入空格
            inputMethodService.commitText(" ");
        }
    }

    private void handleSymbol(String symbol) {
        if (currentInput.length() > 0) {
            // 如果有输入的拼音，先确认选择
            if (candidates != null && !candidates.isEmpty()) {
                commitCandidate(candidates.get(0));
            }
            currentInput.setLength(0); // 符号输入强制结束拼音
        }
        // 输入符号
        inputMethodService.commitText(symbol);
        updateCandidates();
    }

    private void handleEnter() {
        if (currentInput.length() > 0) {
            // 如果有输入的拼音，先确认选择
            if (candidates != null && !candidates.isEmpty()) {
                commitCandidate(candidates.get(0));
            }
            currentInput.setLength(0); // 回车强制结束拼音
        }
        // 输入回车
        inputMethodService.commitText("\n");
        updateCandidates();
    }

    /**
     * 隐藏输入法
     */
    private void handleHide() {
        if (inputMethodService != null) {
            // 请求隐藏键盘
            inputMethodService.requestHideSelf(0);
        }
    }

    private void updateCandidates() {
        if (candidateContainer == null) return;

        candidateContainer.removeAllViews();
        if (currentInput.length() == 0) {
            // 清空拼音显示
            if (pinyinDisplay != null) {
                pinyinDisplay.setText("");
            }
            // 重置分页信息
            currentPage = 0;
            totalPages = 0;
            if (pageInfo != null) {
                pageInfo.setText("");
            }
            // 清空候选词列表
            candidates = null;
            displayedCandidates = null;
            // 隐藏拼音和候选区
            if (pinyinDisplay != null) {
                pinyinDisplay.setVisibility(android.view.View.VISIBLE);
            }
            if (candidateContainer != null) {
                candidateContainer.setVisibility(android.view.View.VISIBLE);
            }
            // 更新分页按钮状态
            updatePageButtons();
            // 清空当前分词状态
            currentSegmentation = null;
            return;
        }

        String input = currentInput.toString();

        // 处理分词分隔符（单引号）场景
        if (input.contains("'")) {
            // 直接使用原始输入显示拼音串，保留分隔符
            if (pinyinDisplay != null) {
                pinyinDisplay.setText(input);
            }

            // 提取最后一个分隔符前后的子串
            int lastIdx = input.lastIndexOf('\'');
            String prePart = input.substring(0, lastIdx);
            String postPart = input.substring(lastIdx + 1);

            // 生成完整词组匹配
            List<String> fullMatches = BaiduDictHelper.getChineseByPinyin(input);

            // 生成前缀的首音单字候选（如果存在前缀）
            List<String> firstSegSingle = new ArrayList<>();
            if (!prePart.isEmpty()) {
                List<String> preSegList = PinyinHelper.segmentPinyin(prePart);
                if (preSegList != null && !preSegList.isEmpty()) {
                    String firstSeg = preSegList.get(0);
                    List<String> firstSegChars = PinyinHelper.getChineseByPinyin(firstSeg);
                    if (firstSegChars != null) {
                        for (String ch : firstSegChars) {
                            if (ch.length() == 1 && !firstSegSingle.contains(ch)) {
                                firstSegSingle.add(ch);
                            }
                        }
                    }
                }
            }

            // 生成后缀（当前输入段）的候选词
            List<String> postCandidates = null;
            if (!postPart.isEmpty()) {
                List<String> postSegList = PinyinHelper.segmentPinyin(postPart);
                postCandidates = generateCandidatesFromSegmentation(postSegList);
            }

            // 合并候选词：完整词组 > 首音单字候选 > 后缀候选
            candidates = new ArrayList<>();
            if (fullMatches != null && !fullMatches.isEmpty()) {
                candidates.addAll(fullMatches);
            }
            if (!firstSegSingle.isEmpty()) {
                candidates.addAll(firstSegSingle);
            }
            if (postCandidates != null) {
                candidates.addAll(postCandidates);
            }
            // 若所有列表均为空，则候选词列表保持为空，以便后续自动分词能够继续产生结果
        } else if (currentSegmentation != null && !currentSegmentation.isEmpty()) {
            // 合并可能的特殊声母并生成显示字符串
            String displayStr = buildPinyinDisplayString(currentSegmentation);
            if (pinyinDisplay != null) {
                pinyinDisplay.setText(displayStr);
            }

            // 根据分词结果生成候选词
            if (currentSegmentation.size() > 1) {
                // 多音节：仅针对最后一个音节获取候选词
                String lastPinyin = currentSegmentation.get(currentSegmentation.size() - 1);
                candidates = generateCandidatesFromSinglePinyin(lastPinyin);
            } else {
                // 单音节（包括 zh、ch、sh 等组合）：直接使用整体拼音生成候选词
                candidates = generateCandidatesFromSegmentation(currentSegmentation);
            }
        } else {
            // 自动分词并显示在拼音栏
            List<String> autoSegments = PinyinHelper.segmentPinyin(input);
            if (pinyinDisplay != null) {
                if (autoSegments.size() > 1) {
                    // 合并特殊双字母声母（zh、ch、sh）后再显示
                    pinyinDisplay.setText(buildPinyinDisplayString(autoSegments));
                } else {
                    pinyinDisplay.setText(input);
                }
            }
            // 使用分词候选生成逻辑，确保单字等完整显示
            candidates = generateCandidatesFromSegmentation(autoSegments);
        }

        // 重置分页至第1页，确保切换音节或选词后页码从头开始
        currentPage = 0;
        // 计算分页
        calculatePagination();

        // 显示当前页的候选词
        displayCurrentPageCandidates();

        // 更新分页按钮状态
        updatePageButtons();
    }


    private boolean isEnglishMode = false; // 是否是英文输入模式

    private void handleSwitch() {
        // 切换中英文输入模式
        isEnglishMode = !isEnglishMode;
        // 更新键盘布局，使字母大小写同步切换
        updateKeyboardLayout();
    }

    /**
     * 更新键盘布局（中英文模式）
     */
    private void updateKeyboardLayout() {
        // 根据输入模式更新键盘显示
        if (isEnglishMode) {
            // 英文模式 - 按钮显示小写字母
            updateKeyButtonText(keyQ, "q");
            updateKeyButtonText(keyW, "w");
            updateKeyButtonText(keyE, "e");
            updateKeyButtonText(keyR, "r");
            updateKeyButtonText(keyT, "t");
            updateKeyButtonText(keyY, "y");
            updateKeyButtonText(keyU, "u");
            updateKeyButtonText(keyI, "i");
            updateKeyButtonText(keyO, "o");
            updateKeyButtonText(keyP, "p");
            updateKeyButtonText(keyA, "a");
            updateKeyButtonText(keyS, "s");
            updateKeyButtonText(keyD, "d");
            updateKeyButtonText(keyF, "f");
            updateKeyButtonText(keyG, "g");
            updateKeyButtonText(keyH, "h");
            updateKeyButtonText(keyJ, "j");
            updateKeyButtonText(keyK, "k");
            updateKeyButtonText(keyL, "l");
            updateKeyButtonText(keyZ, "z");
            updateKeyButtonText(keyX, "x");
            updateKeyButtonText(keyC, "c");
            updateKeyButtonText(keyV, "v");
            updateKeyButtonText(keyB, "b");
            updateKeyButtonText(keyN, "n");
            updateKeyButtonText(keyM, "m");
        } else {
            // 中文模式 - 按钮显示大写字母
            updateKeyButtonText(keyQ, "Q");
            updateKeyButtonText(keyW, "W");
            updateKeyButtonText(keyE, "E");
            updateKeyButtonText(keyR, "R");
            updateKeyButtonText(keyT, "T");
            updateKeyButtonText(keyY, "Y");
            updateKeyButtonText(keyU, "U");
            updateKeyButtonText(keyI, "I");
            updateKeyButtonText(keyO, "O");
            updateKeyButtonText(keyP, "P");
            updateKeyButtonText(keyA, "A");
            updateKeyButtonText(keyS, "S");
            updateKeyButtonText(keyD, "D");
            updateKeyButtonText(keyF, "F");
            updateKeyButtonText(keyG, "G");
            updateKeyButtonText(keyH, "H");
            updateKeyButtonText(keyJ, "J");
            updateKeyButtonText(keyK, "K");
            updateKeyButtonText(keyL, "L");
            updateKeyButtonText(keyZ, "Z");
            updateKeyButtonText(keyX, "X");
            updateKeyButtonText(keyC, "C");
            updateKeyButtonText(keyV, "V");
            updateKeyButtonText(keyB, "B");
            updateKeyButtonText(keyN, "N");
            updateKeyButtonText(keyM, "M");
        }
        // 异步刷新视图
        this.post(new Runnable() {
            @Override
            public void run() {
                KeyboardInputView.this.invalidate();
                KeyboardInputView.this.requestLayout();
            }
        });
    }

    /**
     * 更新按键文本
     */
    private void updateKeyButtonText(Button btn, String text) {
        if (btn != null) {
            btn.setAllCaps(false);
            btn.setText(text);
            btn.post(new Runnable() {
                @Override
                public void run() {
                    btn.invalidate();  // 在主线程中重新刷新
                }
            });
        }
    }

    /**
     * 计算分页信息
     */
    private void calculatePagination() {
        if (candidates == null || candidates.isEmpty()) {
            totalPages = 0;
            currentPage = 0;
            return;
        }

        // 计算总页数
        totalPages = (int) Math.ceil((double) candidates.size() / CANDIDATES_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        // 确保当前页在有效范围内
        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
        if (currentPage < 0) {
            currentPage = 0;
        }

        // 更新页码信息显示
        if (pageInfo != null) {
            pageInfo.setText((currentPage + 1) + "/" + totalPages);
        }
    }

    /**
     * 显示当前页的候选词
     */
    private void displayCurrentPageCandidates() {
        if (candidateContainer == null || candidates == null || candidates.isEmpty()) {
            return;
        }

        candidateContainer.removeAllViews();

        // 计算当前页的起始和结束索引
        int startIndex = currentPage * CANDIDATES_PER_PAGE;
        int endIndex = Math.min(startIndex + CANDIDATES_PER_PAGE, candidates.size());

        // 显示当前页的候选词
        displayedCandidates = candidates.subList(startIndex, endIndex);
        for (String candidate : displayedCandidates) {
            TextView candidateView = new TextView(context);
            candidateView.setText(candidate);
            candidateView.setPadding(16, 8, 16, 8);
            candidateView.setTextSize(16);
            candidateView.setBackground(context.getResources().getDrawable(R.drawable.key_background));
            candidateView.setClickable(true);
            candidateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 选择候选词
                    commitCandidate(candidate);
                }
            });

            candidateContainer.addView(candidateView);
        }
    }

    /**
     * 更新分页按钮状态
     */
    private void updatePageButtons() {
        if (pageUpBtn != null) {
            pageUpBtn.setEnabled(currentPage > 0);
            if (currentPage > 0) {
                pageUpBtn.setTextColor(context.getResources().getColor(android.R.color.black));
            } else {
                pageUpBtn.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            }
        }

        if (pageDownBtn != null) {
            pageDownBtn.setEnabled(currentPage < totalPages - 1 && totalPages > 1);
            if (currentPage < totalPages - 1 && totalPages > 1) {
                pageDownBtn.setTextColor(context.getResources().getColor(android.R.color.black));
            } else {
                pageDownBtn.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            }
        }

        if (pageInfo != null) {
            pageInfo.setText((currentPage + 1) + "/" + totalPages);
        }
    }

    /**
     * 上一页
     */
    private void handlePageUp() {
        if (currentPage > 0) {
            currentPage--;
            displayCurrentPageCandidates();
            updatePageButtons();
        }
    }

    /**
     * 下一页
     */
    private void handlePageDown() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayCurrentPageCandidates();
            updatePageButtons();
        }
    }

    /**
     * 切换数字键盘的显示状态
     */
    private void handleToggleNumeric() {
        if (numericContainer == null) return;
        if (numericContainer.getVisibility() == View.VISIBLE) {
            numericContainer.setVisibility(View.GONE);
        } else {
            numericContainer.setVisibility(View.VISIBLE);
        }
    }

    public String getCurrentInput() {
        return currentInput.toString();
    }

    public void clearInput() {
        currentInput.setLength(0);
        updateCandidates();
    }

    /**
     * 处理分词按钮点击事件
     */
    private void handleSegment() {
        if (currentInput.length() == 0) {
            // no input, do nothing
            return;
        }
        // Append segmentation delimiter to current input
        currentInput.append("'");
        // Clear any previous segmentation state
        currentSegmentation = null;
        // Update display and candidates
        updateCandidates();
    }

    /**
     * 显示分词选项供用户选择
     */
    private void displaySegmentationOptions(List<List<String>> segmentationOptions) {
        if (candidateContainer == null) return;

        candidateContainer.removeAllViews();

        // 为每种分词方案创建一个选项
        for (int i = 0; i < segmentationOptions.size(); i++) {
            List<String> option = segmentationOptions.get(i);
            if (option.size() <= 1) continue; // 跳过单个拼音的选项
            
            StringBuilder optionText = new StringBuilder();
            for (int j = 0; j < option.size(); j++) {
                if (j > 0) {
                    optionText.append("'"); // 使用单引号分隔
                }
                optionText.append(option.get(j));
            }

            TextView optionView = new TextView(context);
            optionView.setText(optionText.toString());
            optionView.setPadding(16, 8, 16, 8);
            optionView.setTextSize(16);
            optionView.setBackground(context.getResources().getDrawable(R.drawable.key_background));
            optionView.setClickable(true);
            final List<String> selectedOption = segmentationOptions.get(i); // 添加final修饰符
            optionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 应用选中的分词方案
                    applySegmentation(selectedOption);
                    // 恢复正常的候选词显示
                    updateCandidates();
                }
            });

            candidateContainer.addView(optionView);
        }

        // 更新页码信息
        if (pageInfo != null) {
            pageInfo.setText("1/1");
        }

        // 禁用分页按钮，因为现在显示的是分词选项而不是候选词
        if (pageUpBtn != null) {
            pageUpBtn.setEnabled(false);
        }
        if (pageDownBtn != null) {
            pageDownBtn.setEnabled(false);
        }
    }

    /**
     * 应用分词结果
     */
    private void applySegmentation(List<String> segmentation) {
        // 将分词结果显示在拼音显示框中，使用统一的分词展示函数，确保使用单引号分隔
        String displayStr = buildPinyinDisplayString(segmentation);
        if (pinyinDisplay != null) {
            // 强制显示分词结果，使用单引号分隔
            pinyinDisplay.setText(displayStr);
            // 确保拼音显示框可见并立即刷新
            pinyinDisplay.setVisibility(android.view.View.VISIBLE);
            pinyinDisplay.requestLayout();
            pinyinDisplay.invalidate(); // 强制重绘，确保分词标点即时可见
        }

        // 清空原有候选词，以免误导
        if (candidateContainer != null) {
            candidateContainer.removeAllViews();
            // 同时隐藏候选词容器，避免残留旧候选词干扰视图
            candidateContainer.setVisibility(android.view.View.GONE);
        }

        // 保存分词结果以供后续候选词计算
        currentSegmentation = segmentation; // 保存当前分词结果

        // 立即刷新候选词，确保基于分词结果生成
        updateCandidates();
    }

    /**
     * 根据分词结果生成候选词
     */
    private List<String> generateCandidatesFromSegmentation(List<String> segmentation) {
        if (segmentation == null || segmentation.isEmpty()) {
            return new ArrayList<>();
        }
        // 合并特殊双字母声母（zh、ch、sh）防止被误拆分
        if (segmentation.size() >= 2) {
            String first = segmentation.get(0);
            String second = segmentation.get(1);
            String combined = first + second;
            if (combined.equals("zh") || combined.equals("ch") || combined.equals("sh")) {
                List<String> merged = new java.util.ArrayList<>();
                merged.add(combined);
                if (segmentation.size() > 2) {
                    merged.addAll(segmentation.subList(2, segmentation.size()));
                }
                segmentation = merged;
            }
        }

        String key = String.join("'", segmentation);
        // 如果有多音节，指定第一个音节以获取单字候选
        String firstSeg = (segmentation.size() > 1) ? segmentation.get(0) : null;
        return generateCandidatesForPinyinKey(key, firstSeg);
    }

    // 在自动分词展示时同样合并特殊声母
    private String buildPinyinDisplayString(List<String> segments) {
        if (segments.size() >= 2) {
            String first = segments.get(0);
            String second = segments.get(1);
            String combined = first + second;
            if (combined.equals("zh") || combined.equals("ch") || combined.equals("sh")) {
                java.util.List<String> merged = new java.util.ArrayList<>();
                merged.add(combined);
                if (segments.size() > 2) {
                    merged.addAll(segments.subList(2, segments.size()));
                }
                segments = merged;
            }
        }
        if (segments.size() > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < segments.size(); i++) {
                if (i > 0) sb.append("'");
                sb.append(segments.get(i));
            }
            return sb.toString();
        } else {
            return segments.get(0);
        }
    }

    /**
     * 为单个音节生成候选词（用于已分词场景，仅展示该音节的候选字）
     */
    private List<String> generateCandidatesFromSinglePinyin(String pinyin) {
        return generateCandidatesForPinyinKey(pinyin, null);
    }

    /**
     * 根据拼音Key生成候选词（通用逻辑）
     * 规则：
     * 1. 精确匹配：单字优先，其余按长度升序
     * 2. 首音节单字：如果是多音节输入，在精确匹配后补充首音节的单字
     * 3. 拓展匹配：仅在当前输入后再扩展一个字母的拼音（如 d → da/di/du，da → dan/dao）
     */
    private List<String> generateCandidatesForPinyinKey(String key, String firstSeg) {
        List<String> result = new ArrayList<>();
        if (key == null || key.isEmpty()) return result;

        boolean singleSegment = !key.contains("'");
        // 规则1：单字母或特殊双字母声母输入时不进行完整词库查询，只扩展一个字母的拼音
        // 特殊双字母声母包括 zh、ch、sh
        boolean isSpecialTwoLetterInitial = key.equals("zh") || key.equals("ch") || key.equals("sh");
        if (singleSegment && (key.length() == 1 || isSpecialTwoLetterInitial)) {
            // 获取匹配的拼音键，长度必须为 2（即再扩展一个字母）
            List<String> matchingKeys = new ArrayList<>();
            List<String> baiduKeys = BaiduDictHelper.getMatchingPinyins(key);
            if (baiduKeys != null) matchingKeys.addAll(baiduKeys);
            List<String> helperKeys = PinyinHelper.getMatchingPinyins(key);
            if (helperKeys != null) {
                for (String k : helperKeys) {
                    if (!matchingKeys.contains(k)) matchingKeys.add(k);
                }
            }
            for (String k : matchingKeys) {
                if (k.length() == key.length() + 1) {
                    List<String> vals = BaiduDictHelper.getChineseByPinyin(k);
                    if (vals == null) vals = PinyinHelper.getChineseByPinyin(k);
                    if (vals != null) {
                        for (String s : vals) {
                            if (!result.contains(s)) result.add(s);
                        }
                    }
                }
            }
            return result;
        }

        // 规则2：其余情况保留原有的精确匹配逻辑（单字优先，其余长度升序）
        List<String> exactSingle = new ArrayList<>();
        List<String> exactMulti = new ArrayList<>();

        List<String> baiduMatches = BaiduDictHelper.getChineseByPinyin(key);
        if (baiduMatches != null) {
            for (String s : baiduMatches) {
                if (s.length() == 1) {
                    if (!exactSingle.contains(s)) exactSingle.add(s);
                } else {
                    if (!exactMulti.contains(s)) exactMulti.add(s);
                }
            }
        }

        if (!key.contains("'")) {
            List<String> helperMatches = PinyinHelper.getChineseByPinyin(key);
            if (helperMatches != null) {
                for (String s : helperMatches) {
                    if (s.length() == 1) {
                        if (!exactSingle.contains(s)) exactSingle.add(s);
                    } else {
                        if (!exactMulti.contains(s)) exactMulti.add(s);
                    }
                }
            }
        }

        exactMulti.sort((a, b) -> Integer.compare(a.length(), b.length()));
        result.addAll(exactSingle);
        result.addAll(exactMulti);

        // 规则3：如果是多音节输入，补充首音节的单字候选
        if (firstSeg != null) {
            List<String> segCandidates = PinyinHelper.getChineseByPinyin(firstSeg);
            if (segCandidates != null) {
                for (String s : segCandidates) {
                    if (s.length() == 1 && !result.contains(s)) result.add(s);
                }
            }
        }

        // 规则4：拓展匹配，仅扩展一个字母的拼音（key 长度 +1）
        List<String> matchingKeys = new ArrayList<>();
        List<String> baiduKeys = BaiduDictHelper.getMatchingPinyins(key);
        if (baiduKeys != null) matchingKeys.addAll(baiduKeys);
        List<String> helperKeys = PinyinHelper.getMatchingPinyins(key);
        if (helperKeys != null) {
            for (String k : helperKeys) {
                if (!matchingKeys.contains(k)) matchingKeys.add(k);
            }
        }

        for (String k : matchingKeys) {
            if (!k.equals(key) && k.startsWith(key) && k.length() == key.length() + 1) {
                List<String> vals = BaiduDictHelper.getChineseByPinyin(k);
                if (vals == null) vals = PinyinHelper.getChineseByPinyin(k);
                if (vals != null) {
                    vals.sort((a, b) -> Integer.compare(a.length(), b.length()));
                    for (String s : vals) {
                        if (!result.contains(s)) result.add(s);
                    }
                }
            }
        }

        return result;
    }
}
