package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName ums_comment_agreement
 */
@TableName(value ="ums_comment_agreement")
@Data
public class UmsCommentAgreement implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private Integer userid;

    /**
     * 
     */
    private Integer commentid;

    /**
     * 
     */
    private Integer agreement;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}