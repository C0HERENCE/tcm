package cn.ccwisp.tcm.dto;

import cn.ccwisp.tcm.generated.domain.Fms;
import lombok.Data;

@Data
public class FmsResponse {
    private Fms thread;
    private int fav;
    private int like;
    private int views;
    private boolean haveFav;
    private boolean haveLike;
}
