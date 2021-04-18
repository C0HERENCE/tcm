package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * null
 * @TableName fms
 */
@TableName(value ="fms")
@Data
public class Fms implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private Integer authorid;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private Integer enabled;

    /**
     * 
     */
    private Date createtime;

    /**
     * 
     */
    private Date modifytime;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private Integer categoryid;

    /**
     * 
     */
    private Integer typeid;

    /**
     * 
     */
    private String categorylogo;

    /**
     * 
     */
    private String categorytitle;

    /**
     * 
     */
    private String categoryintro;

    /**
     * 
     */
    private String typename;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}