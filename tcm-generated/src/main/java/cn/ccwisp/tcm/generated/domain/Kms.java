package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName kms
 */
@TableName(value ="kms")
@Data
public class Kms implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private Integer esid;

    /**
     * 
     */
    private String latinname;

    /**
     * 
     */
    private String chinesename;

    /**
     * 
     */
    private Integer categoryType;

    /**
     * 
     */
    private Integer subcategoryid;

    /**
     * 
     */
    private String subcategorychinesename;

    /**
     * 
     */
    private String subcategorylatinname;

    /**
     * 
     */
    private Integer parentcategoryid;

    /**
     * 
     */
    private String parentcategorychinesename;

    /**
     * 
     */
    private String parentcategorylatinname;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}