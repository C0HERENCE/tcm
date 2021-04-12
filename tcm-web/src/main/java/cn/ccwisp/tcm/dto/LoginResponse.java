package cn.ccwisp.tcm.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
}
