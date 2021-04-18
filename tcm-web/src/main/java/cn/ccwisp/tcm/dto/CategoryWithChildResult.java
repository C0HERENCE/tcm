package cn.ccwisp.tcm.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CategoryWithChildResult {
    public CategoryWithChildResult() {
        this.children = new ArrayList<>();
    }
    private int key;
    private String title;
    private ArrayList<CategoryWithChildResult> children;
}
