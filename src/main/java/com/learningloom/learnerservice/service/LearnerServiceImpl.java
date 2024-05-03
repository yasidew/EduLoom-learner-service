package com.learningloom.learnerservice.service;


import com.learningloom.learnerservice.dto.LearnerDto;
import com.learningloom.learnerservice.entity.Course;
import com.learningloom.learnerservice.entity.CourseInfo;
import com.learningloom.learnerservice.entity.Learner;
import com.learningloom.learnerservice.repository.LearnerRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class LearnerServiceImpl implements LearnerService{


    @Autowired
    private LearnerRepository learnerRepository;

    @Autowired
    private CourseClient courseClient;


    @Override
    public List<Course> getAllCourses(){
        return courseClient.getAllCourses();
    }


    @Override
    public Learner getLearnerById(Long learnerId) {
        return learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with ID: " + learnerId));
    }

    public Learner registerLearner(LearnerDto learnerDto){
        try{

            Learner learner = new Learner();
            learner.setId(learnerDto.getId());
            learner.setName(learnerDto.getName());
//            learner.setFirstName(learnerDto.getFirstName());
//            learner.setLastName(learnerDto.getLastName());
            learner.setEmail(learnerDto.getEmail());
            learner.setCardNumber(learnerDto.getCardNumber());
//            learner.setPassword(learnerDto.getPassword());
            learner.setEnrolledCourses(new HashMap<>());
            learner.setCompletedCourses(new HashMap<>()); // Initialize the completed courses map
            learner.setInProgressCourses(new HashMap<>()); // Initialize the in-progress courses map
            return learnerRepository.save(learner);

        }catch(Exception e){
            throw new RuntimeException("Failed to register learner", e);
        }


    }


    public void enrollCourse(Long learnerId, Long courseId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with id: " + learnerId));

        Course course = courseClient.getCourseById(courseId);
        if (course == null) {
            throw new RuntimeException("Course not found with id: " + courseId);
        }

        if (learner.getEnrolledCourses().containsKey(courseId)) {
            throw new RuntimeException("Learner is already enrolled in the course");
        }

        CourseInfo courseInfo = new CourseInfo(course.getName(), course.getCoursePrice(), "Not Paid");
        learner.getEnrolledCourses().put(courseId, courseInfo);
        learner.getInProgressCourses().put(courseId, course.getName()); // Add the course to the in-progress courses map
        learnerRepository.save(learner);
    }

    public void cancelCourseEnrollment(Long learnerId, Long courseId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with id: " + learnerId));

        if (!learner.getEnrolledCourses().containsKey(courseId)) {
            throw new RuntimeException("Learner is not enrolled in the course");
        }

        learner.getEnrolledCourses().remove(courseId);
        learnerRepository.save(learner);
    }

    @Override
    public int getEnrolledCourseCount(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with ID: " + learnerId));
        return learner.getEnrolledCourses().size(); // Return the size of the enrolled courses map
    }

    @Override
    public int getCompletedCourseCount(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with ID: " + learnerId));
        return learner.getCompletedCourses().size(); // Return the size of the completed courses map
    }

    @Override
    public int getInProgressCourseCount(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with ID: " + learnerId));
        return learner.getInProgressCourses().size(); // Return the size of the in-progress courses map
    }


    public void updateCourseProgress(Long learnerId, Long courseId, boolean isCompleted) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with id: " + learnerId));

        // Ensure learner is enrolled in the course
        if (!learner.getEnrolledCourses().containsKey(courseId)) {
            throw new RuntimeException("Learner is not enrolled in the course");
        }

        // Update course progress based on completion status
        String courseName = learner.getEnrolledCourses().get(courseId).getName();

        // Update course progress based on completion status
        if (isCompleted) {
            // Remove from in-progress and add to completed
            learner.getInProgressCourses().remove(courseId);
            learner.getCompletedCourses().put(courseId, courseName);
        } else {
            // Remove from completed and add to in-progress
            learner.getCompletedCourses().remove(courseId);
            learner.getInProgressCourses().put(courseId, courseName);
        }

        // Save the updated learner information
        learnerRepository.save(learner);
    }



//    public void updatePaymentStatus(Long learnerId, Long courseId) {
//        Learner learner = learnerRepository.findById(learnerId)
//                .orElseThrow(() -> new RuntimeException("Learner not found with id: " + learnerId));
//
//        if (!learner.getEnrolledCourses().containsKey(courseId)) {
//            throw new RuntimeException("Learner is not enrolled in the course");
//        }
//
//        CourseInfo courseInfo = learner.getEnrolledCourses().get(courseId);
//        courseInfo.setPaymentStatus("Paid"); // Update the payment status to "Paid"
//
//        // Save the updated learner information
//        learnerRepository.save(learner);
//    }




}
