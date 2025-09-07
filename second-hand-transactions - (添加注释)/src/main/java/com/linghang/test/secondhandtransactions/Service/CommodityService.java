package com.linghang.test.secondhandtransactions.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linghang.test.secondhandtransactions.Entity.Commodity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author c'w
* @description 针对表【products(商品表)】的数据库操作Service
* @createDate 2025-07-17 14:55:55
*/
public interface CommodityService extends IService<Commodity> {

    Page<Commodity> getAllProducts(long current, long size);

    Page<Commodity> getProductsByCategory(String category, long current, long size);


    boolean updateCommodity(Commodity commodity, String uid);

    boolean checkCommodityOwner(Long commodityPid, String uid);

    Page<Commodity> getProductsByUid(String uid, long current, long size);
}
