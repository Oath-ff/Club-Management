package com.project.service;

import com.project.pojo.Event;

import java.util.List;

public interface EventService {
    void addEvent(Event event);
    void removeEvent(Integer eventId);
    List<Event> listEventsByClubId(Integer clubId);
    Event findEventById(Integer eventId);
    Event findEventByName(String name);
    void updateEvent(Event event);
    void approveEvent(Integer eventId);
    void rejectEvent(Integer eventId);
    List<Event> listPendingOrRejectedEventsByClubId(Integer clubId);

    List<Event> getEventList(Integer clubId);
}
