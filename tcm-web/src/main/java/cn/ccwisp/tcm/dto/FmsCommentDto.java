package cn.ccwisp.tcm.dto;

import cn.ccwisp.tcm.generated.domain.FmsComment;
import lombok.Data;

@Data
public class FmsCommentDto {
    FmsComment fmsComment;
    int userId;
    String avatar;
    String nickname;
    int agreed;
    int enabled = 1;
}
