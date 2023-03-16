package cn.shmedo.monitor.monibotbaseapi.controller;

/**
 * 监测平台接口文档
 * @Author cyf
 * @Date 2023/3/10 17:43
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller
 * @ClassName: MonitorDemo
 * @Description: TODO
 * @Version 1.0
 */
public class MonitorDemo {

    /**
     * @api {POST} /MonitorPointInfo 监测点管理分页
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName MonitorPointInfo
     * @apiDescription 监测点管理分页
     * @apiParam (请求体) {String} Name 监测点名称
     * @apiParam (请求体) {Int} MonitorItemID 监测项目ID
     * @apiParam (请求体) {Int} dropDownList 下拉列表-todo
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (响应结果) {Object}[] list 监测点列表
     * @apiSuccess (响应结果) {Int} list.ID 监测点ID
     * @apiSuccess (响应结果) {Int} list.projectID 工程项目ID
     * @apiSuccess (响应结果) {Int} list.monitorType 监测类型
     * @apiSuccess (响应结果) {Int} list.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {String} list.name 监测点名称
     * @apiSuccess (响应结果) {String} list.installLocation 安装位置
     * @apiSuccess (响应结果) {String} list.gpsLocation 地图位置
     * @apiSuccess (响应结果) {String} list.imageLocation 底图位置
     * @apiSuccess (响应结果) {String} list.overallViewLocation 全景位置
     * @apiSuccess (响应结果) {String} list.spatialLocation 三维位置
     * @apiSuccess (响应结果) {Int} list.enable 是否启用，1：启用，0：停用
     * @apiSuccess (响应结果) {String} list.exValues 拓展字段
     * @apiSuccess (响应结果) {String} list.displayOrder 排序字段
     * @apiSuccess (响应结果) {Object}[] list.sensorList 监测传感器列表
     * @apiSuccess (响应结果) {int} list.sensorList.ID 传感器ID
     * @apiSuccess (响应结果) {int} list.sensorList.projectID 工程项目ID
     * @apiSuccess (响应结果) {int} list.sensorList.templateID 监测类型模板ID
     * @apiSuccess (响应结果) {String} list.sensorList.dataSourceID 数据源分布式唯一ID
     * @apiSuccess (响应结果) {int} list.sensorList.monitorType 监测类型
     * @apiSuccess (响应结果) {String} list.sensorList.name 传感器名称
     * @apiSuccess (响应结果) {String} list.sensorList.alias 传感器别名
     * @apiSuccess (响应结果) {int} list.sensorList.kind 传感器类型
     * @apiSuccess (响应结果) {int} list.sensorList.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {int} list.sensorList.monitorPointID 关联监测点ID
     * @apiSuccess (响应结果) {String} list.sensorList.configFieldValue 存储监测类型拓展配置值
     * @apiSuccess (响应结果) {String} list.sensorList.exValues 拓展信息
     * @apiSuccess (响应结果) {int} list.sensorList.status 传感器状态
     * @apiSuccess (响应结果) {boolean} list.sensorList.warnNoData 无数据报警是否开启
     * @apiSuccess (响应结果) {Date} list.sensorList.monitorBeginTime 监测数据开始时间
     * @apiSuccess (响应结果) {String} list.sensorList.imagePath 传感器图片
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSampleRequest off
     * @apiPermission 系统权限:query
     * */
    public Object monitorPointInfo(){
        return null;
    }

    /**
     * @api {POST} /AddMonitorPoint 新增监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName AddMonitorPoint
     * @apiDescription 新增监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {String} name 监测点名称
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:add
     * */
    public Object addMonitorPoint(){
        return null;
    }

    /**
     * @api {POST} /DeleteMonitorPoint 删除监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName DeleteMonitorPoint
     * @apiDescription 删除监测点
     * @apiParam (请求体) {Int} ID 监测点ID
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:delete
     * */
    public Object deleteMonitorPoint(){
        //监测点下有传感器不允许删除-todo
        return null;
    }

    /**
     * @api {POST} /UpdateMonitorPoint 修改监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName UpdateMonitorPoint
     * @apiDescription 修改监测点
     * @apiParam (请求体) {Int} ID 监测点ID
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {String} name 监测点名称
     * @apiParam (请求体) {String} installLocation 安装位置
     * @apiParam (请求体) {String} gpsLocation 地图位置
     * @apiParam (请求体) {String} imageLocation 底图位置
     * @apiParam (请求体) {String} overallViewLocation 全景位置
     * @apiParam (请求体) {String} spatialLocation 三维位置
     * @apiParam (请求体) {Int} enable 是否启用
     * @apiParam (请求体) {String} exValues 拓展字段
     * @apiParam (请求体) {String} displayOrder 排序字段
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:update
     * */
    public Object updateMonitorPoint(){
        return null;
    }

    /**
     * @api {POST} /queryMonitorPoint 查询单个监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName queryMonitorPoint
     * @apiDescription 查询单个监测点
     * @apiParam (请求体) {Int} ID 监测点ID
     * @apiSuccess (响应结果) {Int} ID 监测点ID
     * @apiSuccess (响应结果) {Int} projectID 工程项目ID
     * @apiSuccess (响应结果) {Int} monitorType 监测类型
     * @apiSuccess (响应结果) {Int} monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {String} name 监测点名称
     * @apiSuccess (响应结果) {String} installLocation 安装位置
     * @apiSuccess (响应结果) {String} gpsLocation 地图位置
     * @apiSuccess (响应结果) {String} imageLocation 底图位置
     * @apiSuccess (响应结果) {String} overallViewLocation 全景位置
     * @apiSuccess (响应结果) {String} spatialLocation 三维位置
     * @apiSuccess (响应结果) {Int} enable 是否启用，1：启用，0：停用
     * @apiSuccess (响应结果) {String} exValues 拓展字段
     * @apiSuccess (响应结果) {String} displayOrder 排序字段
     * @apiSuccess (响应结果) {Object}[] sensorList 监测传感器列表
     * @apiSuccess (响应结果) {int} sensorList.ID 传感器ID
     * @apiSuccess (响应结果) {int} sensorList.projectID 工程项目ID
     * @apiSuccess (响应结果) {int} sensorList.templateID 监测类型模板ID
     * @apiSuccess (响应结果) {String} sensorList.dataSourceID 数据源分布式唯一ID
     * @apiSuccess (响应结果) {int} sensorList.monitorType 监测类型
     * @apiSuccess (响应结果) {String} sensorList.name 传感器名称
     * @apiSuccess (响应结果) {String} sensorList.alias 传感器别名
     * @apiSuccess (响应结果) {int} sensorList.kind 传感器类型
     * @apiSuccess (响应结果) {int} sensorList.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {int} sensorList.monitorPointID 关联监测点ID
     * @apiSuccess (响应结果) {String} sensorList.configFieldValue 存储监测类型拓展配置值
     * @apiSuccess (响应结果) {String} sensorList.exValues 拓展信息
     * @apiSuccess (响应结果) {int} sensorList.status 传感器状态
     * @apiSuccess (响应结果) {boolean} sensorList.warnNoData 无数据报警是否开启
     * @apiSuccess (响应结果) {Date} sensorList.monitorBeginTime 监测数据开始时间
     * @apiSuccess (响应结果) {String} sensorList.imagePath 传感器图片
     * @apiSampleRequest off
     * @apiPermission 系统权限:query
     * */
    public Object queryMonitorPoint(){
        return null;
    }

    /**
     * @api {POST} /MonitorPointConfig 监测点操作-配置-todo
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName MonitorPointConfig
     * @apiDescription 监测点操作-配置
     * @apiParam (请求体) {Int} ID 监测点ID
     * @apiParam (请求体) {Object}[] sensorList 监测传感器列表
     * @apiParam (请求体) {Object} sensorList.oneID 传感器1
     * @apiParam (请求体) {Object} sensorList.twoID 传感器2
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:update
     * */
    public Object monitorPointConfig(){
        return null;
    }

    /**
     * @api {POST} /MonitorPointData 监测点操作-数据-还没做
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName MonitorPointData
     * @apiDescription 监测点操作-数据
     * @apiParam (请求体) {Int} monitorID 监测点ID
     * @apiParam (请求体) {String} monitorName 监测点ID
     * @apiParam (请求体) {String} monitorProject 监测项目
     * @apiParam (请求体) {Object}[] sensorList 监测传感器列表
     * @apiParam (请求体) {Object} sensorList.one 传感器1
     * @apiParam (请求体) {Object} sensorList.two 传感器2
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:update
     * */
    public Object monitorPointData(){
        return null;
    }

    /**
     * @api {POST} /AddMonitorGroup 新建监测组别
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName AddMonitorGroup
     * @apiDescription 新建监测组别
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} name 监测组别名称
     * @apiParam (请求体) {Int} enable 监测组别状态
     * @apiParam (请求体) {Object}[] list 监测项目列表
     * @apiParam (请求体) {Int} list.monitorItemID 监测项目列表
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:add
     * */
    public Object addMonitorGroup(){
        return null;
    }

    /**
     * @api {POST} /UpdateMonitorGroup 修改监测组别
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName UpdateMonitorGroup
     * @apiDescription 修改监测组别
     * @apiParam (请求体) {Int} ID ID
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} name 监测组别名称
     * @apiParam (请求体) {Int} enable 监测组别状态
     * @apiParam (请求体) {Object}[] list 监测项目列表
     * @apiParam (请求体) {Int} list.monitorItemID 监测项目列表
     * @apiParam (请求体) {Object}[] monitorItemList 监测项目列表
     * @apiParam (请求体) {Int} monitorItemList.ID 监测组ID
     * @apiParam (请求体) {Int} monitorItemList.enable 监测组状态
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:update
     * */
    public Object updateMonitorGroup(){
        return null;
    }

    /**
     * @api {POST} /DeleteMonitorGroup 删除监测组别
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName DeleteMonitorGroup
     * @apiDescription 删除监测组别
     * @apiParam (请求体) {Int} ID 监测组别ID
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:delete
     * */
    public Object deleteMonitorGroup(){
        return null;
    }

    /**
     * @api {POST} /monitorGroupList 监测组别分页查询
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName monitorGroupList
     * @apiParam (请求体) {Int} name 监测组别名称
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiDescription 监测组别分页查询
     * @apiSuccess (响应结果) {Object}[] list 监测组别列表
     * @apiSuccess (响应结果) {Int} list.ID 监测组别ID
     * @apiSuccess (响应结果) {String} list.name 监测组别名称
     * @apiSuccess (响应结果) {Boolean} list.enable 是否启用
     * @apiSuccess (响应结果) {String} list.exValue 拓展字段
     * @apiSuccess (响应结果) {Int} list.displayOrder 排序字段
     * @apiSuccess (响应结果) {Object}[] list.monitorItem 监测项目列表
     * @apiSuccess (响应结果) {Int} list.monitorItem.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {String} list.monitorItem.name 监测项目名称
     * @apiSuccess (响应结果) {Object}[] list.monitorGroup 监测组列表
     * @apiSuccess (响应结果) {Int} list.monitorGroup.ID 监测组ID
     * @apiSuccess (响应结果) {Int} list.monitorGroup.name 监测组名称
     * @apiSuccess (响应结果) {Int} list.monitorGroup.enable 是否启用
     * @apiSuccess (响应结果) {String} list.monitorGroup.imagePath 底图地址
     * @apiSuccess (响应结果) {String} list.monitorGroup.exValue 拓展字段
     * @apiSuccess (响应结果) {Int} list.monitorGroup.displayOrder 排序字段
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSampleRequest off
     * @apiPermission 系统权限:query
     * */
    public Object monitorGroupList(){
        return null;
    }

    /**
     * @api {POST} /AddMonitorSet 新建监测组
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName AddMonitorSet
     * @apiDescription 新建监测组
     * @apiParam (请求体) {Int} ID 监测组别ID
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} name 监测组名称
     * @apiParam (请求体) {Int} enable 监测组状态
     * @apiParam (请求体) {Object}[] list 监测点列表
     * @apiParam (请求体) {Int} list.ID 监测点ID
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:add
     * */
    public Object addMonitorSet(){
        return null;
    }

    /**
     * @api {POST} /UpdateMonitorSet 修改监测组别
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName UpdateMonitorSet
     * @apiDescription 修改监测组别
     * @apiParam (请求体) {Int} ID 监测组ID
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} name 监测组名称
     * @apiParam (请求体) {Int} enable 监测组状态
     * @apiParam (请求体) {Object}[] list 监测点列表
     * @apiParam (请求体) {Int} list.ID 监测点ID
     * @apiParam (请求体) {String} list.ImageLocation 监测点底图位置
     * @apiParam (请求体) {String} imagePath 底图地址
     * @apiSuccess (返回结果) {Int} code 响应码
     * @apiSampleRequest off
     * @apiPermission 系统权限:update
     * */
    public Object updateMonitorSet(){
        return null;
    }

    /**
     * @api {POST} /QueryMonitorItemList 查询监测项目列表
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName QueryMonitorItemList
     * @apiDescription 查询监测项目列表
     * @apiParam (请求体) {String} CompanyID 公司ID
     * @apiParam (请求体) {String} monitorProject 监测项目
     * @apiParam (请求体) {Int} CreateType 创建类型
     * @apiSuccess (响应结果) {Object}[] list 监测项目列表
     * @apiSuccess (响应结果) {Int} list.ID ID
     * @apiSuccess (响应结果) {Int} list.companyID 公司ID
     * @apiSuccess (响应结果) {Int} list.monitorType 监测类型
     * @apiSuccess (响应结果) {String} list.name 监测项目名称
     * @apiSuccess (响应结果) {String} list.alias 监测项目别名
     * @apiSuccess (响应结果) {Int} list.createType 创建类型
     * @apiSuccess (响应结果) {Int} list.projectType 项目类型（行业信息）
     * @apiSuccess (响应结果) {String} list.exValue 拓展字段
     * @apiSuccess (响应结果) {Int} list.displayOrder 显示排序
     * @apiSampleRequest off
     * @apiPermission 系统权限:query
     * */
    public Object queryMonitorItemList(){
        return null;
    }
    /**
     * @api {POST} /QueryMonitorPointList 查询监测点列
     * @apiVersion 1.0.0
     * @apiGroup 监测模块
     * @apiName QueryMonitorPointList
     * @apiDescription 查询监测点列表
     * @apiParam (请求体) {String} companyID 公司ID
     * @apiParam (请求体) {String} name 监测点名称
     * @apiParam (请求体) {Object}[] list 监测项目列表
     * @apiParam (请求体) {Int} list.MonitorItemID 监测项目ID
     * @apiSuccess (响应结果) {Object}[] list 监测点列表
     * @apiSuccess (响应结果) {String} list.ID 监测点ID
     * @apiSuccess (响应结果) {String} list.projectID 工程项目ID
     * @apiSuccess (响应结果) {Int} list.monitorType 监测点类型
     * @apiSuccess (响应结果) {Int} list.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {String} list.name 监测名称
     * @apiSuccess (响应结果) {String} list.monitorProject 监测项目
     * @apiSuccess (响应结果) {String} list.installLocation 安装位置
     * @apiSuccess (响应结果) {String} list.gpsLocation 地图位置
     * @apiSuccess (响应结果) {String} list.imageLocation 底图位置
     * @apiSuccess (响应结果) {String} list.overallViewLocation 全景位置
     * @apiSuccess (响应结果) {String} list.spatialLocation 三维位置
     * @apiSuccess (响应结果) {Int} list.enable 是否启用，1：启用，0：停用
     * @apiSuccess (响应结果) {String} list.exValues 拓展字段
     * @apiSuccess (响应结果) {String} list.displayOrder 排序字段
     * @apiSampleRequest off
     * @apiPermission 系统权限:query
     * */
    public Object queryMonitorPointList(){
        return null;
    }
}
