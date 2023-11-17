package cn.shmedo.monitor.monibotbaseapi.model.entity;

import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-11-12 17:56
 **/
@Data
public class MonitorTypeExValue {
    private List<Integer> displayDensity;
    private List<Integer> statisticalMethods;
}
