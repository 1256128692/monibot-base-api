package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnLogService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WtWarnController {
    private final ITbWarnLogService warnLogService;
    /**
     * @api {POST} /QueryWtWarnPage 查询报警分页
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtWarnPage
     * @apiDescription 查询报警分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} queryType 查询类型 0.预留 1.实时记录 2.历史记录
     * @apiParam (请求参数) {String} [queryCode] 关键字,支持模糊搜索报警名称、工程名称、报警内容
     * @apiParam (请求参数) {Int} [monitorTypeID] 监测类型ID
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {Int} [warnLevel] 报警等级 1.Ⅰ级 2.Ⅱ级 3.Ⅲ级 4.Ⅳ级
     * @apiParam (请求参数) {Int} [orderType] 排序规则 1.按照报警时间降序排序(默认) 2.按照报警时间升序排序
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 记录条数
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.warnID 报警记录ID
     * @apiSuccess (返回结果) {String} currentPageData.warnName 报警名称
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorTypeID 监测类型ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} currentPageData.monitorTypeAlias 监测类型别称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} currentPageData.warnLevel 报警等级 1.Ⅰ级 2.Ⅱ级 3.Ⅲ级 4.Ⅳ级
     * @apiSuccess (返回结果) {DateTime} currentPageData.warnTime 报警时间
     * @apiSuccess (返回结果) {String} currentPageData.warnContext 报警内容
     * @apiSuccess (返回结果) {String} currentPageData.orderCode 关联工单编号
     * @apiSuccess (返回结果) {Int} currentPageData.orderStatus 处置状态 1.待接单 2.处置中 3.已处置 4.审核中 5.已结束 6.已关闭
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseWarn
     */
//    @Permission(permissionName = "mdmbase:ListBaseWarn")
    @PostMapping(value = "/QueryWtWarnPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtWarnPage(@Valid @RequestBody Object param) {
        // table name: tbbbb
        // select tb.* from tbbbb tb left join (select MAX(time) latest_time,group_id from tbbbb group by group_id) tb1 on tb.group_id=tb1.group_id where tb1.latest_time=tb.time
        // 实时记录是测点报警状态最新一条报警数据，并不是指当天的；历史记录是除了最新报警状态的报警数据其他触发报警状态的报警数据
        return null;
    }

    /**
     * @api {POST} /QueryWtWarnInfoDetail 查询报警详情
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtWarnInfoDetail
     * @apiDescription 查询报警详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnID 报警记录ID
     * @apiSuccess (返回结果) {String} name 报警名称
     * @apiSuccess (返回结果) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {Int} monitorTypeID 监测类型ID
     * @apiSuccess (返回结果) {String} monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} monitorTypeAlias 监测类型别称
     * @apiSuccess (返回结果) {Int} monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} level 报警等级
     * @apiSuccess (返回结果) {String} context 报警内容
     * @apiSuccess (返回结果) {Int} monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} monitorPointLocation 监测点位置
     * @apiSuccess (返回结果) {DateTime} warnTime 报警时间
     * @apiSuccess (返回结果) {String} compareRule 比较区间json
     * @apiSuccess (返回结果) {String} triggerRule 触发规则json
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseWarn
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseWarn")
    @PostMapping(value = "/QueryWtWarnInfoDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtWarnInfoDetail(@Valid @RequestBody Object param) {
        //TODO 获取对应报警记录,获取对应规则(获取限制),将点位、时间数据（前3天后3天）传输至第三方服务后获取并封装对应测点曲线数据
        return null;
    }
}
