package com.school.course.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.school.course.dto.CourseDto;
import com.school.course.mapper.CourseMapper;
import com.school.thrift.user.UserInfo;
import com.school.thrift.user.dto.TeacherDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ServiceProvider serviceProvider;

    @Override
    public List<CourseDto> courseList() {
        List<CourseDto> courseDtos = courseMapper.listCourse();
        for (CourseDto courseDto : courseDtos) {
            Integer teacherId = courseMapper.getCourseTeacher(courseDto.getId());
            if (teacherId!=null) {
                try {
                    UserInfo userInfo = serviceProvider.getUserService().getTeacherById(teacherId);
                    courseDto.setTeacherDto(tran2Teacher(userInfo));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }
        return courseDtos;
    }

    private TeacherDto tran2Teacher(UserInfo userInfo) {
        TeacherDto teacherDto = new TeacherDto();
        BeanUtils.copyProperties(userInfo, teacherDto);
        return teacherDto;
    }
}
