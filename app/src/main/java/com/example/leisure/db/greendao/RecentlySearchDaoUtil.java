package com.example.leisure.db.greendao;

import com.example.leisure.greenDao.gen.RecentlySearchDao;

public class RecentlySearchDaoUtil {
    public static void insertBean(RecentlySearchDao dao, RecentlySearch bean) {
        dao.insertOrReplace(bean);
    }
}
