package com.project.service.impl;

import com.project.mapper.ActivityParticipationMapper;
import com.project.pojo.ActivityParticipation;
import com.project.service.ActivityParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityParticipationServiceImpl implements ActivityParticipationService {

    @Autowired
    private ActivityParticipationMapper activityParticipationMapper;

    @Override
    public void addParticipation(ActivityParticipation participation) {
        activityParticipationMapper.addParticipation(participation);
    }

    @Override
    public ActivityParticipation findParticipation(Integer membershipId, Integer eventId) {
        return activityParticipationMapper.findParticipation(membershipId, eventId);
    }

    @Override
    public void updateParticipationStatus(Integer participationId, ActivityParticipation.Status status) {
        activityParticipationMapper.updateParticipationStatus(participationId, status);
    }

    @Override
    public List<ActivityParticipation> listParticipationsByEventId(Integer eventId) {
        return activityParticipationMapper.listParticipationsByEventId(eventId);
    }

    @Override
    public List<ActivityParticipation> listParticipationsByUserId(Integer userId) {
        return activityParticipationMapper.listParticipationsByUserId(userId);
    }

    @Override
    public void deleteParticipationsByEventId(Integer eventId) {
        activityParticipationMapper.deleteParticipationsByEventId(eventId);
    }
}
