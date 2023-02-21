package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
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
     * @apiGroup 工程项目管理模块
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
     * @api {post} /QueryProjectPageList 分页查询工程项目列表
     * @apiDescription 分页查询工程项目列表
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理
     * @apiName QueryProjectPageList
     * @apiParam {String} [projectName] 项目名称,支持模糊查询
     * @apiParam {String} [directlyManageUnit] 直管单位,支持模糊查询
     * @apiParam {String} [companyName] 企业名称,支持模糊查询
     * @apiParam {Int[]} [projectTypeList] 项目类型列表
     * @apiParam {Int} [status] 项目状态，null:全选，1:启用，0:停用
     * @apiParam {Int[]} [platformTypeList] 平台类型列表
     * @apiParam {DateTime} [verifyDate] 有效期
     * @apiParam {DateTime} [beginCreateTime] 创建时间-开始
     * @apiParam {DateTime} [endCreatTime] 创建时间-结束
     * @apiParam {Object[]} [propertyQueryEntity] 属性查询实体
     * @apiParam {Int} propertyQueryEntity.propertyID 属性ID
     * @apiParam {String} [propertyQueryEntity.value] 属性值，仅字符串类型支持模糊查询
     * @apiParam {Int} pageSize 页大小
     * @apiParam {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} dataList 项目信息列表
     * @apiSuccess (返回结果) {Int} dataList.projectID 项目id
     * @apiSuccess (返回结果) {Int} dataList.companyID 公司id
     * @apiSuccess (返回结果) {String} dataList.companyName 所属公司名称
     * @apiSuccess (返回结果) {String} dataList.projectName 项目名称
     * @apiSuccess (返回结果) {String} dataList.shortName 项目简称
     * @apiSuccess (返回结果) {Int} dataList.projectType 项目类型
     * @apiSuccess (返回结果) {Int} dataList.projectTypeName 项目类型名称
     * @apiSuccess (返回结果) {Int} dataList.PlatformType 平台类型
     * @apiSuccess (返回结果) {Int} dataList.PlatformTypeName 平台名称
     * @apiSuccess (返回结果) {String} dataList.directlyManageUnit 直管单位
     * @apiSuccess (返回结果) {DateTime} dataList.ExpiryDate 项目有效期
     * @apiSuccess (返回结果) {Bool} dataList.isValid 是否有效
     * @apiSuccess (返回结果) {String} dataList.projectAddress 项目地址
     * @apiSuccess (返回结果) {Double} dataList.latitude 项目经度
     * @apiSuccess (返回结果) {Double} dataList.longitude 项目纬度
     * @apiSuccess (返回结果) {String} [dataList.imagePath] 项目图片地址
     * @apiSuccess (返回结果) {String} [dataList.desc] 项目简介
     * @apiSuccess (返回结果) {DateTime} dataList.createTime 创建时间
     * @apiSuccess (返回结果) {Int} dataList.createUserID 创建用户ID
     * @apiSuccess (返回结果) {DateTime} dataList.updateTime 修改时间
     * @apiSuccess (返回结果) {Int} dataList.updateUserID 修改用户ID
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    //查询-添加条件还没有设计
    @RequestMapping(value = "/QueryProjectPageList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryProjectList(String name){
        return null;
    }


    /**
     * @api {post} /QueryProjectInfo 查询工程项目详情
     * @apiDescription 查询工程项目详情
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理
     * @apiName QueryProjectInfo
     * @apiParam {Int} projectID 项目ID
     * @apiSuccess (返回结果) {Int} projectID 项目id
     * @apiSuccess (返回结果) {Int} companyID 公司id
     * @apiSuccess (返回结果) {String} companyName 所属公司名称
     * @apiSuccess (返回结果) {String} projectName 项目名称
     * @apiSuccess (返回结果) {String} shortName 项目简称
     * @apiSuccess (返回结果) {Int} projectType 项目类型
     * @apiSuccess (返回结果) {Int} projectTypeName 项目类型名称
     * @apiSuccess (返回结果) {Int} platformType 平台类型
     * @apiSuccess (返回结果) {Int} platformTypeName 平台名称
     * @apiSuccess (返回结果) {String} directlyManageUnit 直管单位
     * @apiSuccess (返回结果) {DateTime} expiryDate 项目有效期
     * @apiSuccess (返回结果) {Bool} isValid 是否有效
     * @apiSuccess (返回结果) {String} projectAddress 项目地址
     * @apiSuccess (返回结果) {Double} latitude 项目经度
     * @apiSuccess (返回结果) {Double} longitude 项目纬度
     * @apiSuccess (返回结果) {String} [imagePath] 项目图片地址
     * @apiSuccess (返回结果) {String} [desc] 项目简介
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {Int} createUserID 创建用户ID
     * @apiSuccess (返回结果) {DateTime} updateTime 修改时间
     * @apiSuccess (返回结果) {Int} updateUserID 修改用户ID
     * @apiSuccess (返回结果) {Object[]} propertyList 项目基础信息列表
     * @apiSuccess (返回结果) {Int} propertyList.propertyID 项目属性ID
     * @apiSuccess (返回结果) {Int} propertyList.propertyType 属性类型:1.数值,2.字符串,3.枚举,4.日期时间
     * @apiSuccess (返回结果) {String} propertyList.className 结构名称
     * @apiSuccess (返回结果) {String} propertyList.name 属性名称
     * @apiSuccess (返回结果) {Bool} propertyList.required 是否必填
     * @apiSuccess (返回结果) {Bool} propertyList.multiSelect 是否多选
     * @apiSuccess (返回结果) {Int} propertyList.createType 创建类型 0-预定义 1-自定义
     * @apiSuccess (返回结果) {String} [propertyList.enumField] 枚举字段
     * @apiSuccess (返回结果) {String} [propertyList.unit] 属性单位
     * @apiSuccess (返回结果) {String} [propertyList.value] 属性值
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryProjectInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object queryProjectInfo(){
        return null;
    }

    /**
     * @api {post} /UpdateProject 修改工程项目
     * @apiDescription 修改工程项目
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理
     * @apiName UpdateProject
     * @apiParam {Int} projectID 项目ID
     * @apiParam {String} projectName 项目名称
     * @apiParam {String} shortName 项目简称
     * @apiParam {String} [directlyManageUnit] 直管单位
     * @apiParam {DateTime} expiryDate 有效期
     * @apiParam {Int} status 项目状态,1启用，0停用
     * @apiParam {String} projectAddress 项目地址
     * @apiParam {Object[]} tagData 标签信息
     * @apiParam {String} tagKey 标签键
     * @apiParam {String} tagValue 标签值
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/UpdateProjectData", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object updateProjectData(Object a){
        return null;
    }

    /**
     * @api {POST} /TransferProject 转移项目
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
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
     * @apiGroup 工程项目管理模块
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
     * @api {POST} /QueryModelList 查询模板
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
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

    /**
     * @api {POST} /UpdateProperty 设置属性
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName UpdateProperty
     * @apiDescription 设置属性
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Json[]} propertyList 属性列表
     * @apiParam (请求体) {Int} propertyList.propertyID 属性ID
     * @apiParam (请求体) {String} propertyList.value 属性值
     * @apiSuccess (响应结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    @RequestMapping(value = "UpdateProperty", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateProperty() {
        return null;
    }
}
