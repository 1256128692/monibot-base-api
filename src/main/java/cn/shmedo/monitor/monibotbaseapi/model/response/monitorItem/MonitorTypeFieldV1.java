package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-11 15:03
 **/
@Data
public class MonitorTypeFieldV1 {

    private Integer fieldID;
    private String fieldToken;
    private String fieldName;
    private String fieldAlias;
    private String fieldDesc;
    private Integer fieldUnit;
    private String engUnit;
    private Integer itemID;

}
