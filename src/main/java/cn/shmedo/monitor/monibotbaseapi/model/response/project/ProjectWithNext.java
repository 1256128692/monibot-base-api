package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.AuthService;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-12-01 18:33
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectWithNext extends TbProjectInfo {
    private List<AuthService> serviceList;
    private List<ProjectWithNext> downLevelProjectList;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProjectInfo that = (ProjectInfo) o;
        return Objects.equals(super.getID(), that.getID());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
