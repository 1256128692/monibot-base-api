package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 17:00
 * @desc: 通用配置接口
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectConfigController {
    private final TbProjectInfoMapper tbProjectInfoMapper;

    public Object getProjectConfig(Object addProjectConfigParam) {
        return null;
    }

    public Object setProjectConfig(Object deleteProjectConfigParam) {
        return null;
    }

    public Object listProjectConfig(Object listProjectConfigParam) {
        return null;
    }

    public Object batchSetProjectConfig(Object updateProjectConfigParam) {
        return null;
    }
}
