package com.school.course.dto;

import com.school.thrift.user.dto.TeacherDto;

import java.io.Serializable;

public class CourseDto implements Serializable {
    private int id;
    private String title;
    private String description;
    private TeacherDto teacherDto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TeacherDto getTeacherDto() {
        return teacherDto;
    }

    public void setTeacherDto(TeacherDto teacherDto) {
        this.teacherDto = teacherDto;
    }
}
