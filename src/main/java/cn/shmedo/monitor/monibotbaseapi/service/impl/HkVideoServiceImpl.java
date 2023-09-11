package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.config.ArtemisConfigWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkMonitorPointInfo;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.*;


@EnableTransactionManagement
@Service
@AllArgsConstructor
public class HkVideoServiceImpl implements HkVideoService {


    private final FileConfig fileConfig;

    private final ArtemisConfigWrapper artemisConfigWrapper;
    private final String ARTEMIS_PATH = "/artemis";


    private final String contentType = "application/json";

    @Override
    public HkMonitorPointInfo queryHkVideoPage(Integer pageNo) {

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
        paramMap.put("pageNo", pageNo.toString());
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

    /**
     * 获取取流地址
     *
     * @see <a href="https://open.hikvision.com/docs/docId?productId=5c67f1e2f05948198c909700&version=%2F60df74fdc6f24041ac3d2d7f81c32325&tagPath=API%E5%88%97%E8%A1%A8-%E8%A7%86%E9%A2%91%E4%B8%9A%E5%8A%A1-%E8%A7%86%E9%A2%91%E5%8A%9F%E8%83%BD#b5bd6fd9">接口文档-获取监控点预览取流URLv2</a>
     */
    @Override
    public String getStreamUrl(String deviceSerial, Integer streamType, String protocol, Integer transmode, String expand, String streamform) {
        final String previewURLsApi = ARTEMIS_PATH + "/api/video/v2/cameras/previewURLs";
        Map<String, String> path = new HashMap<>(2) {
            {
                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };
        Map<String, Object> paramMap = new HashMap<>(16) {
            {
                put("cameraIndexCode", deviceSerial);
                Optional.ofNullable(streamType).ifPresent(u -> put("streamType", u));
                Optional.ofNullable(protocol).ifPresent(u -> put("protocol", u));
                Optional.ofNullable(transmode).ifPresent(u -> put("transmode", u));
                Optional.ofNullable(expand).ifPresent(u -> put("expand", u));
                Optional.ofNullable(streamform).ifPresent(u -> put("streamform", u));
            }
        };
        String body = JSONUtil.toJsonStr(paramMap);
        try {
            String responseBody = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
            return Optional.ofNullable(responseBody).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                    .filter(u -> "0".equals(u.getStr("code", "-1"))).map(u -> u.get("data")).map(JSONUtil::parseObj)
                    .map(u -> u.getStr("url")).orElseThrow(() -> new RuntimeException("海康接口调用失败,responseBody: " + responseBody));
        } catch (Exception e) {
            throw new RuntimeException("海康接口调用失败!");
        }
    }

    /**
     * 获取回放取流地址
     *
     * @return {"list":[{"lockType":1,"beginTime":"2018-08-07T14:44:04.923+08:00","endTime":"2018-08-07T14:54:18.183+08:00","size":66479332}],"uuid":"e33421g1109046a79b6280bafb6fa5a7","url":"rtsp://10.2.145.66:6304/EUrl/Dib1ErK"}
     * @see <a href="https://open.hikvision.com/docs/docId?productId=5c67f1e2f05948198c909700&version=%2F60df74fdc6f24041ac3d2d7f81c32325&tagPath=API%E5%88%97%E8%A1%A8-%E8%A7%86%E9%A2%91%E4%B8%9A%E5%8A%A1-%E8%A7%86%E9%A2%91%E5%8A%9F%E8%83%BD#c2c7d879">接口文档-获取监控点回放取流URLv2</a>
     */
    @Override
    public Map<String, Object> getPlayBackStreamInfo(String deviceSerial, String recordLocation, String protocol,
                                                     Integer transmode, String beginTime, String endTime, String uuid,
                                                     String expand, String streamform, Integer lockType) {
        final String previewURLsApi = ARTEMIS_PATH + "/api/video/v2/cameras/playbackURLs";
        Map<String, String> path = new HashMap<>(2) {
            {
                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };
        Map<String, Object> paramMap = new HashMap<>(16) {
            {
                put("cameraIndexCode", deviceSerial);
                Optional.ofNullable(recordLocation).ifPresent(u -> put("recordLocation", u));
                Optional.ofNullable(protocol).ifPresent(u -> put("protocol", u));
                Optional.ofNullable(transmode).ifPresent(u -> put("transmode", u));
                Optional.ofNullable(beginTime).ifPresent(u -> put("beginTime", u));
                Optional.ofNullable(endTime).ifPresent(u -> put("endTime", u));
                Optional.ofNullable(uuid).ifPresent(u -> put("uuid", u));
                Optional.ofNullable(expand).ifPresent(u -> put("expand", u));
                Optional.ofNullable(streamform).ifPresent(u -> put("streamform", u));
                Optional.ofNullable(lockType).ifPresent(u -> put("lockType", u));
            }
        };
        String body = JSONUtil.toJsonStr(paramMap);
        try {
            String responseBody = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
            return Optional.ofNullable(responseBody).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                    .filter(u -> "0".equals(u.getStr("code", "-1"))).map(u -> u.get("data"))
                    .map(JSONUtil::parseObj).orElseThrow(() -> new RuntimeException("海康接口调用失败,responseBody: " + responseBody));
        } catch (Exception e) {
            throw new RuntimeException("海康接口调用失败!");
        }
    }

    /**
     * 云台操作
     *
     * @see <a href="https://open.hikvision.com/docs/docId?productId=5c67f1e2f05948198c909700&version=%2F60df74fdc6f24041ac3d2d7f81c32325&tagPath=API%E5%88%97%E8%A1%A8-%E8%A7%86%E9%A2%91%E4%B8%9A%E5%8A%A1-%E8%A7%86%E9%A2%91%E5%8A%9F%E8%83%BD#e6643a97">接口文档-根据监控点编号进行云台操作</a>
     */
    @Override
    public Map<String, String> controllingPtz(String deviceSerial, Integer action, String command, Integer speed, Integer presetIndex) {
        final String previewURLsApi = ARTEMIS_PATH + "/api/video/v1/ptzs/controlling";
        Map<String, String> path = new HashMap<>(2) {
            {
                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };
        Map<String, Object> paramMap = new HashMap<>(16) {
            {
                put("cameraIndexCode", deviceSerial);
                put("action", action);
                put("command", command);
                Optional.ofNullable(speed).ifPresent(u -> put("speed", u));
                Optional.ofNullable(presetIndex).ifPresent(u -> put("presetIndex", u));
            }
        };
        String body = JSONUtil.toJsonStr(paramMap);
        try {
            String responseBody = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
            return Optional.ofNullable(responseBody).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                    .map(u -> new HashMap<String, String>() {
                        {
                            Optional.ofNullable(u.get("code")).map(String::valueOf).ifPresent(w -> put("code", w));
                            Optional.ofNullable(u.get("msg")).map(String::valueOf).ifPresent(w -> put("msg", w));
                        }
                    }).orElseThrow(() -> new RuntimeException("海康接口调用失败,responseBody: " + responseBody));
        } catch (Exception e) {
            throw new RuntimeException("海康接口调用失败!");
        }
    }
}
