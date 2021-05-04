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
    private String categorytitle;

    /**
     * 
     */
    private String categoryintro;

    /**
     * 
     */
    private String typename;

    /**
     * 
     */
    private String avatar;

    /**
     * 
     */
    private String nickname;

    /**
     * 
     */
    private String topic;

    /**
     * 
     */
    private Integer categoryenabled;

    /**
     * 
     */
    private String categoryenglish;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}