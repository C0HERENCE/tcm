package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName fms_thread_kms_knowledge
 */
@TableName(value ="fms_thread_kms_knowledge")
@Data
public class FmsThreadKmsKnowledge implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer threadid;

    /**
     * 
     */
    @TableField
    private Integer knowledgeid;

    /**
     * 
     */
    private Integer enabled;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}