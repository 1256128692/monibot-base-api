package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-21 10:39
 **/
@RestController
public class ProjectController {
    /**
     * @api {POST} /AddProject 新增项目
     * @apiVersion 1.0.0
     * @apiGroup 项目模块
     * @apiName AddProject
     * @apiDescription 新增项目
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} projectName 项目名称(<=50)
     * @apiParam (请求体) {String} [shortName] 项目简称(<=10)
     * @apiParam (请求体) {Int} projectType 项目类型
     * @apiParam (请求体) {String} [imgBase64Content] 图片内容,该项存在则imgSuffix不能为空
     * @apiParam (请求体) {String} [imgSuffix] 图片格式
     * @apiParam (请求体) {DateTime} expiryDate 有效日期，经度到天
     * @apiParam (请求体) {String} [directManageUnit] 直管单位(<=50)
     * @apiParam (请求体) {Int} platformType 所属平台类型
     * @apiParam (请求体) {Boolean} enable 开启状态
     * @apiParam (请求体) {Int} regionID 行政区域ID,需要为第4级
     * @apiParam (请求体) {String} projectAddress 项目地址(<=100)
     * @apiParam (请求体) {Double} latitude 项目位置经度
     * @apiParam (请求体) {Double} longitude 项目位置纬度
     * @apiParam (请求体) {String} [projectDesc] 项目描述(<=2000)
     * @apiParam (请求体) {Int[]} [tagIDList] 项目标签
     * @apiParam (请求体) {Int[]} [monitorTypeList] 检测类型列表
     * @apiParam (请求体) {Int} [modelID] 模型ID
     * @apiParam (请求体) {Jons[]} [modelValueList] 模型值列表
     * @apiParam (请求体) {Int} modelValueList.pID 属性ID
     * @apiParam (请求体) {String} modelValueList.value 属性值
     * @apiParam (请求体) {String} [modelName模型名称]
     * @apiParam (请求体) {Json[]} [modelPropertyList] 自定义属性列表
     * @apiParam (请求体) {String} modelPropertyList.name 属性名称
     * @apiParam (请求体) {Int} modelPropertyList.type 属性类型：数值，字符串，枚举，日期
     * @apiParam (请求体) {String} [modelPropertyList.unit] 自定义属性单位
     * @apiParam (请求体) {Boolean} modelPropertyList.required 自定义属性是否必填
     * @apiParam (请求体) {String} [modelPropertyList.enumField] 枚举字段，json数组
     * @apiParam (请求体) {Boolean} [modelPropertyList.multiSelect] 可否多选,限定枚举
     * @apiParam (请求体) {Int} [modelPropertyList.createType] 创建类型
     * @apiParam (请求体) {Int} [modelPropertyList.className] 类名称
     * @apiParam (请求体) {Int} [modelPropertyList.displayOrder] 展示顺序
     * @apiParam (请求体) {String} [modelPropertyList.value] 属性类型值
     * @apiSuccess (响应结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    @RequestMapping(value = "AddProject", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addProject() {
        return null;
    }

    /**
     * @api {POST} /TransferProject 转移项目
     * @apiVersion 1.0.0
     * @apiGroup 项目模块
     * @apiName AddProject
     * @apiDescription 项目转移到其他企业
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiSuccess (响应结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    @RequestMapping(value = "TransferProject", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object transferProject() {
        return null;
    }

    /**
     * @api {POST} /RaiseExpiryDate 推迟有效期
     * @apiVersion 1.0.0
     * @apiGroup 项目模块
     * @apiName RaiseExpiryDate
     * @apiDescription 推迟有效期
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Date} newRetireDate 项目ID
     * @apiSuccess (响应结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    @RequestMapping(value = "RaiseExpiryDate", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object raiseExpiryDate() {
        return null;
    }

    /**
     * @api {POST} /  QueryModelList 查询模板
     * @apiVersion 1.0.0
     * @apiGroup 项目模块
     * @apiName QueryModelList
     * @apiDescription 查询模板列表
     * @apiParam (请求体) {Int} projectType 项目类型
     * @apiParam (请求体) {Int} [createType] 创建类型
     * @apiSuccess (响应结果) {Json[]} modelList  模板列表
     * @apiSuccess (响应结果) {Int} modelList.ID  模板ID
     * @apiSuccess (响应结果) {String} modelList.name  模板名称
     * @apiSuccess (响应结果) {Int} modelList.createType  模板创建类型
     * @apiSuccess (响应结果) {Json[]} modelList.propertyList  模板的属性列表
     * @apiSuccess (响应结果) {Int} modelList.propertyList.propertyID  属性ID
     * @apiSuccess (响应结果) {String} modelList.propertyList.name  模板的属性名称
     * @apiSuccess (响应结果) {Int} modelList.propertyList.type  属性类型：数值，字符串，枚举，日期
     * @apiSuccess (响应结果) {String} [modelList.propertyList.unit] 自定义属性单位
     * @apiSuccess (响应结果) {Boolean} modelList.propertyList.required 自定义属性是否必填
     * @apiSuccess (响应结果) {String} [modelList.propertyList.enumField] 枚举字段，json数组
     * @apiSuccess (响应结果) {Boolean} [modelList.propertyList.multiSelect] 可否多选,限定枚举
     * @apiSuccess (响应结果) {Int} [modelList.propertyList.createType] 创建类型
     * @apiSuccess (响应结果) {Int} [modelList.propertyList.className] 类名称
     * @apiSuccess (响应结果) {Int} [modelList.propertyList.displayOrder] 展示顺序
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    @RequestMapping(value = "QueryModelList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryModelList() {
        return null;
    }
}
