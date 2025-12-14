#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
根据name.txt文件删除人名
不仅删除完全匹配的姓名，还删除匹配后两个字的词语
"""

import os
import glob

def load_names_from_file(filename):
    """
    从文件中加载姓名列表
    """
    with open(filename, 'r', encoding='utf-8') as f:
        content = f.read().strip()
    
    # 姓名以逗号分隔
    names = [name.strip() for name in content.split('、') if name.strip()]
    return names

def remove_names_from_files(names_list):
    """
    从所有txt文件中删除人名
    """
    # 获取所有txt文件
    txt_files = glob.glob("*.txt")
    
    # 创建一个集合，包含所有姓名和后两个字的组合
    names_set = set(names_list)
    last_two_chars_set = set()
    
    for name in names_list:
        if len(name) >= 2:
            # 添加后两个字
            last_two_chars = name[-2:]
            if len(last_two_chars) == 2:
                last_two_chars_set.add(last_two_chars)
    
    print(f"加载了 {len(names_list)} 个姓名")
    print(f"提取了 {len(last_two_chars_set)} 个后两个字的组合")
    
    for file_path in txt_files:
        print(f"正在处理文件: {file_path}")
        
        # 读取原文件
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        
        # 过滤行
        filtered_lines = []
        removed_count = 0
        
        for line in lines:
            line = line.strip()
            if not line:
                filtered_lines.append(line + '\n')
                continue
            
            # 分割行，获取中文词语部分（第一部分）
            parts = line.split(' ', 1)
            if len(parts) < 2:
                # 如果格式不正确，保留原行
                filtered_lines.append(line + '\n')
                continue
            
            chinese_word = parts[0]
            
            # 检查是否完全匹配姓名
            is_full_match = chinese_word in names_set
            
            # 检查是否匹配后两个字
            is_suffix_match = False
            if len(chinese_word) >= 2:
                word_suffix = chinese_word[-2:]
                is_suffix_match = word_suffix in last_two_chars_set
            
            # 如果是完全匹配或后缀匹配，则删除（不添加到结果中）
            if is_full_match or is_suffix_match:
                removed_count += 1
            else:
                # 不匹配则保留
                filtered_lines.append(line + '\n')
        
        # 备份原文件
        backup_path = file_path + ".backup_names_list_removed"
        os.rename(file_path, backup_path)
        
        # 写入过滤后的内容
        with open(file_path, 'w', encoding='utf-8') as f:
            f.writelines(filtered_lines)
        
        print(f"已处理完成: {file_path} (原文件已备份为 {backup_path})")
        print(f"原始行数: {sum(1 for line in open(backup_path, 'r', encoding='utf-8'))}, 过滤后行数: {len(filtered_lines)}, 删除了 {removed_count} 行")
    
    print("\n所有文件处理完成！")

def main():
    # 从name.txt加载姓名列表
    names_list = load_names_from_file("name.txt")
    
    # 从所有txt文件中删除人名
    remove_names_from_files(names_list)

if __name__ == "__main__":
    main()
