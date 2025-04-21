package com.project.controller;

import com.project.pojo.*;
import com.project.service.*;
import com.project.utils.ThreadLocalUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/club")
@Validated // Spring验证框架
public class ClubController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private ActivityParticipationService activityParticipationService;
    @PostMapping("/createClub") // 创建新的社团
    public Result createClub(@Valid @RequestBody Club club) {

        // 判断社团名字是否已经注册
        List<Club> existingClubs = clubService.findByName(club.getName());
        for (Club existingClub : existingClubs) {
            if ("已审核".equals(existingClub.getStatus())) {
                return Result.error("该社团已经存在，请勿重新注册");
            }
        }

        // 获取当前登录用户的信息
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User currentUser = userService.findByUserName(username);
        // 判断当前用户是否已经拥有社团
        if (!clubService.findByLeaderId(currentUser.getUserId()).isEmpty()) {
            return Result.error("您已经拥有一个社团，不能创建多个社团");
        }

        // 设置社团状态为“待审核”
        club.setStatus("待审核");
        // 创建社团
        clubService.createClub(club);
        return Result.success();
    }

    @GetMapping("/detailByLeaderID") // 根据团长获取社团信息
    public Result<?> getClubByLeaderID() {

        // 使用ThreadLocal获取当前用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        if (username == null) {
            return Result.error("用户信息获取失败，请重新登录！");
        }
        User user = userService.findByUserName(username);
        Integer leaderID = user.getUserId();
        List<Club> clubs = clubService.findByLeaderId(leaderID);
        if (clubs.isEmpty()) {
            return Result.error("当前用户没有创建社团或者不是任何社团的团长");
        }

        // 检查社团状态并返回信息
        for (Club club : clubs) {
            if (!"待审核".equals(club.getStatus()) && !"审核不通过".equals(club.getStatus())) {
                return Result.success(clubs);
            }
        }

        return Result.error("当前用户没有创建社团或者不是任何社团的团长");
    }

    @GetMapping("/detail") // 获取社团信息
    public Result<?> getClub(@RequestParam(required = false) Integer clubId, @RequestParam(required = false) String clubName) {
        if (clubId != null) {
            Club club = clubService.findById(clubId);
            if (club == null) {
                return Result.error("社团不存在，请重新尝试");
            }
            return Result.success(club);
        }
        if (clubName != null) {
            List<Club> clubs = clubService.findByName(clubName);
            if (clubs.isEmpty()) {
                return Result.error("没有找到匹配的社团");
            }
            return Result.success(clubs);
        }
        return Result.error("请输入社团ID或社团名字进行查询");
    }

    @GetMapping("/list") // 获取所有已审核社团信息
    public Result<List<Club>> getAllClubs() {

        // 获取社团状态为"已审核"的社团信息
        List<Club> clubs = clubService.findByStatus("已审核");
        return Result.success(clubs);
    }

    @GetMapping("/pending") // 获取所有待审核社团信息
    public Result<List<Club>> getPendingClubs() {

        // 获取社团状态为"待审核"的社团信息
        List<Club> clubs = clubService.findByStatus("待审核");
        return Result.success(clubs);
    }

    @GetMapping("/createdClubs") // 查询当前用户创建的所有社团记录
    public Result<List<Club>> getCreatedClubs() {

        // 使用ThreadLocal获取当前用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        if (username == null) {
            return Result.error("用户信息获取失败，请重新登录！");
        }
        User user = userService.findByUserName(username);
        Integer leaderID = user.getUserId();
        List<Club> clubs = clubService.findByLeaderId(leaderID);
        return Result.success(clubs);
    }

    @PatchMapping("/approve/{clubId}")
    @Transactional
    public Result approveClub(@PathVariable Integer clubId) {
        // 查找社团
        Club club = clubService.findById(clubId);
        if (club == null) {
            return Result.error("社团不存在");
        }

        // 获取社团团长
        User leader = club.getLeader();
        if (leader == null) {
            return Result.error("社团没有设置团长");
        }

        // 更新用户角色为“团长”
        if ("用户".equals(leader.getRole())) {
            leader.setRole("团长");
            userService.updateUser(leader); // 更新用户角色
        }

        // 更新社团状态为“已审核”
        club.setStatus("已审核");
        clubService.update(club);

        return Result.success();
    }


    @PatchMapping("/reject/{clubId}") // 拒绝社团审核，将社团状态修改为"审核不通过"
    public Result rejectClub(@PathVariable Integer clubId) {

        Club club = clubService.findById(clubId);
        if (club == null) {
            return Result.error("社团不存在");
        }
        club.setStatus("审核不通过");
        clubService.update(club);
        return Result.success();
    }

    @PatchMapping("/update/{clubId}") // 修改社团信息
    public Result updateClub(@PathVariable Integer clubId, @Valid @RequestBody Club club) {

        // 获取修改前的社团信息
        Club oldClub = clubService.findById(clubId);
        String oldLeaderUsername = oldClub.getLeader().getUsername();
        User oldLeader = userService.findByUserName(oldLeaderUsername);

        // 获取新的社团团长的username
        String newLeaderUsername = club.getLeader().getUsername();
        // 根据username获取新的leader的userId
        User newLeader = userService.findByUserName(newLeaderUsername);
        // 设置新的leader的userId
        club.getLeader().setUserId(newLeader.getUserId());

        // 选择社团ID然后进行修改
        club.setClubId(clubId);

        // 将修改前的团长角色设置成用户
        oldLeader.setRole("用户");
        userService.updateUser(oldLeader);

        // 更新社团信息
        clubService.update(club);

        // 将修改后的团长角色设置成团长
        newLeader.setRole("团长");
        userService.updateUser(newLeader);

        return Result.success();
    }

    @DeleteMapping("/delete/{clubId}") // 删除社团信息
    public Result deleteClub(@PathVariable Integer clubId) {

        // 获取社团信息
        Club club = clubService.findById(clubId);

        // 将团长的角色设置为“用户”
        String LeaderUsername = club.getLeader().getUsername();
        User leader = userService.findByUserName(LeaderUsername);
        if (leader != null) {
            leader.setRole("用户");
            userService.updateUser(leader);
        }

        // 删除社团的活动参与记录
        List<Event> events = eventService.listEventsByClubId(clubId);
        for (Event event : events) {
            activityParticipationService.deleteParticipationsByEventId(event.getEventId());
        }

        // 删除社团未审批的活动参与记录
        List<Event> pendingEvents = eventService.listPendingOrRejectedEventsByClubId(clubId);
        for (Event event : pendingEvents) {
            activityParticipationService.deleteParticipationsByEventId(event.getEventId());
        }

        // 删除社团的活动
        for (Event event : events) {
            eventService.removeEvent(event.getEventId());
        }

        // 删除社团未审批的活动
        for (Event event : pendingEvents) {
            eventService.removeEvent(event.getEventId());
        }

        // 删除社团成员
        List<Membership> memberships = membershipService.listApprovedMembers(clubId);
        for (Membership membership : memberships) {
            membershipService.removeMember(membership.getMembershipId());
        }

        // 删除未审批的社团成员
        List<Membership> pendingMembersMemberships = membershipService.listPendingMembers(clubId);
        for (Membership membership : pendingMembersMemberships) {
            membershipService.removeMember(membership.getMembershipId());
        }

        // 通过社团ID删除社团信息
        clubService.deleteById(clubId);
        return Result.success();
    }


}
