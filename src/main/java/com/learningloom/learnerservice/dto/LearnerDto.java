package com.learningloom.learnerservice.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearnerDto {

    private Long id;
//    private Long userId;
    private String name;

    private String email;

    private String cardNumber;


}
