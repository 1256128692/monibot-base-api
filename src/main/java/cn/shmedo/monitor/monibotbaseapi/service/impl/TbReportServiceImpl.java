package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.param.report.WtQueryReportParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.report.WtQueryReportInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbReportService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.shmedo.monitor.monibotbaseapi.model.response.report.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:09
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbReportServiceImpl implements ITbReportService {
    private final RedisService redisService;

    @Override
    public WtQueryReportInfo queryReport(WtQueryReportParam param) {
//        WtQueryReportInfo.WtQueryReportInfoBuilder builder = WtQueryReportInfo.builder().period(param.getPeriod())
//                .companyName(Optional.of(param.getCompanyID()).map(Object::toString)
//                        .map(u -> {
//                            Collection<Object> collection = new ArrayList<>();
//                            collection.add(u);
//                            return collection;
//                        }).map(u -> redisService.multiGet(RedisKeys.COMPANY_INFO_KEY, u, Company.class))
//                        .filter(CollectionUtil::isNotEmpty).map(u -> u.get(0)).map(Company::getShortName).orElse(""))
//                .startTime(param.getStartTime()).endTime(param.getEndTime());
//        //TODO
////        REGION_AREA_KEY areaCode
//
//
//
//
//        if (CollectionUtil.isNotEmpty(param.getProjectIDList())) {
//            //TODO deal project
//        }
//        return builder.build();
        return null;
    }
}
