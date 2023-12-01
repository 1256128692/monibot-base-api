package cn.shmedo.monitor.monibotbaseapi.model.response.asset;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-08 10:52
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TbAssetLog4Web extends TbAssetLog {
    private String assetName;
    private String assetVendor;
    private Byte assetType;
    private String assetTypeStr;
    private Byte assetUnit;
    private String assetUnitStr;
    private String houseName;
    private String houseCode;
    private String houseAddress;
    private String houseComment;
}
