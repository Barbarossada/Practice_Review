import re
import sys

def parse_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    questions = []
    current_question = None
    seen_questions = set()
    
    # Regex patterns
    # Question start: **1.** or 1. or 1、
    # Capture groups: 1: marker(**), 2: number, 3: separator, 4: content
    q_start_pattern = re.compile(r'^(\*\*|\s*)(\d+)(\.|、)\s*(.*)')
    
    # Answer pattern: **A** or **对** at end of line
    answer_pattern = re.compile(r'\*\*([A-F对错])\*\*\s*$')
    
    # Option pattern: A. or A、
    option_pattern = re.compile(r'\s*([A-F])(\.|、)\s*')
    
    # Chapter headers to ignore
    header_pattern = re.compile(r'^\s*(\*\*|##)?\s*(第一章|第二章|第三章|第四章|判断题)\s*(\*\*|##)?\s*$')

    is_judgment_section = False

    for line in lines:
        line = line.strip()
        if not line:
            continue
            
        # Check for headers
        if header_pattern.match(line):
            if "判断题" in line:
                is_judgment_section = True
            continue
            
        # Check for repetition marker (specific to this file's corruption)
        if "CSMA/CA 是竞争型介质访第一章" in line:
            break # Stop processing if we hit the corrupted repetition start

        # Check if it's a new question
        q_match = q_start_pattern.match(line)
        if q_match:
            # Save previous question
            if current_question:
                # Check for duplicate
                q_sig = current_question['content']
                if q_sig not in seen_questions:
                    questions.append(current_question)
                    seen_questions.add(q_sig)
                current_question = None

            # Start new question
            q_num = q_match.group(2)
            content = q_match.group(4)
            
            # Extract answer from content if present
            ans_match = answer_pattern.search(content)
            answer = None
            if ans_match:
                answer = ans_match.group(1)
                content = content[:ans_match.start()].strip() # Remove answer from content
            
            current_question = {
                'type': 'judgment' if is_judgment_section else 'choice', # Initial guess, refine later
                'content': content,
                'options': [],
                'answer': answer,
                'analysis': []
            }
            continue

        # If inside a question
        if current_question:
            # Check for answer at end of line (if not found in question line)
            if not current_question['answer']:
                ans_match = answer_pattern.search(line)
                if ans_match:
                    current_question['answer'] = ans_match.group(1)
                    line = line[:ans_match.start()].strip()
                    if not line: continue # Line was just the answer

            # Check if it's an option line (only for choice questions)
            # Options can be on one line: A. xxx B. xxx
            # Or multiple lines
            # We assume if it starts with A. it's an option line
            # But be careful about analysis text starting with A. (unlikely)
            
            # Simple heuristic: if line contains "A." or "A、" and we are not in judgment section (or even if we are, maybe it was misclassified)
            # Actually, judgment questions don't have options.
            
            # Let's try to find options in the line
            # We split the line by option markers
            
            # If it looks like options
            if re.search(r'\s*[A-F](\.|、)', line):
                # It's likely options.
                current_question['type'] = 'choice' # Confirm it's choice
                
                # Split by options. 
                # A. xxx B. xxx -> ["A. xxx", "B. xxx"]
                # We can use regex split but need to keep delimiters.
                
                # Find all option starts
                matches = list(re.finditer(r'\s*([A-F])(\.|、)', line))
                if matches:
                    last_idx = 0
                    for i, m in enumerate(matches):
                        start = m.start()
                        if i > 0:
                            # Text between previous match and this match is the previous option content
                            opt_text = line[last_idx:start].strip()
                            current_question['options'].append(opt_text)
                        
                        last_idx = start
                    
                    # Last option
                    current_question['options'].append(line[last_idx:].strip())
                    continue

            # If not options, and not empty, it's likely analysis or continuation of question
            # For now, treat as analysis if we already have options or if it's judgment
            if current_question['type'] == 'choice' and current_question['options']:
                current_question['analysis'].append(line)
            elif current_question['type'] == 'judgment':
                current_question['analysis'].append(line)
            else:
                # Maybe continuation of question text?
                # If we haven't seen options yet, append to content?
                # But usually question text is one line.
                # Let's assume it's analysis for now, or check if it looks like analysis
                current_question['analysis'].append(line)

    # Append last question
    if current_question:
        q_sig = current_question['content']
        if q_sig not in seen_questions:
            questions.append(current_question)

    return questions

def format_questions(questions):
    output_lines = []
    answers = []
    
    # Separate Choice and Judgment
    choice_qs = [q for q in questions if q['type'] == 'choice']
    judgment_qs = [q for q in questions if q['type'] == 'judgment']
    
    # Sort/Reorder? The user said "reorder". 
    # I'll just keep them in the order found (which is Chapter 1, 2, 3, 4) but grouped by type.
    # Or maybe mix them? Standard usually separates them.
    # The user said "reorder this question" -> maybe just renumber 1..N.
    # I will put Choice first, then Judgment.
    
    all_qs = choice_qs + judgment_qs
    
    for i, q in enumerate(all_qs, 1):
        # Format Question
        q_text = f"{i}. {q['content']}"
        output_lines.append(q_text)
        
        # Format Options
        if q['type'] == 'choice':
            for opt in q['options']:
                output_lines.append(opt)
        
        # Format Analysis
        if q['analysis']:
            # Join analysis lines
            analysis_text = "".join(q['analysis'])
            # Ensure it starts with "解析："
            if not analysis_text.startswith("解析") and not analysis_text.startswith("答案解析"):
                analysis_text = "解析：" + analysis_text
            output_lines.append(analysis_text)
            
        output_lines.append("") # Empty line
        
        # Collect Answer
        ans = q['answer']
        if ans:
            if ans == '对': ans = 'A' # Standard says A for Correct? 
            # Wait, standard says: "判断题答案：A 表示正确，B 表示错误" in Answer Area.
            # But in text it says "答案：正确" or "True".
            # The user wants "answers at the end".
            # So I should convert to A/B for the answer block.
            if ans == '对': ans = 'A'
            if ans == '错': ans = 'B'
            answers.append(ans)
        else:
            answers.append("?") # Missing answer

    # Format Answer Area
    # 1-5 A B C D E
    output_lines.append("")
    output_lines.append("================================================================================")
    output_lines.append("答案区")
    output_lines.append("================================================================================")
    
    chunk_size = 5
    for i in range(0, len(answers), chunk_size):
        chunk = answers[i:i+chunk_size]
        start_num = i + 1
        end_num = i + len(chunk)
        ans_str = " ".join(chunk)
        output_lines.append(f"{start_num}-{end_num} {ans_str}")

    return output_lines

if __name__ == "__main__":
    input_file = r"f:\Development\Java\IDEA_Projects\Final_Practice\data\无线网络.txt"
    output_file = r"f:\Development\Java\IDEA_Projects\Final_Practice\data\无线网络_标准格式.txt"
    
    qs = parse_file(input_file)
    formatted = format_questions(qs)
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("\n".join(formatted))
        
    print(f"Processed {len(qs)} questions.")
    print(f"Saved to {output_file}")
