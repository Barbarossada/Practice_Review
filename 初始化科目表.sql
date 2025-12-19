-- 初始化科目表数据（基于现有question表中的数据）
-- 执行此脚本前，请确保已经创建了subject表

-- 1. 清空subject表（如果需要重新初始化）
-- TRUNCATE TABLE subject;

-- 2. 从question表中统计各科目的题目数量并插入到subject表
INSERT INTO subject (name, question_count, create_time)
SELECT 
    subject AS name,
    COUNT(*) AS question_count,
    NOW() AS create_time
FROM question
GROUP BY subject
ON DUPLICATE KEY UPDATE 
    question_count = VALUES(question_count);

-- 3. 查看结果
SELECT * FROM subject ORDER BY question_count DESC, name ASC;
