package com.aipaly.autoglm;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import android.util.Base64;
import android.util.Log;
import java.nio.charset.StandardCharsets;

/**
 * 百度词库帮助类
 * 用于加载和管理百度输入法词库数据
 */
public class BaiduDictHelper {
    private static HashMap<String, List<String>> fullDict = new HashMap<>();
    private static volatile boolean isLoaded = false;
    private static Context context;

    /**
     * 初始化词库工具类
     */
    public static void init(Context ctx) {
        context = ctx;
    }

    /**
     * 压缩字符串
     *
     * @param str 待压缩的字符串
     * @return 压缩后的字符串
     * @throws IOException 压缩过程中的IO异常
     */
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }

    /**
     * 解压缩字符串
     *
     * @param str 待解压缩的字符串
     * @return 解压缩后的字符串
     * @throws IOException 解压缩过程中的IO异常
     */
    public static String decompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(Base64.decode(str, Base64.DEFAULT));
        GZIPInputStream gzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toString(StandardCharsets.UTF_8.name());
    }

    /**
     * 加载百度词库数据
     * 从assets目录中的baidu_dict文件夹加载词库文件
     */
    public static synchronized void loadDict() {
        if (isLoaded) return;
        if (context == null) return;

        try {
            // 加载assets目录下的所有词库文件
            String[] dictFiles = {"baidu_dict/1.txt", "baidu_dict/2.txt", "baidu_dict/3.txt", "baidu_dict/4.txt", "baidu_dict/5.txt", "baidu_dict/6.txt", "baidu_dict/7.txt", "baidu_dict/8.txt", "baidu_dict/9.txt", "baidu_dict/10.txt"};

            for (String fileName : dictFiles) {
                try {
                    // 使用AssetManager加载资源
                    InputStream inputStream = context.getAssets().open(fileName);
                    if (inputStream != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (!line.isEmpty() && line.contains(" ")) {
                                // 解析每行格式：汉字拼音 汉字（如：yin'qi'bian'er'sui'ceng'ju'xi 因气变而遂曾举兮）
                                String[] parts = line.split(" ", 2); // 只分割为两部分
                                if (parts.length >= 2) {
                                    String pinyin = parts[1].trim();
                                    String chinese = parts[0].trim();

                                    // 存储拼音到汉字的映射
                                    List<String> list = fullDict.get(pinyin);
                                    if (list == null) {
                                        list = new ArrayList<>();
                                        fullDict.put(pinyin, list);
                                    }
                                    list.add(chinese);

                                    // 优化：不再存储单个拼音映射，大幅减少内存占用和加载时间
                                    // 单个拼音的匹配由PinyinHelper内置字典提供支持
                                }
                            }
                        }
                        reader.close();
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // 继续尝试下一个文件
                }
            }
            isLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据拼音获取对应的汉字（返回所有候选汉字的拼接字符串）
     */
    public static List<String> getChineseByPinyin(String pinyin) {
        if (!isLoaded) {
            loadDict();
        }
        List<String> list = fullDict.get(pinyin);
        return (list == null || list.isEmpty()) ? null : list;
    }

    /**
     * 检查是否包含指定的拼音
     */
    public static boolean containsPinyin(String pinyin) {
        if (!isLoaded) {
            loadDict();
        }
        return fullDict.containsKey(pinyin);
    }

    /**
     * 获取所有匹配的拼音
     * 注意：在大词库下遍历keySet非常耗时，应谨慎调用
     */
    public static List<String> getMatchingPinyins(String input) {
        if (!isLoaded) {
            loadDict();
        }
        List<String> matches = new ArrayList<>();
        for (String pinyin : fullDict.keySet()) {
            if (pinyin.startsWith(input)) {
                matches.add(pinyin);
            }
        }
        return matches;
    }

    /**
     * 获取所有匹配的汉字
     */
    public static List<String> getMatchingChinese(String input) {
        if (!isLoaded) {
            loadDict();
        }
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : fullDict.entrySet()) {
            if (entry.getKey().startsWith(input)) {
                matches.addAll(entry.getValue());
            }
        }
        return matches;
    }

    /**
     * 获取词典大小
     */
    public static int getDictSize() {
        if (!isLoaded) {
            loadDict();
        }
        return fullDict.size();
    }

    /**
     * 获取完整词典
     */
    public static Map<String, List<String>> getFullDict() {
         if (!isLoaded) {
            loadDict();
        }
        return new HashMap<>(fullDict);
    }

    /**
     * 获取匹配的拼音键（用于智能提示）
     */
    public static List<String> getMatchingPinyinKeys(String input) {
         if (!isLoaded) {
            loadDict();
        }
        List<String> matches = new ArrayList<>();
        for (String pinyin : fullDict.keySet()) {
            if (pinyin.startsWith(input) && pinyin.length() > input.length()) {
                matches.add(pinyin);
            }
        }
        return matches;
    }
}
