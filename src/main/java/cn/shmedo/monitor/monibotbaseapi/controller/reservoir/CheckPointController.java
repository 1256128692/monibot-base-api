package cn.shmedo.monitor.monibotbaseapi.controller.reservoir;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint.*;
import cn.shmedo.monitor.monibotbaseapi.service.CheckPointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 水库巡检点
 *
 * @author Chengfs on 2024/2/27
 */
@RestController
@RequiredArgsConstructor
public class CheckPointController {

    private final CheckPointService service;

    /**
     * @api {POST} /AddCheckPoint 新增巡检点
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName AddCheckPoint
     * @apiDescription 新增巡检点
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} projectID 项目id
     * @apiParam (请求参数) {Int} serviceID 平台id
     * @apiParam (请求参数) {String} name 巡检点名称(10)
     * @apiParam (请求参数) {String} address 巡检点地址
     * @apiParam (请求参数) {String} location 巡检点地址经纬度
     * @apiSuccess (返回结果) {Int} data 巡检点id
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateCheckPoint
     */
    @Permission(permissionName = "mdmbase:UpdateCheckPoint")
    @LogParam(moduleName = "巡检管理", operationName = "新增巡检点", operationProperty = OperationProperty.ADD)
    @PostMapping(value = "AddCheckPoint", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object add(@Valid @RequestBody AddCheckPointRequest body) {
        return service.save(body);
    }

    /**
     * @api {POST} /UpdateCheckPoint 更新巡检点
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName UpdateCheckPoint
     * @apiDescription 更新巡检点(为空的字段不会更新)
     * @apiParam (请求参数) {Int} id 巡检点id
     * @apiParam (请求参数) {String} [name] 巡检点名称(10)
     * @apiParam (请求参数) {String} [address] 巡检点地址
     * @apiParam (请求参数) {String} [location] 巡检点地址经纬度
     * @apiParam (请求参数) {Boolean} [enable] 是否启用
     * @apiParam (请求参数) {Int} [groupID] 巡检点分组
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateCheckPoint
     */
    @Permission(permissionName = "mdmbase:UpdateCheckPoint")
    @LogParam(moduleName = "巡检管理", operationName = "更新巡检点", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "UpdateCheckPoint", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object update(@Valid @RequestBody UpdateCheckPointRequest body) {
        service.update(body);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /BatchUpdateCheckPoint 批量更新巡检点
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName BatchUpdateCheckPoint
     * @apiDescription 批量更新巡检点(为空的字段不会更新)
     * @apiParam (请求参数) {Int[]} [idList] 巡检点id集合
     * @apiParam (请求参数) {Boolean} [enable] 是否启用(当此参数不为空时，必须提供idList)
     * @apiParam (请求参数) {Int} [groupID] 巡检点分组
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateCheckPoint
     */
    @Permission(permissionName = "mdmbase:UpdateCheckPoint")
    @LogParam(moduleName = "巡检管理", operationName = "更新巡检点", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "BatchUpdateCheckPoint", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object batchUpdate(@Valid @RequestBody BatchUpdateCheckPointRequest body) {
        service.batchUpdate(body);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteCheckPoint 删除巡检点
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName DeleteCheckPoint
     * @apiDescription 删除巡检点
     * @apiParam (请求参数) {Int[]} idList 巡检点id集合
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteCheckPoint
     */
    @Permission(permissionName = "mdmbase:DeleteCheckPoint")
    @LogParam(moduleName = "巡检管理", operationName = "删除巡检任务", operationProperty = OperationProperty.DELETE)
    @PostMapping(value = "DeleteCheckPoint", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object delete(@Valid @RequestBody DeleteCheckPointRequest body) {
        service.delete(body);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryCheckPointPage 查询巡检点分页
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName QueryCheckPointPage
     * @apiDescription 查询巡检点分页
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} serviceID 所属平台id
     * @apiParam (请求参数) {String} [keyword] 模糊搜索关键字(巡检点编码/巡检点名称/巡检组)
     * @apiParam (请求参数) {Int} [projectID] 项目id
     * @apiParam (请求参数) {Boolean} [enable] 是否启用
     * @apiParam (请求参数) {Int} [groupID] 巡检组id
     * @apiParam (请求参数) {Boolean} [allowUngrouped] 是否查询未分组的点，默认为true
     * @apiParam (请求参数) {Int} currentPage 当前页(>0)
     * @apiParam (请求参数) {Int} pageSize 记录条数(1-100)
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 巡检点id
     * @apiSuccess (返回结果) {String} currentPageData.serialNumber 巡检点编码
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程id
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (返回结果) {String} currentPageData.name 巡检点名称
     * @apiSuccess (返回结果) {Boolean} currentPageData.enable 是否启用
     * @apiSuccess (返回结果) {Int} [currentPageData.groupID] 巡检组id
     * @apiSuccess (返回结果) {String} [currentPageData.groupName] 巡检组名称
     * @apiSuccess (返回结果) {String} currentPageData.address 巡检点地址
     * @apiSuccess (返回结果) {String} currentPageData.location 巡检点地址经纬度
     * @apiSuccess (返回结果) {DateTime} currentPageData.lastCheckTime 最后巡检时间
     * @apiSuccess (返回结果) {String} [currentPageData.exValue] 扩展字段
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListCheckPoint
     */
    @Permission(permissionName = "mdmbase:ListCheckPoint")
    @PostMapping(value = "/QueryCheckPointPage", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object page(@Valid @RequestBody QueryCheckPointPageRequest body) {
        return service.page(body);
    }

    /**
     * @api {POST} /QueryCheckPointList 查询巡检点列表
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName QueryCheckPointList
     * @apiDescription 查询巡检点列表
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {String} serviceID 所属平台id
     * @apiParam (请求参数) {String} [keyword] 模糊搜索关键字(巡检点编码/巡检点名称/巡检组)
     * @apiParam (请求参数) {Int} [projectID] 项目id
     * @apiParam (请求参数) {Boolean} [enable] 是否启用
     * @apiParam (请求参数) {Int} [groupID] 巡检组id
     * @apiParam (请求参数) {Boolean} [allowUngrouped] 是否查询未分组的点，默认为true
     * @apiSuccess (返回结果) {Object[]} data 数据集
     * @apiSuccess (返回结果) {Int} data.id 巡检点id
     * @apiSuccess (返回结果) {String} data.serialNumber 巡检点编码
     * @apiSuccess (返回结果) {Int} data.projectID 工程id
     * @apiSuccess (返回结果) {String} data.projectName 工程名称
     * @apiSuccess (返回结果) {String} data.name 巡检点名称
     * @apiSuccess (返回结果) {Boolean} data.enable 是否启用
     * @apiSuccess (返回结果) {Int} [data.groupID] 巡检组id
     * @apiSuccess (返回结果) {String} [data.groupName] 巡检组名称
     * @apiSuccess (返回结果) {String} data.address 巡检点地址
     * @apiSuccess (返回结果) {String} data.location 巡检点地址经纬度
     * @apiSuccess (返回结果) {DateTime} data.lastCheckTime 最后巡检时间
     * @apiSuccess (返回结果) {String} [data.exValue] 扩展字段
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListCheckPoint
     */
    @Permission(permissionName = "mdmbase:ListCheckPoint")
    @PostMapping(value = "/QueryCheckPointList", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object list(@Valid @RequestBody QueryCheckPointListRequest body) {
        return service.list(body);
    }

    /**
     * @api {POST} /QueryCheckPoint 查询巡检点详情
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName QueryCheckPoint
     * @apiDescription 查询巡检点详情
     * @apiParam (请求参数) {Int} id 巡检点id
     * @apiSuccess (返回结果) {Int} id 巡检点id
     * @apiSuccess (返回结果) {String} serialNumber 巡检点编码
     * @apiSuccess (返回结果) {Int} projectID 工程id
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {String} name 巡检点名称
     * @apiSuccess (返回结果) {Boolean} enable 是否启用
     * @apiSuccess (返回结果) {Int} [groupID] 巡检组id
     * @apiSuccess (返回结果) {String} [groupName] 巡检组名称
     * @apiSuccess (返回结果) {String} address 巡检点地址
     * @apiSuccess (返回结果) {String} location 巡检点地址经纬度
     * @apiSuccess (返回结果) {DateTime} lastCheckTime 最后巡检时间
     * @apiSuccess (返回结果) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} createUserName 创建人
     * @apiSuccess (返回结果) {Int} createUserID 创建人id
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {String} updateUserName 更新人
     * @apiSuccess (返回结果) {Int} updateUserID 更新人id
     * @apiSuccess (返回结果) {DateTime} updateTime 更新时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeCheckPoint
     */
    @Permission(permissionName = "mdmbase:DescribeCheckPoint")
    @PostMapping(value = "/QueryCheckPoint", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object single(@Valid @RequestBody QueryCheckPointRequest body) {
        return service.single(body);
    }

    /**
     * @api {POST} /AddCheckPointGroup 新增巡检组
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName AddCheckPointGroup
     * @apiDescription 新增巡检组
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} serviceID 平台id
     * @apiParam (请求参数) {String} name 巡检组名称(10)
     * @apiParam (请求参数) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {Int} data 巡检组id
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateCheckPointGroup
     */
    @Permission(permissionName = "mdmbase:UpdateCheckPointGroup")
    @LogParam(moduleName = "巡检管理", operationName = "新增巡检组", operationProperty = OperationProperty.ADD)
    @PostMapping(value = "AddCheckPointGroup", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object addGroup(@Valid @RequestBody AddCheckPointGroupRequest body) {
        return service.saveGroup(body);
    }

    /**
     * @api {POST} /UpdateCheckPointGroup 更新巡检组
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName UpdateCheckPointGroup
     * @apiDescription 更新巡检组
     * @apiParam (请求参数) {Int} id 巡检组id
     * @apiParam (请求参数) {String} [name] 巡检组名称(10)
     * @apiParam (请求参数) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateCheckPointGroup
     */
    @Permission(permissionName = "mdmbase:UpdateCheckPointGroup")
    @LogParam(moduleName = "巡检管理", operationName = "更新巡检组", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "UpdateCheckPointGroup", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object updateGroup(@Valid @RequestBody UpdateCheckPointGroupRequest body) {
        service.updateGroup(body);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteCheckPointGroup 删除巡检组
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName DeleteCheckPointGroup
     * @apiDescription 删除巡检组
     * @apiParam (请求参数) {Int[]} idList 巡检组id集合
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteCheckPointGroup
     */
    @Permission(permissionName = "mdmbase:DeleteCheckPointGroup")
    @LogParam(moduleName = "巡检管理", operationName = "删除巡检组", operationProperty = OperationProperty.DELETE)
    @PostMapping(value = "DeleteCheckPointGroup", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object deleteGroup(@Valid @RequestBody DeleteCheckPointGroupRequest body) {
        service.deleteGroup(body);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryCheckPointGroupList 查询巡检组列表
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检点模块
     * @apiName QueryCheckPointGroupList
     * @apiDescription 查询巡检组列表
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} serviceID 平台id
     * @apiParam (请求参数) {String} [keyword] 模糊搜索关键字(巡检组名称)
     * @apiSuccess (返回结果) {Object[]} data 结果集合
     * @apiSuccess (返回结果) {Int} data.id 巡检点id
     * @apiSuccess (返回结果) {Int} data.projectID 工程id
     * @apiSuccess (返回结果) {String} data.name 巡检组名称
     * @apiSuccess (返回结果) {String} [data.exValue] 扩展字段
     * @apiSuccess (返回结果) {Int} [data.pointCount] 下属巡检点数量
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListCheckPointGroup
     */
    @Permission(permissionName = "mdmbase:ListCheckPointGroup")
    @PostMapping(value = "/QueryCheckPointGroupList", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object listGroup(@Valid @RequestBody QueryCheckPointGroupListRequest body) {
        return service.listGroup(body);
    }
}