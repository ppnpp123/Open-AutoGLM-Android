#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
删除1.txt到10.txt中以"村，屯，镇，庄，岗，沟，乡"结尾的三字词语
"""

import os
import glob

def remove_place_names():
    """
    删除所有txt文件中以特定字结尾的三字词语
    """
    # 定义结尾字集合
    end_chars = {'村', '屯', '镇', '庄', '岗', '沟', '乡'}
    
    # 获取所有txt文件
    txt_files = glob.glob("*.txt")
    
    print(f"找到 {len(txt_files)} 个txt文件: {txt_files}")
    print(f"目标结尾字: {end_chars}")
    
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
            
            # 检查是否为三字词语且以目标字结尾
            is_target_word = (len(chinese_word) == 3 and 
                             chinese_word[2] in end_chars)
            
            if is_target_word:
                removed_count += 1
            else:
                # 不匹配则保留
                filtered_lines.append(line + '\n')
        
        # 备份原文件
        backup_path = file_path + ".backup_place_names_removed"
        os.rename(file_path, backup_path)
        
        # 写入过滤后的内容
        with open(file_path, 'w', encoding='utf-8') as f:
            f.writelines(filtered_lines)
        
        print(f"已处理完成: {file_path} (原文件已备份为 {backup_path})")
        print(f"原始行数: {sum(1 for line in open(backup_path, 'r', encoding='utf-8'))}, 过滤后行数: {len(filtered_lines)}, 删除了 {removed_count} 行")
    
    print("\n所有文件处理完成！")

def main():
    remove_place_names()

if __name__ == "__main__":
    main()
