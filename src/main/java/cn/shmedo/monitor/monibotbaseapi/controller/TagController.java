package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 10:19
 **/
@RestController
public class TagController {

    /**
     * @api {POST} /QueryTagList 查询标签
     * @apiVersion 1.0.0
     * @apiGroup 标签模块
     * @apiName QueryTagList
     * @apiDescription 查询标签
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String}  [fuzzyKey] 模糊Key
     * @apiParam (请求体) {String} [fuzzyValue] 模糊Value
     * @apiSuccess (返回结果) {Json[]} tagList  标签列表
     * @apiSuccess (返回结果) {Int} tagList.id  标签ID
     * @apiSuccess (返回结果) {String} tagList.key  标签键
     * @apiSuccess (返回结果) {String} tagList.value  标签值
     * @apiSampleRequest off
     * @apiPermission 项目权限:
     */
    @RequestMapping(value = "QueryTagList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryTagList() {
        return null;
    }
}
