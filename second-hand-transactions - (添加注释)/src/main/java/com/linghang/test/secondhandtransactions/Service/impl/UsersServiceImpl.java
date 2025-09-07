package com.linghang.test.secondhandtransactions.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghang.test.secondhandtransactions.Entity.Commodity;
import com.linghang.test.secondhandtransactions.Entity.User_collection;
import com.linghang.test.secondhandtransactions.Entity.Users;
import com.linghang.test.secondhandtransactions.Service.CommodityService;
import com.linghang.test.secondhandtransactions.Service.UsersService;
import com.linghang.test.secondhandtransactions.Mapper.UsersMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
* @author c'w
* @description 针对表【users】的数据库操作Service实现
* @createDate 2025-07-17 16:22:35
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

    @Resource
    private UsersMapper usersMapper;
    @Resource
    private CommodityService commodityService;
    @Override
    public Users usersVFindByUid(String uid) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return usersMapper.selectOne(queryWrapper) ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCollection(String uid, Long pid) {
        // 校验商品是否存在
        Commodity commodity = commodityService.getById(pid);
        if (commodity == null) {
            return false;
        }

        // 检查是否已收藏
        List<User_collection> existing = usersMapper.selectUserCollections(uid);
        boolean exists = existing.stream()
                .anyMatch(c -> c.getPid().equals(pid));

        if (exists) {
            return false;
        }

        // 插入收藏记录
        int rows = usersMapper.insertCollection(uid, pid);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeCollection(String uid, Long pid) {
        int rows = usersMapper.deleteCollection(uid, pid);
        return rows > 0;
    }

    @Override
    public boolean findUid(String uid) {
        // 创建查询条件构造器，指定查询uid字段
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid); // 条件：uid字段等于传入的uid值

        // 根据条件查询唯一记录（因为uid有唯一约束，最多只有一条条记录）
        Users user = usersMapper.selectOne(queryWrapper);

        // 如果查询结果不为null，说明uid已存在，返回true；否则返回false
        return user != null;
    }


    @Override
    public List<User_collection> listByUserId(String userId) {
        return usersMapper.selectUserCollections(userId);
    }
}




