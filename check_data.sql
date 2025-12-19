-- 查询所有不同的科目
SELECT DISTINCT subject, COUNT(*) as count FROM question GROUP BY subject;

-- 查询所有不同的题型
SELECT DISTINCT type, COUNT(*) as count FROM question GROUP BY type;

-- 查询"习思想"科目的所有题目（前5条）
SELECT id, type, subject, LEFT(content, 50) as content_preview FROM question WHERE subject LIKE '%习思想%' LIMIT 5;

-- 查询判断题（前5条）
SELECT id, type, subject, LEFT(content, 50) as content_preview FROM question WHERE type = 'judge' LIMIT 5;

-- 查询所有题目的type和subject分布
SELECT type, subject, COUNT(*) as count FROM question GROUP BY type, subject ORDER BY subject, type;
