package cn.shmedo.document;

/**
 * @Author cyf
 * @Date 2023/2/21 9:44
 * @PackageName:cn.shmedo.document
 * @ClassName: document
 * @Description: TODO
 * @Version 1.0
 */
public class document {

    /**
     * @api {post} /amout/QueryProjectBriefList 查询项目简略信息列表
     * @apiDescription 通过项目名称模糊查询项目列表
     * @apiVersion 1.0
     * @apiGroup 工程项目管理
     * @apiName QueryProjectBriefList
     * @apiParam {String} projectName 项目名称
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
    public Object selectQuick(String name){
        return null;
    }
}
