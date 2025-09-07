package com.linghang.test.secondhandtransactions.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghang.test.secondhandtransactions.Entity.User_collection;
import com.linghang.test.secondhandtransactions.Service.UserCollectionService;
import com.linghang.test.secondhandtransactions.mapper.UserCollectionMapper;
import org.springframework.stereotype.Service;

/**
* @author c'w
* @description 针对表【user_collection(用户收藏表)】的数据库操作Service实现
* @createDate 2025-07-21 16:17:54
*/
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, User_collection>
    implements UserCollectionService{

}




