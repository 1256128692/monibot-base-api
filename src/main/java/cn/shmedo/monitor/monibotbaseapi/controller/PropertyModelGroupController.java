package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup.*;
import cn.shmedo.monitor.monibotbaseapi.service.PropertyModelGroupService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wuxl
 * @Date 2023/9/15 16:53
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller
 * @ClassName: PropertyModelGroupController
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@RequestMapping
public class PropertyModelGroupController {
    private final PropertyModelGroupService propertyModelGroupService;

    public PropertyModelGroupController(PropertyModelGroupService propertyModelGroupService) {
        this.propertyModelGroupService = propertyModelGroupService;
    }

    /**
     * @api {POST} /AddPropertyModelGroup 新增属性模板组
     * @apiVersion 1.0.0
     * @apiGroup 项目属性模板组模块
     * @apiName AddPropertyModelGroup
     * @apiDescription 新增属性模板组
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} groupType 属性模板组类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiParam (请求体) {Int} [groupTypeSubType] 模板组类型子分类（冗余字段，当且仅当groupType为2-工作流时候，有值：0-工单、1-巡检、2-调度）
     * @apiParam (请求体) {String} [platform] 所属平台（groupType为工作流时，必填）
     * @apiParam (请求体) {String} name 属性模板组名称
     * @apiParam (请求体) {String} [desc] 模板描述
     * @apiParam (请求体) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {Int} ID ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddPropertyModelGroup
     */
    @LogParam(moduleName = "项目属性模板组模块", operationName = "新增属性模板组", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddPropertyModelGroup")
    @PostMapping("/AddPropertyModelGroup")
    public Object addPropertyModelGroup(@Valid @RequestBody AddPropertyModelGroupParam addPropertyModelGroupParam) {
        return propertyModelGroupService.addPropertyModelGroup(addPropertyModelGroupParam);
    }

    /**
     * @api {POST} /UpdatePropertyModelGroup 更新属性模板组
     * @apiVersion 1.0.0
     * @apiGroup 项目属性模板组模块
     * @apiName UpdatePropertyModelGroup
     * @apiDescription 更新属性模板组
     * @apiParam (请求体) {Int} ID ID
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [groupType] 属性模板组类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiParam (请求体) {Int} [groupTypeSubType] 模板组类型子分类（冗余字段，当且仅当groupType为2-工作流时候，有值：0-工单、1-巡检、2-调度）
     * @apiParam (请求体) {String} [platform] 所属平台
     * @apiParam (请求体) {String} [name] 属性模板组名称
     * @apiParam (请求体) {String} [desc] 模板描述
     * @apiParam (请求体) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {Bool} data 更新结果状态
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdatePropertyModelGroup
     */
    @LogParam(moduleName = "项目属性模板组模块", operationName = "更新属性模板组", operationProperty = OperationProperty.UPDATE)
    @Permission(permissionName = "mdmbase:UpdatePropertyModelGroup")
    @PostMapping("/UpdatePropertyModelGroup")
    public Object updatePropertyModelGroup(@Valid @RequestBody UpdatePropertyModelGroupParam updatePropertyModelGroupParam) {
        return propertyModelGroupService.updatePropertyModelGroup(updatePropertyModelGroupParam);
    }

    /**
     * @api {POST} /QueryPropertyModelGroup 查看属性模板组
     * @apiVersion 1.0.0
     * @apiGroup 项目属性模板组模块
     * @apiName QueryPropertyModelGroup
     * @apiDescription 更新属性模板组
     * @apiParam (请求体) {Int} ID 属性模板组ID
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} groupType 属性模板组类型
     * @apiSuccess (返回结果) {Int} ID 属性模板组ID
     * @apiSuccess (返回结果) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Int} groupType 属性模板组类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiSuccess (返回结果) {Int} [groupTypeSubType] 模板组类型子分类（冗余字段，当且仅当groupType为2-工作流时候，有值：0-工单、1-巡检、2-调度）
     * @apiSuccess (返回结果) {String} [platform] 所属平台（groupType为工作流时，有值）
     * @apiSuccess (返回结果) {String} name 属性模板组名称
     * @apiSuccess (返回结果) {String} [desc] 模板描述
     * @apiSuccess (返回结果) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} createTime 创建时间
     * @apiSuccess (返回结果) {Int} [createUserID] 创建人ID
     * @apiSuccess (返回结果) {String} [createUserName] 创建人名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:QueryPropertyModelGroup
     */
    @Permission(permissionName = "mdmbase:QueryPropertyModelGroup")
    @PostMapping("/QueryPropertyModelGroup")
    public Object queryPropertyModelGroup(@Valid @RequestBody QueryPropertyModelGroupParam queryPropertyModelGroupParam) {
        return propertyModelGroupService.queryPropertyModelGroup(queryPropertyModelGroupParam);
    }

    /**
     * @api {POST} /DeletePropertyModelGroup 删除属性模板组
     * @apiVersion 1.0.0
     * @apiGroup 项目属性模板组模块
     * @apiName DeletePropertyModelGroup
     * @apiDescription 删除属性模板组
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} IDList 属性模板组ID集合
     * @apiSuccess (返回结果) {Int} rows 删除行数
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeletePropertyModelGroup
     */
    @LogParam(moduleName = "项目属性模板组模块", operationName = "删除属性模板组", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeletePropertyModelGroup")
    @PostMapping("/DeletePropertyModelGroup")
    public Object deletePropertyModelGroup(@Valid @RequestBody DeletePropertyModelGroupParam deletePropertyModelGroupParam) {
        return propertyModelGroupService.deletePropertyModelGroup(deletePropertyModelGroupParam);
    }

    /**
     * @api {POST} /QueryPropertyModelGroupList 查看属性模板组列表
     * @apiVersion 1.0.0
     * @apiGroup 项目属性模板组模块
     * @apiName QueryPropertyModelGroupList
     * @apiDescription 查看属性模板组列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} groupType 属性模板组类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiParam (请求体) {String} [groupName] 属性模板组名称
     * @apiSuccess (返回结果) {Object[]} groupList 属性模板组列表
     * @apiSuccess (返回结果) {Int} groupList.ID 属性模板组ID
     * @apiSuccess (返回结果) {Int} groupList.companyID 公司ID
     * @apiSuccess (返回结果) {String} groupList.name 属性模板组名称
     * @apiSuccess (返回结果) {String} groupList.platform 所属平台
     * @apiSuccess (返回结果) {Int} groupList.groupType 属性模板组类型（0-工程项目（默认值）；1-设备；2-工作流）
     * @apiSuccess (返回结果) {Int} [groupList.groupTypeSubType] 模板组类型子分类
     * @apiSuccess (返回结果) {String} [groupList.desc] 模板描述
     * @apiSuccess (返回结果) {String} [groupList.exValue] 扩展字段
     * @apiSuccess (返回结果) {String} createTime 创建时间
     * @apiSuccess (返回结果) {Int} [createUserID] 创建人ID
     * @apiSuccess (返回结果) {String} [createUserName] 创建人名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:QueryPropertyModelGroupList
     */
    @Permission(permissionName = "mdmbase:QueryPropertyModelGroupList")
    @PostMapping("/QueryPropertyModelGroupList")
    public Object queryPropertyModelGroupList(@Valid @RequestBody QueryPropertyModelGroupListParam queryPropertyModelGroupListParam) {
        return propertyModelGroupService.queryPropertyModelGroupList(queryPropertyModelGroupListParam);
    }
}
