package kh.edu.social_members.controller;

import kh.edu.social_members.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
@RestController
@RequiredArgsConstructor
public class ApiController {

    @Autowired
    MemberServiceImpl memberService;
    //카카오톡으로 전달받은 값 -> DB에 저장하는
    //PostMapping 작성

    @PostMapping("/api/signup")
    public void signup(@RequestBody Member member) {
        System.out.println("회원가입 : "+member);
        memberService.insertMember(member);
    }
}
