package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem.*;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorItemService;
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

    private MonitorItemService monitorItemService;

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
     * @apiParam (请求参数) {Boolean} enable 是否开启
     * @apiParam (请求参数) {String} [exValue] 拓展字段(500)
     * @apiParam (请求参数) {Int} [displayOrder] 排序字段
     * @apiParam (请求参数) {Int[]} fieldIDList 监测类型字段ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorItem
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorItem(@RequestBody @Validated AddMonitorItemParam pa) {
        monitorItemService.addMonitorItem(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
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
     * @apiParam (请求参数) {Boolean} enable 是否开启
     * @apiParam (请求参数) {String} alias 别名(max = 20)
     * @apiParam (请求参数) {String} [exValue] 拓展字段(500)
     * @apiParam (请求参数) {Int} [displayOrder] 排序字段
     * @apiParam (请求参数) {Int[]}   [fieldIDList] 监测类型字段ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorItem
     */
//    @Permission(permissionName = "mdmbase:UpdateMonitorItem")
    @PostMapping(value = "/UpdateMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateMonitorItem(@RequestBody @Validated UpdateMonitorItemParam pa) {
        monitorItemService.updateMonitorItem(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddCompanyMonitorItem 保存为公司监测项目模板
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName AddCompanyMonitorItem
     * @apiDescription 保存为公司监测项目模板, 本质是进行复制
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]}  monitorItemIDList 监测项目列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddCompanyMonitorItem
     */
//    @Permission(permissionName = "mdmbase:AddCompanyBaseMonitorItem")
    @PostMapping(value = "/AddCompanyMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addCompanyMonitorItem(@RequestBody @Validated AddCompanyMonitorItemParam pa) {
        monitorItemService.addCompanyMonitorItem(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryMonitorItemPageList 查询监测项目分页
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName QueryMonitorItemPageList
     * @apiDescription 查询监测项目分页
     * @apiParam (请求参数) {Int} [projectID] 工程项目ID
     * @apiParam (请求参数) {Int} [monitorItemName] 监测项目名称, 模糊查询
     * @apiParam (请求参数) {Int} [monitorType] 监测类型
     * @apiParam (请求参数) {String} [fieldToken] 属性标识, 模糊查询
     * @apiParam (请求参数) {String} [fieldName] 属性名称, 模糊查询
     * @apiParam (请求参数) {Int} [createType] 创建类型 null:所有,0:预定义,1:自定义
     * @apiParam (请求参数) {Boolean} [companyItem] 公司监测项目模板？
     * @apiParam (请求参数) {Int} pageSize （1-100）
     * @apiParam (请求参数) {Int} currentPage（>=1）
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 监测项目ID
     *  @apiSuccess (返回结果) {Boolean} currentPageData.enable 是否开启
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} currentPageData.name 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.alias 监测项目别名
     * @apiSuccess (返回结果) {Int} currentPageData.monitorType 监测类型
     * @apiSuccess (返回结果) {String} currentPageData.typeName 监测类型名称
     * @apiSuccess (返回结果) {String} currentPageData.typeAlias 监测类型别名
     * @apiSuccess (返回结果) {Int} currentPageData.createType 创建类型
     * @apiSuccess (返回结果) {Int} [currentPageData.enable] 项目中是否可见
     * @apiSuccess (返回结果) {Object[]} currentPageData.fieldList 字段列表
     * @apiSuccess (返回结果) {Int} currentPageData.fieldList.id 字段ID
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.token 字段标识
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.name 字段名称
     * @apiSuccess (返回结果) {String} [currentPageData.fieldList.desc] 字段描述
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.unit 单位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorItem
     */
//    @Permission(permissionName = "mdmbase:ListBaseMonitorItem")
    @PostMapping(value = "/QueryMonitorItemPageList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorItemPageList(@RequestBody @Validated QueryMonitorItemPageListParam pa) {
        return monitorItemService.queryMonitorItemPageList(pa);
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
     * @apiPermission 项目权限 mdmbase:DeleteBaseMonitorItem
     */
//    @Permission(permissionName = "mdmbase:DeleteBaseMonitorItem")
    @PostMapping(value = "/DeleteMonitorItem", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorItem(@RequestBody @Validated DeleteMonitorItemParam pa) {
        monitorItemService.deleteMonitorItem(pa.getMonitorItemIDList());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryWtMonitorItemList 查询水利监测项目列表
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName QueryWtMonitorItemList
     * @apiDescription 查询水利监测项目列表, monitorClass为空时, 查该项目下已有的监测类型, monitorClass不为空时,则进入编辑页面,查全部监测类型
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {Int} [monitorClass] 监测类别(0:环境监测 1:安全监测 2:工情监测 3:防洪调度指挥监测 4:视频监测 空:all)
     * @apiSuccess (返回结果) {Object[]} data.monitorClassList 监测类别列表
     * @apiSuccess (返回结果) {Int} monitorClassList.monitorClass 监测类别ID
     * @apiSuccess (返回结果) {String} monitorClassList.monitorClassCnName 监测类别中文名称
     * @apiSuccess (返回结果) {String} monitorClassList.density 查询密度
     * @apiSuccess (返回结果) {Boolean} monitorClassList.enable 开启状态
     * @apiSuccess (返回结果) {Object[]} data.monitorClassList.monitorTypeList 监测类型列表
     * @apiSuccess (返回结果) {Int} data.monitorClassList.monitorTypeList.monitorType 监测类型
     * @apiSuccess (返回结果) {String} data.monitorClassList.monitorTypeList.typeName 监测类型名称
     * @apiSuccess (返回结果) {String} data.monitorClassList.monitorTypeList.typeAlias 监测类型别名
     * @apiSuccess (返回结果) {Object[]} data.monitorClassList.monitorTypeList.monitorItemList 监测项目列表
     * @apiSuccess (返回结果) {Int} data.monitorClassList.monitorTypeList.monitorItemList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Int} data.monitorClassList.monitorTypeList.monitorItemList.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} data.monitorClassList.monitorTypeList.monitorItemList.name 监测项目名称
     * @apiSuccess (返回结果) {String} data.monitorClassList.monitorTypeList.monitorItemList.alias 监测项目别名
     * @apiSuccess (返回结果) {String} data.monitorClassList.monitorTypeList.monitorItemList.monitorClass 监测类别,以此来区分是否为当前监测类别
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorItem
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorItem")
    @PostMapping(value = "/QueryWtMonitorItemList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtMonitorItemList(@RequestBody @Validated QueryWtMonitorItemListParam request) {
        return monitorItemService.queryWtMonitorItemList(request);
    }

    /**
     * @api {POST} /QueryMonitorItemList 查询监测项目列表
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName QueryMonitorItemList
     * @apiDescription 查询监测项目列表
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {String} [monitorItemName] 监测项目名称
     * @apiParam (请求参数) {Int} [monitorType] 监测类型
     * @apiSuccess (返回结果) {Object[]} list 监测项目列表
     * @apiSuccess (返回结果) {Int} list.itemID 监测项目ID
     * @apiSuccess (返回结果) {String} list.itemName 监测项目名称
     * @apiSuccess (返回结果) {String} list.itemAlias 监测项目别名
     * @apiSuccess (返回结果) {Boolean} list.enable 是否开启
     * @apiSuccess (返回结果) {Int} list.monitorType 监测类型
     * @apiSuccess (返回结果) {String} list.typeName 监测类型名称
     * @apiSuccess (返回结果) {String} list.typeAlias 监测类型别名
     * @apiSuccess (返回结果) {Int} list.createType 创建类型
     * @apiSuccess (返回结果) {Boolean} list.enable 项目中是否可见
     * @apiSuccess (返回结果) {Object[]} list.fieldList 监测项目属性列表
     * @apiSuccess (返回结果) {Int} list.fieldList.fieldID 字段ID
     * @apiSuccess (返回结果) {String} list.fieldList.fieldToken 字段标识
     * @apiSuccess (返回结果) {String} list.fieldList.fieldName 字段名称
     * @apiSuccess (返回结果) {String} [list.fieldList.fieldDesc] 字段描述
     * @apiSuccess (返回结果) {String} list.fieldList.engUnit 单位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorItem
     */
    //@Permission(permissionName = "mdmbase:ListBaseMonitorItem")
    @PostMapping(value = "/QueryMonitorItemList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorItemList(@RequestBody @Validated QueryMonitorItemListParam pa) {
        return monitorItemService.queryMonitorItemList(pa);
    }

    /**
     * @api {POST} /QuerySuperMonitorItemList 查询系统监测项目列表
     * @apiVersion 1.0.0
     * @apiGroup 监测项目模块
     * @apiName QuerySuperMonitorItemList
     * @apiDescription 查询系统监测项目列表
     * @apiParam (请求参数) {Int} [createType] 创建类型
     * @apiParam (请求参数) {Int} [companyID] 公司ID 预定义监测项目该项传-1
     * @apiParam (请求参数) {Int} [projectID] 项目ID, 公司监测项目模板该项传-1
     * @apiSuccess (返回结果) {Object[]} list 监测项目列表
     * @apiSuccess (返回结果) {Int} list.id 监测项目ID
     * @apiSuccess (返回结果) {String} list.name 监测项目名称
     * @apiSuccess (返回结果) {String} list.alias 监测项目别名
     * @apiSuccess (返回结果) {Boolean} list.enable 是否开启
     * @apiSuccess (返回结果) {Int} list.monitorType 监测类型
     * @apiSampleRequest off
     * @apiPermission xxx mdmbase:xxx
     */
    //@Permission(permissionName = "mdmbase:ListBaseMonitorItem")
    @PostMapping(value = "/QuerySuperMonitorItemList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object querySuperMonitorItemList(@RequestBody @Validated QuerySuperMonitorItemListParam pa) {
        return monitorItemService.querySuperMonitorItemList(pa);
    }
}
