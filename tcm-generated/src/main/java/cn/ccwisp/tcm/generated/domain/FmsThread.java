package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * null
 * @TableName fms_thread
 */
@TableName(value ="fms_thread")

@Data
public class FmsThread implements Serializable {
    /**
     * 
     */
    @TableId(type= IdType.AUTO)
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
    private Integer enabled;

    /**
     * 
     */
    private String topic;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}