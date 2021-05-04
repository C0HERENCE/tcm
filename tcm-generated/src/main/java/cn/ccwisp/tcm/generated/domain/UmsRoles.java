package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * null
 * @TableName ums_roles
 */
@TableName(value ="ums_roles")
@Data
public class UmsRoles implements Serializable {
    /**
     * 
     */
    private Integer userid;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private Date createtime;

    /**
     * 
     */
    private String rolename;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}