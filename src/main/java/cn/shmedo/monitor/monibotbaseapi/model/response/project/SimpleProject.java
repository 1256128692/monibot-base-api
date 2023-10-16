package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-25 14:47
 **/
@Data
@Builder
public class SimpleProject {
    private Integer id;
    private String name;

    private Byte level;
    private String platformTypeSet;
    private Byte projectType;
    private String projectTypeStr;

    private List<SimpleProject> nnLevelProjectList;
}
