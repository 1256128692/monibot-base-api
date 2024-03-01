package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.enums.BulletinPublishStatus;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IPlatformCheck;
import cn.shmedo.monitor.monibotbaseapi.util.MybatisFieldUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 17:02
 */
@Data
public class QueryBulletinPageParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "页大小不能为空")
    @Range(min = 1, max = 100, message = "分页大小必须在1~100之间")
    private Integer pageSize;
    @NotNull(message = "当前页不能为空")
    @Positive(message = "当前页必须大于0")
    private Integer currentPage;
    private Integer platform;
    @Range(max = 1, message = "发布状态 0.未发布 1.已发布")
    private Integer status;
    private String queryCode;
    @Range(max = 3, message = "公告类型 0.其他 1.行业政策 2.重要新闻 3.工作公告")
    private Integer type;
    @Range(min = 1, max = 4, message = "排序规则 1.创建时间降序排序(默认); 2.创建时间升序排序 3.置顶优先,发布时间降序排序(要求发布状态为1.已发布); 4.置顶优先,发布时间升序排序(要求发布状态为1.已发布);")
    private Integer orderType;
    private Date startTime;
    private Date endTime;
    @JsonIgnore
    private List<OrderItem> orderItemList;

    @Override
    public ResultWrapper<?> validate() {
        platform = Objects.isNull(platform) || DefaultConstant.IGNORE_SERVICE_LIMIT_ID_SET.contains(platform) ? null : platform;
        if (Objects.nonNull(platform) && !validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在");
        }
        orderType = Objects.isNull(orderType) ? 1 : orderType;
        if ((Objects.isNull(status) || BulletinPublishStatus.UNPUBLISHED.getCode().equals(status)) && (orderType == 3 || orderType == 4)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "按\"置顶优先,发布时间升序/降序\"排序时,发布状态必须为1.已发布");
        }
        orderItemList = switch (orderType) {
            case 1 -> OrderItem.descs(MybatisFieldUtil.columnToString(TbBulletinData::getCreateTime)
                    , MybatisFieldUtil.columnToString(TbBulletinData::getId));
            case 2 -> OrderItem.ascs(MybatisFieldUtil.columnToString(TbBulletinData::getCreateTime),
                    MybatisFieldUtil.columnToString(TbBulletinData::getId));
            case 3 -> OrderItem.descs(MybatisFieldUtil.columnToString(TbBulletinData::getTopMost),
                    MybatisFieldUtil.columnToString(TbBulletinData::getUpdateTime),
                    MybatisFieldUtil.columnToString(TbBulletinData::getId));
            case 4 -> List.of(OrderItem.desc(MybatisFieldUtil.columnToString(TbBulletinData::getTopMost)),
                    OrderItem.asc(MybatisFieldUtil.columnToString(TbBulletinData::getUpdateTime)),
                    OrderItem.asc(MybatisFieldUtil.columnToString(TbBulletinData::getId)));
            default -> throw new IllegalArgumentException();
        };
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
