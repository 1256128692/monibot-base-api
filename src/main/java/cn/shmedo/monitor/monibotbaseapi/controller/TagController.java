package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.tag.QueryTagListParam;
import cn.shmedo.monitor.monibotbaseapi.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
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

    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * @api {POST} /QueryTagList 查询标签
     * @apiVersion 1.0.0
     * @apiGroup 标签模块
     * @apiName QueryTagList
     * @apiDescription 查询标签, 结果为2维数组，根据tagKey区分
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String}  [fuzzyKey] 模糊Key
     * @apiParam (请求体) {String} [fuzzyValue] 模糊Value
     * @apiSuccess (响应结果) {[][]} list 全部数据
     * @apiSuccess (响应结果) {Object[]} list.list 一列数据
     * @apiSuccess (响应结果) {Int} list.list.id 标签ID
     * @apiSuccess (响应结果) {String} list.list.tagKey 标签键
     * @apiSuccess (响应结果) {String} [list.list.tagValue] 标签值
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListMdmbaseTag
     */
    @Permission(permissionName = "mdmbase:ListMdmbaseTag")
    @RequestMapping(value = "QueryTagList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryTagList(@Validated @RequestBody QueryTagListParam param) {
        return tagService.queryTagList(param);
    }
}
