package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * null
 * @TableName ums_admin_logs
 */
@TableName(value ="ums_admin_logs")
@Data
public class UmsAdminLogs implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private Integer adminid;

    /**
     * 
     */
    private String type;

    /**
     * 
     */
    private Date createtime;

    /**
     * 
     */
    private String note;

    /**
     * 
     */
    private String op1;

    /**
     * 
     */
    private String op2;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}