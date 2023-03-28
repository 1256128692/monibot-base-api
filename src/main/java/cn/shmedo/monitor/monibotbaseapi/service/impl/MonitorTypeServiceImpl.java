package cn.shmedo.monitor.monibotbaseapi.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.TbMonitorType4web;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.TypeAndCount;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorTypeService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-24 17:02
 **/
@Service
@AllArgsConstructor
public class MonitorTypeServiceImpl extends ServiceImpl<TbMonitorTypeMapper, TbMonitorType> implements MonitorTypeService {
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;
    private final TbMonitorTypeMapper tbMonitorTypeMapper;
    private final TbMonitorTypeTemplateMapper tbMonitorTypeTemplateMapper;

    @Override
    public PageUtil.Page<TbMonitorType4web> queryMonitorTypePage(QueryMonitorTypePageParam pa) {
        Page<TbMonitorType4web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        List<Integer> typeList;
        if (ObjectUtil.isAllEmpty(pa.getFuzzyFieldName(), pa.getFuzzyFieldToken())){
            typeList = null;
        }else {
            typeList = tbMonitorTypeFieldMapper.queryMonitorTypeByFuzzyNameAndFuzzyToken(pa.getFuzzyFieldName(), pa.getFuzzyFieldToken());
            if (ObjectUtil.isEmpty(typeList)){
                return PageUtil.Page.empty();
            }
        }
        IPage<TbMonitorType4web> pageData =  tbMonitorTypeMapper.queryPage(page, pa.getCompanyID(), pa.getCreateType(), pa.getFuzzyTypeName(), typeList);
        // 处理字段和数据源统计
        List<Integer> monitorTypeList = pageData.getRecords().stream().map(TbMonitorType4web::getMonitorType).collect(Collectors.toList());
        List<TbMonitorTypeField> temp = tbMonitorTypeFieldMapper.queryByMonitorTypes(monitorTypeList);
        Map<Integer, List<TbMonitorTypeField>> typeMap = temp.stream().collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType));
        List<TypeAndCount>  countList= tbMonitorTypeTemplateMapper.countGroupByMonitorType(monitorTypeList);
        Map<Integer, Integer> countMap = countList.stream().collect(Collectors.toMap(TypeAndCount::getType, TypeAndCount::getCount));
        pageData.getRecords().forEach(item ->{
            item.setDatasourceCount(countMap.get(item.getMonitorType()));
            item.setFieldList(typeMap.get(item.getMonitorType()));
        });
        return new PageUtil.Page<>(pageData.getPages(),pageData.getRecords(),pageData.getTotal());
    }
}
