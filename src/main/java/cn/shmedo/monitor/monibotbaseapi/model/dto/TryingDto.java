package cn.shmedo.monitor.monibotbaseapi.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 获取试运行参数 响应体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class TryingDto {

    private Integer calType;

    private List<MonitorTypeField> fieldList;

    private String script;

    private List<Param> paramList;

    public record Param(String name, String value, Integer unitID, String origin) {
    }

}

    
    