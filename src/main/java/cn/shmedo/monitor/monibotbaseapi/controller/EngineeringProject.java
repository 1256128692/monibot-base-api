package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author cyf
 * @Date 2023/2/21 9:44
 * @PackageName:cn.shmedo.document
 * @ClassName: document
 * @Description: TODO
 * @Version 1.0
 */
@RestController
public class EngineeringProject {

    /**
     * @api {post} /QueryProjectBriefList 查询项目简略信息列表
     * @apiDescription 查询项目列表
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理
     * @apiName QueryProjectBriefList
     * @apiParam {Int} pageSize 页大小
     * @apiParam {Int} currentPage 当前页
     * @apiParam {projectDto} projectDto 项目请求体
     * @apiParam {String} projectDto.projectName 项目名称
     * @apiParam {String} projectDto.managementUnit 项目直接管理单位（直管单位）
     * @apiParam {int} projectDto.projectName 项目类型
     * @apiParam {String} projectDto.clientEnterprise 客户企业名称
     * @apiParam {int} projectDto.projectStatus 项目状态
     * @apiParam {int} projectDto.platform 项目平台
     * @apiParam {DateTime} projectDto.validityPeriod 有效期
     * @apiParam {DateTime} projectDto.beginCreatTime 创建时间-开始
     * @apiParam {DateTime} projectDto.endCreatTime 创建时间-结束
     * @apiParam {String} projectDto.riverName 所在河流名称
     * @apiParam {int} projectDto.scale 工程规模
     * @apiSuccess (返回结果) {Int} totalCount 查询条件下的数据总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} dataList 项目信息列表
     * @apiSuccess (返回结果) {Int} dataList.projectID 项目id
     * @apiSuccess (返回结果) {String} dataList.projectName 项目名称
     * @apiSuccess (返回结果) {Int} dataList.projectType 项目类型
     * @apiSuccess (返回结果) {String} dataList.managementUnit 项目直接管理单位（直管单位）
     * @apiSuccess (返回结果) {DateTime} dataList.creatTime 创建日期
     * @apiSuccess (返回结果) {Int} dataList.projectStatus 项目状态
     * @apiSuccess (返回结果) {String} dataList.platform 平台
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    //查询-添加条件还没有设计
    @RequestMapping(value = "/QueryProjectBriefList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object quickProjectList(String name){
        return null;
    }


    /**
     * @api {post} /QueryProjectData 查询项目详情
     * @apiDescription 查询单个项目详情
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理
     * @apiName QueryProjectData
     * @apiParam {int} projectID 项目id
     * @apiSuccess (返回结果) {basicInformation[]} basicDataList 项目基础信息列表
     * @apiSuccess (返回结果) {Int} basicDataList.projectID 项目id
     * @apiSuccess (返回结果) {String} basicDataList.projectName 项目名称
     * @apiSuccess (返回结果) {Int} basicDataList.projectType 项目类型
     * @apiSuccess (返回结果) {String} basicDataList.abbreviation 项目简称
     * @apiSuccess (返回结果) {String} basicDataList.platform 平台
     * @apiSuccess (返回结果) {String} basicDataList.managementUnit 项目直接管理单位（直管单位）
     * @apiSuccess (返回结果) {Int} basicDataList.label 项目标签
     * @apiSuccess (返回结果) {DateTime} basicDataList.validityPeriod 有效期
     * @apiSuccess (返回结果) {DateTime} basicDataList.creatTime 创建日期
     * @apiSuccess (返回结果) {Int} basicDataList.projectStatus 项目状态
     * @apiSuccess (返回结果) {clientEnterprise[]} clientEnterprise 客户企业信息列表
     * @apiSuccess (返回结果) {Int} clientEnterprise.client 客户企业id
     * @apiSuccess (返回结果) {String} clientEnterprise.clientEnterprise 客户企业名称
     * @apiSuccess (返回结果) {String} clientEnterprise.contactPerson 联系人
     * @apiSuccess (返回结果) {int} clientEnterprise.phone 联系人电话
     * @apiSuccess (返回结果) {String} clientEnterprise.address 联系人地址
     * @apiSuccess (返回结果) {String} clientEnterprise.introduction 简介
     * @apiSuccess (返回结果) {projectAddress[]} projectAddress 项目地址信息列表
     * @apiSuccess (返回结果) {String} projectAddress.province 行政区划-省
     * @apiSuccess (返回结果) {String} projectAddress.city 行政区划-市
     * @apiSuccess (返回结果) {String} projectAddress.county 行政区划-区/县
     * @apiSuccess (返回结果) {String} projectAddress.fullAddress 详细地址
     * @apiSuccess (返回结果) {Basicdevelopment[]}  basicData 基础拓展信息
     * @apiSuccess (返回结果) {String} basicData.river 所在河流
     * @apiSuccess (返回结果) {int} basicData.reservoirType 水库类型
     * @apiSuccess (返回结果) {int} basicData.reservoirScale 水库规模
     * @apiSuccess (返回结果) {int} basicData.fullAddress 工程等别
     * @apiSuccess (返回结果) {int} basicData.situation 工程情况
     * @apiSuccess (返回结果) {int} basicData.quality 水质
     * @apiSuccess (返回结果) {DateTime} basicData.startWork 开工时间
     * @apiSuccess (返回结果) {DateTime} basicData.completion 竣工时间
     * @apiSuccess (返回结果) {ReservoirFunction[]} ReservoirFunction 水库功能
     * @apiSuccess (返回结果) {hydrology[]} hydrology 水文特性
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryProjectData", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object quickProjectData(int projcetId){
        return null;
    }

    /**
     * @api {post} /UpdateProjectData 工程项目编辑
     * @apiDescription 工程项目编辑
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理
     * @apiName UpdateProjectData
     * @apiParam {projectDto} projectDto 基本信息请求参数
     * @apiParam {int} projectDto.projectID 项目id
     * @apiParam {String} projectDto.projectName 项目名称
     * @apiParam {String}  projectDto.abbreviation 项目简称
     * @apiParam {String} projectDto.managementUnit 项目直接管理单位（直管单位）
     * @apiParam {DateTime} projectDto.validityPeriod 有效期
     * @apiParam {int} projectDto.projectStatus 项目状态
     * @apiParam {int} projectDto.label 项目标签
     * @apiParam {clientEnterprise[]} clientEnterprise 客户企业
     * @apiParam {Int} clientEnterprise.client 客户企业id
     * @apiParam {String} clientEnterprise.clientEnterprise 客户企业名称
     * @apiParam {String} clientEnterprise.contactPerson 联系人
     * @apiParam {int} clientEnterprise.phone 联系人电话
     * @apiParam {String} clientEnterprise.address 联系人地址
     * @apiParam {String} clientEnterprise.introduction 简介
     * @apiParam {String} {projectAddress[]} projectAddress 项目地址信息列表
     * @apiParam {String} {String} projectAddress.province 行政区划-省
     * @apiParam {String}) {String} projectAddress.city 行政区划-市
     * @apiParam {String} {String} projectAddress.county 行政区划-区/县
     * @apiParam {String} {String} projectAddress.fullAddress 详细地址
     * @apiSuccess (返回结果) {String} message 信息
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/UpdateProjectData", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object updateProjectData(Object a){
        return null;
    }

    /**
     * @api {POST} /QuickClientEnterprise 查询客户企业信息
     * @apiDescription 查询客户企业信息
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理
     * @apiName QuickClientEnterprise
     * @apiParam (请求体) {String} clientEnterprise 客户企业名称
     * @apiSuccess (返回结果) {clientEnterprise[]} clientEnterprise 客户企业信息列表
     * @apiSuccess (返回结果) {Int} clientEnterprise.client 客户企业id
     * @apiSuccess (返回结果) {String} clientEnterprise.clientEnterprise 客户企业名称
     * @apiSuccess (返回结果) {String} clientEnterprise.contactPerson 联系人
     * @apiSuccess (返回结果) {int} clientEnterprise.phone 联系人电话
     * @apiSuccess (返回结果) {String} clientEnterprise.address 联系人地址
     * @apiSuccess (返回结果) {String} clientEnterprise.introduction 简介
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    @RequestMapping(value = "/QuickClientEnterprise", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object quickClientEnterprise(String name){
        return null;
    }
}
