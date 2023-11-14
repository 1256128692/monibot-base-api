package cn.shmedo.monitor.monibotbaseapi.dal.redis;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCompany;

import java.util.List;

public interface RedisCompanyInfoDao {


    List<TbCompany> selectAllCompanyInfoList(List<Integer> companyIDList);

    TbCompany selectCompanyInfo(Integer companyID);
}
