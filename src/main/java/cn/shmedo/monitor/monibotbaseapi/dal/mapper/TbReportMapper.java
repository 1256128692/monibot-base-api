package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.response.report.TbBaseReportInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-28 09:34
 */
@Mapper
public interface TbReportMapper {
    List<TbBaseReportInfo> queryBaseReportInfo(@Param("companyID") Integer companyID, @Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime);
}
