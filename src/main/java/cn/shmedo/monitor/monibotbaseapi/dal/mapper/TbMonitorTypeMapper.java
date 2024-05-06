package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.dto.MonitorTypeWithField;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypeFieldListV2Param;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeFieldListV2Info;
import cn.shmedo.monitor.monibotbaseapi.model.response.TbMonitorType4web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.MonitorTypeBaseInfoV1;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbMonitorTypeMapper extends BaseMapper<TbMonitorType> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorType record);

    int insertSelective(TbMonitorType record);

    TbMonitorType selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorType record);

    int updateByPrimaryKey(TbMonitorType record);

    List<TbMonitorType> selectAll();

    List<MonitorTypeBaseInfo> selectMonitorBaseInfo(List<Integer> monitorItemIDList);

    @Deprecated
    IPage<TbMonitorType4web> queryPage(Page<TbMonitorType4web> page,
                                       @Param("companyID") Integer companyID,
                                       @Param("createType") Byte createType,
                                       @Param("queryCode") String queryCode,
                                       @Param("typeList") List<Integer> typeList,
                                       @Param("monitorType") Integer monitorType,
                                       @Param("projectID") Integer projectID,
                                       @Param("typeName") String typeName);

    List<TbMonitorType4web> queryPage(@Param("companyID") Integer companyID,
                                       @Param("createType") Byte createType,
                                       @Param("queryCode") String queryCode,
                                       @Param("typeList") List<Integer> typeList,
                                       @Param("monitorType") Integer monitorType,
                                       @Param("projectID") Integer projectID,
                                       @Param("typeName") String typeName);

    List<TbMonitorType> queryByTemplateIDList(List<Integer> templateIDList);

    void deleteByMonitorTypeList(List<Integer> monitorTypeList);

    /**
     * 查询监测类型及其字段
     *
     * @param wrapper 条件构造器 {@link Wrapper<Void>}
     * @return {@link MonitorTypeWithField}
     */
    List<MonitorTypeWithField> queryMonitorTypeWithField(@Param(Constants.WRAPPER) Wrapper<Void> wrapper);

    TbMonitorType queryByType(Integer type);

    List<MonitorTypeBaseInfoV1> selectByMonitorTypeList(List<Integer> monitorTypeList);

    List<MonitorTypeBaseInfoV1> selectAllMonitorTypeBaseInfoV1();

    List<MonitorTypeFieldListV2Info> selectMonitorTypeFieldListV2(@Param("param") QueryMonitorTypeFieldListV2Param param);
}