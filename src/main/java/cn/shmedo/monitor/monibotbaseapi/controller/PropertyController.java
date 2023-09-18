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
import org.apache.ibatis.annotations.Update;
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
     * @apiParam (请求体) {Int} projectType 项目类型（已废弃，待适配后删除）
     * @apiParam (请求体) {Int} modelType 模板类型（0-监测项目；1-设备；2-工作流）
     * @apiParam (请求体) {Int} groupID 模板组ID
     * @apiParam (请求体) {String} modelName 模型名称
     * @apiParam (请求体) {String} [desc] 模板描述
     * @apiParam (请求体) {Object[]} modelPropertyList 自定义属性列表
     * @apiParam (请求体) {String} modelPropertyList.name 属性名称
     * @apiParam (请求体) {Int} modelPropertyList.type 属性类型：数值，字符串，枚举，日期，图片
     * @apiParam (请求体) {String} [modelPropertyList.unit] 自定义属性单位
     * @apiParam (请求体) {Boolean} modelPropertyList.required 自定义属性是否必填
     * @apiParam (请求体) {String} [modelPropertyList.enumField] 枚举字段，json数组, 限制长度为10
     * @apiParam (请求体) {Boolean} [modelPropertyList.multiSelect] 可否多选,限定枚举
     * @apiParam (请求体) {String} [modelPropertyList.className] 类名称
     * @apiParam (请求体) {Int} [modelPropertyList.displayOrder] 展示顺序
     * @apiParam (请求体) {String} [modelPropertyList.exValue] 额外属性
     * @apiSuccess (返回结果) {Int} modelID  模板ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddModel
     */
    @LogParam(moduleName = "属性管理模块", operationName = "新增模板", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddModel")
    @RequestMapping(value = "AddModel", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addModel(@RequestBody @Validated AddModelParam param) {
        propertyService.addModel(param, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateModel 修改模板
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName UpdateModel
     * @apiDescription 修改模板
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} modelID 模板ID
     * @apiParam (请求体) {Int} projectType 项目类型（已废弃，待适配后删除）
     * @apiParam (请求体) {Int} [modelType] 模板类型（0-监测项目；1-设备；2-工作流）
     * @apiParam (请求体) {Int} [groupID] 模板组ID
     * @apiParam (请求体) {String} [modelName] 模型名称
     * @apiParam (请求体) {String} [desc] 模板描述
     * @apiParam (请求体) {Object[]} [modelPropertyList] 自定义属性列表
     * @apiParam (请求体) {Int} modelPropertyList.ID 属性ID
     * @apiParam (请求体) {String} modelPropertyList.name 属性名称
     * @apiParam (请求体) {Int} modelPropertyList.type 属性类型：数值，字符串，枚举，日期，图片
     * @apiParam (请求体) {String} modelPropertyList.unit 自定义属性单位
     * @apiParam (请求体) {Boolean} modelPropertyList.required 自定义属性是否必填
     * @apiParam (请求体) {String} modelPropertyList.enumField 枚举字段，json数组, 限制长度为10
     * @apiParam (请求体) {Boolean} modelPropertyList.multiSelect 可否多选,限定枚举
     * @apiParam (请求体) {String} modelPropertyList.className 类名称
     * @apiParam (请求体) {Int} modelPropertyList.displayOrder 展示顺序
     * @apiParam (请求体) {String} modelPropertyList.exValue 额外属性
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateModel
     */
    @LogParam(moduleName = "属性管理模块", operationName = "修改模板", operationProperty = OperationProperty.UPDATE)
    @Permission(permissionName = "mdmbase:UpdateModel")
    @RequestMapping(value = "UpdateModel", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateModel(@RequestBody @Valid UpdateModelParam param) {
        propertyService.updateModel(param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteModel 删除模板
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName DeleteModel
     * @apiDescription 删除模板
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} modelIDList 模型ID集合
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
     * @apiParam (请求体) {Int} projectType 项目类型（已废弃，待适配后删除）
     * @apiParam (请求体) {Int} modelType 模板类型（0-监测项目；1-设备；2-工作流）
     * @apiParam (请求体) {Int} [createType] 创建类型
     * @apiSuccess (返回结果) {Object[]} modelList  模板列表
     * @apiSuccess (返回结果) {Int} modelList.modelID  模板ID
     * @apiSuccess (返回结果) {Int} modelList.companyID  公司ID
     * @apiSuccess (返回结果) {Int} modelList.modelType  模板类型（0-监测项目；1-设备；2-工作流）
     * @apiSuccess (返回结果) {Int} modelList.groupID  模板组ID
     * @apiSuccess (返回结果) {String} modelList.name  模板名称
     * @apiSuccess (返回结果) {Int} modelList.createType  模板创建类型
     * @apiSuccess (返回结果) {Object[]} modelList.propertyList  模板的属性列表
     * @apiSuccess (返回结果) {Int} modelList.propertyList.propertyID  属性ID
     * @apiSuccess (返回结果) {String} modelList.propertyList.name  模板的属性名称
     * @apiSuccess (返回结果) {Int} modelList.propertyList.type  属性类型：1数值，2字符串，3枚举，4日期，5图片
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
     * @apiDescription 更新项目属性
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 项目ID
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
     * @apiDescription 查询指定属性所有值（去重）
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [projectID] 项目ID
     * @apiParam (请求体) {Int} projectType 项目类型
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
}
