package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName kms_feedback
 */
@TableName(value ="kms_feedback")
@Data
public class KmsFeedback implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer esid;

    /**
     * 
     */
    private String esindex;

    /**
     * 
     */
    private String anchor;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String message;

    /**
     * 
     */
    private Date createtime;

    /**
     * 
     */
    private Integer userid;

    /**
     * 
     */
    private Integer operatorid;

    /**
     * 
     */
    private Integer processed;

    /**
     * 
     */
    private String note;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}