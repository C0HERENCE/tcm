package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName ums_userinfo
 */
@TableName(value ="ums_userinfo")
@Data
public class UmsUserinfo implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private Integer userid;

    /**
     * 
     */
    private String nickname;

    /**
     * 
     */
    private String realname;

    /**
     * 
     */
    private String qq;

    /**
     * 
     */
    private String email;

    /**
     * 
     */
    private String job;

    /**
     * 
     */
    private String hobby;

    /**
     * 
     */
    private String intro;

    /**
     * 
     */
    private String phone;

    /**
     * 
     */
    private String avatar;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}