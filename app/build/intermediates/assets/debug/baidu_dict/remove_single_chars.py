#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
删除词库中的单字条目
保留两个或更多字符的词语
"""

import os
import glob

def remove_single_chars(file_path):
    """
    删除文件中所有单字符的条目
    """
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    filtered_lines = []
    for line in lines:
        line = line.strip()
        if not line:
            continue
        
        # 分割行，获取中文词语部分（第一部分）
        parts = line.split(' ', 1)
        if len(parts) < 2:
            # 如果格式不正确，保留原行
            filtered_lines.append(line + '\n')
            continue
            
        chinese_word = parts[0]
        
        # 计算中文词语的字符数
        word_length = len(chinese_word)
        
        # 只保留包含2个或更多字符的词语
        if word_length >= 2:
            filtered_lines.append(line + '\n')
    
    return filtered_lines

def main():
    # 获取当前目录下所有的.txt文件
    txt_files = glob.glob("*.txt")
    
    print(f"找到 {len(txt_files)} 个txt文件: {txt_files}")
    
    for file_path in txt_files:
        print(f"正在处理文件: {file_path}")
        
        try:
            filtered_lines = remove_single_chars(file_path)
            
            # 备份原文件
            backup_path = file_path + ".backup_single_removed"
            os.rename(file_path, backup_path)
            
            # 写入过滤后的内容
            with open(file_path, 'w', encoding='utf-8') as f:
                f.writelines(filtered_lines)
            
            print(f"已处理完成: {file_path} (原文件已备份为 {backup_path})")
            print(f"原始行数: {sum(1 for line in open(backup_path, 'r', encoding='utf-8'))}, 过滤后行数: {len(filtered_lines)}")
        
        except Exception as e:
            print(f"处理文件 {file_path} 时出错: {str(e)}")
    
    print("\n所有文件处理完成！")

if __name__ == "__main__":
    main()
