package com.project.service;

import com.project.pojo.ActivityParticipation;

import java.util.List;

public interface ActivityParticipationService {

    // 报名参加活动
    void addParticipation(ActivityParticipation participation);

    // 查找活动参与记录
    ActivityParticipation findParticipation(Integer membershipId, Integer eventId);

    // 报名参加活动并完成签到
    void updateParticipationStatus(Integer participationId, ActivityParticipation.Status status);

    // 获取指定活动的参与记录
    List<ActivityParticipation> listParticipationsByEventId(Integer eventId);

    // 获取指定用户的参与记录
    List<ActivityParticipation> listParticipationsByUserId(Integer userId);

    // 删除指定活动的所有参与记录
    void deleteParticipationsByEventId(Integer eventId);
}
