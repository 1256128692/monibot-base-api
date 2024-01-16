package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnBaseConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:48
 */
@Service
public class TbWarnBaseConfigServiceImpl extends ServiceImpl<TbWarnBaseConfigMapper, TbWarnBaseConfig> implements ITbWarnBaseConfigService {
    @Override
    public TbWarnBaseConfig queryByCompanyIDAndPlatform(Integer companyID, Integer platform) {
        return this.list(new LambdaQueryWrapper<TbWarnBaseConfig>().eq(TbWarnBaseConfig::getCompanyID, companyID)
                .eq(TbWarnBaseConfig::getPlatform, platform)).stream().findFirst().orElse(getDefault(companyID, platform));
    }

}
