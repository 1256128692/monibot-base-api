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
     * @apiGroup 项目模块
     * @apiName SetDeviceModelData
     * @apiDescription 新增项目
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiSuccess (响应结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    @RequestMapping(value = "AddProject", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    @Permission
    public Object addProject() {
        return null;
    }
    /**
     * @api {post} /QueryProjectBriefList 查询项目简略信息列表
     * @apiDescription 查询项目列表
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理
     * @apiName QueryProjectBriefList
     * @apiParam {Int} pageSize 页大小
     * @apiParam {Int} currentPage 当前页
     * @apiParam {String} projectName 项目名称
     * @apiParam {String} managementUnit 项目直接管理单位（直管单位）
     * @apiParam {int} projectName 项目类型
     * @apiParam {String} clientEnterprise 客户企业名称
     * @apiParam {int} projectStatus 项目状态
     * @apiParam {int} platform 项目平台
     * @apiParam {DateTime} validityPeriod 有效期
     * @apiParam {DateTime} beginCreatTime 创建时间-开始
     * @apiParam {DateTime} endCreatTime 创建时间-结束
     * @apiParam {Map} condition 添加条件
     * @apiSuccess (返回结果) {Int} totalCount 查询条件下的数据总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 项目id
     * @apiSuccess (返回结果) {String} currentPageData.projectName 项目名称
     * @apiSuccess (返回结果) {Int} currentPageData.projectType 项目类型
     * @apiSuccess (返回结果) {String} currentPageData.managementUnit 项目直接管理单位（直管单位）
     * @apiSuccess (返回结果) {DateTime} currentPageData.creatTime 创建日期
     * @apiSuccess (返回结果) {Int} currentPageData.projectStatus 项目状态
     * @apiSuccess (返回结果) {String} currentPageData.platform 平台
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryProjectBriefList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
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
     * @apiSuccess (返回结果) {Int} projectID 项目id
     * @apiSuccess (返回结果) {String} projectName 项目名称
     * @apiSuccess (返回结果) {Int} projectType 项目类型
     * @apiSuccess (返回结果) {String} abbreviation 项目简称
     * @apiSuccess (返回结果) {String} platform 平台
     * @apiSuccess (返回结果) {String} managementUnit 项目直接管理单位（直管单位）
     * @apiSuccess (返回结果) {Map} blabel 项目标签
     * @apiSuccess (返回结果) {DateTime} validityPeriod 有效期
     * @apiSuccess (返回结果) {DateTime} creatTime 创建日期
     * @apiSuccess (返回结果) {Int} projectStatus 项目状态
     * @apiSuccess (返回结果) {Int} client 客户企业id
     * @apiSuccess (返回结果) {String} clientEnterprise 客户企业名称
     * @apiSuccess (返回结果) {String} contactPerson 联系人
     * @apiSuccess (返回结果) {int} phone 联系人电话
     * @apiSuccess (返回结果) {String} address 联系人地址
     * @apiSuccess (返回结果) {String} introduction 简介
     * @apiSuccess (返回结果) {String} province 行政区划-省
     * @apiSuccess (返回结果) {String} city 行政区划-市
     * @apiSuccess (返回结果) {String} county 行政区划-区/县
     * @apiSuccess (返回结果) {String} fullAddress 详细地址
     * @apiSuccess (返回结果) {Map[]}  BasicData 基础拓展信息
     * @apiSuccess (返回结果) {Map[]} hydrology 水文特性
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
     * @apiParam {int} projectID 项目id
     * @apiParam {String} projectName 项目名称
     * @apiParam {String} abbreviation 项目简称
     * @apiParam {String} managementUnit 项目直接管理单位（直管单位）
     * @apiParam {DateTime} validityPeriod 有效期
     * @apiParam {int} projectStatus 项目状态
     * @apiParam {int} label 项目标签
     * @apiParam {int} client 客户企业id
     * @apiParam {String} province 行政区划-省 -待定
     * @apiParam {String} city 行政区划-市 -待定
     * @apiParam {String} county 行政区划-区/县 -待定
     * @apiParam {String} fullAddress 详细地址 -待定
     * @apiSuccess (返回结果) {String} message 信息
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/UpdateProjectData", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object updateProjectData(Object a){
        return null;
    }
}
