package com.example.demo.member;

import java.time.*;

import org.springframework.context.support.BeanDefinitionDsl.*;

import com.example.demo.enums.*;
import com.example.demo.enums.Role;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {
	private String username;
	private String password;
	private String name;
	private String email;
	private LocalDate joinday;
	private long totalpurchase;
	private Grade grade;
	private Integer loginFailCount;
	private Boolean enabled;
	private Role role;
}
