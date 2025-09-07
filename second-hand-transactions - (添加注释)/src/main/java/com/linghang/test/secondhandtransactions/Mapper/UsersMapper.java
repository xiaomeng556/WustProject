package com.linghang.test.secondhandtransactions.Mapper;

import com.linghang.test.secondhandtransactions.Entity.User_collection;
import com.linghang.test.secondhandtransactions.Entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author c'w
* @description 针对表【users】的数据库操作Mapper
* @createDate 2025-07-17 16:22:36
* @Entity com.linghang.test.secondhandtransactions.Entity.Users
*/
public interface UsersMapper extends BaseMapper<Users> {

    List<User_collection> selectUserCollections(String uid);

    int insertCollection(String uid, Long pid);

    int deleteCollection(String uid, Long pid);
}




