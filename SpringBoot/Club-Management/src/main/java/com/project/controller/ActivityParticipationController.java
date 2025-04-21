package com.project.controller;

import com.project.pojo.ActivityParticipation;
import com.project.pojo.Event;
import com.project.pojo.Result;
import com.project.pojo.Membership;
import com.project.service.ActivityParticipationService;
import com.project.service.EventService;
import com.project.service.MembershipService;
import com.project.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/participation")
@Validated // Spring验证框架
public class ActivityParticipationController {

    @Autowired
    private ActivityParticipationService activityParticipationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MembershipService membershipService;

    @PostMapping("/join") // 报名参加活动
    public Result joinActivity(@RequestParam Integer eventId) {

        // 检查活动是否存在
        Event event = eventService.findEventById(eventId);
        if (event == null) {
            return Result.error("该活动不存在，请确认活动ID。");
        }

        // 获取当前用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("user_id");

        // 获取用户的所有 membership 信息
        List<Membership> memberships = membershipService.findMembershipsByUserId(userId);
        if (memberships == null || memberships.isEmpty()) {
            return Result.error("您不是任何社团的成员，无法报名参加活动。");
        }

        // 查找用户是否是当前活动所属社团的成员
        Membership targetMembership = memberships.stream()
                .filter(m -> Integer.valueOf(m.getClub().getClubId()).equals(event.getClub().getClubId()))
                .findFirst()
                .orElse(null);
        if (targetMembership == null || !"已批准".equals(targetMembership.getStatus())) {
            return Result.error("您不是该社团的成员，无法报名参加该社团的活动。");
        }

        // 检查活动是否已经参加
        ActivityParticipation existingParticipation = activityParticipationService.findParticipation(targetMembership.getMembershipId(), eventId);
        if (existingParticipation != null) {
            return Result.error("您已经报名了该活动，请勿重复报名。");
        }

        // 创建活动参与记录
        ActivityParticipation participation = new ActivityParticipation();
        participation.setMembership(targetMembership);
        participation.setEvent(event);
        participation.setStatus(ActivityParticipation.Status.报名);

        // 添加活动参与记录
        activityParticipationService.addParticipation(participation);
        return Result.success();
    }


    @PatchMapping("/signIn") // 报名参加活动并完成签到
    public Result signInActivity(@RequestParam Integer participationId) {

        // 更新参与状态为"签到"
        activityParticipationService.updateParticipationStatus(participationId, ActivityParticipation.Status.签到);
        return Result.success();
    }

    @GetMapping("/event/{eventId}") // 获取指定活动的参与记录
    public Result<List<ActivityParticipation>> listParticipationsByEventId(@PathVariable Integer eventId) {

        List<ActivityParticipation> participations = activityParticipationService.listParticipationsByEventId(eventId);
        return Result.success(participations);
    }

    @GetMapping("/user/{userId}") // 获取指定用户的参与记录
    public Result<List<ActivityParticipation>> listParticipationsByUserId(@PathVariable Integer userId) {

        List<ActivityParticipation> participations = activityParticipationService.listParticipationsByUserId(userId);
        return Result.success(participations);
    }

}
