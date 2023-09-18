package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup.*;

/**
 * @Author wuxl
 * @Date 2023/9/15 16:55
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service
 * @ClassName: PropertyModelGroupService
 * @Description: TODO
 * @Version 1.0
 */
public interface PropertyModelGroupService {
    Object addPropertyModelGroup(AddPropertyModelGroupParam addPropertyModelGroupParam);

    Object updatePropertyModelGroup(UpdatePropertyModelGroupParam updatePropertyModelGroupParam);

    Object describePropertyModelGroup(DescribePropertyModelGroupParam describePropertyModelGroupParam);

    Object deletePropertyModelGroup(DeletePropertyModelGroupParam describePropertyModelGroupParam);

    Object describePropertyModelGroupList(DescribePropertyModelGroupListParam describePropertyModelGroupListParam);
}
