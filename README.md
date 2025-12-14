# AutoGLM Android 输入法

这是一个基于AutoGLM项目的安卓输入法应用，支持拼音输入和百度词库，旨在提供流畅的中文输入体验。

## 功能特点

- **完整键盘布局**：包含标准QWERTY键盘布局，支持字母输入
- **拼音输入**：支持中文拼音输入，自动匹配汉字
- **词库支持**：集成百度词库，提供丰富的词汇
- **智能候选**：根据输入拼音智能推荐候选汉字
- **功能键支持**：支持删除、空格、回车等常用功能键
- **自定义词库**：可扩展的词库系统

## 安装步骤

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 在app模块中构建APK
4. 安装APK到Android设备

## 使用方法

1. 安装完成后，在系统设置中启用"AutoGLM输入法"
2. 在需要输入的地方选择"AutoGLM输入法"
3. 使用虚拟键盘进行输入
4. 拼音输入时，候选词会显示在候选栏中，点击选择需要的汉字

## 文件结构

```
autoGLM-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/aipaly/autoglm/     # Java源代码
│   │   │   ├── AutoGLMInputMethodService.java  # 输入法服务主类
│   │   │   ├── KeyboardInputView.java         # 键盘输入视图
│   │   │   ├── PinyinHelper.java              # 拼音匹配辅助类
│   │   │   └── BaiduDictHelper.java           # 百度词库辅助类
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── keyboard_view.xml          # 键盘布局文件
│   │   │   └── drawable/
│   │   │       ├── key_background.xml          # 按键背景样式
│   │   │       ├── key_background_delete.xml   # 删除键背景样式
│   │   │       └── key_background_enter.xml    # 回车键背景样式
│   │   └── assets/
│   │       └── baidu_dict/                     # 百度词库文件
└── README.md
```

## 技术实现

### 键盘布局
- 使用LinearLayout实现标准QWERTY键盘布局
- 包含四个字母行和功能键行
- 响应式设计，适配不同屏幕尺寸

### 拼音匹配
- 集成pinyin_dict_notone.js词典
- 支持单字和多音节词匹配
- 智能前缀匹配算法

### 词库集成
- 百度词库数据存储在assets/baidu_dict目录
- 支持从文本文件加载词库
- 大小约为5GB的完整词库

### 输入法服务
- 继承InputMethodService实现输入法功能
- 处理键盘事件和文本输入
- 与其他应用交互输入内容

## 扩展功能

- 可通过修改assets/baidu_dict目录下的文件扩展词库
- 支持添加自定义短语和常用词
- 可扩展键盘布局以支持其他输入方式

## 注意事项

- 需要在系统设置中启用输入法服务
- 初始词库加载可能需要一些时间
- 应用需要存储权限以加载词库文件

## 开发说明

本项目基于Android输入法框架开发，遵循Android输入法服务规范。主要组件包括：
- KeyboardInputView：处理键盘事件和显示
- PinyinHelper：拼音匹配和汉字转换
- BaiduDictHelper：词库管理和检索
- AutoGLMInputMethodService：输入法服务主类

## 许可证

本项目仅供学习和研究使用。
