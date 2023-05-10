package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DepartmentController {

//    private DeptService deptService;

    /**
     * @api {POST} /AddDepartment 添加部门
     * @apiVersion 1.0.0
     * @apiGroup 部门模块
     * @apiName AddDepartment
     * @apiDescription 添加部门
     * @apiParam (请求参数) {Int} companyID 部门所属公司ID
     * @apiParam (请求参数) {String} name 部门名称 (100)
     * @apiParam (请求参数) {Int} displayOrder 排序字段
     * @apiParam (请求参数) {Int} [parentID] 所属部门ID
     * @apiParam (请求参数) {String} [desc] 部门描述 (500)
     * @apiSuccess (返回结果) {Int} data 部门ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:UpdateDepartment
     */
    @Permission(permissionName = "user:UpdateDepartment")
    @LogParam(moduleName = "部门模块", operationName = "新增部门", operationProperty = OperationProperty.ADD)
    @PostMapping(value = "/AddDepartment", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object add(@Valid @RequestBody Object parameter) {

//        return ResultWrapper.success(deptService.save(parameter));
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateDepartment 修改部门信息
     * @apiVersion 1.0.0
     * @apiGroup 部门模块
     * @apiName UpdateDepartment
     * @apiDescription 修改部门信息
     * @apiParam (请求参数) {Int} companyID 部门所属公司ID
     * @apiParam (请求参数) {Int} departmentID 部门ID
     * @apiParam (请求参数) {String} [name] 部门名称 (100)
     * @apiParam (请求参数) {String} [desc] 部门描述 (500)
     * @apiParam (请求参数) {Int} [parentID] 所属部门ID
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:UpdateDepartment
     */
    @Permission(permissionName = "user:UpdateDepartment")
    @LogParam(moduleName = "部门模块", operationName = "修改部门信息", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "/UpdateDepartment", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object update(@Valid @RequestBody Object parameter) {
//        deptService.update(parameter);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteDepartment 删除部门
     * @apiDescription 删除部门
     * @apiVersion 1.0.0
     * @apiGroup 部门模块
     * @apiName DeleteDepartment
     * @apiParam (请求参数){Int} companyID 公司ID
     * @apiParam (请求参数){Int[]} departmentIDList 部门ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:DeleteDepartment
     */
    @Permission(permissionName = "user:DeleteDepartment")
    @LogParam(moduleName = "部门模块", operationName = "删除部门", operationProperty = OperationProperty.DELETE)
    @PostMapping(value = "/DeleteDepartment", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object delete(@RequestBody @Valid Object parameter) {
//        deptService.delete(parameter);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryDepartmentOrganization 获取部门信息列表
     * @apiDescription 获取部门信息列表
     * @apiVersion 1.0.0
     * @apiGroup 部门模块
     * @apiName QueryDepartmentOrganization
     * @apiParam (请求参数){Int} companyID 公司ID
     * @apiParam (请求参数){Int[]} departmentIDList 部门ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:DescribeDepartment
     */
    @Permission(permissionName = "user:DescribeDepartment")
    @PostMapping(value = "/QueryDepartmentOrganization", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDepartmentOrganization(@RequestBody @Valid Object parameter) {
//        deptService.delete(parameter);
        return ResultWrapper.successWithNothing();
    }


}
