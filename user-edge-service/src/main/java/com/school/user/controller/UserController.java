package com.school.user.controller;

import com.school.thrift.user.UserInfo;
import com.school.thrift.user.dto.UserDto;
import com.school.user.redis.RedisClient;
import com.school.user.response.LoginResponse;
import com.school.user.response.Response;
import com.school.user.thrift.ServiceProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.util.Random;

@Controller
@RequestMapping("/user")
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Response register(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam(value = "mobile", required = false) String mobile,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "verifyCode") String verifyCode) {
        if (StringUtils.isEmpty(mobile) && StringUtils.isEmpty(password)) {
            return Response.MOBILE_OR_EMAIL_REQUIREO;
        }
        if (StringUtils.isNotBlank(mobile)) {
            String redisCode = redisClient.get(mobile);
            if (!verifyCode.equals(redisCode)) {
                return Response.VERIFY_CODE_INVALID;
            }
        } else if (StringUtils.isNotBlank(email)) {
            String redisCode = redisClient.get(email);
            if (!verifyCode.equals(redisCode)) {
                return Response.VERIFY_CODE_INVALID;
            }
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(md5(password));
        userInfo.setMobile(mobile);
        userInfo.setEmail(email);
        try {
            serviceProvider.getUserService().regiserUser(userInfo);
        } catch (TException e) {
            e.printStackTrace();
            return Response.expection(e);
        }
        return Response.SUCCESS;

    }





    @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST)
    @ResponseBody
    public Response sendVerifyCode(@RequestParam(value = "email", required = false) String email, @RequestParam(value = "mobile", required = false) String mobile) {
        String message = "Very code is";
        String code = randomCode("0123456789", 6);
        try {

            boolean result = false;
            if (StringUtils.isNotBlank(mobile)) {
                result = serviceProvider.getJavaMessage().sendMobileMessage(mobile, message + code);
                redisClient.set(mobile, code);
            } else if (StringUtils.isNotBlank(email)) {
                result = serviceProvider.getJavaMessage().sendEmailMessage(email,message + code);
                redisClient.set(mobile, code);
            }
            if (!result) {
                return Response.MOBILE_VERICODE;
            }
        } catch (TException e) {
            e.printStackTrace();
            return Response.expection(e);
        }
        return Response.SUCCESS;
    }

    private UserDto toDto(UserInfo userInfo) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userInfo,  userDto);
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


    @RequestMapping(value = "/authentication", method = RequestMethod.POST)
    @ResponseBody
    public UserDto authentication(@RequestHeader("token") String token) {
        return redisClient.get(token);
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

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
}
