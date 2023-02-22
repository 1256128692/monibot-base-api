package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {
    /**
     * @api {POST} /AddModel 新增模板
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName AddModel
     * @apiDescription 新增模板
     * @apiParam (请求体) {String} modelName 模型名称
     * @apiParam (请求体) {Int} projectType 项目类型
     * @apiParam (请求体) {String} [desc] 模板描述
     * @apiParam (请求体) {Object[]} [modelPropertyList] 自定义属性列表
     * @apiParam (请求体) {String} modelPropertyList.name 属性名称
     * @apiParam (请求体) {Int} modelPropertyList.type 属性类型：数值，字符串，枚举，日期
     * @apiParam (请求体) {String} [modelPropertyList.unit] 自定义属性单位
     * @apiParam (请求体) {Boolean} modelPropertyList.required 自定义属性是否必填
     * @apiParam (请求体) {String} [modelPropertyList.enumField] 枚举字段，json数组
     * @apiParam (请求体) {Boolean} [modelPropertyList.multiSelect] 可否多选,限定枚举
     * @apiParam (请求体) {Int} [modelPropertyList.createType] 创建类型
     * @apiParam (请求体) {Int} [modelPropertyList.className] 类名称
     * @apiParam (请求体) {Int} [modelPropertyList.displayOrder] 展示顺序
     * @apiSuccess (返回结果) {Int} modelID  模板ID
     * @apiSampleRequest off
     * @apiPermission 项目权限:
     */
    @RequestMapping(value = "AddModel", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addModel() {
        return null;
    }

    /**
     * @api {POST} /QueryModelList 查询模板信息
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName QueryModelList
     * @apiDescription 查询模板信息
     * @apiParam (请求体) {Int} projectType 项目类型
     * @apiParam (请求体) {Int} [createType] 创建类型
     * @apiSuccess (返回结果) {Object[]} modelList  模板列表
     * @apiSuccess (返回结果) {Int} modelList.modelID  模板ID
     * @apiSuccess (返回结果) {String} modelList.name  模板名称
     * @apiSuccess (返回结果) {Int} modelList.createType  模板创建类型
     * @apiSuccess (返回结果) {Object[]} modelList.propertyList  模板的属性列表
     * @apiSuccess (返回结果) {Int} modelList.propertyList.propertyID  属性ID
     * @apiSuccess (返回结果) {String} modelList.propertyList.name  模板的属性名称
     * @apiSuccess (返回结果) {Int} modelList.propertyList.type  属性类型：1数值，2字符串，3枚举，4日期
     * @apiSuccess (返回结果) {String} [modelList.propertyList.unit] 属性单位
     * @apiSuccess (返回结果) {Boolean} modelList.propertyList.required 属性是否必填
     * @apiSuccess (返回结果) {String} [modelList.propertyList.enumField] 枚举字段，json数组
     * @apiSuccess (返回结果) {Boolean} [modelList.propertyList.multiSelect] 可否多选,限定枚举
     * @apiSuccess (返回结果) {Int} modelList.propertyList.createType 创建类型
     * @apiSuccess (返回结果) {String} [modelList.propertyList.className] 工程名称
     * @apiSuccess (返回结果) {Int} [modelList.propertyList.displayOrder] 展示顺序
     * @apiSuccess (返回结果) {String} [modelList.propertyList.exValue] 拓展信息
     * @apiSampleRequest off
     * @apiPermission 项目权限:
     */
    @RequestMapping(value = "QueryModelList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryModelList() {
        return null;
    }

    /**
     * @api {POST} /UpdateProperty 设置项目属性
     * @apiVersion 1.0.0
     * @apiGroup 项目属性管理模块
     * @apiName UpdateProperty
     * @apiDescription 设置项目属性
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Object[]} propertyList 属性列表
     * @apiParam (请求体) {Int} propertyList.propertyID 属性ID
     * @apiParam (请求体) {String} propertyList.value 属性值
     * @apiSuccess (返回结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission 项目权限:
     */
    @RequestMapping(value = "UpdateProperty", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateProperty() {
        return null;
    }
}
