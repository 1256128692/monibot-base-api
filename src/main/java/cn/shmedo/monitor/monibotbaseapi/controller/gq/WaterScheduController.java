package cn.shmedo.monitor.monibotbaseapi.controller.gq;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.service.GqMonitorPointService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
     * @apiSuccess (响应结果) {Int} dataList.name       用水单位名称
     * @apiSuccess (响应结果) {String} dataList.townRegion       所属行政区划
     * @apiSuccess (响应结果) {String} dataList.townRegionStr  所属行政区划中文名
     * @apiSuccess (响应结果) {String} dataList.liaison       联系人
     * @apiSuccess (响应结果) {String} dataList.phoneNumber   手机号
     * @apiSuccess (响应结果) {Object[]} dataList.monitorPointInfoList  监测点信息
     * @apiSuccess (响应结果) {Int} dataList.monitorPointInfoList.projectID 工程项目类型
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


}
