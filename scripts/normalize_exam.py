# -*- coding: utf-8 -*-
"""
标准化试题 TXT 文件并生成可供前端转换的答案区。
用法（在项目根目录运行）：
python scripts/normalize_exam.py 密码学.txt 密码学_标准格式.txt

脚本会：
- 去掉选项行前面的圆圈标记（○、√）
- 移除“我的答案：”“参考答案：”“正确答案：”等行（但会提取正确答案用于答案区）
- 在文件末尾追加答案区（每行格式：起始题号-结束题号 答案）

注：本脚本仅对常见的单选/多选/判断题答案（字母 A-E）生成答案区；填空、简答型题目会保留原文但不写入答案区。
"""
import sys
import re
from pathlib import Path

if len(sys.argv) < 3:
    print("Usage: python normalize_exam.py <input.txt> <output.txt>")
    sys.exit(1)

in_path = Path(sys.argv[1])
out_path = Path(sys.argv[2])

if not in_path.exists():
    print(f"Input file not found: {in_path}")
    sys.exit(1)

with in_path.open('r', encoding='utf-8') as f:
    lines = f.readlines()

normalized = []
answers = {}  # qnum -> answer string (A, B, C, D, E or multiple like ABC)
current_q = None
q_pattern = re.compile(r'题目\s*(\d+)')
correct_pattern = re.compile(r'正确答案[：:]*\s*(.+)')
# match letters A-E (possibly multiple, possibly separated by non-letters)
letters_pattern = re.compile(r'[A-E]+')

for raw in lines:
    line = raw.rstrip('\n')
    # detect question number
    m = q_pattern.search(line)
    if m:
        current_q = int(m.group(1))
        normalized.append(line)
        continue

    # extract correct answer
    m2 = correct_pattern.search(line)
    if m2 and current_q is not None:
        ans_text = m2.group(1).strip()
        # try to find letter answers A-E sequence
        # normalize full-width commas and chinese commas
        ans_text_norm = ans_text.replace('，', ',').replace('、', ',')
        # find contiguous letter groups
        found = letters_pattern.findall(ans_text_norm.upper())
        if found:
            # join with no separator (e.g., ['A','B'] -> 'AB') or keep single
            combined = ''.join(found)
            answers[current_q] = combined
        # do not append this line to normalized (we remove it)
        continue

    # remove lines that start with 我的答案： 参考答案：
    if line.strip().startswith('我的答案') or line.strip().startswith('参考答案'):
        continue

    # remove leading marker symbols (○、√) possibly preceded by spaces
    line2 = re.sub(r'^\s*[○√]\s*', '', line)
    # also remove a leading full-width or ascii circle if present in middle like '  ○ A.'
    line2 = re.sub(r'\s+[○√]\s+', ' ', line2)

    normalized.append(line2)

# Build answer area lines
answer_lines = []
for q in sorted(answers.keys()):
    answer_lines.append(f"{q}-{q} {answers[q]}")

# Write output: normalized content followed by a blank line and answer area
with out_path.open('w', encoding='utf-8') as f:
    for ln in normalized:
        f.write(ln + '\n')
    f.write('\n')
    f.write('# 答案区（本行为自动生成，格式：起始题号-结束题号 答案）\n')
    for ln in answer_lines:
        f.write(ln + '\n')

print(f"标准化完成，输出：{out_path}")
print(f"共提取 {len(answer_lines)} 个题目的字母答案到答案区（填空/简答题未包含）。")
