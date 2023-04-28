package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.report.WtQueryReportParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.report.WtQueryReportInfo;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:08
 */
public interface ITbReportService {
    WtQueryReportInfo queryReport(WtQueryReportParam param);
}
