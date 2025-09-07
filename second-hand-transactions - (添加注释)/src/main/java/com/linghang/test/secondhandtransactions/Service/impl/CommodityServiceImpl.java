package com.linghang.test.secondhandtransactions.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghang.test.secondhandtransactions.Entity.Commodity;
import com.linghang.test.secondhandtransactions.Service.CommodityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author c'w
* @description 针对表【products(商品表)】的数据库操作Service实现
* @createDate 2025-07-17 14:55:55
*/
@Service
public class CommodityServiceImpl extends ServiceImpl<com.linghang.test.secondhandtransactions.Mapper.CommodityMapper, Commodity>
    implements CommodityService {
    @Override
    //按类别筛选商品
    public Page<Commodity> getProductsByCategory(String   status, long current, long size) {
        LambdaQueryWrapper<Commodity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Commodity::getStatus,status)// 添加类别筛选条件
                .orderByDesc(Commodity::getDate);// 按创建时间降序排列
        return page(new Page<>(current, size), queryWrapper);// 执行分页查询
    }



    @Override
    public boolean updateCommodity(Commodity commodity, String uid) {
        // 构建更新条件：pid相等且uid属于当前用户
        UpdateWrapper<Commodity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("pid", commodity.getPid())
                .eq("uid", uid);

        // 设置需要更新的实体数据
        return this.update(commodity, updateWrapper);
    }

    @Override
    public boolean checkCommodityOwner(Long commodityPid, String uid) {
        // 根据 pid 查询商品，校验 uid 是否匹配
        Commodity commodity = getById(commodityPid);
        return commodity != null && uid.equals(commodity.getUid());
    }

    @Override
    public Page<Commodity> getProductsByUid(String uid, long current, long size) {
        LambdaQueryWrapper<Commodity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Commodity::getUid,uid);
        return page(new Page<>(current, size), queryWrapper);
    }




    @Override
    //获取全部商品
    public Page<Commodity> getAllProducts(long current, long size) {
        LambdaQueryWrapper<Commodity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Commodity::getDate);// 按创建时间降序排列
        return page(new Page<>(current, size), queryWrapper);// 执行分页查询

    }

}




