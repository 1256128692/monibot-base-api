package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import cn.shmedo.monitor.monibotbaseapi.model.param.asset.*;
import cn.shmedo.monitor.monibotbaseapi.service.IAssetService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-18 14:44
 **/
@RestController
@AllArgsConstructor
public class AssetController {
    private final IAssetService assetService;
    /**
     * @api {post} /AddAsset 新增资产
     * @apiDescription 新增资产
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName AddAsset
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} name 名称型号
     * @apiParam (请求体) {String} vendor 厂商品牌
     * @apiParam (请求体) {Int}   unit 单位12345678， 对应件、台、个、组、毫克、克、千克、吨
     * @apiParam (请求体) {Int}  type 类型10, 20 救灾物资、备品备件
     * @apiParam (请求体) {Int} warnValue  预警值(>=0)
     * @apiParam (请求体) {String} comparison 比较方式< ,> , =, <=,  >=
     * @apiParam (请求体) {String} [exValue] 扩展字段,json字符串（500）
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @LogParam(moduleName = "资产模块", operationName = "新增资产", operationProperty = OperationProperty.ADD)
    @RequestMapping(value = "AddAsset", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addAsset(@Validated @RequestBody AddAssetParam pa) {
        assetService.save(pa.toEntity(CurrentSubjectHolder.getCurrentSubject().getSubjectID()));
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /UpdateAsset 更新资产
     * @apiDescription 更新资产
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName UpdateAsset
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} ID 资产ID
     * @apiParam (请求体) {String} name 名称型号
     * @apiParam (请求体) {String} vendor 厂商品牌
     * @apiParam (请求体) {Int} warnValue  预警值
     * @apiParam (请求体) {String} comparison 比较方式< ,> , =, <=,  >=
     * @apiParam (请求体) {String} [exValue] 扩展字段,json字符串（500）
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @LogParam(moduleName = "资产模块", operationName = "更新资产", operationProperty = OperationProperty.UPDATE)
    @RequestMapping(value = "UpdateAsset", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateAsset(@Validated @RequestBody UpdateAssetParam pa) {
        assetService.updateById(pa.update(CurrentSubjectHolder.getCurrentSubject().getSubjectID()));
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /DeleteAsset 删除资产
     * @apiDescription 删除资产，有库存则不允许删除
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName DeleteAsset
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} assetIDList 资产ID列表
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @RequestMapping(value = "DeleteAsset", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteAsset(@Validated @RequestBody DeleteAssetParam pa) {
        assetService.removeByIds(pa.getAssetIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /QueryAssetList 查询资产列表
     * @apiDescription 查询资产列表
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName QueryAssetList
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} type 类型10, 20 救灾物资、备品备件
     * @apiSuccess (返回结果) {Jsonp[]} list 资产列表
     * @apiSuccess (返回结果) {Int} list.ID 资产ID
     * @apiSuccess (返回结果) {String} list.name 名称型号
     * @apiSuccess (返回结果) {String} list.vendor 厂商品牌
     * @apiSuccess (返回结果) {Int}   list.unit 单位12345678， 对应件、台、个、组、毫克、克、千克、吨
     * @apiSuccess (返回结果) {Int}  list.type 类型12 救灾物资、备品备件
     * @apiSuccess (返回结果) {Int} list.warnValue  预警值
     * @apiSuccess (返回结果) {String} list.comparison 比较方式< ,> , =, <=,  >=
     * @apiSuccess (返回结果) {String} [exValue] 扩展字段,json字符串（500）
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @RequestMapping(value = "QueryAssetList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryAssetList(@Validated @RequestBody QueryAssetListParam pa) {
        return assetService.list(new LambdaQueryWrapper<TbAsset>()
                .eq(TbAsset::getCompanyID, pa.getCompanyID())
                .eq(TbAsset::getType, pa.getType())
                .orderByDesc(TbAsset::getID)
        );
    }

    /**
     * @api {post} /AddAssetHouse 新增资产库
     * @apiDescription 新增资产库
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName AddAssetHouse
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} name 名称
     * @apiParam (请求体) {String} code 编号
     * @apiParam (请求体) {String}   address 地址
     * @apiParam (请求体) {String}  [comment] 备注
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
//    @LogParam(moduleName = "资产模块", operationName = "新增资产库", operationProperty = OperationProperty.ADD)
    @RequestMapping(value = "AddAssetHouse", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object AddAssetHouse(@Validated @RequestBody AddAssetHouseParam pa) {
        assetService.addAssetHouse(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /UpdateAssetHouse 更新资产库
     * @apiDescription 更新资产库
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName UpdateAssetHouse
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} ID 资产库ID
     * @apiParam (请求体) {String} name 名称
     * @apiParam (请求体) {String} code 编号
     * @apiParam (请求体) {String}   address 地址
     * @apiParam (请求体) {String}  [comment] 备注
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @LogParam(moduleName = "资产模块", operationName = "更新资产库", operationProperty = OperationProperty.UPDATE)
    @RequestMapping(value = "UpdateAssetHouse", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateAssetHouse(@Validated @RequestBody UpdateAssetHouseParam pa) {
        assetService.updateAssetHouse(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /DeleteAssetHouse 删除资产库
     * @apiDescription 删除资产库, 资产库下有资产时，不允许删除
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName DeleteAssetHouse
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} houseIDList 资产库ID列表
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @LogParam(moduleName = "资产模块", operationName = "删除资产库", operationProperty = OperationProperty.DELETE)
    @RequestMapping(value = "DeleteAssetHouse", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object DeleteAssetHouse(@Validated @RequestBody DeleteAssetHouseParam pa) {
        assetService.deleteAssetHouse(pa.getHouseIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /QueryAssetHouseList 查询资产库列表
     * @apiDescription 查询资产库列表
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName QueryAssetHouseList
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Jsonp[]} list 资产库列表
     * @apiSuccess (返回结果) {Int} list.ID 资产库ID
     * @apiSuccess (返回结果) {String} list.name 名称型号
     * @apiSuccess (返回结果) {String} list.code 编号
     * @apiSuccess (返回结果) {String}   list.address 地址
     * @apiSuccess (返回结果) {String}  [list.comment] 备注
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @RequestMapping(value = "QueryAssetHouseList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryAssetHouseList(@Validated @RequestBody QueryAssetHouseListParam pa) {
        return assetService.queryAssetHouseList(pa.getCompanyID());
    }

    /**
     * @api {post} /IOAsset 入库出库
     * @apiDescription 入库出库
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName IOAsset
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} assetID 资产ID
     * @apiParam (请求体) {Int} houseID 资产库ID
     * @apiParam (请求体) {Int} value 数量, 正数为入库，负数为出库
     * @apiParam (请求体) {String}  [comment] 备注
     * @apiSuccess (返回结果) {String}   none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @LogParam(moduleName = "资产模块", operationName = "入库出库", operationProperty = OperationProperty.UPDATE)
    @RequestMapping(value = "IOAsset", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object ioAsset(@Validated @RequestBody IOAssetParam pa) {
        assetService.ioAsset(pa, CurrentSubjectHolder.getCurrentSubject());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /QueryAssetIOLogPage 查询资产出入库日志分页
     * @apiDescription 查询资产出入库日志分页, 时间倒叙
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName QueryAssetIOLogPage
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [fuzzyItem] 模糊查询资产型号/名称， 厂商品牌
     * @apiParam (请求体) {Int} [type] 资产类型
     * @apiParam (请求体) {Int} [houseID] 资产库ID
     * @apiParam (请求体) {Boolean} [inOrOut] 出库还是入库
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Json[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.ID日志ID
     * @apiSuccess (返回结果) {DateTime} currentPageData.time 时间
     * @apiSuccess (返回结果) {Int} currentPageData.assetID 资产ID
     * @apiSuccess (返回结果) {String} currentPageData.assetName 资产名称
     * @apiSuccess (返回结果) {String} currentPageData.assetVendor 资产厂商品牌
     * @apiSuccess (返回结果) {Int} currentPageData.assetType 资产类型
     * @apiSuccess (返回结果) {Int} currentPageData.assetUnit 资产单位
     * @apiSuccess (返回结果) {Int} currentPageData.houseID 资产库ID
     * @apiSuccess (返回结果) {String} currentPageData.houseName 资产库名称
     * @apiSuccess (返回结果) {Int} currentPageData.value 数量
     * @apiSuccess (返回结果) {Int} currentPageData.userID 人ID
     * @apiSuccess (返回结果) {Int} currentPageData.userName 人名称
     * @apiSuccess (返回结果) {String} [currentPageData.comment] 备注
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @RequestMapping(value = "QueryAssetIOLogPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryAssetIOLogPage(@Validated @RequestBody QueryAssetIOLogPageParam pa) {
        return assetService.queryAssetIOLogPage(pa);
    }

    /**
     * @api {post} /QueryAssetPage 分页查询资产
     * @apiDescription 分页查询资产
     * @apiVersion 1.0.0
     * @apiGroup 资产模块
     * @apiName QueryAssetPage
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [fuzzyItem] 模糊查询资产型号/名称， 厂商品牌，
     * @apiParam (请求体) {Int} [type] 资产类型
     * @apiParam (请求体) {Int} [houseID] 资产库ID
     * @apiParam (请求体) {Boolean} [isWarn] 是否预警
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Json[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.ID 资产ID
     * @apiSuccess (返回结果) {String} currentPageData.name 资产名称
     * @apiSuccess (返回结果) {String} currentPageData.vendor 资产厂商品牌
     * @apiSuccess (返回结果) {Int} currentPageData.type 资产类型
     * @apiSuccess (返回结果) {Int} currentPageData.unit 资产单位
     * @apiSuccess (返回结果) {Int} currentPageData.warnValue  预警值
     * @apiSuccess (返回结果) {String} currentPageData.comparison 比较方式< ,> , =, <=,  >=
     * @apiSuccess (返回结果) {Int} currentPageData.houseID 资产库ID
     * @apiSuccess (返回结果) {String} currentPageData.houseName 资产库名称
     * @apiSuccess (返回结果) {Int} currentPageData.currentValue 当前数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:XX
     */
//    @Permission(permissionName = "mdmbase:XX")
    @RequestMapping(value = "QueryAssetPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryAssetPage(@Validated @RequestBody QueryAssetPageParam pa) {
        return assetService.queryAssetPage(pa);
    }
}
