package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * @author Chengfs on 2023/5/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryUserBatchRequest {

    private Integer companyID;

    private Collection<Integer> userIDList;

    private String account;

    private String name;

    private String cellPhone;

    private List<String> excludeGroupNameList;

    private Integer deptID;
}