package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName kms_category
 */
@TableName(value ="kms_category")
@Data
public class KmsCategory implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

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
    private String enabled;

    /**
     * 
     */
    private Integer type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}