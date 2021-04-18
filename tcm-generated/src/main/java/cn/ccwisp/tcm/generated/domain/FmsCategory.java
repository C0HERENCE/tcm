package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName fms_category
 */
@TableName(value ="fms_category")
@Data
public class FmsCategory implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private String logo;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String intro;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}