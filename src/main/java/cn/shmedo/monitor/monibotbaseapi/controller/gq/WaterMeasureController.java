package cn.shmedo.monitor.monibotbaseapi.controller.gq;

import cn.shmedo.iot.entity.api.ResultWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 量测水管理
 *
 * @author Chengfs on 2023/12/12
 */
@RestController
@RequiredArgsConstructor
public class WaterMeasureController {

    /**
     * @api {POST} /QueryWaterMeasurePointPage 分页查询量水点
     * @apiVersion 1.0.0
     * @apiGroup 灌区量测水管理
     * @apiName QueryWaterMeasurePointPage
     * @apiDescription 分页查询量水点
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [keyword] 模糊搜索关键字 (监测点名称)
     * @apiParam (请求参数) {Int} currentPage 当前页 (>=1)
     * @apiParam (请求参数) {Int} pageSize 页大小 (1-100)
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.companyID 公司ID
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (返回结果) {Int} currentPageData.projectType 工程类型
     * @apiSuccess (返回结果) {String} currentPageData.projectTypeName 工程类型名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点id
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} currentPageData.sensorID 监测传感器ID
     * @apiSuccess (返回结果) {String} currentPageData.sensorKind 传感器类型1-自动化传感器 3-人工传感器
     * @apiSuccess (返回结果) {Int} [currentPageData.waterMeasureType] 量水类型 (1特设量水设施量水 2仪器仪表量水 3水工建筑量水 4标准断面量水 5流速仪量水)
     * @apiSuccess (返回结果) {Int} [currentPageData.waterMeasureWay] 量水方式 </br>
     * 11三角堰 12矩形堰 13梯形堰 14巴歇尔槽 15无喉道段量水槽</br>
     * 21浮子式水位计 22超声波水位计 23压力式水位计 24水表 25电磁流量计 26超声波流量计 27其他量水仪表</br>
     * 31水闸 32渡槽 33跌水 34放水涵 35其他</br>41U形断面 42梯形断面 43矩形断面</br>51流速仪
     * @apiSuccess (返回结果) {Int} [currentPageData.calculateType] 计算方法 (1水工建筑法 2水位流量关系法 3流速面积法 4量水堰槽法)
     * @apiSuccess (返回结果) {Int[]} [currentPageData.monitorElements] 监测要素集 (2水位 3流速 14流量)
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListWaterMeasurePoint
     */
//    @Permission(permissionName = "mdmbase:ListWaterMeasurePoint")
    @PostMapping(value = "/QueryWaterMeasurePointPage", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object measurePointPage(@Validated @RequestBody Void request) {

        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryWaterMeasurePoint 查询量水点详情
     * @apiVersion 1.0.0
     * @apiGroup 灌区量测水管理
     * @apiName QueryWaterMeasurePoint
     * @apiDescription 查询量水点详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} sensorID 监测传感器ID
     * @apiSuccess (返回结果) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {Int} projectType 工程类型
     * @apiSuccess (返回结果) {String} projectTypeName 工程类型名称
     * @apiSuccess (返回结果) {Int} monitorType 监测类型
     * @apiSuccess (返回结果) {String} monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Int} monitorPointID 监测点id
     * @apiSuccess (返回结果) {String} monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} monitorItemID 监测项id
     * @apiSuccess (返回结果) {String} monitorItemName 监测项名称
     * @apiSuccess (返回结果) {String} gpsLocation 监测点位置(经纬度)
     * @apiSuccess (返回结果) {Int} sensorID 传感器id
     * @apiSuccess (返回结果) {Int} sensorKind 传感器类型1-自动化传感器 3-人工传感器
     * @apiSuccess (返回结果) {Int} waterMeasureType 量水类型 (1特设量水设施量水 2仪器仪表量水 3水工建筑量水 4标准断面量水 5流速仪量水)
     * @apiSuccess (返回结果) {Int} waterMeasureWay 量水方式 </br>
     * 11三角堰 12矩形堰 13梯形堰 14巴歇尔槽 15无喉道段量水槽</br>
     * 21浮子式水位计 22超声波水位计 23压力式水位计 24水表 25电磁流量计 26超声波流量计 27其他量水仪表</br>
     * 31水闸 32渡槽 33跌水 34放水涵 35其他</br>41U形断面 42梯形断面 43矩形断面</br>51流速仪
     * @apiSuccess (返回结果) {Int} calculateType 计算方法 (1水工建筑法 2水位流量关系法 3流速面积法 4量水堰槽法)
     * @apiSuccess (返回结果) {Int[]} monitorElements 监测要素集 (2水位 3流速 14流量)
     * @apiSuccess (返回结果) {Int} sensorID 监测传感器id
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeWaterMeasurePoint
     */
//    @Permission(permissionName = "mdmbase:DescribeWaterMeasurePoint")
    @PostMapping(value = "/QueryWaterMeasurePoint", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object measurePointSingle(@Validated @RequestBody Void request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateWaterMeasurePoint 更新量水点
     * @apiVersion 1.0.0
     * @apiGroup 灌区量测水管理
     * @apiName UpdateWaterMeasurePoint
     * @apiDescription 更新量水点
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} sensorID 监测传感器id
     * @apiParam (请求参数) {String} [gpsLocation] 监测点位置(经纬度)
     * @apiParam (请求参数) {Int} [waterMeasureType] 量水类型(sensorKind=1时可为空)</br>1特设量水设施量水 2仪器仪表量水 3水工建筑量水 4标准断面量水 5流速仪量水
     * @apiParam (请求参数) {Int} [waterMeasureWay] 量水方式(sensorKind=1时可为空) </br>
     * 11三角堰 12矩形堰 13梯形堰 14巴歇尔槽 15无喉道段量水槽</br>
     * 21浮子式水位计 22超声波水位计 23压力式水位计 24水表 25电磁流量计 26超声波流量计 27其他量水仪表</br>
     * 31水闸 32渡槽 33跌水 34放水涵 35其他</br>41U形断面 42梯形断面 43矩形断面</br>51流速仪
     * @apiParam (请求参数) {Int} [calculateType] 计算方法(sensorKind=1时可为空)</br>1水工建筑法 2水位流量关系法 3流速面积法 4量水堰槽法
     * @apiParam (请求参数) {Int[]} [monitorElements] 监测要素集(sensorKind=1时可为空)</br>2水位 3流速 14流量
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateWaterMeasurePoint
     */
//    @Permission(permissionName = "mdmbase:UpdateWaterMeasurePoint")
    @PostMapping(value = "/UpdateWaterMeasurePoint", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object updateMeasurePoint(@Validated @RequestBody Void request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddWaterMeasurePoint 新增量水点
     * @apiVersion 1.0.0
     * @apiGroup 灌区量测水管理
     * @apiName AddWaterMeasurePoint
     * @apiDescription 新增量水点
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {String} monitorPointName 监测点名称
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {String} gpsLocation 监测点位置(经纬度)
     * @apiParam (请求参数) {Int} monitorItemID 监测项id
     * @apiParam (请求参数) {Int} waterMeasureType 量水类型 (1特设量水设施量水 2仪器仪表量水 3水工建筑量水 4标准断面量水 5流速仪量水)
     * @apiParam (请求参数) {Int} waterMeasureWay 量水方式 </br>
     * 11三角堰 12矩形堰 13梯形堰 14巴歇尔槽 15无喉道段量水槽</br>
     * 21浮子式水位计 22超声波水位计 23压力式水位计 24水表 25电磁流量计 26超声波流量计 27其他量水仪表</br>
     * 31水闸 32渡槽 33跌水 34放水涵 35其他</br>41U形断面 42梯形断面 43矩形断面</br>51流速仪
     * @apiParam (请求参数) {Int} calculateType 计算方法 (1水工建筑法 2水位流量关系法 3流速面积法 4量水堰槽法)
     * @apiParam (请求参数) {Int[]} monitorElements 监测要素集 (2水位 3流速 14流量)
     * @apiParam (请求参数) {Int} sensorID 监测传感器id
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddWaterMeasurePoint
     */
//    @Permission(permissionName = "mdmbase:AddWaterMeasurePoint")
    @PostMapping(value = "/AddWaterMeasurePoint", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object addMeasurePoint(@Validated @RequestBody Void request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /ListWaterMeasureSensor 量水点人工传感器列表
     * @apiVersion 1.0.0
     * @apiGroup 灌区量测水管理
     * @apiName ListWaterMeasureSensor
     * @apiDescription 量水点人工传感器列表
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiSuccess (返回结果) {Object[]} data 传感器列表
     * @apiSuccess (返回结果) {Int} data.id 传感器id
     * @apiSuccess (返回结果) {String} data.alias 传感器别名
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeWaterMeasurePoint
     */
//    @Permission(permissionName = "mdmbase:DescribeWaterMeasurePoint")
    @PostMapping(value = "/ListWaterMeasureSensor", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object listWaterMeasureSensor(@Validated @RequestBody Void request) {
        return ResultWrapper.successWithNothing();
    }
}