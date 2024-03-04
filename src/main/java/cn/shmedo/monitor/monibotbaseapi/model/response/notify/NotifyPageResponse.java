package cn.shmedo.monitor.monibotbaseapi.model.response.notify;

import cn.shmedo.monitor.monibotbaseapi.model.response.third.auth.NotifyPageInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;

/**
 * @Author wuxl
 * @Date 2024/3/4 11:55
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.notify
 * @ClassName: NotifyPageResponse
 * @Description: TODO
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotifyPageResponse extends NotifyPageInfo {
    private Integer serviceID;
    private String serviceName;
    private Integer relationID;
    private Integer relationType;

    public record Page<T>(long totalPage, Collection<T> currentPageData, long totalCount, long unReadCount) {

        public static <E> PageUtil.Page<E> empty() {
            return new PageUtil.Page<>(0, Collections.emptyList(), 0);
        }
    }
}
