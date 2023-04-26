package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WtDeviceController {

    /**
     * @api {POST} /QueryWtVideoPageList 分页查询视频设备
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryWtVideoPageList
     * @apiDescription 分页查询视频设备
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程项目ID列表
     * @apiParam (请求参数) {String} [queryCode] 关键词,支持模糊查询设备SN/工程名称/监测点名称
     * @apiParam (请求参数) {String} [videoType] 视频设备型号
     * @apiParam (请求参数) {Bool} [online] 在线状态 在线:true 离线:false
     * @apiParam (请求参数) {Int} [status] 设备状态 0.正常 1.异常
     * @apiParam (请求参数) {String} [areaCode] 行政区划编号
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {Int} [ruleID] 规则引擎ID
     * @apiParam (请求参数) {Bool} [select] 是否选中 true:选中 false:未选中
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 记录条数
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {String} currentPageData.videoSN 设备SN
     * @apiSuccess (返回结果) {String} currentPageData.videoType 设备型号
     * @apiSuccess (返回结果) {Bool} currentPageData.online 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} currentPageData.status 设备状态 0.正常 1.异常
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} currentPageData.projectShortName 工程项目简称
     * @apiSuccess (返回结果) {String} currentPageData.location 工程项目行政区划
     * @apiSuccess (返回结果) {Int} currentPageData.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} currentPageData.pointGpsLocation 监测点GPS位置
     * @apiSuccess (返回结果) {String} currentPageData.[pointImageLocation] 监测点底图位置
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseVideo
     */
    @Permission(permissionName = "mdmbase:ListBaseVideo")
    @PostMapping(value = "/QueryWtVideoPageList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtVideoPageList(@Valid @RequestBody Object param) {
        return null;
    }

    /**
     * @api {POST} /QueryWtVideoTypeList 查询视频型号列表
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryWtVideoTypeList
     * @apiDescription 查询视频型号列表
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程项目ID列表
     * @apiSuccess (返回结果) {String[]} videoTypeList 视频型号列表
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseVideo
     */
    @Permission(permissionName = "mdmbase:ListBaseVideo")
    @PostMapping(value = "/QueryWtVideoTypeList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtVideoTypeList(@Valid @RequestBody Object param) {
        return null;
    }

    /**
     * @api {POST} /QueryWtVideoInfo 查询视频基本信息需求
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryWtVideoInfo
     * @apiDescription 查询视频基本信息需求
     * @apiParam (请求参数) {Int} ProjectID 工程项目ID
     * @apiParam (请求参数) {String} videoSn 视频SN
     * @apiSuccess (返回结果) {String} videoName 视频设备名称
     * @apiSuccess (返回结果) {String} videoType 视频设备型号
     * @apiSuccess (返回结果) {Int} status 在线状态：0-不在线，1-在线
     * @apiSuccess (返回结果) {String} netAddress ip地址
     * @apiSuccess (返回结果) {String} netType 网络类型
     * @apiSuccess (返回结果) {String} signal 信号强度
     * @apiSuccess (返回结果) {DateTime} updateTime 修改时间
     * @apiSuccess (返回结果) {Int} isEncrypt 是否加密：0-不加密，1-加密
     * @apiSuccess (返回结果) {Int} riskLevel 设备风险安全等级，0-安全，大于零，有风险，风险越高，值越大
     * @apiSuccess (返回结果) {Int} offlineNotify 设备下线是否通知：0-不通知 1-通知
     * @apiSuccess (返回结果) {Int} alarmSoundMode 告警声音模式：0-短叫，1-长叫，2-静音
     * @apiSuccess (返回结果) {Int} defence 能力的设备布撤防状态：0-睡眠，8-在家，16-外出，普通IPC布撤防状态：0-撤防，1-布防
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryWtVideoInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtVideoInfo(@Valid @RequestBody Object param) {
        return null;
    }

    /**
     * @api {POST} /QueryWtDevicePageList 分页查询物联网设备
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryWtDevicePageList
     * @apiDescription 分页查询物联网设备
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程项目ID列表
     * @apiParam (请求参数) {String} [queryCode] 关键词,支持模糊查询设备SN/工程名称/监测点名称
     * @apiParam (请求参数) {Int} [productID] 产品ID(设备型号)
     * @apiParam (请求参数) {Bool} [online] 在线状态 在线:true 离线:false
     * @apiParam (请求参数) {Int} [status] 设备状态 0.正常 1.异常
     * @apiParam (请求参数) {String} [areaCode] 行政区划编号
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {Int} [ruleID] 规则引擎ID
     * @apiParam (请求参数) {Bool} [select] 是否选中 true:选中 false:未选中
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 记录条数
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {String} currentPageData.deviceSN 设备SN
     * @apiSuccess (返回结果) {String} [currentPageData.firewallVersion] 固件版本
     * @apiSuccess (返回结果) {String} currentPageData.productID 产品ID
     * @apiSuccess (返回结果) {String} currentPageData.productName 产品名称
     * @apiSuccess (返回结果) {Bool} currentPageData.online 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} currentPageData.status 设备状态 0.正常 1.异常
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} currentPageData.projectShortName 工程项目简称
     * @apiSuccess (返回结果) {String} currentPageData.location 工程项目行政区划
     * @apiSuccess (返回结果) {Object[]} currentPageData.monitorPointList 监测点信息
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointList.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointList.pointGpsLocation 监测点GPS位置
     * @apiSuccess (返回结果) {String} currentPageData.[monitorPointList.pointImageLocation] 监测点底图位置
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseDevice")
    @PostMapping(value = "/QueryWtDevicePageList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtDevicePageList(@Valid @RequestBody Object param) {
        return null;
    }
}
