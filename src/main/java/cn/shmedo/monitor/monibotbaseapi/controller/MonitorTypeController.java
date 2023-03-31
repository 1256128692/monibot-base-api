package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.*;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorTypeService;
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
    private final MonitorTypeService monitorTypeService;
    /**
     * @api {POST} /AddPredefinedMonitorType 新增预定义监测类型
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName AddPredefinedMonitorType
     * @apiDescription 新增预定义监测类型
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {String} typeName 监测类型名称
     * @apiParam (请求参数) {Boolean} multiSensor 多传感器么
     * @apiParam (请求参数) {Boolean} apiDataSource 开启api数据源
     * @apiParam (请求参数) {Object[]} fieldList 属性列表
     * @apiParam (请求参数) {String} fieldList.fieldName 属性名称
     * @apiParam (请求参数) {String} fieldList.fieldToken 属性标识
     * @apiParam (请求参数) {Int} fieldList.fieldClass 属性分类  123基础属性，扩展属性，扩展配置
     * @apiParam (请求参数) {String} fieldList.fieldDataType 属性数据类型，String，Double，Long
     * @apiParam (请求参数) {Int} fieldList.fieldUnitID 属性单位ID
     * @apiParam (请求参数) {Int} fieldList.createType 创建类型
     * @apiParam (请求参数) {String} [fieldList.desc] 属性描述
     * @apiParam (请求参数) {String} [fieldList.exValue] 额外属性
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseMonitorType
     */
    @PostMapping(value = "/AddPredefinedMonitorType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addPredefinedMonitorType(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddCustomizedMonitorType 新增自定义监测类型
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName AddCustomizedMonitorType
     * @apiDescription 新增自定义监测类型
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int} [monitorType] 监测类型, 未设置则自动生成
     * @apiParam (请求参数) {String} typeName 监测类型名称 (max=50)
     * @apiParam (请求参数) {String} [typeAlias]  别名(max = 50 )未设置则用typeName
     * @apiParam (请求参数) {Boolean} multiSensor 多传感器么
     * @apiParam (请求参数) {Boolean} apiDataSource 开启api数据源
     * @apiParam (请求参数) {String} [exValues] 拓展数据 (max = 500)
     * @apiParam (请求参数) {Object[]} fieldList 属性列表 (max=50)
     * @apiParam (请求参数) {String} fieldList.fieldName 属性名称(max=50)
     * @apiParam (请求参数) {String} fieldList.fieldToken 属性标识(max=50)
     * @apiParam (请求参数) {Int} fieldList.fieldClass 属性分类  123基础属性，扩展属性，扩展配置
     * @apiParam (请求参数) {String} fieldList.fieldDataType 属性数据类型，String，Double，Long
     * @apiParam (请求参数) {Int} fieldList.fieldUnitID 属性单位ID
     * @apiParam (请求参数) {Int} fieldList.createType 创建类型
     * @apiParam (请求参数) {String} [fieldList.fieldDesc] 属性描述(max = 500)
     * @apiParam (请求参数) {String} [fieldList.exValues] 额外属性
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddCustomizedMonitorType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addCustomizedMonitorType(@RequestBody @Validated AddCustomizedMonitorTypeParam pa) {
        monitorTypeService.addCustomizedMonitorType(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateCustomizedMonitorType 更新自定义监测类型
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName UpdateCustomizedMonitorType
     * @apiDescription 更新自定义监测类型
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {String} typeName 监测类型名称 (max=50)
     * @apiParam (请求参数) {String} typeAlias 监测类型别名
     * @apiParam (请求参数) {Boolean} apiDataSource 开启api数据源
     * @apiParam (请求参数) {String} exValues 开启api数据源(max = 500)
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/UpdateCustomizedMonitorType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateCustomizedMonitorType(@RequestBody @Validated UpdateCustomizedMonitorTypeParam pa) {
        monitorTypeService.updateCustomizedMonitorType(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateCustomizedMonitorTypeField 更新自定义监测类型属性
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName UpdateCustomizedMonitorTypeField
     * @apiDescription 更新自定义监测类型属性
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {Object[]} fieldList 属性列表(max = 10)
     * @apiParam (请求参数) {Int} fieldList.ID 属性ID
     * @apiParam (请求参数) {String} fieldList.fieldName 属性名称(max=50)
     * @apiParam (请求参数) {String} fieldList.fieldDataType 属性数据类型，String，Double，Long
     * @apiParam (请求参数) {Int} fieldList.fieldUnitID 属性单位ID
     * @apiParam (请求参数) {String} [fieldList.desc] 属性描述
     * @apiParam (请求参数) {String} [fieldList.exValue] 额外属性
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/UpdateCustomizedMonitorTypeField", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateCustomizedMonitorTypeField(@RequestBody @Validated UpdateCustomizedMonitorTypeFieldParam pa) {
        monitorTypeService.updateCustomizedMonitorTypeField(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddMonitorTypeField 新增监测类型属性
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName AddMonitorTypeField
     * @apiDescription 新增监测类型属性 （需要校验未设置模板？）
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {Object[]} fieldList 属性列表
     * @apiParam (请求参数) {String} fieldList.fieldName 属性名称
     * @apiParam (请求参数) {String} fieldList.fieldToken 属性标识
     * @apiParam (请求参数) {Int} fieldList.fieldClass 属性分类  123基础属性，扩展属性，扩展配置
     * @apiParam (请求参数) {String} fieldList.fieldDataType 属性数据类型，String，Double，Long  还可以是DateTime Enum
     * @apiParam (请求参数) {Int} fieldList.fieldUnitID 属性单位ID
     * @apiParam (请求参数) {Int} fieldList.fieldCalOrder 属性计算排序
     * @apiParam (请求参数) {Int} fieldList.createType 创建类型
     * @apiParam (请求参数) {String} [fieldList.desc] 属性描述
     * @apiParam (请求参数) {String} [fieldList.exValue] 额外属性, 可包含默认值，是否多选，可选范围
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddMonitorTypeField", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorTypeField(@RequestBody @Validated AddMonitorTypeFieldParam pa) {
        monitorTypeService.addMonitorTypeField(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteMonitorTypeFieldBatch 批量删除监测类型属性
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName DeleteMonitorTypeFieldBatch
     * @apiDescription 批量删除监测类型属性 （需要校验未设置模板？）
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParam (请求参数) {Int[]} fieldIDList  模板属性ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/DeleteMonitorTypeFieldBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorTypeFieldBatch(@RequestBody @Validated DeleteMonitorTypeFieldBatchParam pa) {
        monitorTypeService.deleteMonitorTypeFieldBatch(pa.getFieldIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryMonitorTypePage 查询监测类型分页
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName QueryMonitorTypePage
     * @apiDescription 查询监测类型分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [createType] 定义类型，01预定义自定义, 注意排序，自定义在前
     * @apiParam (请求参数) {String} [fuzzyTypeName] 模糊监测类型名称
     * @apiParam (请求参数) {String} [fuzzyFieldName] 模糊属性名称
     * @apiParam (请求参数) {String} [fuzzyFieldToken] 模糊字段属性
     * @apiParam (请求参数) {Boolean} [allFiled] 全属性，否则fieldClass只展示12
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
     * @apiSuccess (返回结果) {String} [currentPageData.fieldList.fieldDesc] 描述
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryMonitorTypePage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorTypePage(@RequestBody @Validated QueryMonitorTypePageParam request) {
        return monitorTypeService.queryMonitorTypePage(request);
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
     * @apiPermission 系统权限 mdmbase:DeleteBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/DeleteMonitorTypeBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorTypeBatch(@RequestBody @Validated DeleteMonitorTypeBatchParam pa) {
        monitorTypeService.deleteMonitorTypeBatch(pa.getMonitorTypeList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryRecommendedMonitorType 推荐监测类型
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName QueryRecommendedMonitorType
     * @apiDescription 推荐监测类型, 类似iot的接口
     * @apiParam (请求参数) {Int} [createType]  不填默认自定义
     * @apiSuccess (返回结果) {Int} monitorType 推荐的monitorType
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryRecommendedMonitorType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryRecommendedMonitorType(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryMonitorTypeDetail 查看监测类型详情
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName QueryMonitorTypeDetail
     * @apiDescription 查看监测类型详情
     * @apiParam (请求参数) {Int} [companyID]  公司ID
     * @apiParam (请求参数) {Int} monitorType  监测类型
     * @apiSuccess (返回结果) {Int} ID 监测类型ID
     * @apiSuccess (返回结果) {Int} monitorType 监测类型
     * @apiSuccess (返回结果) {String} typeName 监测类型名称
     * @apiSuccess (返回结果) {String} typeAlias 监测类型别名
     * @apiSuccess (返回结果) {Int} createType 定义类型
     * @apiSuccess (返回结果) {Boolean} multiSensor 多传感器么
     * @apiSuccess (返回结果) {Int} datasourceCount 数据源个数
     * @apiSuccess (返回结果) {Object[]} fieldList 属性列表
     * @apiSuccess (返回结果) {Int} fieldList.ID 属性ID
     * @apiSuccess (返回结果) {String} fieldList.fieldName 属性名称
     * @apiSuccess (返回结果) {String} fieldList.fieldToken 属性标识
     * @apiSuccess (返回结果) {Int} fieldList.fieldClass 属性分类  123基础属性，扩展属性，扩展配置
     * @apiSuccess (返回结果) {String} fieldList.fieldDataType 属性数据类型，String，Double，Long
     * @apiSuccess (返回结果) {Int} fieldList.fieldUnitID 属性单位ID
     * @apiSuccess (返回结果) {Int} [fieldList.parentID] 父属性ID
     * @apiSuccess (返回结果) {Int} fieldList.fieldCalOrder 属性计算排序
     * @apiSuccess (返回结果) {Int} fieldList.createType 创建类型
     * @apiSuccess (返回结果) {String} [fieldList.exValue] 额外属性
     * @apiSuccess (返回结果) {String} [fieldList.desc] 描述
     * @apiSuccess (返回结果) {Object[]} class3FieldList 类型3属性列表，字段与fieldList一致
     * @apiSuccess (返回结果) {Object[]} templateList 模板列表
     * @apiSuccess (返回结果) {Boolean} templateList.defaultTemplate  默认模板 对于单一物模型，单一物联网触感其的模板，是否作为默认模板使用
     * @apiSuccess (返回结果) {Int} templateList.monitorType  监测类型
     * @apiSuccess (返回结果) {String} templateList.name  模板名称
     * @apiSuccess (返回结果) {Int} templateList.createType  创建类型
     * @apiSuccess (返回结果) {Int} templateList.calType  计算方式 1,2,3,-1 公式，脚本，外部http，不设置计算
     * @apiSuccess (返回结果) {Int} templateList.displayOrder  排序
     * @apiSuccess (返回结果) {String} [templateList.exValue]  拓展信息。比如：对于 大于1个的物联网传感器，大于1个的监测传感器，物联网传感器+监测传感器组合的数据源，存储计算触发模式，限定数据时间边界等。
     * @apiSuccess (返回结果) {Object[]} templateList.tokenList  标识列表
     * @apiSuccess (返回结果) {Int} templateList.tokenList.datasourceType  12 物联网，监测传感器
     * @apiSuccess (返回结果) {String} templateList.tokenList.token  标识
     * @apiSuccess (返回结果) {String} templateList.tokenList.name  名称
     * @apiSuccess (返回结果) {String} templateList.tokenList.unit  单位
     * @apiSuccess (返回结果) {String} templateList.tokenList.desc  描述
     * @apiSuccess (返回结果) {String} [templateList.script]  计算脚本，与公式二选一
     * @apiSuccess (返回结果) {Object[]} [templateList.formulaList]  公式列表
     * @apiSuccess (返回结果) {Int} templateList.formulaList.fieldID  监测类型ID
     * @apiSuccess (返回结果) {String} templateList.formulaList.formula  公式字符串
     * @apiSuccess (返回结果) {String} templateList.formulaList.displayFormula  展示用公式字符串
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryMonitorTypeDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorTypeDetail(@RequestBody @Validated QueryMonitorTypeDetailParam pa) {
        return monitorTypeService.queryMonitorTypeDetail(pa.getMonitorType());
    }

    /**
     * @api {POST} /AddTemplate  添加模板
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName AddTemplate
     * @apiDescription 为监测类型添加模板(模板 + 数据源 + 公式 / 脚本)
     * @apiParam (请求参数) {Int} [companyID]  公司ID 预定义该项会设置为-1
     * @apiParam (请求参数) {Boolean} defaultTemplate  默认模板 对于单一物模型，单一物联网触感其的模板，是否作为默认模板使用
     * @apiParam (请求参数) {Int} monitorType  监测类型
     * @apiParam (请求参数) {String} name  模板名称 (100) 无唯一性校验
     * @apiParam (请求参数) {Int} createType  创建类型
     * @apiParam (请求参数) {Int}  dataSourceComposeType  模板数据来源类型 1单一物模型单一传感器,2多个物联网传感器（同一物模型多个或者不同物模型多个）3物联网传感器+监测传感器4单个监测传感器5多个监测传感器,100API 推送500 - 人工监测数据
     * @apiParam (请求参数) {Int} [calType]  计算方式 1,2,3,-1 公式，脚本，外部http，不设置计算
     * @apiParam (请求参数) {Int} displayOrder  排序
     * @apiParam (请求参数) {String} [exValues]  拓展信息。比如：对于 大于1个的物联网传感器，大于1个的监测传感器，物联网传感器+监测传感器组合的数据源，存储计算触发模式，限定数据时间边界等。
     * @apiParam (请求参数) {Object[]} tokenList  标识列表(max =10)
     * @apiParam (请求参数) {Int} tokenList.datasourceType  12 物联网，监测传感器
     * @apiParam (请求参数) {String} tokenList.token  标识
     * @apiParam (请求参数) {String} [script]  计算脚本，与公式二选一 (max = 2000)
     * @apiParam (请求参数) {Object[]} [formulaList]  公式列表
     * @apiParam (请求参数) {Int} formulaList.fieldID  监测类型ID
     * @apiParam (请求参数) {Int} formulaList.fieldCalOrder  计算排序
     * @apiParam (请求参数) {String} formulaList.formula  公式字符串(max = 2000)
     * @apiParam (请求参数) {String} formulaList.displayFormula  公式字符串展示用(max = 2000)
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddTemplate", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addTemplate(@RequestBody @Validated AddTemplateParam pa) {
         monitorTypeService.addTemplate(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
         return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteTemplateBatch 批量删除模板
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName DeleteTemplateBatch
     * @apiDescription 批量删除模板，数据源及公式一并删除，会进行校验
     * @apiParam (请求参数) {Int} companyID
     * @apiParam (请求参数) {Int[]} templateIDList(max =10)  模板ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/DeleteTemplateBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteTemplateBatch(@RequestBody @Validated DeleteTemplateBatchParam pa) {
        monitorTypeService.deleteTemplateBatch(pa.getTemplateIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /SetFormula 设置计算公式
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName SetFormula
     * @apiDescription 设置计算公式, 覆盖处理
     * @apiParam (请求参数) {Int} companyID  公司ID
     * @apiParam (请求参数) {Int} monitorType  监测类型
     * @apiParam (请求参数) {Int} templateID  模板ID
     * @apiParam (请求参数) {Object[]} formulaList  公式列表
     * @apiParam (请求参数) {Int} formulaList.fieldID  监测类型ID
     * @apiParam (请求参数) {String} formulaList.formula  公式字符串
     * @apiParam (请求参数) {String} formulaList.displayFormula  公式字符串展示用
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/SetFormula", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object setFormula(@RequestBody @Validated SetFormulaParam pa) {
        monitorTypeService.setFormula(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryMonitorTypeFieldWithFormula 查询监测类型属性与公式
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName QueryMonitorTypeFieldWithFormula
     * @apiDescription 查询监测类型属性与公式, 只包含12类型的属性
     * @apiParam (请求参数) {Int} monitorType  监测类型
     * @apiParam (请求参数) {Int} templateID  模板ID
     * @apiSuccess (返回结果) {Object[]} list 当前页数据
     * @apiSuccess (返回结果) {Int} list.ID 属性ID
     * @apiSuccess (返回结果) {String} list.fieldName 属性名称
     * @apiSuccess (返回结果) {String} list.fieldToken 属性标识
     * @apiSuccess (返回结果) {Int} list.fieldClass 属性分类  123基础属性，扩展属性，扩展配置
     * @apiSuccess (返回结果) {String} list.fieldDataType 属性数据类型，String，Double，Long
     * @apiSuccess (返回结果) {Int} list.fieldUnitID 属性单位ID
     * @apiSuccess (返回结果) {String} list.engUnit 英文单位
     * @apiSuccess (返回结果) {String} list.chnUnit 中文单位
     * @apiSuccess (返回结果) {Int} [list.parentID] 父属性ID
     * @apiSuccess (返回结果) {Int} list.fieldCalOrder 属性计算排序
     * @apiSuccess (返回结果) {Int} list.createType 创建类型
     * @apiSuccess (返回结果) {String} [list.exValue] 额外属性
     * @apiSuccess (返回结果) {String} list.formula  公式字符串
     * @apiSuccess (返回结果) {String} list.displayFormula  公式字符串展示用
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryMonitorTypeFieldWithFormula", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorTypeFieldWithFormula(@RequestBody @Validated QueryMonitorTypeFieldWithFormulaParam pa) {
        return monitorTypeService.queryMonitorTypeFieldWithFormula(pa);
    }

    /**
     * @api {POST} /SetParam  设置参数
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName SetParam
     * @apiDescription 为公式，脚本，传感器设置参数, 根据ID覆盖处理
     * @apiParam (请求参数) {Int} companyID  公司ID
     * @apiParam (请求参数) {Int} subjectType  类型1234 公式脚本传感器模板
     * @apiParam (请求参数) {Boolean} [deleteOnly]  仅进行删除，根据ID
     * @apiParam (请求参数) {Object[]} paramList  标识列表(max = 100)
     * @apiParam (请求参数) {Int} paramList.ID  参数记录的ID
     * @apiParam (请求参数) {Int} paramList.subjectID  主体ID
     * @apiParam (请求参数) {String} paramList.dataType  数据类型 String,Double,Long
     * @apiParam (请求参数) {String} paramList.token  参数标识(max = 50)
     * @apiParam (请求参数) {String} paramList.name  参数名称(max = 100)
     * @apiParam (请求参数) {String} paramList.paValue  参数值    (max = 1000)
     * @apiParam (请求参数) {Int} paramList.paUnitID  参数单位
     * @apiParam (请求参数) {String} [paramList.paDesc] 参数描述
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/SetParam", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object setParam(@RequestBody @Validated SetParamParam pa) {
        monitorTypeService.setParam(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryParam  查询参数
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName QueryParam
     * @apiDescription 查询参数
     * @apiParam (请求参数) {Int} companyID  公司ID
     * @apiParam (请求参数) {Int} subjectType  类型1234 公式脚本传感器模板
     * @apiParam (请求参数) {String[]} subjectTokenList  主体IDList(max=100)
     * @apiSuccess (返回结果) {String[]} paramList  参数列表
     * @apiSuccess (返回结果) {Int} paramList.subjectID  主体ID
     * @apiSuccess (返回结果) {String} paramList.dataType  数据类型 String,Double,Long
     * @apiSuccess (返回结果) {Int} paramList.token  参数标识
     * @apiSuccess (返回结果) {String} paramList.name  参数名称
     * @apiSuccess (返回结果) {String} paramList.paValue  参数值
     * @apiSuccess (返回结果) {String} paramList.paUnitID  参数单位
     * @apiSuccess (返回结果) {String} [paramList.paDesc] 参数描述
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseMonitorType
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/QueryParam", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryParam(@RequestBody @Validated QueryParamParam pa) {
        return monitorTypeService.queryParam(pa);
    }

}
