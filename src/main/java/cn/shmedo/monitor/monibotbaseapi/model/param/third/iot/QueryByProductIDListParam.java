package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-09 09:45
 */
public class QueryByProductIDListParam {
    private List<Integer> productIDList;

    public QueryByProductIDListParam(List<Integer> productIDList) {
        this.productIDList = productIDList;
    }

    public QueryByProductIDListParam() {
    }

    public List<Integer> getProductIDList() {
        return productIDList;
    }

    public void setProductIDList(List<Integer> productIDList) {
        this.productIDList = productIDList;
    }
}
