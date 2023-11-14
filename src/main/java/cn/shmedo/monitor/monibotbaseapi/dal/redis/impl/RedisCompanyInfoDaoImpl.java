package cn.shmedo.monitor.monibotbaseapi.dal.redis.impl;

import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.RedisCompanyInfoDao;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class RedisCompanyInfoDaoImpl implements RedisCompanyInfoDao {

    private RedisTemplate rawTemplate;

    @Autowired
    public RedisCompanyInfoDaoImpl(RedisTemplate rawTemplate) {
        this.rawTemplate = rawTemplate;
    }


    @Override
    public List<TbCompany> selectAllCompanyInfoList(List<Integer> companyIDList) {
        HashOperations<String, String, String> hashOperations = rawTemplate.opsForHash();
        List<String> values = hashOperations.values(RedisKeys.COMPANY_ID_KEY);
        List<TbCompany> companyList = new LinkedList<>();
        values.forEach(item -> {
            TbCompany company = JSONUtil.toBean(item, TbCompany.class);
            if (companyIDList.contains(company.getId())) {
                companyList.add(company);
            }
        });
        return companyList;
    }

    @Override
    public TbCompany selectCompanyInfo(Integer companyID) {
        HashOperations<String, String, String> hashOperations = rawTemplate.opsForHash();
        String s = hashOperations.get(RedisKeys.COMPANY_ID_KEY, companyID.toString());
        return JSONUtil.toBean(s, TbCompany.class);
    }


}
