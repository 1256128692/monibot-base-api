package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-01 15:17
 */
@Data
public class BulletinDataBaseInfo {
    private Integer companyID;
    @JsonIgnore
    private String platformStr;
    private List<Integer> platform;
    private String platformDesc;
    private Integer type;
    private String name;
    private String content;
    private String createUser;
    private Date publishTime;

    /**
     * 处理平台描述
     *
     * @param serviceIdNameMap <pre>Map<{@code serviceId},{@code serviceName}></pre>
     */
    public void handlePlatform(final Map<Integer, String> serviceIdNameMap) {
        Optional.ofNullable(platformStr).filter(StrUtil::isNotEmpty).ifPresent(u -> {
            this.setPlatform(Arrays.stream(u.split(",")).map(Integer::parseInt).distinct().sorted().toList());
            this.setPlatformDesc(String.join(",", platform.stream().distinct().sorted().map(serviceIdNameMap::get)
                    .filter(Objects::nonNull).toList()));
        });
    }
}
