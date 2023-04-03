package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-21 14:15
 **/
@RestController
@AllArgsConstructor
public class MonitorItemController {

    /**
     * @api {POST} /AddMonitorItem 新增监测项目
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName AddMonitorItem
     * @apiDescription 新增监测项目
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {Int} monitorType 监测类型，预定义时，该项不能为自定义的监测类型
     * @apiParam (请求参数) {String} monitorItemName 监测项目名称(20)
     * @apiParam (请求参数) {Int} createType 创建类型 0:预定义,1:自定义
     * @apiParam (请求参数) {Boolean} enable 监测项目是否开启
     * @apiParam (请求参数) {String} [exValue] 拓展字段(500)
     * @apiParam (请求参数) {Int} [displayOrder] 排序字段
     * @apiParam (请求参数) {Int[]} fieldIDList 监测类型字段ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateMonitorItem
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorItem(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateMonitorItem 修改监测项目
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName UpdateMonitorItem
     * @apiDescription 修改监测项目
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} monitorItemID 监测项目ID
     * @apiParam (请求参数) {String} alias 别名
     * @apiParam (请求参数) {Boolean} enable 是否开启
     * @apiParam (请求参数) {String} [exValue] 拓展字段(500)
     * @apiParam (请求参数) {Int} [displayOrder] 排序字段
     * @apiParam (请求参数) {Int[]}   [fieldIDList] 监测类型字段ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateMonitorItem
     */
//    @Permission(permissionName = "mdmbase:UpdateMonitorItem")
    @PostMapping(value = "/UpdateMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateMonitorItem(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddCompanyMonitorItem 保存为公司监测项目模板
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName AddCompanyMonitorItem
     * @apiDescription 保存为公司监测项目模板
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]}  monitorItemIDList 监测项目列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddCompanyMonitorItem
     */
//    @Permission(permissionName = "mdmbase:AddCompanyMonitorItem")
    @PostMapping(value = "/AddCompanyMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addCompanyMonitorItem(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryMonitorItemPageList 查询监测项目分页
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName QueryMonitorItemPageList
     * @apiDescription 查询监测项目分页
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {Int} [monitorItemName] 监测项目名称
     * @apiParam (请求参数) {Int} [monitorType] 监测类型
     * @apiParam (请求参数) {Int} [fieldToken] 属性标识
     * @apiParam (请求参数) {Int} [fieldName] 属性名称
     * @apiParam (请求参数) {Int} [createType] 创建类型 null:所有,0:预定义,1:自定义
     * @apiParam (请求参数) {Int} pageSize
     * @apiParam (请求参数) {Int} currentPage
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.ID 监测项目ID
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} currentPageData.name 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.alias 监测项目别名
     * @apiSuccess (返回结果) {Int} currentPageData.monitorType 监测类型
     * @apiSuccess (返回结果) {String} currentPageData.typeName 监测类型名称
     * @apiSuccess (返回结果) {String} currentPageData.typeAlias 监测类型别名
     * @apiSuccess (返回结果) {Int} currentPageData.createType 创建类型
     * @apiSuccess (返回结果) {Int} [currentPageData.enable] 项目中是否可见
     * @apiSuccess (返回结果) {Object[]} currentPageData.fieldList 字段列表
     * @apiSuccess (返回结果) {Int} currentPageData.fieldList.ID 字段ID
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.token 字段标识
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.name 字段名称
     * @apiSuccess (返回结果) {String} [currentPageData.fieldList.desc] 字段描述
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.unit 单位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListMonitorItem
     */
//    @Permission(permissionName = "mdmbase:ListMonitorItem")
    @PostMapping(value = "/QueryMonitorItemPageList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorItemPageList(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteMonitorItem 删除监测项目
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName DeleteMonitorItemBatch
     * @apiDescription 批量删除监测项目
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int[]} monitorItemIDList
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteMonitorItem
     */
//    @Permission(permissionName = "mdmbase:DeleteMonitorItem")
    @PostMapping(value = "/DeleteMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorItem(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }
}
