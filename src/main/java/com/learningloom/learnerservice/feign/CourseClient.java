package com.learningloom.learnerservice.feign;


import com.learningloom.learnerservice.entity.Course;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name =  "CourseManagementService")
public interface CourseClient {

    @GetMapping("/api/instructor/courses/{courseId}")
    Course getCourseById(@PathVariable("courseId") Long courseId);

    @GetMapping("/api/instructor/courses")
    List<Course> getAllCourses();

}
