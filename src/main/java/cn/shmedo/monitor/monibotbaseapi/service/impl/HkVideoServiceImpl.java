package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.config.ArtemisConfigWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.JSON;


@EnableTransactionManagement
@Service
@AllArgsConstructor
public class HkVideoServiceImpl implements HkVideoService {


    private final FileConfig fileConfig;

    private final ArtemisConfigWrapper artemisConfigWrapper;
    private final String ARTEMIS_PATH = "/artemis";


    private final String contentType = "application/json";

//    @Override
//    public Object QueryHkVideoPage(Object param) {
//
//
//        /**
//         * 设置接口的URI地址
//         */
//        final String previewURLsApi = ARTEMIS_PATH + "/api/resource/v1/cameras";
//        Map<String, String> path = new HashMap<String, String>(2) {
//            {
//                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
//            }
//        };
//
//        /**
//         * 组装请求参数
//         */
//        Map<String, String> paramMap = new HashMap<String, String>(2);// post请求Form表单参数
//        paramMap.put("pageNo", "1");
//        paramMap.put("pageSize", "1000");
//        String body = JSONUtil.toJsonStr(paramMap);
//
//        /**
//         * 调用接口
//         */
//        try {
//            String responseData = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
//
//            // 解析外层的 JSON
//            JSON outerJson = JSONUtil.parse(responseData);
//            String outerFormattedJson = outerJson.toStringPretty();
//
//            // 获取 "data" 字段的值（一个 JSON 字符串）
//            String innerJsonString = outerJson.getByPath("data", String.class);
//
//            // 解析内层的 JSON
//            JSON innerJson = JSONUtil.parse(innerJsonString);
//            String innerFormattedJson = innerJson.toStringPretty();
//
//            // 输出格式化后的 JSON
//            System.out.println("Formatted Outer JSON:\n" + outerFormattedJson);
//            System.out.println("\nFormatted Inner JSON:\n" + innerFormattedJson);
//
//            // 返回格式化后的 JSON 字符串
//            return innerJsonString;
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
