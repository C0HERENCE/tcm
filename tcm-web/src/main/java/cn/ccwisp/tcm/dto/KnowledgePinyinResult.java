package cn.ccwisp.tcm.dto;

import cn.ccwisp.tcm.generated.domain.Kms;
import cn.ccwisp.tcm.generated.domain.KmsCategory;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class KnowledgePinyinResult {

    public KnowledgePinyinResult() {
        this.map = new HashMap<>();
    }
    private HashMap<Character, ArrayList<Kms>> map;
    private int total;
}
