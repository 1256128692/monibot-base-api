package cn.shmedo.monitor.monibotbaseapi.model.response.userLog;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserLog;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Chengfs on 2024/2/20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryUserLogResult {

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private List<TbUserLog> dataList;

}