package cn.shmedo.monitor.monibotbaseapi.util.base;



import cn.shmedo.monitor.monibotbaseapi.model.exception.InvalidParameterException;

import java.util.List;

/**
 * Created by Liudongdong on 18/1/19.
 */
public class PageUtil {
    public static <T> PageResult<T> page(List<T> data, int pageSize, int currentPage) {
        if (currentPage < 1)
            throw new InvalidParameterException("当前页不能小于1");
        if (pageSize < 1)
            throw new InvalidParameterException("页大小不能小于1");
        if (CollectionUtil.isNullOrEmpty(data))
            return PageResult.empty();
        int totalPage = (data.size() - 1) / pageSize + 1;
        int beginIndex = (currentPage - 1) * pageSize;
        if (beginIndex >= data.size())
            throw new InvalidParameterException("当前页超出总页数");
        int endIndex = currentPage * pageSize > data.size() ?
                data.size() : (currentPage * pageSize);
        List<T> tempData = data.subList(beginIndex, endIndex);
        return new PageResult<>(totalPage, tempData, data.size());
    }

    public static int totalPage(int totalCount, int pageSize) {
        if (totalCount <= 0)
            return 0;
        if (pageSize < 1)
            throw new InvalidParameterException("非法的页大小参数：" + Integer.toString(pageSize));
        return (totalCount - 1) / pageSize + 1;
    }

    public static class PageResult<T> {
        private int totalCount;
        private int totalPage;
        private List<T> currentPageData;

        public PageResult(int totalPage, List<T> currentPageData, int totalCount) {
            this.totalPage = totalPage;
            this.currentPageData = currentPageData;
            this.totalCount = totalCount;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<T> getCurrentPageData() {
            return currentPageData;
        }

        public void setCurrentPageData(List<T> currentPageData) {
            this.currentPageData = currentPageData;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public static PageResult empty() {
            return new PageResult<>(0, null, 0);
        }
    }
}
