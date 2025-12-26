-- 删除旧触发器
DROP TRIGGER IF EXISTS `before_question_insert`;

-- 创建新触发器（按科目+题型组合管理display_order）
CREATE TRIGGER `before_question_insert` BEFORE INSERT ON `question` FOR EACH ROW BEGIN
    DECLARE max_order INT;
    -- 获取该科目+该类型的最大序号
    SELECT IFNULL(MAX(display_order), 0) INTO max_order FROM question WHERE type = NEW.type AND subject = NEW.subject;
    -- 设置新题目的序号
    SET NEW.display_order = max_order + 1;
END;
