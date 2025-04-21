package com.project.controller;

import com.project.pojo.*;
import com.project.service.*;
import com.project.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ActivityParticipationService activityParticipationService;

    @GetMapping("/find") // 根据用户ID或用户名查找成员记录
    public Result<List<Membership>> findMembershipByUserIdOrUsername(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String username) {

        List<Membership> memberships = null;
        if (userId != null) {
            memberships = membershipService.findMembershipsByUserId(userId);
        } else if (username != null) {
            memberships = membershipService.findMembershipsByUsername(username);
        }
        if (memberships == null || memberships.isEmpty()) {
            return Result.error("未找到该成员信息");
        }
        return Result.success(memberships);
    }


    @PostMapping("/add") // 添加成员
    public Result addMember(@RequestBody Membership membership) {

        // 检查是否已经申请加入且状态为待审或已批准
        Membership existingMembership = membershipService.findMembershipByUserIdAndClubId(membership.getUser().getUserId(), membership.getClub().getClubId());
        if (existingMembership != null) {
            return Result.error("您已申请加入该社团或已是该社团成员，请勿重复申请！");
        }
        // 设置加入日期为当前日期
        membership.setJoinDate(LocalDateTime.now());
        membershipService.addMember(membership);
        return Result.success();
    }

    @PatchMapping("/approve") // 同意用户加入社团
    public Result approveMember(@RequestParam Integer userId, @RequestParam Integer clubId) {

        // 根据 userId 和 clubId 查询待处理的成员记录
        Membership membership = membershipService.findMembershipByUserIdAndClubId(userId, clubId);
        if (membership == null) {
            return Result.error("未找到该成员信息");
        }
        // 更新状态为 "已批准"
        membership.setStatus("已批准");
        membershipService.updateMemberStatus(membership);
        return Result.success();
    }



    @DeleteMapping("/remove/{membershipId}") // 移除社团成员
    public Result removeMember(@PathVariable Integer membershipId) {

        Membership membership = membershipService.findMembershipByMembershipId(membershipId);
        List<ActivityParticipation> participations = activityParticipationService.listParticipationsByUserId(membership.getUser().getUserId());
        for (ActivityParticipation participation : participations) {
            activityParticipationService.deleteParticipationsByEventId(participation.getEvent().getEventId());
        }
        membershipService.removeMember(membershipId);
        return Result.success();
    }

    @GetMapping("/list/{clubId}") // 获取社团已批准成员列表
    public Result<List<Membership>> listApprovedMembers(@PathVariable Integer clubId) {
        List<Membership> members = membershipService.listApprovedMembers(clubId);
        return Result.success(members);
    }

    @GetMapping("/pendinglist/{clubId}") // 获取社团待审成员列表
    public Result<List<Membership>> listPendingMembers(@PathVariable Integer clubId) {
        List<Membership> members = membershipService.listPendingMembers(clubId);
        return Result.success(members);
    }

    @PatchMapping("/reject") // 拒绝用户加入社团
    public Result rejectMember(@RequestParam Integer userId, @RequestParam Integer clubId) {
        // 根据 userId 和 clubId 查询待处理的成员记录
        Membership membership = membershipService.findMembershipByUserIdAndClubId(userId, clubId);
        if (membership == null) {
            return Result.error("未找到该成员信息");
        }

        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");

        User sender = userService.findByUserName(username);

        // 发送通知
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setTitle("加入社团申请结果");
        notification.setContent("您的加入社团申请已被拒绝。");
        notification.setDate(LocalDateTime.now());
        notification.setClub(membership.getClub());
        notification.setRecipient(membership.getUser());
        notificationService.sendNotification(notification);

        // 修改成员状态为“不批准”
        membership.setStatus("不批准");
        membershipService.updateMemberStatus(membership);
        return Result.success();
    }


    @GetMapping("/applicationRecords") // 查询当前用户申请加入社团的记录
    public Result<List<Membership>> getApplicationRecords() {
        // 获取当前登录用户的信息
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUserName(username);
        if (user == null) {
            return Result.error("用户信息获取失败，请重新登录！");
        }
            List<Membership> applicationRecords = membershipService.findMembershipsByUserId(user.getUserId());
        return Result.success(applicationRecords);
    }
}
