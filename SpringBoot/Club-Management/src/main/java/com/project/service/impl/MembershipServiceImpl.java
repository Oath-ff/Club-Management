package com.project.service.impl;

import com.project.mapper.MembershipMapper;
import com.project.pojo.Membership;
import com.project.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipServiceImpl implements MembershipService {

    @Autowired
    private MembershipMapper membershipMapper;

    @Override
    public Membership findMembershipByUserId(Integer userId) {
        return membershipMapper.findMembershipByUserId(userId);
    }

    @Override
    public Membership findMembershipByUsername(String username) {
        return membershipMapper.findMembershipByUsername(username);
    }

    @Override
    public Membership findMembershipByMembershipId(Integer membershipId) {
        return membershipMapper.findMembershipByMembershipId(membershipId);
    }

    @Override
    public void addMember(Membership membership) {
        membershipMapper.addMember(membership);
    }

    @Override
    public void updateMemberStatus(Membership membership) {
        membershipMapper.updateMemberStatus(membership);
    }

    @Override
    public void removeMember(Integer membershipId) {
        membershipMapper.removeMember(membershipId);
    }

    @Override
    public List<Membership> listApprovedMembers(Integer clubId) {
        return membershipMapper.listApprovedMembers(clubId);
    }

    @Override
    public List<Membership> listPendingMembers(Integer clubId) {
        return membershipMapper.listPendingMembers(clubId);
    }

    @Override
    public List<Membership> findMembershipsByUserId(Integer userId) {
        return membershipMapper.findMembershipsByUserId(userId);
    }

    @Override
    public List<Membership> findMembershipsByUsername(String username) {
        return membershipMapper.findMembershipsByUsername(username);
    }
    @Override
    public Membership findMembershipByUserIdAndClubId(Integer userId, Integer clubId) {
        return membershipMapper.findMembershipByUserIdAndClubId(userId, clubId);
    }

}
