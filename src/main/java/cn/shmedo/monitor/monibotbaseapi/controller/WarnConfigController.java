package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-08 10:06
 */
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WarnConfigController {
    /**
     * @api {POST} /QueryWarnBaseConfig 查询平台报警基础配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName QueryWarnBaseConfig
     * @apiDescription 查询平台报警基础配置
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} platform 平台key
     * @apiSuccess (返回结果) {Int} [id] 基础配置id,为空时表示默认
     * @apiSuccess (返回结果) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Int} platform 平台key
     * @apiSuccess (返回结果) {Int} warnTag 报警标签枚举 1.报警 2.告警 3.预警
     * @apiSuccess (返回结果) {Int} warnLevelType 报警等级类型枚举 1: 4级 2: 3级(配置阈值前可修改)
     * @apiSuccess (返回结果) {Int} warnLevelStyle 等级样式枚举 1: 红橙黄蓝 2: 1,2,3,4级 3: Ⅰ,Ⅱ,Ⅲ,Ⅳ级
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryWarnBaseConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWarnBaseConfig(@Valid @RequestBody CompanyPlatformParam param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateWarnBaseConfig 编辑平台报警基础配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName UpdateWarnBaseConfig
     * @apiDescription 编辑平台报警基础配置
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} platform 平台key
     * @apiParam (请求参数) {Int} [warnTag] 报警标签枚举 1.报警 2.告警 3.预警
     * @apiParam (请求参数) {Int} [warnLevelType] 报警等级类型枚举 1: 4级 2: 3级(配置了阈值或已产生报警后不可修改)
     * @apiParam (请求参数) {Int} [warnLevelStyle] 等级样式枚举 1: 红,橙,黄,蓝 2: 1,2,3,4级 3: Ⅰ,Ⅱ,Ⅲ,Ⅳ级
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/UpdateWarnBaseConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWarnBaseConfig(@Valid @RequestBody UpdateWarnBaseConfigParam param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryWarnNotifyConfigList 查询报警通知配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName QueryWarnNotifyConfigList
     * @apiDescription 查询报警通知配置
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} platform 平台key
     * @apiSuccess (返回结果) {Int} warnTag 报警标签枚举 1.报警 2.告警 3.预警
     * @apiSuccess (返回结果) {Int} warnLevelType 报警等级类型枚举 1: 4级 2: 3级(配置阈值前可修改)
     * @apiSuccess (返回结果) {Int} warnLevelStyle 等级样式枚举 1: 红橙黄蓝 2: 1,2,3,4级 3: Ⅰ,Ⅱ,Ⅲ,Ⅳ级
     * @apiSuccess (返回结果) {Object[]} deviceWarnList 设备报警通知配置列表
     * @apiSuccess (返回结果) {Int} deviceWarnList.id 设备报警通知配置ID
     * @apiSuccess (返回结果) {Int[]} [deviceWarnList.notifyMethod] 通知方式(多选),枚举值: 1.平台消息 2.短信
     * @apiSuccess (返回结果) {Boolean} deviceWarnList.allProject 是否是全部工程 true:是; false: 否
     * @apiSuccess (返回结果) {Object[]} [deviceWarnList.projectList] 关联工程List,关联全部工程时,此项为null
     * @apiSuccess (返回结果) {Int} deviceWarnList.projectList.projectID 工程ID
     * @apiSuccess (返回结果) {String} deviceWarnList.projectList.projectName 工程名称
     * @apiSuccess (返回结果) {String} deviceWarnList.projectList.projectShortName 工程简称
     * @apiSuccess (返回结果) {Object[]} deviceWarnList.userList 企业内用户列表
     * @apiSuccess (返回结果) {Int} deviceWarnList.userList.userID 用户ID
     * @apiSuccess (返回结果) {String} deviceWarnList.userList.userName 用户名
     * @apiSuccess (返回结果) {Object[]} deviceWarnList.externalList 外部联系人列表
     * @apiSuccess (返回结果) {Int} deviceWarnList.externalList.userID 用户ID
     * @apiSuccess (返回结果) {String} deviceWarnList.externalList.userName 用户名
     * @apiSuccess (返回结果) {Object[]} dataWarnList 数据报警通知配置列表
     * @apiSuccess (返回结果) {Int} dataWarnList.id 数据报警通知配置ID
     * @apiSuccess (返回结果) {Int[]} [dataWarnList.notifyMethod] 通知方式(多选),枚举值: 1.平台消息 2.短信
     * @apiSuccess (返回结果) {Boolean} dataWarnList.allProject 是否是全部工程 true:是; false: 否
     * @apiSuccess (返回结果) {Object[]} [dataWarnList.projectList] 关联工程List,关联全部工程时,此项为null
     * @apiSuccess (返回结果) {Int} dataWarnList.projectList.projectID 工程ID
     * @apiSuccess (返回结果) {String} dataWarnList.projectList.projectName 工程名称
     * @apiSuccess (返回结果) {String} dataWarnList.projectList.projectShortName 工程简称
     * @apiSuccess (返回结果) {Int[]} dataWarnList.warnLevel 报警等级枚举key(多选),关联全部报警值时此项为空,枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口
     * @apiSuccess (返回结果) {Object[]} dataWarnList.userList 企业内用户列表
     * @apiSuccess (返回结果) {Int} dataWarnList.userList.userID 用户ID
     * @apiSuccess (返回结果) {String} dataWarnList.userList.userName 用户名
     * @apiSuccess (返回结果) {Object[]} dataWarnList.externalList 外部联系人列表
     * @apiSuccess (返回结果) {Int} dataWarnList.externalList.userID 用户ID
     * @apiSuccess (返回结果) {String} dataWarnList.externalList.userName 用户名
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryWarnNotifyConfigList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWarnNotifyConfigList(@Valid @RequestBody CompanyPlatformParam param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddWarnNotifyConfig 新增报警通知配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName AddWarnNotifyConfig
     * @apiDescription 新增报警通知配置
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} platform 平台key
     * @apiParam (请求参数) {Boolean} [allProject] 是否是全部工程,默认false true:全部工程,false:指定工程
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程ID list,若allProject参数为true时,此项为null
     * @apiParam (请求参数) {Int} notifyType 通知配置类型 1.设备报警通知 2.数据报警通知
     * @apiParam (请求参数) {Int[]} notifyMethod 通知方式(多选),枚举值: 1.平台消息 2.短信
     * @apiParam (请求参数) {Int[]} [warnLevel] 报警等级枚举key(多选),枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口,仅noticeType==2时有该项
     * @apiParam (请求参数) {Int[]} [deptList] 选中的部门
     * @apiParam (请求参数) {Int[]} [userList] 选中的用户
     * @apiParam (请求参数) {Int[]} [roleList] 选中的角色
     * @apiParam (请求参数) {String} [exValue] 扩展信息,包括对某个用户id指定特殊电话,格式 [{"用户id":"用户联系电话"}]
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/AddWarnNotifyConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWarnNotifyConfig(@Valid @RequestBody AddWarnNotifyConfigParam param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryWarnNotifyConfigDetail 查询报警通知配置详情
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName QueryWarnNotifyConfigDetail
     * @apiDescription 查询报警通知配置详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} platform 平台key
     * @apiParam (请求参数) {Int} notifyConfigID 报警通知配置ID
     * @apiSuccess (返回结果) {Boolean} allProject 是否是全部工程, true:全部工程,false:指定工程
     * @apiSuccess (返回结果) {Int[]} projectIDList 工程ID list
     * @apiSuccess (返回结果) {Int[]} deptIDList 部门IDList
     * @apiSuccess (返回结果) {Int[]} userIDList 员工IDList
     * @apiSuccess (返回结果) {Int[]} externalIDList 外部联系人IDList
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryWarnNotifyConfigDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWarnNotifyConfigDetail(@Valid @RequestBody QueryWarnNotifyConfigDetailParam param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateWarnNotifyConfig 编辑报警通知配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName UpdateWarnNotifyConfig
     * @apiDescription 编辑报警通知配置
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} platform 平台key
     * @apiParam (请求参数) {Int} notifyConfigID 报警通知配置ID
     * @apiParam (请求参数) {Boolean} [allProject] 是否是全部工程 true:全部工程,false:指定工程
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程ID list,若allProject参数为true时,此项为null
     * @apiParam (请求参数) {Int[]} [warnLevel] 报警等级枚举key(多选),枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口,仅noticeType==2时有该项
     * @apiParam (请求参数) {Int[]} [notifyMethod] 通知方式(多选),枚举值: 1.平台消息 2.短信
     * @apiParam (请求参数) {Int[]} [deptList] 选中的部门
     * @apiParam (请求参数) {Int[]} [userList] 选中的用户
     * @apiParam (请求参数) {Int[]} [roleList] 选中的角色
     * @apiParam (请求参数) {String} [exValue] 扩展信息,包括对某个用户id指定特殊电话,格式 [{"用户id":"用户联系电话"}]
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/UpdateWarnNotifyConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWarnNotifyConfig(@Valid @RequestBody UpdateWarnNotifyConfigParam param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteWarnNotifyConfigBatch 批量删除报警通知配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName DeleteWarnNotifyConfigBatch
     * @apiDescription 批量删除报警通知配置
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} notifyConfigIDList 报警通知配置ID List
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/DeleteWarnNotifyConfigBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteWarnNotifyConfigBatch(@Valid @RequestBody DeleteWarnNotifyConfigBatchParam param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryWarnThresholdConfigList 查询报警阈值配置不分页
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName QueryWarnThresholdConfigList
     * @apiDescription 查询报警阈值配置不分页
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {Int} platform 平台key
     * @apiParam (请求参数) {Int} monitorItemID 监测类型ID
     * @apiParam (请求参数) {Boolean} [status] 设置状态 false:未设置 true:已设置
     * @apiParam (请求参数) {Int[]} [monitorPointIDList] 监测点ID List
     * @apiParam (请求参数) {Int[]} [sensorIDList] 传感器ID List
     * @apiSuccess (返回结果) {Object[]} [aliasList] 别名数据
     * @apiSuccess (返回结果) {Int} aliasList.key 报警等级枚举key,枚举值:<br>1: 红色/1级/Ⅰ级;<br>2: 橙色/2级/Ⅱ级;<br>3: 黄色/3级/Ⅲ级;<br>4: 蓝色/4级/Ⅳ级，依次往后推
     * @apiSuccess (返回结果) {String} aliasList.alias 别名名称
     * @apiSuccess (返回结果) {Int} warnTag 报警标签枚举 1.报警 2.告警 3.预警
     * @apiSuccess (返回结果) {Int} warnLevelType 报警等级类型枚举 1: 4级 2: 3级(配置阈值前可修改)
     * @apiSuccess (返回结果) {Int} warnLevelStyle 等级样式枚举 1: 红橙黄蓝 2: 1,2,3,4级 3: Ⅰ,Ⅱ,Ⅲ,Ⅳ级
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} dataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Object[]} dataList.sensorList 传感器List
     * @apiSuccess (返回结果) {Int} dataList.sensorList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} dataList.sensorList.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} dataList.sensorList.sensorAlias 传感器别名
     * @apiSuccess (返回结果) {Object[]} dataList.sensorList.fieldList 属性列表
     * @apiSuccess (返回结果) {Int} dataList.sensorList.fieldList.fieldID 监测属性ID
     * @apiSuccess (返回结果) {String} dataList.sensorList.fieldList.fieldName 监测属性名称
     * @apiSuccess (返回结果) {String} dataList.sensorList.fieldList.fieldToken 监测属性Token
     * @apiSuccess (返回结果) {Int} [dataList.sensorList.fieldList.configID] 配置ID
     * @apiSuccess (返回结果) {String} [dataList.sensorList.fieldList.warnName] 触发的报警名称
     * @apiSuccess (返回结果) {Int} [dataList.sensorList.fieldList.compareMode] 比较方式 1.在区间内 2.偏离区间 3.大于 4.大于等于 5.小于 6.小于等于
     * @apiSuccess (返回结果) {Boolean} [dataList.sensorList.fieldList.enable] 是否启用 false.未启用 true.启用
     * @apiSuccess (返回结果) {String} [dataList.sensorList.fieldList.value] 报警等级阈值配置json,格式{"1":{"upper":100,"lower":50},"2":{"upper":50,"lower":25}},其中key为报警等级枚举key;<br>如果比较方式为区间,则value里有upper和lower两个值,否则只有一个upper值
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryWarnThresholdConfigList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWarnThresholdConfigList(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateWarnThresholdConfig 编辑报警阈值配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName UpdateWarnThresholdConfig
     * @apiDescription 编辑报警阈值配置
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} sensorID 传感器ID
     * @apiParam (请求参数) {Int} fieldID 监测属性ID
     * @apiParam (请求参数) {String} [warnName] 报警名称
     * @apiParam (请求参数) {Int} [compareMode] 比较方式 1.在区间内 2.偏离区间 3.大于 4.大于等于 5.小于 6.小于等于
     * @apiParam (请求参数) {Boolean} [enable] 是否启用 false.未启用 true.启用
     * @apiParam (请求参数) {String} [value] 报警等级阈值配置json,格式{"1":{"upper":100,"lower":50},"2":{"upper":50,"lower":25}},其中key为报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口;<br>如果比较方式为区间,则value里有upper和lower两个值,否则只有一个upper值
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/UpdateWarnThresholdConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWarnThresholdConfig(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddWarnThresholdConfigBatch 批量阈值设置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName AddWarnThresholdConfigBatch
     * @apiDescription 批量阈值设置
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Boolean} [isEmptyCoverage] 是否空值覆盖.<br>若之前有配置过某个传感器属性的某一级配置(记为A),在本次批量配置中又配置到了该传感器属性的另一级配置(记为B,有A!=B),但A级配置处不为空时,如果本项为true,则会用空值覆盖掉之前A级的配置
     * @apiParam (请求参数) {Int[]} sensorIDList 传感器ID List
     * @apiParam (请求参数) {Object[]} dataList 数据列表
     * @apiParam (请求参数) {Int} dataList.fieldID 监测属性ID
     * @apiParam (请求参数) {String} dataList.warnName 报警名称
     * @apiParam (请求参数) {Int} dataList.compareMode 比较方式 1.在区间内 2.偏离区间 3.大于 4.大于等于 5.小于 6.小于等于
     * @apiParam (请求参数) {Boolean} [dataList.enable] 是否启用 false.未启用 true.启用(默认启用)
     * @apiParam (请求参数) {String} [dataList.value] 报警等级阈值配置json,格式{"1":{"upper":100,"lower":50},"2":{"upper":50,"lower":25}},其中key为报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口;<br>如果比较方式为区间,则value里有upper和lower两个值,否则只有一个upper值
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/AddWarnThresholdConfigBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWarnThresholdConfigBatch(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryThresholdBaseConfig 查询阈值全局配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName QueryThresholdBaseConfig
     * @apiDescription 查询阈值全局配置
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {Int} platform 平台
     * @apiParam (请求参数) {Int} monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Int} triggerType 触发设置类型 1.有数据满足规则,直接触发对应等级报警 2.有连续n次数据满足规则,再触发对应等级报警
     * @apiSuccess (返回结果) {Int} triggerTimes 满足规则触发报警的次数,triggerType==1时该项恒为-1
     * @apiSuccess (返回结果) {Int} warnTag 报警标签枚举 1.报警 2.告警 3.预警
     * @apiSuccess (返回结果) {Int} warnLevelType 报警等级类型枚举 1: 4级 2: 3级(配置阈值前可修改)
     * @apiSuccess (返回结果) {Int} warnLevelStyle 等级样式枚举 1: 红橙黄蓝 2: 1,2,3,4级 3: Ⅰ,Ⅱ,Ⅲ,Ⅳ级
     * @apiSuccess (返回结果) {Object[]} fieldList 属性列表
     * @apiSuccess (返回结果) {Int} fieldList.fieldID 监测属性ID
     * @apiSuccess (返回结果) {String} fieldList.fieldName 监测属性名称
     * @apiSuccess (返回结果) {String} fieldList.fieldToken 监测属性token
     * @apiSuccess (返回结果) {Object[]} [fieldList.aliasConfigList] 别名配置列表
     * @apiSuccess (返回结果) {Int} fieldList.aliasConfigList.warnLevel 报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口
     * @apiSuccess (返回结果) {String} fieldList.aliasConfigList.alias 别名
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryThresholdBaseConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryThresholdBaseConfig(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateThresholdBaseConfig 编辑阈值全局配置
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName UpdateThresholdBaseConfig
     * @apiDescription 编辑阈值全局配置
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {Int} platform 平台
     * @apiParam (请求参数) {Int} monitorItemID 监测项目ID
     * @apiParam (请求参数) {Int} [triggerType] 触发设置类型 1.有数据满足规则,直接触发对应等级报警 2.有连续n次数据满足规则,再触发对应等级报警
     * @apiParam (请求参数) {Int} [triggerTimes] 满足规则触发报警的次数,triggerType==1时该项恒为-1
     * @apiParam (请求参数) {Object[]} aliasConfigList 别名配置列表
     * @apiParam (请求参数) {Int} aliasConfigList.fieldID 监测属性ID
     * @apiParam (请求参数) {Object[]} [aliasConfigList.dataList] 数据列表
     * @apiParam (请求参数) {Int} aliasConfigList.dataList.warnLevel 报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口
     * @apiParam (请求参数) {String} aliasConfigList.dataList.alias 别名
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/UpdateThresholdBaseConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateThresholdBaseConfig(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /SaveDataWarn 写入数据报警
     * @apiVersion 1.0.0
     * @apiGroup 报警配置模块
     * @apiName SaveDataWarn
     * @apiDescription 写入数据报警 (仅限服务内部调用)
     * @apiParam (请求参数) {Int} thresholdID 阈值配置id
     * @apiParam (请求参数) {Int} warnLevel 报警等级(1-4)
     * @apiParam (请求参数) {Double} warnValue 报警数据值
     * @apiParam (请求参数) {DateTime} warnTime 报警时间(yyyy-MM-dd HH:mm:ss)
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 应用权限, 不允许用户调用 mdmbase:WriteBaseWarn
     */
    @Permission(permissionName = "mdmbase:WriteBaseWarn", allowUser = false, allowApplication = true)
    @PostMapping(value = "/SaveDataWarn", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object saveDataWarn(@Valid @RequestBody Void request) {
        //TODO 根据平台配置构建报警内容、发送通知
        return ResultWrapper.successWithNothing();
    }
}
