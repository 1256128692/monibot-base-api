package cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-09 10:10
 **/
@Data
public class ExportWtVideoParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private List<Integer> projectIDList;

    /**
     * 关键词,支持模糊查询设备SN/工程名称/监测点名称
     */
    private String queryCode;

    /**
     * 视频设备型号
     */
    private String videoType;

    private Boolean online;

    /**
     * 设备状态 0.正常 1.异常
     */
    private Integer status;

    private String areaCode;

    private Integer monitorItemID;

    private Integer ruleID;

    /**
     * 是否选中 true:选中 false:未选中
     */
    private Boolean select;

    @Override
    public ResultWrapper<?> validate() {
        Collection<Integer> permissionProjectList = PermissionUtil.getHavePermissionProjectList(companyID, projectIDList);
        if (permissionProjectList.isEmpty()) {
            return ResultWrapper.withCode(ResultCode.NO_PERMISSION, "没有权限访问该公司下的项目");
        }
        this.projectIDList = permissionProjectList.stream().toList();

        // TODO 检查工程项目列表是否有数据没有配置监测类别,如果没有配置视频类型监测类别,则返回报错

        if (ruleID != null) {
            if (select == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "引擎ruleID不为空时,参数select不能为空");
            }
        }
        return null;
    }

    @Override
    public List<Resource> parameter() {

        Set<Resource> collect = projectIDList.stream().map(item -> {
            if (item != null) {
                return new Resource(item.toString(), ResourceType.BASE_PROJECT);
            }
            return null;
        }).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(item -> ResourceType.BASE_PROJECT + item.toString()))));

        return new ArrayList<>(collect);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}
