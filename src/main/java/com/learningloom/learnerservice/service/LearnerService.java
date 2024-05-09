package com.learningloom.learnerservice.service;

import com.learningloom.learnerservice.dto.LearnerDto;
import com.learningloom.learnerservice.entity.Course;
import com.learningloom.learnerservice.entity.CourseInfo;
import com.learningloom.learnerservice.entity.Learner;

import java.util.List;
import java.util.Map;

public interface LearnerService {

    Learner registerLearner(LearnerDto learnerDto);

     Learner getLearnerById(Long learnerId);

    void enrollCourse(Long learnerId, Long courseId);

     void cancelCourseEnrollment(Long learnerId, Long courseId);

    int getEnrolledCourseCount(Long learnerId);

     int  getCompletedCourseCount(Long learnerId);

     int getInProgressCourseCount(Long learnerId);

     List<Course> getAllCourses();

    List<Course> getInProgressCourses(Long learnerId);

    void completeCourse(Long learnerId, Long courseId);


    ///////////////////////////////////////////////////////////

    Map<Long, CourseInfo> getEnrolledCourses(Long learnerId);
    void updateAllPaymentStatus(Long learnerId);
}
