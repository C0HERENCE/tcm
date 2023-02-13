package cn.ccwisp.tcm.search.service;

import cn.ccwisp.tcm.search.service.utils.TransApi;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class TranslateService {
    // PP
    private static final String APP_ID = "baiduapiid";
    private static final String SECURITY_KEY = "baiduapikey";
    private static long lastMilli = 0;
    // {"
    // from":"zh",
    // "to":"en",
    // "trans_result":
    // [
    // {"src":"\u5c71\u4e0d\u5728\u9ad8\uff0c\u6709\u4ed9\u5219\u7075\uff0c\u6c34\u4e0d\u5728\u6df1\uff0c\u6709\u9f99\u5219\u7075",
    // "dst":"If the mountain is not high, there will be fairies. If the water is not deep, there will be dragons"}
    // ]}
    @Data
    public static class Result {
        private String src;
        private String dst;
    }
    @Data
    public static class TransResult {
        private String from;
        private String to;
        private ArrayList<Result> trans_result;
    }

    public String Translate(String query) {
        if (query == null) {
            return null;
        }
        if (query.trim().equals(""))
            return query;
        if (query.getBytes().length>5989) {
            return query;
        }
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        try {
            long l = System.currentTimeMillis() - lastMilli;
            System.out.println("上"+lastMilli);
            if (l < 1100) {
                TimeUnit.MILLISECONDS.sleep(1100-l);
            }
            System.out.println("下:" + System.currentTimeMillis());
            String transResult = api.getTransResult(query, "zh", "en");
            lastMilli = System.currentTimeMillis();

            TransResult transResult1 = new TransResult();
            TransResult transResult2 = JSON.parseObject(transResult, transResult1.getClass());
            if (transResult2 == null)
                return query;
            StringBuilder resultStr = new StringBuilder();
            if (transResult2.getTrans_result() == null) {
                System.out.println("QPS!");
                return query;
            }
            for (Result result : transResult2.getTrans_result()) {
                resultStr.append(result.getDst()).append('\n');
            }
            return resultStr.toString();

        } catch (
                UnsupportedEncodingException | InterruptedException e) {
            e.printStackTrace();
            return query;
        }
    }
}
