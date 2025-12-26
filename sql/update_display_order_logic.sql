-- ==========================================
-- 1. 更新触发器：按科目+题型组合管理display_order
-- ==========================================
DROP TRIGGER IF EXISTS `before_question_insert`;

CREATE TRIGGER `before_question_insert` BEFORE INSERT ON `question` FOR EACH ROW BEGIN
    DECLARE max_order INT;
    -- 获取该科目+该类型的最大序号
    SELECT IFNULL(MAX(display_order), 0) INTO max_order FROM question WHERE type = NEW.type AND subject = NEW.subject;
    -- 设置新题目的序号
    SET NEW.display_order = max_order + 1;
END;

-- ==========================================
-- 2. 重置现有数据：重新编号所有题目
-- ==========================================
SET @row_num = 0;
-- 显式指定排序规则，防止 Illegal mix of collations 错误
SET @prev_subject = '' COLLATE utf8mb4_unicode_ci;
SET @prev_type = '' COLLATE utf8mb4_unicode_ci;

UPDATE question
SET display_order = IF(@prev_subject != subject OR @prev_type != type,
    @row_num := 1,
    @row_num := @row_num + 1),
    subject = @prev_subject := subject,
    type = @prev_type := type
ORDER BY subject, type, display_order;

SELECT 'SQL脚本执行完成。触发器已更新，现有数据已重新编号。' as result;
