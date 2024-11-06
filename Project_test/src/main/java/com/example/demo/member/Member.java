package com.example.demo.member;

import java.time.*;

import org.springframework.context.support.BeanDefinitionDsl.*;

import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
public class Member {
	@Id
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
