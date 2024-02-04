package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryUserContactParam {

    private Collection<Integer> depts;
    private Collection<Integer> users;
    private Collection<Integer> roles;
}
