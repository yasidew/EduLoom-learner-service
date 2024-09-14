package com.learningloom.learnerservice.controller;


import com.learningloom.learnerservice.dto.LearnerDto;
import com.learningloom.learnerservice.entity.Course;
import com.learningloom.learnerservice.entity.CourseInfo;
import com.learningloom.learnerservice.entity.Learner;
import com.learningloom.learnerservice.service.impl.LearnerServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
//import org.springframework.security.web.csrf.CsrfToken;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("api/v1/learners")
public class LearnerController {

    @Autowired
    private LearnerServiceImpl learnerService;

//    @GetMapping("/csrf-token")
//    public Map<String, String> getCsrfToken(HttpServletRequest request) {
//        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//        return Collections.singletonMap("token", csrfToken.getToken());
//    }
    @PostMapping("/register")
    public ResponseEntity<Learner> registerLearner(@Valid @RequestBody LearnerDto learnerDto ){
        // Sanitize user input AFTER validation
        learnerDto.setName(HtmlUtils.htmlEscape(learnerDto.getName()));
        learnerDto.setEmail(HtmlUtils.htmlEscape(learnerDto.getEmail()));
        learnerDto.setCardNumber(HtmlUtils.htmlEscape(learnerDto.getCardNumber()));

        Learner registeredLearner = learnerService.registerLearner(learnerDto);
        return new ResponseEntity<>(registeredLearner, HttpStatus.CREATED);
    }



    @GetMapping("/{learnerId}")
    public ResponseEntity<Learner> getLearnerById(@PathVariable Long learnerId) {
        try{
            if(learnerId == null){
                throw new IllegalArgumentException("Learner ID cannot be null");
            }
            Learner learner = learnerService.getLearnerById(learnerId);
            return ResponseEntity.ok(learner);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{learnerId}/enroll/{courseId}")
    public ResponseEntity<String> enrollCourse(@PathVariable Long learnerId, @PathVariable Long courseId){
        try{
            if(learnerId == null || courseId == null){
                throw new IllegalArgumentException("Learner ID and Course ID cannot be null");
            }
            learnerService.enrollCourse(learnerId, courseId);
            return ResponseEntity.ok().build();

        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{learnerId}/unenroll/{courseId}")
    public ResponseEntity<Void> cancelCourseEnrollment(@PathVariable Long learnerId, @PathVariable Long courseId){
        try{
            if(learnerId == null || courseId == null){
                throw new IllegalArgumentException(" Learner ID and Course ID cannot be null");
            }
            learnerService.cancelCourseEnrollment(learnerId, courseId);
            return ResponseEntity.ok().build();

        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/{learnerId}/enrolledCoursesCount")
    public ResponseEntity<Integer> getEnrolledCoursesCount(@PathVariable Long learnerId) {
        try {
            if (learnerId == null) {
                return ResponseEntity.badRequest().body(null);
            }

            int enrolledCoursesCount = learnerService.getEnrolledCourseCount(learnerId);
            return ResponseEntity.ok(enrolledCoursesCount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{learnerId}/progress")
    public ResponseEntity<Map<String, Object>> getLearnerProgress(@PathVariable Long learnerId) {
        try {
            if (learnerId == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Learner ID cannot be null"));
            }

            int completedCourses = learnerService.getCompletedCourseCount(learnerId);
            int inProgressCourses = learnerService.getInProgressCourseCount(learnerId);
            int enrolledCourses = learnerService.getEnrolledCourseCount(learnerId);

            Map<String, Object> progressMap = new HashMap<>();
            progressMap.put("completedCourses", completedCourses);
            progressMap.put("inProgressCourses", inProgressCourses);
            progressMap.put("enrolledCourses", enrolledCourses);

            return ResponseEntity.ok(progressMap);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred"));
        }
    }

    @GetMapping("/allCourses")
    public ResponseEntity<Object> getAllCourses() {
        try {
            return ResponseEntity.ok(learnerService.getAllCourses());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred"));
        }
    }


    @PutMapping("/{learnerId}/courses/{courseId}/progress")
    public ResponseEntity<Void> updateCourseProgress(@PathVariable Long learnerId, @PathVariable Long courseId, @RequestParam boolean isCompleted) {
        try {
            if (learnerId == null || courseId == null) {
                throw new IllegalArgumentException("Learner ID and Course ID cannot be null");
            }
            learnerService.updateCourseProgress(learnerId, courseId, isCompleted);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{learnerId}/inProgressCourses")
    public ResponseEntity<List<Course>> getInProgressCourses(@PathVariable Long learnerId) {
        try {
            if (learnerId == null) {
                return ResponseEntity.badRequest().body(null);
            }

            List<Course> inProgressCourses = learnerService.getInProgressCourses(learnerId);
            return ResponseEntity.ok(inProgressCourses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{learnerId}/complete/{courseId}")
    public ResponseEntity<Void> completeCourse(@PathVariable Long learnerId, @PathVariable Long courseId){
        try{
            if(learnerId == null || courseId == null){
                throw new IllegalArgumentException("Learner ID and Course ID cannot be null");
            }
            learnerService.completeCourse(learnerId, courseId);
            return ResponseEntity.ok().build();

        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }



    @PutMapping("/updatePaymentStatus/{learnerId}")
    public void updatePaymentStatus(@PathVariable Long learnerId) {
        learnerService.updateAllPaymentStatus(learnerId);
    }


    @GetMapping("/{learnerId}/courses")
    public ResponseEntity<Map<Long, CourseInfo>> getEnrolledCourses(@PathVariable Long learnerId) {
        Map<Long, CourseInfo> enrolledCourses = learnerService.getEnrolledCourses(learnerId);
        if (enrolledCourses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(enrolledCourses);
        }
    }



}
