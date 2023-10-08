package cn.shmedo.monitor.monibotbaseapi.model.response.asset;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetLog;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-08 10:52
 **/
@Data
public class TbAssetLog4Web extends TbAssetLog {
    private String assetName;
    private String assetVendor;
    private Byte assetType;
    private String assetTypeStr;
    private Byte assetUnit;
    private String assetUnitStr;
    private String houseName;
}
