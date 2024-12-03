package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

import com.example.demo.member.Member;
import com.example.demo.member.MemberDao;

import lombok.*;


@RequiredArgsConstructor
@Component
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
    private MemberDao memberDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DAO를 사용하여 사용자 정보 조회
        Member member = memberDao.findById(username);
        if (member == null) {throw new UsernameNotFoundException("사용자의 이름을 가진사람을 찾을수없습니다: " + username);}

        String roleAsString = member.getRole().name();

        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(roleAsString) 										// 문자열 형태의 Role 전달
                .disabled(!member.getEnabled())
                .build();
        }
}


