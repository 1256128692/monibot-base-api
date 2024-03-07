package cn.shmedo.monitor.monibotbaseapi.model.param.notify;

import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbNotifyRelation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Chengfs on 2024/3/7
 */
@Data
public class AddNotifyRelationRequest implements ResourcePermissionProvider<Resource> {

    @Positive
    @NotNull
    private Integer companyID;

    @NotEmpty
    @Valid
    private Set<@NotNull Item> dataList;

    private record Item(@Positive @NotNull Integer notifyID,
                        @Positive @NotNull Integer relationID,
                        @Positive @Range(min = 1, max = 4) Integer type) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Item item = (Item) obj;
            return notifyID.equals(item.notifyID) && relationID.equals(item.relationID) && type.equals(item.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(notifyID, relationID, type);
        }
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }

    public List<TbNotifyRelation> toNotifyRelationList() {
        return this.dataList.stream()
                .filter(Objects::nonNull).map(item -> {
                    TbNotifyRelation tbNotifyRelation = new TbNotifyRelation();
                    tbNotifyRelation.setNotifyID(item.notifyID);
                    tbNotifyRelation.setRelationID(item.relationID);
                    tbNotifyRelation.setType(item.type);
                    return tbNotifyRelation;
                }).toList();
    }
}