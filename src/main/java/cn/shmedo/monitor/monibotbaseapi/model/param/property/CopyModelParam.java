package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-24 13:34
 **/
@Data
@ToString
public class CopyModelParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "模板ID不能为空")
    private Integer modelID;

    @JsonIgnore
    private TbPropertyModel tbPropertyModel;

    @Override
    public ResultWrapper<?> validate() {
        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        tbPropertyModel = tbPropertyModelMapper.selectByPrimaryKey(modelID);
        if (Objects.isNull(tbPropertyModel)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未查询到对应模板");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    /**
     * 复制模板时，给模板字段添加后缀
     * todo 优化后缀
     *
     * @param newTbPropertyModel
     */
    public void wrapperTbPropertyModel(TbPropertyModel newTbPropertyModel) {
        newTbPropertyModel.setID(null);
        newTbPropertyModel.setCreateTime(LocalDateTime.now());
        newTbPropertyModel.setCreateUserID(CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        newTbPropertyModel.setName(newTbPropertyModel.getName() + DefaultConstant.PROPERTY_MODEL_COPY_SUFFIX);
    }

    /**
     * 复制模板属性时，给模板属性字段添加后缀
     * todo 优化后缀
     *
     * @param tbPropertyList
     * @param ID 模板ID
     */
    public void wrapperTbProperty(List<TbProperty> tbPropertyList, Integer ID) {
        for(TbProperty tbProperty : tbPropertyList){
            tbProperty.setID(null);
            tbProperty.setModelID(ID);
            tbProperty.setName(tbProperty.getName() + DefaultConstant.PROPERTY_MODEL_COPY_SUFFIX);
        }
    }
}
