package cn.shmedo.monitor.monibotbaseapi.util.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YsPageInfo {

    private Integer total;
    private Integer size;
    private Integer page;
}
