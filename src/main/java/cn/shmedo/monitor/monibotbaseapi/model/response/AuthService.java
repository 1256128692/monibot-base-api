package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-29 18:23
 **/
@Data
public class AuthService {
    private Integer id;
    private String serviceName;
    private String serviceAlias;
    private String serviceDesc;
}
