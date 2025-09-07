package com.linghang.test.secondhandtransactions.Service;

import com.linghang.test.secondhandtransactions.Entity.User_collection;
import com.linghang.test.secondhandtransactions.Entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author c'w
* @description 针对表【users】的数据库操作Service
* @createDate 2025-07-17 16:22:36
*/
public interface UsersService extends IService<Users> {

    Users usersVFindByUid(String uid);

    boolean addCollection(String uid, Long pid);

    List<User_collection> listByUserId(String userId);

    boolean removeCollection(String uid, Long pid);

    boolean findUid(String uid);
}
