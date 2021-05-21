package cn.ccwisp.tcm.service;

import cn.ccwisp.tcm.dto.profile.PersonalInfoDto;
import cn.ccwisp.tcm.generated.domain.Fms;
import cn.ccwisp.tcm.generated.domain.UmsUser;
import cn.ccwisp.tcm.generated.domain.UmsUserinfo;
import cn.ccwisp.tcm.generated.service.impl.FmsServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.UmsUserServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.UmsUserinfoServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AccountService {
    private final UmsUserinfoServiceImpl userinfoService;
    private final UmsUserServiceImpl userService;
    private final FmsServiceImpl fmsService;
    private final RedisService redisService;

    public PersonalInfoDto mappingUserinfoDto(UmsUserinfo userinfo) {
        PersonalInfoDto personalInfoDto = new PersonalInfoDto();
        personalInfoDto.setNickname(userinfo.getNickname());
        personalInfoDto.setRealname(userinfo.getRealname());
        personalInfoDto.setEmail(userinfo.getEmail());
        personalInfoDto.setQq(userinfo.getQq());
        personalInfoDto.setTel(userinfo.getPhone());
        personalInfoDto.setIntro(userinfo.getIntro());
        personalInfoDto.setAvatar(userinfo.getAvatar());
        return personalInfoDto;
    }

    public AccountService(UmsUserinfoServiceImpl userinfoService, UmsUserServiceImpl userService, FmsServiceImpl fmsService, RedisService redisService) {
        this.userinfoService = userinfoService;
        this.userService = userService;
        this.fmsService = fmsService;
        this.redisService = redisService;
    }

    public UmsUserinfo getUserInfoByUserId(int id) {
        QueryWrapper<UmsUserinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", id);
        return userinfoService.getOne(queryWrapper);
    }

    public Map<Integer, Object> getUserPublished(int id) {
        QueryWrapper<Fms> queryWrapper = new QueryWrapper<Fms>()
                .eq("authorId", id)
                .eq("enabled", 1);
        List<Fms> list = fmsService.list(queryWrapper);
        Map<Integer, Object> result = new HashMap<>();
        for (Fms fms : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", fms.getId());
            map.put("createTime", fms.getCreatetime());
            map.put("modifyTime", fms.getModifytime());
            map.put("title", fms.getTitle());
            map.put("categoryTitle", fms.getCategorytitle());
            result.put(fms.getId(),map);
        }
        return result;
    }

    public Map<Integer, Object> getUserFavourite(int id) {
        Set<Integer> integers = redisService.GetUserFavThreadId(id);
        if (integers == null || integers.size() == 0)
            return new HashMap<>();
        QueryWrapper<Fms> queryWrapper = new QueryWrapper<Fms>()
                .in("id", integers);
        List<Fms> list = fmsService.list(queryWrapper);
        Map<Integer, Object> result = new HashMap<>();
        for (Fms fms : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", fms.getId());
            map.put("createTime", fms.getCreatetime());
            map.put("modifyTime", fms.getModifytime());
            map.put("title", fms.getTitle());
            map.put("categoryTitle", fms.getCategorytitle());
            result.put(fms.getId(),map);
        }
        return result;
    }

//    public Map<Integer, Object> getUserHistory(int id) {
//
//    }

    public PersonalInfoDto updateUserInfo(UmsUserinfo userinfo) {
        userinfoService.updateById(userinfo);
        return mappingUserinfoDto(userinfo);
    }
    public UmsUser GetUserById(int id) {
        return userService.getById(id);
    }
    public boolean UpdatePassword(UmsUser user) {
        return userService.updateById(user);
    }

    public String GetAvatar(int id) {
        UmsUserinfo userId = userinfoService.getOne(new QueryWrapper<UmsUserinfo>()
                .eq("userId", id));
        return userId.getAvatar();
    }
}
