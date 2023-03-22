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
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} createType 创建类型 01预定义自定义
     * @apiParam (请求参数) {Int} monitorType 监测类型，预定义时，该项不能为自定义的监测类型
     * @apiParam (请求参数) {Int} [projectID] 项目ID,预定义该项会覆盖成-1， 自定义该项必填
     * @apiParam (请求参数) {Boolean} [enable] 监测项目对于项目是否开启, 自定义该项必填
     * @apiParam (请求参数) {String}  alias   监测项目别名
     * @apiParam (请求参数) {String}  [exValue]   拓展字段
     * @apiParam (请求参数) {Object[]}   fieldList  监测项目字段列表
     * @apiParam (请求参数) {Int}   fieldList.monitorTypeID  监测类型ID
     * @apiParam (请求参数) {Boolean}   fieldList.enable  是否开启
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorItem(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddMonitorItem4Project 为项目添加监测项目
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName AddMonitorItem4Project
     * @apiDescription 为项目添加监测项目
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Object[]}  monitorItemList 监测项目列表
     * @apiParam (请求参数) {Int}  monitorItemList.monitorItemID 监测项目ID
     * @apiParam (请求参数) {enable}  monitorItemList.enable 对于项目是否开启
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddMonitorItem4Project", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorItem4Project(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryMonitorItemPage 查询监测项目分页
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName QueryMonitorItemPage
     * @apiDescription 查询监测项目分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [createType] 创建类型 null表示都要， 01表示预定义自定义
     * @apiParam (请求参数) {Int} [monitorType] 监测类型
     * @apiParam (请求参数) {Int} [projectID] 项目ID
     * @apiParam (请求参数) {Int} [projectType] 项目类型
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiParam (请求参数) {Int} pageSize
     * @apiParam (请求参数) {Int} currentPage
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.ID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.name 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.alias 监测项目别名
     * @apiSuccess (返回结果) {Int} currentPageData.monitorType 监测类型
     * @apiSuccess (返回结果) {String} currentPageData.typeName 监测类型名称
     * @apiSuccess (返回结果) {String} currentPageData.typeAlias 监测类型别名
     * @apiSuccess (返回结果) {Int} currentPageData.creatType 创建类型
     * @apiSuccess (返回结果) {Int} [currentPageData.projectID] 项目ID
     * @apiSuccess (返回结果) {Int} [currentPageData.enable] 项目中是否可见
     * @apiSuccess (返回结果) {Object[]} currentPageData.fieldList 字段列表
     * @apiSuccess (返回结果) {Int} currentPageData.fieldList.ID 字段ID
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.name 字段名称
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.token 字段标识
     * @apiSuccess (返回结果) {String} [currentPageData.fieldList.desc] 字段描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryMonitorItemPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorItemPage(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateMonitorItem 更新监测项目
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName UpdateMonitorItem
     * @apiDescription 更新监测项目
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} monitorItemID 监测项目ID
     * @apiParam (请求参数) {Objet} [info] 监测项目
     * @apiParam (请求参数) {Int} info.alias 别名
     * @apiParam (请求参数) {Boolean}   info.enable  是否开启
     * @apiParam (请求参数) {Object[]}   [fieldList]  修改的字段列表
     * @apiParam (请求参数) {Int}   fieldList.fieldID 字段ID
     * @apiParam (请求参数) {Boolean}   fieldList.enable  是否开启
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/UpdateMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateMonitorItem(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteMonitorItemBatch 批量删除监测项目
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName DeleteMonitorItemBatch
     * @apiDescription 批量删除监测项目
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} monitorItemIDList
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/DeleteMonitorItemBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorItemBatch(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }
}
