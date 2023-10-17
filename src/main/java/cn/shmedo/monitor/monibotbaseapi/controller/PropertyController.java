package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.*;
import cn.shmedo.monitor.monibotbaseapi.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class PropertyController {
    private PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    /**
     * @api {POST} /AddModel 新增模板
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName AddModel
     * @apiDescription 新增模板
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [modelType] 模板类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiParam (请求体) {Int} [modelTypeSubType] 模板类型子分类（冗余字段，当且仅当ModelType为2-工作流时候，有值<0-工单、1-巡检、2-调度>）
     * @apiParam (请求体) {Int} [groupID] 模板组ID（当modelType为0或空时，groupID为projectType）
     * @apiParam (请求体) {Int} projectType 项目类型（modelType为0时，必传，其它情况统一传-1）
     * @apiParam (请求体) {String} modelName 模型名称
     * @apiParam (请求体) {String} [platform] 所属平台
     * @apiParam (请求体) {String} [desc] 模板描述
     * @apiParam (请求体) {Object[]} modelPropertyList 自定义属性列表
     * @apiParam (请求体) {String} modelPropertyList.name 属性名称
     * @apiParam (请求体) {Int} modelPropertyList.type 属性类型：1.数值,2.字符串,3.枚举,4.日期时间,5.图片
     * @apiParam (请求体) {String} [modelPropertyList.unit] 自定义属性单位
     * @apiParam (请求体) {Boolean} modelPropertyList.required 自定义属性是否必填
     * @apiParam (请求体) {String} [modelPropertyList.enumField] 枚举字段，json数组, 限制长度为10
     * @apiParam (请求体) {Boolean} [modelPropertyList.multiSelect] 可否多选,限定枚举
     * @apiParam (请求体) {String} [modelPropertyList.className] 类名称
     * @apiParam (请求体) {Int} [modelPropertyList.displayOrder] 展示顺序
     * @apiParam (请求体) {String} [modelPropertyList.exValue] 额外属性
     * @apiSuccess (返回结果) {Int} ID ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddModel
     */
    @LogParam(moduleName = "属性管理模块", operationName = "新增模板", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddModel")
    @RequestMapping(value = "AddModel", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addModel(@RequestBody @Validated AddModelParam param) {
        return propertyService.addModel(param, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
    }

    /**
     * @api {POST} /UpdateModel 更新模板
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName UpdateModel
     * @apiDescription 更新模板（模板下属性，支持新增、更新、删除；注意，如果对于更新的属性，且属性实体字段均未修改，仍然需要将完整的实体传给后端，包括ID）
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} ID 模板ID
     * @apiParam (请求体) {Int} [modelType] 模板类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiParam (请求体) {Int} [modelTypeSubType] 模板类型子分类（冗余字段，当且仅当ModelType为2-工作流时候，有值<0-工单、1-巡检、2-调度>）
     * @apiParam (请求体) {Int} [groupID] 模板组ID（当modelType为0或空时，groupID为projectType）
     * @apiParam (请求体) {Int} projectType 项目类型（modelType为0时，必传，其它情况统一传-1）
     * @apiParam (请求体) {String} [modelName] 模型名称
     * @apiParam (请求体) {String} [desc] 模板描述
     * @apiParam (请求体) {Object[]} [modelPropertyList] 自定义属性列表
     * @apiParam (请求体) {Int} modelPropertyList.ID 属性ID
     * @apiParam (请求体) {String} modelPropertyList.name 属性名称
     * @apiParam (请求体) {Int} modelPropertyList.type 属性类型：1.数值,2.字符串,3.枚举,4.日期时间,5.图片
     * @apiParam (请求体) {String} modelPropertyList.unit 自定义属性单位
     * @apiParam (请求体) {Boolean} modelPropertyList.required 自定义属性是否必填
     * @apiParam (请求体) {String} modelPropertyList.enumField 枚举字段，json数组, 限制长度为10
     * @apiParam (请求体) {Boolean} modelPropertyList.multiSelect 可否多选,限定枚举
     * @apiParam (请求体) {String} modelPropertyList.className 类名称
     * @apiParam (请求体) {Int} modelPropertyList.createType 创建类型 0-预定义 1-自定义
     * @apiParam (请求体) {Int} modelPropertyList.displayOrder 展示顺序
     * @apiParam (请求体) {String} modelPropertyList.exValue 额外属性
     * @apiSuccess (返回结果) {Bool} data 更新结果状态
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateModel
     */
    @LogParam(moduleName = "属性管理模块", operationName = "更新模板", operationProperty = OperationProperty.UPDATE)
    @Permission(permissionName = "mdmbase:UpdateModel")
    @RequestMapping(value = "UpdateModel", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateModel(@RequestBody @Valid UpdateModelParam param) {
        return propertyService.updateModel(param);
    }

    /**
     * @api {POST} /CopyModel 复制模板
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName CopyModel
     * @apiDescription 复制模板
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} modelID 模板ID
     * @apiSuccess (返回结果) {Int} ID ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:CopyModel
     */
    @LogParam(moduleName = "属性管理模块", operationName = "复制模板", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddModel")
    @RequestMapping(value = "CopyModel", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object copyModel(@RequestBody @Valid CopyModelParam param){
        return propertyService.copyModel(param);
    }

    /**
     * @api {POST} /TransferGrouping 转移分组
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName TransferGrouping
     * @apiDescription 转移分组，此功能目前为设备模板/工作流模板使用
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} modelID 模板ID
     * @apiParam (请求体) {Int} modelType 模板类型
     * @apiParam (请求体) {Int} newGroupID 转移后的分组ID
     * @apiSuccess (返回结果) {Bool} data 更新结果状态
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:TransferGrouping
     */
    @LogParam(moduleName = "属性管理模块", operationName = "转移模板", operationProperty = OperationProperty.UPDATE)
    @Permission(permissionName = "mdmbase:UpdateModel")
    @RequestMapping(value = "TransferGrouping", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object transferGrouping(@RequestBody @Valid TransferGroupingParam param) {
        return propertyService.transferGrouping(param);
    }

    /**
     * @api {POST} /DeleteModel 删除模板
     *
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName DeleteModel
     * @apiDescription 删除模板，设备表单支持分组管理，默认分组不可删除
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} modelIDList 模板ID集合
     * @apiSuccess (返回结果) {Int} rows 删除行数
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteModel
     */
    @LogParam(moduleName = "属性管理模块", operationName = "删除模板", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeleteModel")
    @RequestMapping(value = "DeleteModel", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteModel(@RequestBody @Valid DeleteModelParam param) {
        return propertyService.deleteModel(param);
    }

    /**
     * @api {POST} /QueryModelList 查询模板信息
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName QueryModelList
     * @apiDescription 查询模板信息
     * @apiParam (请求体) {Int} [modelID] 模板ID, 优先级最高的参数
     * @apiParam (请求体) {Int} [companyID] 公司ID
     * @apiParam (请求体) {Int} [projectType] 项目类型（modelType为1或者2时，统一传-1）
     * @apiParam (请求体) {String} [name] 模板名称
     * @apiParam (请求体) {Int} [modelType] 模板类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiParam (请求体) {Int} [modelTypeSubType] 模板类型子分类（冗余字段，当且仅当ModelType为2-工作流时候，有值<0-工单、1-巡检、2-调度>）
     * @apiParam (请求体) {Int} [groupID] 模板组ID（当modelType为0或空时，groupID对应projectType）
     * @apiParam (请求体) {String} [platform] 所属平台（冗余字段，当且仅当ModelType为2-工作流时候，有值）
     * @apiParam (请求体) {Int} [createType] 创建类型
     * @apiSuccess (返回结果) {Object[]} modelList  模板列表
     * @apiSuccess (返回结果) {Int} modelList.modelID  模板ID
     * @apiSuccess (返回结果) {Int} modelList.companyID  公司ID
     * @apiSuccess (返回结果) {Int} modelList.modelType  模板类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiSuccess (返回结果) {Int} modelList.groupID  模板组ID（当modelType为0时，groupID对应projectType）
     * @apiSuccess (返回结果) {Int} modelList.groupName  模板组名称（当modelType为0时，group对应projectName）
     * @apiSuccess (返回结果) {String} modelList.name  模板名称
     * @apiSuccess (返回结果) {Int} modelList.createType  模板创建类型
     * @apiSuccess (返回结果) {Object[]} modelList.propertyList  模板的属性列表
     * @apiSuccess (返回结果) {Int} modelList.propertyList.propertyID  属性ID
     * @apiSuccess (返回结果) {String} modelList.propertyList.name  模板的属性名称
     * @apiSuccess (返回结果) {Int} modelList.propertyList.type  属性类型：1.数值,2.字符串,3.枚举,4.日期时间,5.图片
     * @apiSuccess (返回结果) {Int} modelList.propertyList.groupID  模板组ID（当modelType为0时，groupID对应projectType）
     * @apiSuccess (返回结果) {String} [modelList.propertyList.unit] 属性单位
     * @apiSuccess (返回结果) {Boolean} modelList.propertyList.required 属性是否必填
     * @apiSuccess (返回结果) {String} [modelList.propertyList.enumField] 枚举字段，json数组
     * @apiSuccess (返回结果) {Boolean} [modelList.propertyList.multiSelect] 可否多选,限定枚举
     * @apiSuccess (返回结果) {Int} modelList.propertyList.createType 创建类型
     * @apiSuccess (返回结果) {String} [modelList.propertyList.className] 工程名称
     * @apiSuccess (返回结果) {Int} [modelList.propertyList.displayOrder] 展示顺序
     * @apiSuccess (返回结果) {String} [modelList.propertyList.exValue] 拓展信息
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeModel
     */
    @Permission(permissionName = "mdmbase:DescribeModel")
    @RequestMapping(value = "QueryModelList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryModelList(@RequestBody @Validated QueryModelListParam param) {
        return propertyService.queryModelList(param);
    }

    /**
     * @api {POST} /UpdateProperty 更新项目属性
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName UpdateProperty
     * @apiDescription 更新项目属性，目前只支持模板类型为工程项目，其他类型功能暂未开放
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} [modelType] 模板类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiParam (请求体) {Object[]} modelValueList 模型值列表
     * @apiParam (请求体) {String} modelValueList.id 属性ID
     * @apiParam (请求体) {String} [modelValueList.value] 属性值
     * @apiSuccess (返回结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseProject
     */
    @LogParam(moduleName = "属性管理模块", operationName = "更新项目属性", operationProperty = OperationProperty.UPDATE)
    @Permission(permissionName = "mdmbase:UpdateBaseProject")
    @RequestMapping(value = "UpdateProperty", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateProperty(@RequestBody @Validated UpdatePropertyParam pa) {
        propertyService.updateProperty(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryPropertyValue 查询指定属性所有值
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName QueryPropertyValue
     * @apiDescription 查询指定属性所有值（去重），
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [projectID] 主体ID， 可为项目ID，其他设备ID，工作流ID
     * @apiParam (请求体) {Int} projectType 项目类型（modelType为0时，必传，其它情况统一传-1）
     * @apiParam (请求体) {Int} [modelType] 模板类型（0-工程项目（默认值）；1-其他设备；2-工作流）
     * @apiParam (请求体) {Int} [groupID] 模板组ID（当modelType为0或空时，groupID为projectType）
     * @apiParam (请求体) {Int} [createType] 创建类型 0-预定义 1-自定义, 默认0
     * @apiParam (请求体) {String} propertyName 属性名称
     * @apiSuccess (返回结果) {Object[]} data 属性值列表
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeModel
     */
    @Permission(permissionName = "mdmbase:DescribeModel")
    @PostMapping(value = "QueryPropertyValue", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryPropertyValue(@RequestBody @Validated QueryPropertyValueParam param) {
        return propertyService.queryPropertyValue(param);
    }

    /**
     * @api {POST} /AddPropertyValues 保存模板属性值
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName AddPropertyValues
     * @apiDescription 保存模板属性值
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} subjectType 对象类型，1:工程 2:其他设备 3:工作流
     * @apiParam (请求体) {Int} subjectID 对象ID
     * @apiParam (请求体) {Object[]} propertyValueList 表单属性值列表
     * @apiParam (请求体) {Int} propertyValueList.ID 表单属性值ID
     * @apiParam (请求体) {String} propertyValueList.value 表单属性值
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddModel
     */
    @LogParam(moduleName = "属性管理模块", operationName = "保存模板属性值", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddModel")
    @PostMapping(value = "AddPropertyValues", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addPropertyValues(@RequestBody @Validated AddPropertyValuesParam param) {
        propertyService.AddPropertyValues(param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryPropertyValues 查询模板属性值
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName QueryPropertyValues
     * @apiDescription 查询模板属性值，支持批量查询多个模板
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} subjectType 对象类型，1:工程 2:其他设备 3:工作流
     * @apiParam (请求体) {Int} subjectID 对象ID
     * @apiParam (请求体) {Int[]} modelIDList 模板ID列表
     * @apiSuccess (返回结果) {Object[]} modelList 模板列表
     * @apiSuccess (返回结果) {Int} modelList.modelID 模板ID
     * @apiSuccess (返回结果) {Int} modelList.modelName 模板名称
     * @apiSuccess (返回结果) {Object[]} modelList.propertyValueList 表单属性值列表
     * @apiSuccess (返回结果) {Int} modelList.propertyValueList.ID 表单属性值ID
     * @apiSuccess (返回结果) {String} modelList.propertyValueList.value 表单属性值
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeModel
     */
    @Permission(permissionName = "mdmbase:DescribeModel")
    @PostMapping(value = "QueryPropertyValues", consumes = DefaultConstant.JSON)
    public Object queryPropertyValues(@RequestBody @Validated QueryPropertyValuesParam param) {
        return propertyService.queryPropertyValues(param);
    }

}
