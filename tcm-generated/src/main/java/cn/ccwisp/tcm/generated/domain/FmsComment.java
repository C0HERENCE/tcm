package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * null
 * @TableName fms_comment
 */
@TableName(value ="fms_comment")
@Data
public class FmsComment implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer threadid;

    /**
     * 
     */
    private Integer agreecount;

    /**
     * 
     */
    private Integer disagreecount;

    /**
     * 
     */
    private Integer authorid;

    /**
     * 
     */
    private Date createtime;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private Integer enabled;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}