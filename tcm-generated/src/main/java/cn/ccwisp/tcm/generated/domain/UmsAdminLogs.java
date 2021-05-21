package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ums_admin_logs
 */
@TableName(value ="ums_admin_logs")
@Data
public class UmsAdminLogs implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer adminid;

    /**
     * 
     */
    private String adminusername;

    /**
     * 
     */
    private Date createtime;

    /**
     * 
     */
    private String methodname;

    /**
     * 
     */
    private String args;

    /**
     * 
     */
    private Integer succeed;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}