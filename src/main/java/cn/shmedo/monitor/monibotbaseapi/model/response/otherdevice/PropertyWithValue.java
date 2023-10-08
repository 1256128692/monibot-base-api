package cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice;

import lombok.Builder;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 14:10
 **/
@Data
@Builder
public class PropertyWithValue {
    private Integer ID;
    private String name;
    private String unit;
    private String value;

}
