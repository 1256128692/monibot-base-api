package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import lombok.Data;

import java.util.List;

/**
 * @Author cyf
 * @Date 2023/2/28 14:12
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.project
 * @ClassName: ProjectIDListParam
 * @Description:
 * @Version 1.0
 */
@Data
public class ProjectIDListParam {
    List<Integer> dataIDList;
}
