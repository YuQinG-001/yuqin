package com.yuqin.meinian.api.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.yuqin.meinian.api.service.SysUserService;
import com.yuqin.meinian.api.db.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
* @author YuQin
* @description 针对表【sys_user(系统用户表)】的数据库操作Service实现
* @createDate 2026-04-02 20:29:13
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserEntity>
    implements SysUserService{

}




