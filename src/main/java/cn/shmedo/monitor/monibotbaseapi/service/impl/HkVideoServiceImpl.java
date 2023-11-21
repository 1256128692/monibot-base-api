package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.config.ArtemisConfigWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.DeviceListResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkMonitorPointInfo;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.*;

import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.HikVideoParamKeys.*;

@Slf4j
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
                put(HIK_DEVICE_SERIAL, deviceSerial);
                Optional.ofNullable(streamType).ifPresent(u -> put(HIK_STREAM_TYPE, u));
                Optional.ofNullable(protocol).ifPresent(u -> put(HIK_STREAM_PROTOCOL, u));
                Optional.ofNullable(transmode).ifPresent(u -> put(HIK_TRANS_MODE_PROTOCOL, u));
                Optional.ofNullable(expand).ifPresent(u -> put(HIK_EXPAND, u));
                Optional.ofNullable(streamform).ifPresent(u -> put(HIK_STREAM_FORM, u));
            }
        };
        return getDataUrl(path, paramMap);
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
                put(HIK_DEVICE_SERIAL, deviceSerial);
                Optional.ofNullable(recordLocation).ifPresent(u -> put(HIK_RECORD_LOCATION, u));
                Optional.ofNullable(protocol).ifPresent(u -> put(HIK_STREAM_PROTOCOL, u));
                Optional.ofNullable(transmode).ifPresent(u -> put(HIK_TRANS_MODE_PROTOCOL, u));
                Optional.ofNullable(beginTime).ifPresent(u -> put(HIK_BEGIN_TIME, u));
                Optional.ofNullable(endTime).ifPresent(u -> put(HIK_END_TIME, u));
                Optional.ofNullable(uuid).ifPresent(u -> put(HIK_PLAYBACK_UUID, u));
                Optional.ofNullable(expand).ifPresent(u -> put(HIK_EXPAND, u));
                Optional.ofNullable(streamform).ifPresent(u -> put(HIK_STREAM_FORM, u));
                Optional.ofNullable(lockType).ifPresent(u -> put(HIK_LOCK_TYPE, u));
            }
        };
        String body = JSONUtil.toJsonStr(paramMap);
        try {
            String responseBody = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
            return Optional.ofNullable(responseBody).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                    .map(u -> u.get(HIK_DATA)).map(JSONUtil::parseObj).map(u -> (Map<String, Object>) u).filter(ObjectUtil::isNotEmpty)
                    .map(u -> {
                        Optional.of(u).filter(w -> w.containsKey(HIK_STREAM_URL)).ifPresent(w ->
                                w.put(HIK_STREAM_URL, afterHikUrlPrepare(w.get(HIK_STREAM_URL).toString(), protocol)));
                        return u;
                    }).orElse(Map.of("responseBody", Optional.ofNullable(responseBody).orElse("")));
//                    .filter(u -> HIK_SUCCESS_CODE.equals(u.getStr(HIK_CODE, "-1"))).map(u -> u.get(HIK_DATA))
//                    .map(JSONUtil::parseObj).orElseThrow(() -> new RuntimeException("海康接口调用失败,responseBody: " + responseBody));
        } catch (Exception e) {
            throw new RuntimeException("海康接口调用失败!报错信息:" + e.getMessage());
        }
    }

    /**
     * 语音对讲
     *
     * @see <a href="https://open.hikvision.com/docs/docId?productId=5c67f1e2f05948198c909700&version=%2F60df74fdc6f24041ac3d2d7f81c32325&tagPath=API%E5%88%97%E8%A1%A8-%E8%A7%86%E9%A2%91%E4%B8%9A%E5%8A%A1-%E8%A7%86%E9%A2%91%E5%8A%9F%E8%83%BD#d16e79ef">接口文档-查询对讲URL</a>
     */
    @Override
    public String getTalkStreamInfo(String deviceSerial, String protocol, Integer transmode, String expand,
                                    String eurlExand) {
        final String previewURLsApi = ARTEMIS_PATH + "/api/video/v1/cameras/talkURLs";
        Map<String, String> path = new HashMap<>(2) {
            {
                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };
        Map<String, Object> paramMap = new HashMap<>(16) {
            {
                put(HIK_DEVICE_SERIAL, deviceSerial);
                Optional.ofNullable(protocol).ifPresent(u -> put(HIK_STREAM_PROTOCOL, u));
                Optional.ofNullable(transmode).ifPresent(u -> put(HIK_TRANS_MODE_PROTOCOL, u));
                Optional.ofNullable(expand).ifPresent(u -> put(HIK_EXPAND, u));
                Optional.ofNullable(eurlExand).ifPresent(u -> put(HIK_EURL_EXPAND, u));
            }
        };
        return getDataUrl(path, paramMap);
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
                put(HIK_DEVICE_SERIAL, deviceSerial);
                put(HIK_ACTION, action);
                put(HIK_COMMAND, command);
                Optional.ofNullable(speed).ifPresent(u -> put(HIK_SPEED, u));
                Optional.ofNullable(presetIndex).ifPresent(u -> put(HIK_PRESET_INDEX, u));
            }
        };
        String body = JSONUtil.toJsonStr(paramMap);
        try {
            String responseBody = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
            return Optional.ofNullable(responseBody).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                    .map(u -> new HashMap<String, String>() {
                        {
                            Optional.ofNullable(u.get(HIK_CODE)).map(String::valueOf).ifPresent(w -> put(HIK_CODE, w));
                            Optional.ofNullable(u.get(HIK_MSG)).map(String::valueOf).ifPresent(w -> put(HIK_MSG, w));
                        }
                    }).orElseThrow(() -> new RuntimeException("海康接口调用失败,responseBody: " + responseBody));
        } catch (Exception e) {
            throw new RuntimeException("海康接口调用失败!");
        }
    }

    @Override
    public DeviceListResponse queryHkVideoStatus(Integer pageNo) {
        /**
         * 设置接口的URI地址
         */
        final String previewURLsApi = ARTEMIS_PATH + "/api/nms/v1/online/camera/get";
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
            DeviceListResponse pointInfo = outerJson.getByPath("data", DeviceListResponse.class);

            // 返回格式化后的 JSON 字符串
            return pointInfo;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除预置点信息
     *
     * @see <a href="https://open.hikvision.com/docs/docId?productId=5c67f1e2f05948198c909700&tagPath=API%E5%88%97%E8%A1%A8-%E8%A7%86%E9%A2%91%E4%B8%9A%E5%8A%A1-%E8%A7%86%E9%A2%91%E5%8A%9F%E8%83%BD&version=%2F60df74fdc6f24041ac3d2d7f81c32325#c4281e8b">接口文档-删除预置点信息</a>
     */
    @Override
    public void deletePresetPoint(String deviceSerial, Integer presetIndex) {
        final String previewURLsApi = ARTEMIS_PATH + "/api/video/v1/presets/deletion";
        Map<String, String> path = new HashMap<>(2) {
            {
                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };
        Map<String, Object> paramMap = new HashMap<>(16) {
            {
                put(HIK_DEVICE_SERIAL, deviceSerial);
                put(HIK_PRESET_INDEX, presetIndex);
            }
        };
        String body = JSONUtil.toJsonStr(paramMap);
        try {
            String responseBody = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
            Optional.ofNullable(responseBody).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                    .filter(u -> HIK_SUCCESS_CODE.equals(u.getStr(HIK_CODE, "-1")))
                    .orElseThrow(() -> new RuntimeException("海康接口调用失败,responseBody: " + responseBody));
        } catch (Exception e) {
            throw new RuntimeException("海康接口调用失败!");
        }
    }

    /**
     * 设置预置点信息<br>
     * 若参数传已经存在的预置点编号，则可修改预置点信息
     *
     * @see <a href="https://open.hikvision.com/docs/docId?productId=5c67f1e2f05948198c909700&tagPath=API%E5%88%97%E8%A1%A8-%E8%A7%86%E9%A2%91%E4%B8%9A%E5%8A%A1-%E8%A7%86%E9%A2%91%E5%8A%9F%E8%83%BD&version=%2F60df74fdc6f24041ac3d2d7f81c32325#f0cf5c24">接口文档-设置预置点信息</a>
     */
    @Override
    public Map<String, String> managePresetPoint(String deviceSerial, String presetName, Integer presetIndex) {
        final String previewURLsApi = ARTEMIS_PATH + "/api/video/v1/presets/addition";
        Map<String, String> path = new HashMap<>(2) {
            {
                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };
        Map<String, Object> paramMap = new HashMap<>(16) {
            {
                put(HIK_DEVICE_SERIAL, deviceSerial);
                put(HIK_PRESET_NAME, presetName);
                put(HIK_PRESET_INDEX, presetIndex);
            }
        };
        String body = JSONUtil.toJsonStr(paramMap);
        try {
            String responseBody = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
            return Optional.ofNullable(responseBody).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                    .map(u -> new HashMap<String, String>() {
                        {
                            Optional.ofNullable(u.get(HIK_CODE)).map(String::valueOf).ifPresent(w -> put(HIK_CODE, w));
                            Optional.ofNullable(u.get(HIK_MSG)).map(String::valueOf).ifPresent(w -> put(HIK_MSG, w));
                        }
                    }).orElseThrow(() -> new RuntimeException("海康接口调用失败,responseBody: " + responseBody));

        } catch (Exception e) {
            throw new RuntimeException("海康接口调用失败!");
        }
    }

    /**
     * 获取url，适用于海康接口返回的{@code data}中含有{@code url}字段，且仅需要这个{@code url}值的时候
     *
     * @return url
     */
    private String getDataUrl(Map<String, String> path, Map<String, Object> paramMap) {
        String body = JSONUtil.toJsonStr(paramMap);
        try {
            String responseBody = ArtemisHttpUtil.doPostStringArtemis(artemisConfigWrapper.getArtemisConfig(), path, body, null, null, contentType);
            return Optional.ofNullable(responseBody).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                    .filter(u -> HIK_SUCCESS_CODE.equals(u.getStr(HIK_CODE, "-1"))).map(u -> u.get(HIK_DATA))
                    .map(JSONUtil::parseObj).map(u -> u.getStr(HIK_STREAM_URL))
                    .map(u -> afterHikUrlPrepare(u, paramMap.getOrDefault(HIK_STREAM_PROTOCOL, HIK_PROTOCOL_RTSP).toString()))
                    .orElseThrow(() -> new RuntimeException("海康接口调用失败,responseBody: " + responseBody));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String afterHikUrlPrepare(final String source, final String protocol) {
        return fileConfig.getStreamProtocol() + source.substring(protocol.length());
    }
}
