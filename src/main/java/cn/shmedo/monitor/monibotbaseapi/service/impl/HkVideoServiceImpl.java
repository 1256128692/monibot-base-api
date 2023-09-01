package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.config.ArtemisConfigWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkMonitorPointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsChannelInfo;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public HkMonitorPointInfo queryHkVideoPage(Object param) {

        /**
         * 设置接口的URI地址
         */
        final String previewURLsApi = ARTEMIS_PATH + "/api/resource/v1/cameras";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };

        /**
         * 组装请求参数
         */
        Map<String, String> paramMap = new HashMap<String, String>(2);// post请求Form表单参数
        paramMap.put("pageNo", "1");
        paramMap.put("pageSize", "1000");
        String body = JSONUtil.toJsonStr(paramMap);

        /**
         * 调用接口
         */
        try {
            String responseData = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);

            // 解析外层的 JSON
            JSON outerJson = JSONUtil.parse(responseData);
            HkMonitorPointInfo pointInfo = outerJson.getByPath("data", HkMonitorPointInfo.class);

            // 返回格式化后的 JSON 字符串
            return pointInfo;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HkDeviceInfo queryDevice(String deviceSerial) {

        final String previewURLsApi = ARTEMIS_PATH + "/api/resource/v1/cameras/indexCode";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", previewURLsApi);
            }
        };

        Map<String, String> paramMap = new HashMap<String, String>(1);
        paramMap.put("cameraIndexCode", deviceSerial);
        String body = JSONUtil.toJsonStr(paramMap);

        try {
            String responseData = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);

            // 解析外层的 JSON
            JSON outerJson = JSONUtil.parse(responseData);
//            String outerFormattedJson = outerJson.toStringPretty();

            // 获取 "data" 字段的值（一个 JSON 字符串）
            HkDeviceInfo hkDeviceInfo = outerJson.getByPath("data", HkDeviceInfo.class);

//            // 解析内层的 JSON
//            JSON innerJson = JSONUtil.parse(innerJsonString);
//            String innerFormattedJson = innerJson.toStringPretty();

            // 返回格式化后的 JSON 字符串
            return hkDeviceInfo;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
