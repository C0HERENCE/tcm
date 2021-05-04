package cn.ccwisp.tcm.dto;

import cn.ccwisp.tcm.generated.domain.Fms;
import cn.ccwisp.tcm.generated.domain.FmsComment;
import cn.ccwisp.tcm.generated.domain.FmsThreadKmsKnowledge;
import lombok.Data;

import java.util.List;

@Data
public class FmsDetailResponse {
    private Fms thread;
    private int fav;
    private int liked;
    private int views;
    private List<FmsCommentDto> fmsComments;
    private List<FmsThreadKmsKnowledge> fmsThreadKmsKnowledgeList;
}
