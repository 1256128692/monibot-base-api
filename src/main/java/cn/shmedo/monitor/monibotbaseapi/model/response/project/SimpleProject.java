package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import cn.shmedo.monitor.monibotbaseapi.model.response.AuthService;
import lombok.Builder;
import lombok.Data;

import javax.naming.InitialContext;
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
    private Byte projectType;
    private String projectTypeStr;

    private List<Integer> serviceIDList;
    private List<AuthService> serviceList;

    private List<SimpleProject> nnLevelProjectList;
}
