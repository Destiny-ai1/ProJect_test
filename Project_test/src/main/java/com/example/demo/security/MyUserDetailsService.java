package com.example.demo.security;



import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

import com.example.demo.member.Member;
import com.example.demo.member.MemberDao;

import lombok.*;

// 사용자 정보를 UserDetails 객체로 만들어 스프링 시큐리티에 넘긴다
@RequiredArgsConstructor
@Component
public class MyUserDetailsService implements UserDetailsService {
	private final MemberDao memberDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Member m=memberDao.findById(username).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을수 없습니다."));
		return User.builder().username(m.getUsername()).password(m.getPassword()).accountLocked(!m.getEnabled())
				.roles(m.getRole().name()).build();
	}
}


