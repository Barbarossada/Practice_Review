-- 为所有科目+题型组合重新编号display_order
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
