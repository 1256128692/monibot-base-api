package cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig;

import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-19 10:08
 */
@Data
public class ConfigBaseResponse {
    private Integer targetID;
    private Integer childID;
    private String targetName;
    private String config;
}
