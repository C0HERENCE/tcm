package cn.ccwisp.tcm.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewThreadRequest {
    private String title;
    private String content;
    private int categoryId;
    private List<Integer> relatedKnowledge;
}
