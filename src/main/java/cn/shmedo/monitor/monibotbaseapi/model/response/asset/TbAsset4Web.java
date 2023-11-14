package cn.shmedo.monitor.monibotbaseapi.model.response.asset;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-08 11:22
 **/
@Data
public class TbAsset4Web extends TbAsset {
    private Integer houseID;
    private String houseName;
    private String houseCode;
    private String houseAddress;
    private String houseComment;
    private Integer currentValue;
    private Boolean isWarn;
    private String unitStr;
}
