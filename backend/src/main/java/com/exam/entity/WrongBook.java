package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 错题本实体类
 * 记录用户的错题及其掌握程度
 * 
 * @author Exam System
 * @since 2026-01-03
 */
@Data
@TableName("wrong_book")
public class WrongBook {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 熟练度(0-100, 达到100视为掌握)
     */
    private Integer masteryLevel;

    /**
     * 错误次数
     */
    private Integer errorCount;

    /**
     * 首次加入时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
