package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.*;
import cn.shmedo.monitor.monibotbaseapi.service.IOtherDeviceService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-18 13:42
 **/
@RestController
@AllArgsConstructor
public class OtherDeviceController {
    private final IOtherDeviceService otherDeviceService;

    /**
     * @api {post} /AddOtherDeviceBatch 批量新增其他设备
     * @apiDescription 批量新增其他设备
     * @apiVersion 1.0.0
     * @apiGroup 其他设备模块
     * @apiName AddOtherDeviceBatch
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [templateID] 模板ID
     * @apiParam (请求体) {Json[]} list 列表
     * @apiParam (请求体) {String} list.name 设备名称
     * @apiParam (请求体) {String} list.token 设备编号
     * @apiParam (请求体) {String} list.model 设备型号
     * @apiParam (请求体) {String} list.vendor 设备厂商/品牌
     * @apiParam (请求体) {Int} list.projectID 项目ID
     * @apiParam (请求体) {String} [list.exValue] 扩展字段,json字符串（500）
     * @apiParam (请求体) {Json[]} [list.propertyList] 属性列表
     * @apiParam (请求体) {Int} list.propertyList.ID属性id
     * @apiParam (请求体) {String} list.propertyList.value 属性值
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddOtherDevice
     */
    @Permission(permissionName = "mdmbase:AddOtherDevice")
    @LogParam(moduleName = "其他设备模块", operationName = "批量新增其他设备", operationProperty = OperationProperty.ADD)
    @RequestMapping(value = "AddOtherDeviceBatch", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addOtherDeviceBatch(@Validated @RequestBody AddOtherDeviceBatchParam pa) {
        otherDeviceService.addOtherDeviceBatch(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {post} /UpdateOtherDevice 更新其他设备
     * @apiDescription 更新其他设备
     * @apiVersion 1.0.0
     * @apiGroup 其他设备模块
     * @apiName UpdateOtherDevice
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} ID 设备ID
     * @apiParam (请求体) {String} name 设备名称
     * @apiParam (请求体) {String} token 设备编号
     * @apiParam (请求体) {String} model 设备型号
     * @apiParam (请求体) {String} vendor 设备厂商/品牌
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [exValue] 扩展字段,json字符串（500）
     * @apiParam (请求体) {Json[]} [propertyList] 属性列表
     * @apiParam (请求体) {Int} propertyList.ID 属性id
     * @apiParam (请求体) {String} propertyList.value 属性值
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateOtherDevice
     */
    @Permission(permissionName = "mdmbase:UpdateOtherDevice")
    @LogParam(moduleName = "其他设备模块", operationName = "更新其他设备", operationProperty = OperationProperty.UPDATE)
    @RequestMapping(value = "UpdateOtherDevice", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateOtherDevice(@Validated @RequestBody UpdateOtherDeviceParam pa) {
        otherDeviceService.updateOtherDevice(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /DeleteOtherDevice 删除其他设备
     * @apiDescription 删除其他设备, 级联删除属性
     * @apiVersion 1.0.0
     * @apiGroup 其他设备模块
     * @apiName DeleteOtherDevice
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} deviceIDList 设备ID列表
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteOtherDevice
     */
    @Permission(permissionName = "mdmbase:DeleteOtherDevice")
    @LogParam(moduleName = "其他设备模块", operationName = "删除其他设备", operationProperty = OperationProperty.DELETE)
    @RequestMapping(value = "DeleteOtherDevice", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteOtherDevice(@Validated @RequestBody DeleteOtherDeviceParam pa) {
        otherDeviceService.deleteOtherDevice(pa.getDeviceIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /QueryOtherDevicePage 分页查询其他设备
     * @apiDescription 分页查询其他设备
     * @apiVersion 1.0.0
     * @apiGroup 其他设备模块
     * @apiName QueryOtherDevicePage
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [fuzzyItem] 模糊查询设备名称,编号, 设备型号,厂商/品牌
     * @apiParam (请求体) {Int} [projectID] 项目ID
     * @apiParam (请求体) {Int} [templateID] 模板ID
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Json[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.ID 设备ID
     * @apiSuccess (返回结果) {String} currentPageData.name 设备名称
     * @apiSuccess (返回结果) {String}  currentPageData.token 设备编号
     * @apiSuccess (返回结果) {String} currentPageData.model 设备型号
     * @apiSuccess (返回结果) {String} currentPageData.vendor 设备厂商/品牌
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 项目名称
     * @apiSuccess (返回结果) {Int} currentPageData.templateID 模板ID
     * @apiSuccess (返回结果) {String} currentPageData.templateName 模板名称
     * @apiSuccess (返回结果) {String} currentPageData.templateGroupName 模板分组名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeOtherDevice
     */
    @Permission(permissionName = "mdmbase:DescribeOtherDevice")
    @RequestMapping(value = "QueryOtherDevicePage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryOtherDevicePage(@Validated @RequestBody QueryOtherDevicePageParam pa) {
        return otherDeviceService.queryOtherDevicePage(pa);
    }

    /**
     * @api {post} /QueryOtherDeviceWithProperty 查询其他设备及属性
     * @apiDescription 分页查询其他设备
     * @apiVersion 1.0.0
     * @apiGroup 其他设备模块
     * @apiName QueryOtherDeviceWithProperty
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} ID 设备ID
     * @apiSuccess (返回结果) {Int} ID 设备ID
     * @apiSuccess (返回结果) {String} name 设备名称
     * @apiSuccess (返回结果) {String}  token 设备编号
     * @apiSuccess (返回结果) {String} model 设备型号
     * @apiSuccess (返回结果) {String} vendor 设备厂商/品牌
     * @apiSuccess (返回结果) {Int} projectID 项目ID
     * @apiSuccess (返回结果) {String} projectName 项目名称
     * @apiSuccess (返回结果) {String} projectLocation 项目位置的json字符串
     * @apiSuccess (返回结果) {Int} templateID 模板ID
     * @apiSuccess (返回结果) {String} templateName 模板名称
     * @apiSuccess (返回结果) {Json[]} propertyList 属性列表
     * @apiSuccess (返回结果) {Int} propertyList.ID 属性id
     * @apiSuccess (返回结果) {String} propertyList.name 属性名称
     * @apiSuccess (返回结果) {String} propertyList.unit 属性单位
     * @apiSuccess (返回结果) {String} propertyList.value 属性值
     * @apiSuccess (返回结果) {Integer} propertyList.type 属性类型
     * @apiSuccess (返回结果) {Boolean} propertyList.required 属性是否必須
     * @apiSuccess (返回结果) {String} [propertyList.enumField]  枚举的可选值
     * @apiSuccess (返回结果) {Boolean} [propertyList.multiSelect] 是否多选
     * @apiSuccess (返回结果) {Json[]} [propertyList.fileList]  当为图片或文件时候的文件列表
     * @apiSuccess (返回结果) {String} propertyList.fileList.fileName  文件名称
     * @apiSuccess (返回结果) {String} propertyList.fileList.filePath  文件token
     * @apiSuccess (返回结果) {String} propertyList.fileList.absolutePath  文件绝对路径
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeOtherDevice
     */
    @Permission(permissionName = "mdmbase:DescribeOtherDevice")
    @RequestMapping(value = "QueryOtherDeviceWithProperty", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryOtherDeviceWithProperty(@Validated @RequestBody QueryOtherDeviceWithPropertyParam pa) {
        return otherDeviceService.queryOtherDeviceWithProperty(pa);
    }
}
