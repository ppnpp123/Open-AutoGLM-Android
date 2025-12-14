#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
清理1.txt到10.txt中冗余的词语
删除词语和拼音都匹配的重复行（即完全相同的行）
"""

import os
import glob

def remove_duplicates():
    """
    清理所有txt文件中的冗余词语
    """
    # 获取1.txt到10.txt文件
    txt_files = []
    for i in range(1, 11):
        filename = f"{i}.txt"
        if os.path.exists(filename):
            txt_files.append(filename)
    
    print(f"找到 {len(txt_files)} 个txt文件: {txt_files}")
    
    # 读取所有文件内容并合并
    all_lines = set()  # 使用集合自动去重
    total_original_lines = 0
    
    for file_path in txt_files:
        print(f"正在读取文件: {file_path}")
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        
        for line in lines:
            line = line.strip()
            if line:  # 只处理非空行
                all_lines.add(line)  # 添加到集合中自动去重
                total_original_lines += 1
    
    print(f"原始总行数: {total_original_lines}")
    print(f"去重后行数: {len(all_lines)}")
    print(f"删除了 {total_original_lines - len(all_lines)} 个重复行")
    
    # 将去重后的内容重新分配到各个文件
    # 这里我们保持每个文件的相对大小比例，或者平均分配
    lines_list = list(all_lines)
    total_files = len(txt_files)
    
    # 平均分配行到各个文件
    lines_per_file = len(lines_list) // total_files
    remainder = len(lines_list) % total_files
    
    start_idx = 0
    for i, file_path in enumerate(txt_files):
        # 计算当前文件应包含的行数
        current_file_lines = lines_per_file
        if i < remainder:  # 将余数分配给前面的文件
            current_file_lines += 1
        
        # 获取当前文件的行
        end_idx = start_idx + current_file_lines
        current_lines = lines_list[start_idx:end_idx]
        
        # 备份原文件
        backup_path = file_path + ".backup_deduplicated"
        if os.path.exists(file_path):
            os.rename(file_path, backup_path)
        
        # 写入去重后的内容
        with open(file_path, 'w', encoding='utf-8') as f:
            for line in current_lines:
                f.write(line + '\n')
        
        print(f"文件 {file_path} 已更新: {len(current_lines)} 行 (原文件已备份为 {backup_path})")
        start_idx = end_idx
    
    print("\n所有文件处理完成！")

def main():
    remove_duplicates()

if __name__ == "__main__":
    main()
