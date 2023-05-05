package cn.shmedo.monitor.monibotbaseapi.dal.redis;

public interface YsTokenDao {
    /**
     * 将YS TOKEN存储到REDIS中
     *
     * @param ysToken
     */
    void setToken(String ysToken);

    /**
     * 从REDIS中取得TOKEN
     *
     * @return
     */
    String getToken();
}
