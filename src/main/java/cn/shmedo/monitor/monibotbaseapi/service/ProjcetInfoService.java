package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.response.ProjectInfoResult;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author cyf
 * @Date 2023/2/22 10:28
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service.redis
 * @ClassName: TbProjcetInfoService
 * @Description: TODO
 * @Version 1.0
 */
public interface ProjcetInfoService extends IService<TbProjectInfo> {

    /**
     * 查询单个项目信息详情
     * @param Id
     * @return
     */
    ProjectInfoResult getProjectInfoData(int Id);

}
