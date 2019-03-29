package com.imooc.user.controller;

import com.imooc.thrift.user.UserInfo;
import com.imooc.user.dto.UserDto;
import com.imooc.user.redis.RedisClient;
import com.imooc.user.response.LoginResponse;
import com.imooc.user.response.Response;
import com.imooc.user.thrift.ServiceProvider;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.MessageDigest;
import java.util.Random;

@Controller
public class UserController {
    @Autowired
    private ServiceProvider serviceProvider;
    @Autowired
    private RedisClient redisClient;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Response login(@RequestParam("username")String username, @RequestParam("password") String password) {
        //1. 验证用户名密码　2.生成token 3.缓存用户
        UserInfo userInfo = null;
        try {
            userInfo = serviceProvider.getUserService().getUserByName(username);
        }catch (TException r) {
            r.printStackTrace();
            return Response.USERNAME_PASSWORD_INALID;
        }
        if (userInfo == null) {
            return Response.USERNAME_PASSWORD_INALID;
        }
        if (!userInfo.getPassword().equals(md5(password))) {
            return Response.USERNAME_PASSWORD_INALID;
        }
        String token = genToken();
        redisClient.set(token, toDto(userInfo), 3600);
        return new LoginResponse(token);

    }

    private UserDto toDto(UserInfo userInfo) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDto, userInfo);
        return userDto;
    }


    private String genToken() {
        return randomCode("23", 32);
    }

    private String randomCode(String s, int size) {
        StringBuilder result = new StringBuilder(size);
        Random random = new Random();
        for (int i = 0; i<size; i++) {
            int loc = random.nextInt(s.length());
            result.append(s.charAt(loc));
        }
        return result.toString();
    }


    private String md5(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5 = messageDigest.digest(password.getBytes("utf-8"));
            return HexUtils.toHexString(md5);
        } catch (Exception e) {
            return null;
        }
    }
}
