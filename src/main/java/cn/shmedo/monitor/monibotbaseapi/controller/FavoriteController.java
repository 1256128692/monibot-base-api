package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.model.param.favorite.AddFavoriteParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.favorite.DeleteFavoriteParam;
import cn.shmedo.monitor.monibotbaseapi.service.ITbFavoriteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wuxl
 * @Date 2024/4/29 16:33
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller
 * @ClassName: FavoriteController
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@AllArgsConstructor
public class FavoriteController {
    private final ITbFavoriteService tbFavoriteService;

    /**
     * @api {post} /AddFavorite 新增企业下收藏
     * @apiDescription 新增企业下收藏
     * @apiVersion 1.0.0
     * @apiGroup 企业收藏模块
     * @apiName AddFavorite
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} subjectType 0-监测项目
     * @apiParam (请求体) {Int[]} subjectIDSet 收藏对象ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddMonitorItem
     */
    @Permission(permissionName = "mdmbase:AddFavorite")
    @RequestMapping(value = "AddFavorite", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addFavorite(@Valid @RequestBody AddFavoriteParam pa) {
        tbFavoriteService.addFavorite(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /DeleteFavorite 删除企业下收藏
     * @apiDescription 删除企业下收藏
     * @apiVersion 1.0.0
     * @apiGroup 企业收藏模块
     * @apiName DeleteFavorite
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} IDList 收藏ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteBaseMonitorItem
     */
    @Permission(permissionName = "mdmbase:DeleteFavorite")
    @RequestMapping(value = "DeleteFavorite", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteFavorite(@Valid @RequestBody DeleteFavoriteParam pa) {
        tbFavoriteService.deleteFavorite(pa);
        return ResultWrapper.successWithNothing();
    }
}
