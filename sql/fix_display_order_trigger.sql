-- ========================================
-- 修复题目导入时display_order混乱的问题
-- ========================================
-- 问题：先导入"习近平思想"题目，再导入"密码学"题目后，display_order会继续累加
-- 解决：修改触发器，让display_order按科目+题型组合独立编号
-- ========================================

-- 1. 删除旧触发器
DROP TRIGGER IF EXISTS `before_question_insert`;

-- 2. 创建新触发器（按科目+题型组合管理display_order）
CREATE DEFINER=`root`@`localhost` TRIGGER `before_question_insert` BEFORE INSERT ON `question` FOR EACH ROW BEGIN
    DECLARE max_order INT;
    -- 获取该科目+该类型的最大序号
    SELECT IFNULL(MAX(display_order), 0) INTO max_order FROM question WHERE type = NEW.type AND subject = NEW.subject;
    -- 设置新题目的序号
    SET NEW.display_order = max_order + 1;
END;

-- 3. 验证触发器是否创建成功
SHOW TRIGGERS WHERE `Trigger` = 'before_question_insert';
