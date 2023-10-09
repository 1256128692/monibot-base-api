package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import lombok.Builder;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-25 14:47
 **/
@Data
@Builder
public class IDNameLevel {
    private Integer id;
    private String name;

    private Byte level;
}
