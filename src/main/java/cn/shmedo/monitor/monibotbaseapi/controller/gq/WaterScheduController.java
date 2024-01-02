package cn.shmedo.monitor.monibotbaseapi.controller.gq;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.service.GqMonitorPointService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class WaterScheduController {

    private GqMonitorPointService gqMonitorPointService;


    /**
     * @api {POST} /GqQueryWaterManagementUnitList 查询用水单位列表
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqQueryWaterManagementUnitList
     * @apiDescription 查询用水单位列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [queryContent] 用水单位或者联系人名称,模糊查询
     * @apiParam (请求体) {String} townRegion  区域
     * @apiParamExample 请求体示例
     * {"companyID":1,"queryContent":"1","townRegion":"sss"}
     * @apiSuccess (响应结果) {Object[]} dataList
     * @apiSuccess (响应结果) {Int} dataList.id       用水单位id
     * @apiSuccess (响应结果) {String} dataList.name       用水单位名称
     * @apiSuccess (响应结果) {String} dataList.townRegion       所属行政区划
     * @apiSuccess (响应结果) {String} dataList.townRegionStr  所属行政区划中文名
     * @apiSuccess (响应结果) {String} dataList.liaison       联系人
     * @apiSuccess (响应结果) {String} dataList.phoneNumber   手机号
     * @apiSuccess (响应结果) {Object[]} dataList.monitorPointInfoList  监测点信息
     * @apiSuccess (响应结果) {Int} dataList.monitorPointInfoList.projectID 工程项目ID
     * @apiSuccess (响应结果) {String} dataList.monitorPointInfoList.projectName 工程项目名称
     * @apiSuccess (响应结果) {Int} dataList.monitorPointInfoList.monitorPointID    监测点ID
     * @apiSuccess (响应结果) {String} dataList.monitorPointInfoList.monitorPointName 监测点名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryWaterManagementUnitList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryWaterManagementUnitList(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }

    /**
     * @api {POST} /GqQueryWaterManagementUnitPage 查询用水单位分页
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqQueryWaterManagementUnitPage
     * @apiDescription 查询用水单位分页
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [queryContent] 用水单位或者联系人名称,模糊查询
     * @apiParam (请求体) {String} townRegion  区域
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiParamExample 请求体示例
     * {"companyID":1,"queryContent":"1","townRegion":"sss","pageSize":10,"currentPage":1}
     * @apiSuccess (响应结果) {Int} totalCount 数据总量
     * @apiSuccess (响应结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} currentPageData 项目信息列表
     * @apiSuccess (响应结果) {Int} currentPageData.id       用水单位id
     * @apiSuccess (响应结果) {String} currentPageData.name       用水单位名称
     * @apiSuccess (响应结果) {String} currentPageData.townRegion       所属行政区划
     * @apiSuccess (响应结果) {String} currentPageData.townRegionStr  所属行政区划中文名
     * @apiSuccess (响应结果) {String} currentPageData.liaison       联系人
     * @apiSuccess (响应结果) {String} currentPageData.phoneNumber   手机号
     * @apiSuccess (响应结果) {Object[]} currentPageData.monitorPointInfoList  监测点信息
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointInfoList.projectID 工程项目ID
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointInfoList.projectName 工程项目名称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointInfoList.monitorPointID    监测点ID
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointInfoList.monitorPointName 监测点名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryWaterManagementUnitPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryWaterManagementUnitPage(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqQueryWaterManagementUnitDetail 查询用水单位详情
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqQueryWaterManagementUnitDetail
     * @apiDescription 查询用水单位详情
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} waterManagementUnitID 用水单位id
     * @apiParamExample 请求体示例
     * {"companyID":1,"waterManagementUnitID":"1"}
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {Int} data.id       用水单位id
     * @apiSuccess (响应结果) {String} data.name       用水单位名称
     * @apiSuccess (响应结果) {String} data.townRegion       所属行政区划
     * @apiSuccess (响应结果) {String} data.townRegionStr  所属行政区划中文名
     * @apiSuccess (响应结果) {String} data.liaison       联系人
     * @apiSuccess (响应结果) {String} data.phoneNumber   手机号
     * @apiSuccess (响应结果) {Object[]} data.monitorPointInfoList  监测点信息
     * @apiSuccess (响应结果) {Int} data.monitorPointInfoList.projectID 工程项目ID
     * @apiSuccess (响应结果) {String} data.monitorPointInfoList.projectName 工程项目名称
     * @apiSuccess (响应结果) {Int} data.monitorPointInfoList.monitorPointID    监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointInfoList.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorPointInfoList.projectType 工程项目类型ID
     * @apiSuccess (响应结果) {String} data.monitorPointInfoList.projectTypeName 工程项目类型ID
     * @apiSuccess (响应结果) {String} data.monitorPointInfoList.raiseCropName 种植物名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryWaterManagementUnitDetail", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryWaterManagementUnitDetail(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqQueryProjectWithRaiseCrops 查询田间工程以及种植作物和面积相关信息
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqQueryProjectWithRaiseCrops
     * @apiDescription 查询田间工程以及种植作物和面积相关信息
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParamExample 请求体示例
     * {"companyID":1}
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {Int} data.projectID     工程ID
     * @apiSuccess (响应结果) {String[]} data.raiseCropNameList   种植作物名称
     * @apiSuccess (响应结果) {Double} data.fieldArea       田间面积
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryProjectWithRaiseCrops", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryProjectWithRaiseCrops(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqBatchAddWaterManagementUnitList 批量新增用水单位
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqBatchAddWaterManagementUnitList
     * @apiDescription 批量新增用水单位
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Object[]} dataList 数据列表
     * @apiParam (请求体) {String} dataList.name       用水单位名称
     * @apiParam (请求体) {String} dataList.townRegion    所属行政区划
     * @apiParam (请求体) {String} dataList.liaison       联系人
     * @apiParam (请求体) {String} dataList.phoneNumber   手机号
     * @apiParam (请求体) {Object[]} [dataList.monitorPointInfoList] 绑定监测点列表
     * @apiParam (请求体) {Int} data.monitorPointInfoList.projectID 工程项目ID
     * @apiParam (请求体) {String} data.monitorPointInfoList.monitorPointID    监测点ID
     * @apiParam (请求体) {String} data.monitorPointInfoList.raiseCropName 种植物名称
     * @apiParamExample 请求体示例
     * {"companyID":1,"dataList":[
     * {"name":"名称","townRegion":"12231145","liaison":"斯蒂芬","phoneNumber":"1384654151",
     * "monitorPointInfoList":[{
     * "projectID":"1","monitorPointID":"1","raiseCropName":"水稻"
     * }]}]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqBatchAddWaterManagementUnitList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqBatchAddWaterManagementUnitList(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqUpdateWaterManagementUnit 修改用水单位
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqUpdateWaterManagementUnit
     * @apiDescription 修改用水单位, 绑定监测点列表为空时, 则不修改, 不为空时则按内容重新绑定
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} id 用水单位ID
     * @apiParam (请求体) {String} name       用水单位名称
     * @apiParam (请求体) {String} townRegion    所属行政区划
     * @apiParam (请求体) {String} liaison       联系人
     * @apiParam (请求体) {String} phoneNumber   手机号
     * @apiParam (请求体) {Object[]} [monitorPointInfoList] 绑定监测点列表
     * @apiParam (请求体) {Int} monitorPointInfoList.projectID 工程项目ID
     * @apiParam (请求体) {Int} monitorPointInfoList.monitorPointID 监测点ID
     * @apiParam (请求体) {String} monitorPointInfoList.raiseCropName 种植物名称
     * @apiParamExample 请求体示例
     * {"companyID":1,"id":"1","name":"名称","townRegion":"12231145","liaison":"斯蒂芬","phoneNumber":"1384654151",
     * "monitorPointInfoList":[{
     * "projectID":"1","monitorPointID":"1","propertyID":"11"
     * }]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqUpdateWaterManagementUnit", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqUpdateWaterManagementUnit(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqBatchDeleteWaterManagementUnit 批量删除用水单位
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqBatchDeleteWaterManagementUnit
     * @apiDescription 批量删除用水单位, 以及对应的田间地块绑定信息
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} idList 用水单位ID列表
     * @apiParamExample 请求体示例
     * {"companyID":1,"idList":[1,2,3]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqBatchDeleteWaterManagementUnit", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqBatchDeleteWaterManagementUnit(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqQueryWaterPlanList 查询用水申报列表
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqQueryWaterPlanList
     * @apiDescription 查询用水申报列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [waterManagementUnitName] 用水单位名称,模糊查询
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} townRegion    所属行政区划
     * @apiParamExample 请求体示例
     * {"companyID":1,"waterManagementUnitName":"1","begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31",
     * "townRegion":"12312314"}
     * @apiSuccess (响应结果) {Object[]} dataList
     * @apiSuccess (响应结果) {Int} dataList.id       用水申报id
     * @apiSuccess (响应结果) {Int} dataList.waterManagementUnitID   用水单位id
     * @apiSuccess (响应结果) {Int} dataList.projectID       工程ID
     * @apiSuccess (响应结果) {String} dataList.projectName       工程名称(用水地块)
     * @apiSuccess (响应结果) {String} dataList.waterManagementName       用水单位名称
     * @apiSuccess (响应结果) {Double} dataList.waterConsumption  计划用水量
     * @apiSuccess (响应结果) {String} dataList.townRegion       所属行政区划
     * @apiSuccess (响应结果) {String} dataList.townRegionStr  所属行政区划中文名
     * @apiSuccess (响应结果) {DateTime} dataList.beginTime  计划开始时间
     * @apiSuccess (响应结果) {DateTime} dataList.endTime   计划结束时间
     * @apiSuccess (响应结果) {Int} dataList.dayCount   灌溉天数
     * @apiSuccess (响应结果) {DateTime} dataList.createTime  申请时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryWaterPlanList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryWaterPlanList(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqQueryWaterPlanPage 查询用水申报分页
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqQueryWaterPlanPage
     * @apiDescription 查询用水申报分页
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [waterManagementUnitName] 用水单位名称,模糊查询
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} townRegion    所属行政区划
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiParamExample 请求体示例
     * {"companyID":1,"waterManagementUnitName":"1","begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31",
     * "townRegion":"12312314","pageSize":10,"currentPage":1}
     * @apiSuccess (响应结果) {Int} totalCount 数据总量
     * @apiSuccess (响应结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} currentPageData 项目信息列表
     * @apiSuccess (响应结果) {Int} currentPageData.id       用水申报id
     * @apiSuccess (响应结果) {Int} currentPageData.waterManagementUnitID   用水单位id
     * @apiSuccess (响应结果) {Int} currentPageData.projectID       工程ID
     * @apiSuccess (响应结果) {String} currentPageData.projectName       工程名称(用水地块)
     * @apiSuccess (响应结果) {String} currentPageData.waterManagementName       用水单位名称
     * @apiSuccess (响应结果) {Double} currentPageData.waterConsumption  计划用水量
     * @apiSuccess (响应结果) {String} currentPageData.townRegion       所属行政区划
     * @apiSuccess (响应结果) {String} currentPageData.townRegionStr  所属行政区划中文名
     * @apiSuccess (响应结果) {DateTime} currentPageData.beginTime  计划开始时间
     * @apiSuccess (响应结果) {DateTime} currentPageData.endTime   计划结束时间
     * @apiSuccess (响应结果) {Int} currentPageData.dayCount   灌溉天数
     * @apiSuccess (响应结果) {DateTime} currentPageData.createTime  申请时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryWaterPlanPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryWaterPlanPage(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqBatchAddWaterPlanList 批量新增用水申报列表
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqBatchAddWaterPlanList
     * @apiDescription 批量新增用水申报列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Object[]} dataList
     * @apiParam (请求体) {Int} dataList.waterManagementUnitID 用水单位ID
     * @apiParam (请求体) {Int} dataList.projectID 工程ID
     * @apiParam (请求体) {DateTime} dataList.begin 开始时间
     * @apiParam (请求体) {DateTime} dataList.end   结束时间
     * @apiParam (请求体) {Double} dataList.waterConsumption  计划用水量
     * @apiParamExample 请求体示例
     * {"companyID":1,"dataList":[{
     * "waterManagementUnitName":"1","begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31",
     * * "projectID":"12312314","waterConsumption":"20.11"
     * }]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqBatchAddWaterPlanList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqBatchAddWaterPlanList(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqUpdateWaterPlan 修改用水申报
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqUpdateWaterPlan
     * @apiDescription 修改用水申报
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} id 用水计划ID
     * @apiParam (请求体) {Int} waterManagementUnitID 用水单位ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Double} waterConsumption  计划用水量
     * @apiParamExample 请求体示例
     * {"companyID":1,"id":"1","waterManagementUnitName":"1","begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31",
     * * "projectID":"12312314","waterConsumption":"20.11"
     * }
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqUpdateWaterPlan", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqUpdateWaterPlan(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqBatchDeleteWaterPlan 批量删除用水申报
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqBatchDeleteWaterPlan
     * @apiDescription 修改用水申报
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} idList 用水计划ID列表
     * @apiParamExample 请求体示例
     * {"companyID":1,"idList":[1,2]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqBatchDeleteWaterPlan", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqBatchDeleteWaterPlan(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }


    /**
     * @api {POST} /GqQueryWaterPlanStatisticsInfo 查询用水申报统计信息
     * @apiVersion 1.0.0
     * @apiGroup 灌区供配水调度模块
     * @apiName GqQueryWaterPlanStatisticsInfo
     * @apiDescription 查询用水申报统计信息
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} waterManagementUnitID 用水单位ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParamExample 请求体示例
     * {"companyID":1,"waterManagementUnitID":"1","begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31"}
     * @apiSuccess (响应结果) {Object[]} dataList
     * @apiSuccess (响应结果) {Date} dataList.time       用水申报id
     * @apiSuccess (响应结果) {Double} dataList.totalWaterConsumption  总计划用水量
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryWaterPlanStatisticsInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryWaterPlanStatisticsInfo(@Validated @RequestBody Object pa) {
//        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
        return null;
    }

}
