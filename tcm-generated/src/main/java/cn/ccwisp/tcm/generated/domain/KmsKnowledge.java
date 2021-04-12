package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName kms_knowledge
 */
@TableName(value ="kms_knowledge")
@Data
public class KmsKnowledge implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private Integer enabled;

    /**
     *
     */
    private Integer categoryid;

    /**
     *
     */
    private String chinesename;

    /**
     *
     */
    private String latinname;

    /**
     *
     */
    private Integer esid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}