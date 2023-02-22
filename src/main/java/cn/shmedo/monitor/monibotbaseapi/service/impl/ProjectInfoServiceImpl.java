package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.model.param.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ProjcetInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author cyf
 * @Date 2023/2/22 10:28
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service.redis.Impl
 * @ClassName: TbProjectInfoServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class ProjectInfoServiceImpl extends ServiceImpl<TbProjectInfoMapper, TbProjectInfo> implements ProjcetInfoService {

    @Autowired
    private ProjcetInfoService projcetInfoService;
    @Override
    public ProjectInfoResult getProjectInfoData(int Id) {
        TbProjectInfo projectInfo = projcetInfoService.getById(Id);
        System.out.println(projectInfo.toString());
        return null;
    }
}
