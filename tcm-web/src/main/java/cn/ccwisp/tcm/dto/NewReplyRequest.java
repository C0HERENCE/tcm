package cn.ccwisp.tcm.dto;

import lombok.Data;

@Data
public class NewReplyRequest {
    private int threadId;
    private String content;
}
