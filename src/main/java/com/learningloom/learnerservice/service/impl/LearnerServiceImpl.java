package com.learningloom.learnerservice.service.impl;


import com.learningloom.learnerservice.dto.LearnerDto;
import com.learningloom.learnerservice.entity.Course;
import com.learningloom.learnerservice.entity.CourseInfo;
import com.learningloom.learnerservice.entity.Learner;
import com.learningloom.learnerservice.entity.Notification;
import com.learningloom.learnerservice.feign.CourseClient;
import com.learningloom.learnerservice.feign.NotificationClient;
import com.learningloom.learnerservice.repository.LearnerRepository;
import com.learningloom.learnerservice.service.LearnerService;
import com.learningloom.learnerservice.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LearnerServiceImpl implements LearnerService {


    @Autowired
    private LearnerRepository learnerRepository;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private NotificationClient notificationClient;


    @Override
    public List<Course> getAllCourses(){
        return courseClient.getAllCourses();
    }


//    @Override
//    public Learner getLearnerById(Long learnerId) {
//        return learnerRepository.findById(learnerId)
//                .orElseThrow(() -> new RuntimeException("Learner not found with ID: " + learnerId));
//    }

    @Override
    public Learner getLearnerById(Long learnerId) {
        Learner learner =  learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with ID: " + learnerId));
        try{
            String decryptedCardNumber = EncryptionUtil.decrypt(learner.getCardNumber());
            String maskedCardNumber =  decryptedCardNumber.substring(0, decryptedCardNumber.length()-4).replaceAll(".", "*")
                    +decryptedCardNumber.substring(decryptedCardNumber.length()-4);
            learner.setCardNumber(maskedCardNumber);
        }catch(Exception e){
            throw new RuntimeException("Failed to decrypt card number", e);
        }
        return learner;
    }

    public Learner registerLearner(LearnerDto learnerDto){
        try{

            Learner learner = new Learner();
            learner.setId(learnerDto.getId());
            learner.setName(learnerDto.getName());
            learner.setEmail(learnerDto.getEmail());
            String encryptedCardNumber = EncryptionUtil.encrypt(learnerDto.getCardNumber());
            learner.setCardNumber(encryptedCardNumber);
//            learner.setCardNumber(learnerDto.getCardNumber());
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

        // Send notification to the learner
        try {
            Notification notification = new Notification();
            notification.setToEmail(learner.getEmail());
            notification.setCourseId(courseId.toString());
            notification.setCourseName(course.getName());

            notificationClient.sendEmail(notification);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    public void cancelCourseEnrollment(Long learnerId, Long courseId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with id: " + learnerId));

        if (!learner.getEnrolledCourses().containsKey(courseId)) {
            throw new RuntimeException("Learner is not enrolled in the course");
        }

        learner.getEnrolledCourses().remove(courseId);
        learner.getInProgressCourses().remove(courseId);
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

    public List<Course> getInProgressCourses(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with ID: " + learnerId));
        return learner.getInProgressCourses().entrySet().stream()
                .map(entry -> convertToCourse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Course convertToCourse(Long courseId, String courseName) {
        Course course = new Course();
        course.setId(courseId);
        course.setName(courseName);
        // As the price is not available in the inProgressCourses map, we can't set it here
        return course;
    }

    public void completeCourse(Long learnerId, Long courseId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with id: " + learnerId));

        if (!learner.getEnrolledCourses().containsKey(courseId)) {
            throw new RuntimeException("Learner is not enrolled in the course");
        }

        String courseName = learner.getEnrolledCourses().get(courseId).getName();
        learner.getInProgressCourses().remove(courseId);
        learner.getCompletedCourses().put(courseId, courseName);
        learnerRepository.save(learner);
    }


    /////////////////////////////////////////////////////////////////////////////

    @Override
    public Map<Long, CourseInfo> getEnrolledCourses(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId).orElse(null);
        if (learner != null) {
            return learner.getEnrolledCourses();
        } else {
            return null;
        }
    }

    public void updateAllPaymentStatus(Long learnerId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found with id: " + learnerId));
        learner.getEnrolledCourses().forEach((courseId, courseInfo) -> courseInfo.setPaymentStatus("Paid"));
        learnerRepository.save(learner);
    }

}
