package indi.mofan.storage.service;

import indi.mofan.storage.bean.StorageTbl;

public interface StorageService {

    /**
     * 扣除存储数量
     *
     * @param commodityCode 商品编码
     * @param count         数量
     */
    void deduct(String commodityCode, int count);
    void addBack(String commodityCode, int count);
    StorageTbl snapshot(String commodityCode);
}
