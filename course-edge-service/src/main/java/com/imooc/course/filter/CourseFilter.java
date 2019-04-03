package com.imooc.course.filter;

import com.imooc.thrift.user.dto.UserDto;
import com.imooc.user.client.LoginFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CourseFilter extends LoginFilter {
    @Override
    protected void login(HttpServletRequest request, HttpServletResponse response, UserDto userDto) {
        request.setAttribute("user", userDto);
    }
}
