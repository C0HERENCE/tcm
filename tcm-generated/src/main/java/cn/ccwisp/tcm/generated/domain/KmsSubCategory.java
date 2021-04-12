package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName kms_sub_category
 */
@TableName(value ="kms_sub_category")
@Data
public class KmsSubCategory implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private Integer parentid;

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
    private Integer enabled;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}