package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import lombok.Data;

import java.util.List;

/**
 * 基础配置 响应体
 *
 * @author Chengfs on 2023/4/10
 */
@Data
public class BaseConfigResponse {


    /**
     * 扩展字段
     */
    private List<TbMonitorTypeField> exFields;

    /**
     * 参数
     */
    private List<TbParameter> paramFields;
}

    
    