package com.project.service;

import com.project.pojo.Membership;

import java.util.List;

public interface MembershipService {
    Membership findMembershipByUserId(Integer userId);
    Membership findMembershipByUsername(String username);
    Membership findMembershipByMembershipId(Integer membershipId);
    void addMember(Membership membership);
    void updateMemberStatus(Membership membership);
    void removeMember(Integer membershipId);
    List<Membership> listApprovedMembers(Integer clubId);
    List<Membership> listPendingMembers(Integer clubId);
    List<Membership> findMembershipsByUserId(Integer userId);
    List<Membership> findMembershipsByUsername(String username);
    Membership findMembershipByUserIdAndClubId(Integer userId, Integer clubId);

}
