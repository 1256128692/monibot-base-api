package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.shmedo.iot.entity.base.SubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 参数
 *
 * @author Chengfs on 2023/3/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParameterCacheData extends AbstractCacheData {

    private Integer subjectID;

    private SubjectType subjectType;

    private String dataType;

    private String token;

    private String name;

    private String paValue;

    private Integer paUnitID;

    private String paDesc;

    public static Map<String, List<ParameterCacheData>> valueof2RedisMap(List<TbParameter> parameters) {
      return   parameters.stream().map(
              item -> {
                ParameterCacheData parameterCacheData = new ParameterCacheData();
                parameterCacheData.setID(item.getID());
                parameterCacheData.setSubjectID(item.getSubjectID());
                parameterCacheData.setSubjectType(SubjectType.valueOf(item.getSubjectType()));
                parameterCacheData.setDataType(item.getDataType());
                parameterCacheData.setToken(item.getToken());
                parameterCacheData.setName(item.getName());
                parameterCacheData.setPaValue(item.getPaValue());
                parameterCacheData.setPaUnitID(item.getPaUnitID());
                parameterCacheData.setPaDesc(item.getPaDesc());
                return parameterCacheData;
              }
      ).collect(Collectors.groupingBy(item -> item.getSubjectID().toString()));
    }
}

    
    