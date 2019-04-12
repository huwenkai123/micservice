package com.school.course.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.school.course.dto.CourseDto;
import com.school.course.service.ICourseService;
import com.school.thrift.user.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {

    @Reference
    private ICourseService iCourseService;

    @RequestMapping(value = "/courserList")
    @ResponseBody
    public List<CourseDto> courseDtoList(HttpServletRequest request) {
        UserDto user =  (UserDto) request.getAttribute("user");
        System.out.println(user.toString());
        return iCourseService.courseList();
    }
}
