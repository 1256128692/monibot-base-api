package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryProjectListParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface TbProjectInfoMapper extends BaseMapper<TbProjectInfo> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectInfo record);

    int insertSelective(TbProjectInfo record);

    TbProjectInfo selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectInfo record);

    int updateByPrimaryKey(TbProjectInfo record);

    /**
     * 连表查询项目工程列表信息
     * @return
     */
    List<TbProjectInfo> getProjectInfoList(QueryProjectListParam param);

    void updateCompanyID(Integer projectID, Integer companyID, Integer userID, Date date);

    void updateExpiryDate(Integer projectID, Date newExpiryDate, Integer userID, Date date);

    void deleteProjectList(List ids);
}