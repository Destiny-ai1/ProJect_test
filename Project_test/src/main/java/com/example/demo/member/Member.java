package com.example.demo.member;

import java.time.*;

import org.hibernate.annotations.DynamicUpdate;

import com.example.demo.enums.*;
import com.example.demo.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
public class Member {
	private String username;
	private String password;
	private String name;
	private String email;
	private String phone_number;
	private LocalDateTime joinday;
	private int totalpurchase;
	@Enumerated(EnumType.STRING)
	private Grade grade;
	private Integer loginFailCount;
	private Boolean enabled;
	@Enumerated(EnumType.STRING)
	private Role role;
	private int point;
	
	//패스워드 
}
