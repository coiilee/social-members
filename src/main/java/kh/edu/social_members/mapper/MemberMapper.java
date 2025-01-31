package kh.edu.social_members.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.lang.reflect.Member;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
}
