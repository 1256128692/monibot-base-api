package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
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
 * @create: 2023-03-20 16:28
 **/
@RestController
@AllArgsConstructor
public class MonitorTypeController {
    /**
     * @api {POST} /AddMonitorType 新增监测类型
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName AddMonitorType
     * @apiDescription 新增监测类型
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {String} typeName 监测类型名称
     * @apiParam (请求参数) {Int} createType 定义类型
     * @apiParam (请求参数) {Boolean} multiSensor 多传感器么
     * @apiParam (请求参数) {Boolean} thirdDataSource 开启第三方数据源
     * @apiParam (请求参数) {Object[]} fieldList 属性列表
     * @apiParam (请求参数) {String} fieldList.fieldName 属性名称
     * @apiParam (请求参数) {String} fieldList.fieldToken 属性标识
     * @apiParam (请求参数) {Int} fieldList.fieldClass 属性分类  123基础属性，扩展属性，扩展配置
     * @apiParam (请求参数) {String} fieldList.fieldDataType 属性数据类型，String，Double，Long
     * @apiParam (请求参数) {Int} fieldList.fieldUnitID 属性单位ID
     * @apiParam (请求参数) {Int} fieldList.fieldCalOrder 属性计算排序
     * @apiParam (请求参数) {Int} fieldList.createType 创建类型
     * @apiParam (请求参数) {String} [fieldList.desc] 属性描述
     * @apiParam (请求参数) {String} [fieldList.exValue] 额外属性
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddMonitorType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorType(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }
    /**
     * @api {POST} / AddMonitorTypeField 新增监测类型字段
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName AddMonitorTypeField
     * @apiDescription 新增监测类型字段 （需要校验未设置模板？）
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {Object[]} fieldList 属性列表
     * @apiParam (请求参数) {String} fieldList.fieldName 属性名称
     * @apiParam (请求参数) {String} fieldList.fieldToken 属性标识
     * @apiParam (请求参数) {Int} fieldList.fieldClass 属性分类  123基础属性，扩展属性，扩展配置
     * @apiParam (请求参数) {String} fieldList.fieldDataType 属性数据类型，String，Double，Long
     * @apiParam (请求参数) {Int} fieldList.fieldUnitID 属性单位ID
     * @apiParam (请求参数) {Int} fieldList.fieldCalOrder 属性计算排序
     * @apiParam (请求参数) {Int} fieldList.createType 创建类型
     * @apiParam (请求参数) {String} [fieldList.desc] 属性描述
     * @apiParam (请求参数) {String} [fieldList.exValue] 额外属性
     * @apiSuccess (返回结果) {String} none 无
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddMonitorTypeField", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorTypeField(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }
    /**
     * @api {POST} / DeleteMonitorTypeFieldBatch 批量删除监测类型字段
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName DeleteMonitorTypeFieldBatch
     * @apiDescription 批量删除监测类型字段 （需要校验未设置模板？）
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {Int[]} templateFieldIDList  模板字段ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/DeleteMonitorTypeFieldBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorTypeFieldBatch(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }
    /**
     * @api {POST} /QueryMonitorTypePage 查询监测类型分页
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName QueryMonitorTypePage
     * @apiDescription 查询监测类型分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [createType] 定义类型，01预定义自定义
     * @apiParam (请求参数) {String} [fuzzyTypeName] 模糊监测类型名称
     * @apiParam (请求参数) {String} [fuzzyFieldName] 模糊字段名称
     * @apiParam (请求参数) {String} [fuzzyFieldDesc] 模糊字段描述
     * @apiParam (请求参数) {Int} pageSize
     * @apiParam (请求参数) {Int} currentPage
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.ID 监测类型ID
     * @apiSuccess (返回结果) {Int} currentPageData.monitorType 监测类型
     * @apiSuccess (返回结果) {String} currentPageData.typeName 监测类型名称
     * @apiSuccess (返回结果) {String} currentPageData.typeAlias 监测类型别名
     * @apiSuccess (返回结果) {Int} currentPageData.createType 定义类型
     * @apiSuccess (返回结果) {Boolean} currentPageData.multiSensor 多传感器么
     * @apiSuccess (返回结果) {Int} currentPageData.datasourceCount 数据源个数
     * @apiSuccess (返回结果) {Object[]} currentPageData.fieldList 属性列表
     * @apiSuccess (返回结果) {Int} currentPageData.fieldList.ID 属性ID
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.fieldName 属性名称
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.fieldToken 属性标识
     * @apiSuccess (返回结果) {Int} currentPageData.fieldList.fieldClass 属性分类  123基础属性，扩展属性，扩展配置
     * @apiSuccess (返回结果) {String} currentPageData.fieldList.fieldDataType 属性数据类型，String，Double，Long
     * @apiSuccess (返回结果) {Int} currentPageData.fieldList.fieldUnitID 属性单位ID
     * @apiSuccess (返回结果) {Int} [currentPageData.fieldList.parentID] 父属性ID
     * @apiSuccess (返回结果) {Int} currentPageData.fieldList.fieldCalOrder 属性计算排序
     * @apiSuccess (返回结果) {Int} currentPageData.fieldList.createType 创建类型
     * @apiSuccess (返回结果) {String} [currentPageData.fieldList.exValue] 额外属性
     * @apiSuccess (返回结果) {String} [currentPageData.fieldList.desc] 描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryMonitorTypePage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorTypePage(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteMonitorTypeBatch 批量删除监测类型
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName DeleteMonitorTypeBatch
     * @apiDescription 批量删除监测类型
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} monitorTypeList 监测类型列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/DeleteMonitorTypeBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorTypeBatch(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} / 推荐监测类型
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName QueryRecommendedMonitorType
     * @apiDescription 推荐监测类型, 类似iot的接口
     * @apiParam (请求参数) {Int} [createType]  不填默认自定义
     * @apiSuccess (返回结果) {Int} monitorType 推荐的monitorType
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryRecommendedMonitorType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryRecommendedMonitorType(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} / QueryMonitorTypeDetail 查看监测类型详情
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName QueryMonitorTypeDetail
     * @apiDescription 查看监测类型详情
     * @apiParam (请求参数) {Int} companyID  公司ID
     * @apiParam (请求参数) {Int} monitorType  监测类型
     * @apiSuccess (返回结果) {Int} monitorType xx
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryMonitorTypeDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorTypeDetail(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} / AddTemplate  添加模板
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName AddTemplate添加模板
     * @apiDescription 为监测类型添加模板(模板 + 数据源)
     * @apiParam (请求参数) {Int} [companyID]  公司ID 预定义该项会设置为-1
     * @apiParam (请求参数) {Boolean} defaultTemplate  默认模板 对于单一物模型，单一物联网触感其的模板，是否作为默认模板使用
     * @apiParam (请求参数) {Int} monitorType  监测类型
     * @apiParam (请求参数) {String} name  模板名称
     * @apiParam (请求参数) {Int} createType  创建类型
     * @apiParam (请求参数) {Int} calType  计算方式 123 公式，脚本，外部http
     * @apiParam (请求参数) {Int} displayOrder  排序
     * @apiParam (请求参数) {String} [exValue]  拓展信息。比如：对于 大于1个的物联网传感器，大于1个的监测传感器，物联网传感器+监测传感器组合的数据源，存储计算触发模式，限定数据时间边界等。
     * @apiParam (请求参数) {Object[]} tokenList  标识列表
     * @apiParam (请求参数) {Int} tokenList.datasourceType  12 物联网，监测传感器
     * @apiParam (请求参数) {String} tokenList.token  标识
     * @apiParam (请求参数) {Object[]} [formulaList]  公式列表
     * @apiParam (请求参数) {Int} formulaList.fieldID  监测类型ID
     * @apiParam (请求参数) {String} formulaList.formula  公式字符串
     * @apiSuccess (返回结果) {String} none 无
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddTemplate", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addTemplate(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }
    /**
     * @api {POST} / DeleteTemplateBatch 批量删除模板
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName DeleteTemplateBatch
     * @apiDescription 批量删除模板，数据源及公式一并删除，会进行校验
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int[]} templateIDList  模板ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/DeleteTemplateBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteTemplateBatch(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }
    /**
     * @api {POST} / SetFormula 设置计算公式
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName SetFormula
     * @apiDescription 设置计算公式, 覆盖处理
     * @apiParam (请求参数) {Int} companyID  公司ID 预定义该项会设置为-1
     * @apiParam (请求参数) {Int} monitorType  监测类型
     * @apiParam (请求参数) {Int} templateID  模板ID
     * @apiParam (请求参数) {Object[]} formulaList  公式列表
     * @apiParam (请求参数) {Int} formulaList.fieldID  监测类型ID
     * @apiParam (请求参数) {String} formulaList.formula  公式字符串
     * @apiSuccess (返回结果) {String} none 无
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/SetFormula", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object setFormula(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }
}
