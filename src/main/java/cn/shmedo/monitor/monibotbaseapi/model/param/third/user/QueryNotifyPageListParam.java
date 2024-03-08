package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyByProjectID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-26 10:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryNotifyPageListParam {
    /**
     * 公司ID
     */
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须是正值")
    private Integer companyID;
    /**
     * 页大小，范围为[1-100]
     */
    @NotNull(message = "页大小不能为空")
    @Range(min = 1, max = 100, message = "页大小，范围为[1-100]")
    private Integer pageSize;
    /**
     * 当前页，从1开始
     */
    @NotNull(message = "不能为空")
    @Min(value = 1, message = "当前页不能小于1")
    private Integer currentPage;
    /**
     * 类型，1.报警 2.事件 3.信息
     */
    @Range(min = 1, max = 3, message = "类型，1.报警 2.事件 3.信息")
    private Integer type;
    /**
     * 服务ID
     */
    private Integer serviceID;
    /**
     * 超级搜索，对名称、内容联合查询，支持模糊查询
     */
    private String queryKey;
    /**
     * 通知状态 0.未读 1.已读 2.待办 null.全部
     */
    @Range(min = 0, max = 2, message = "通知状态 0.未读 1.已读 2.待办 null.全部")
    private Integer status;
    /**
     * 开始时间
     */
    private Date begin;
    /**
     * 结束时间
     */
    private Date end;
    /**
     * 创建时间排序 0.降序（默认） 1.升序
     */
    private int timeOrder;
    private List<Integer> notifyIDList;
}
