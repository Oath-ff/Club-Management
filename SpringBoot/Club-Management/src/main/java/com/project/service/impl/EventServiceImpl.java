package com.project.service.impl;

import com.project.mapper.EventMapper;
import com.project.pojo.Event;
import com.project.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventMapper eventMapper;

    @Override
    public void addEvent(Event event) {
        eventMapper.addEvent(event);
    }

    @Override
    public void removeEvent(Integer eventId) {
        eventMapper.removeEvent(eventId);
    }

    @Override
    public List<Event> listEventsByClubId(Integer clubId) {
        return eventMapper.listEventsByClubId(clubId);
    }

    @Override
    public Event findEventById(Integer eventId) {
        return eventMapper.findEventById(eventId);
    }

    @Override
    public Event findEventByName(String name) {
        return eventMapper.findEventByName(name);
    }

    @Override
    public void updateEvent(Event event) {
        eventMapper.updateEvent(event);
    }

    @Override
    public void approveEvent(Integer eventId) {
        eventMapper.approveEvent(eventId);
    }

    @Override
    public void rejectEvent(Integer eventId) {
        eventMapper.rejectEvent(eventId);
    }

    @Override
    public List<Event> listPendingOrRejectedEventsByClubId(Integer clubId) {
        return eventMapper.listPendingOrRejectedEventsByClubId(clubId);
    }

    @Override
    public List<Event> getEventList(Integer clubId) {
        return eventMapper.eventList(clubId);
    }
}
