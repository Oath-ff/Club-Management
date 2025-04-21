package com.project.mapper;

import com.project.pojo.Event;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EventMapper {

    @Insert("INSERT INTO events(club_id, name, date, location, description, status) VALUES(#{club.clubId}, #{name}, #{date}, #{location}, #{description}, #{status})")
    void addEvent(Event event);

    @Delete("DELETE FROM events WHERE event_id = #{eventId}")
    void removeEvent(Integer eventId);

    @Select("SELECT e.event_id, e.name, e.date, e.location, e.description, e.status, " +
            "c.club_id, c.name AS club_name, c.type, c.description AS club_description, " +
            "u.user_id AS leader_id, u.username AS leader_name " +
            "FROM events e " +
            "LEFT JOIN clubs c ON e.club_id = c.club_id " +
            "LEFT JOIN users u ON c.leader_id = u.user_id " +
            "WHERE e.club_id = #{clubId} AND e.status = '审核通过'")
    @Results({
            @Result(property = "eventId", column = "event_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "date", column = "date"),
            @Result(property = "location", column = "location"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "club_name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "club_description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_name")
    })
    List<Event> listEventsByClubId(Integer clubId);

    @Select("SELECT e.event_id, e.name, e.date, e.location, e.description, e.status, " +
            "c.club_id, c.name AS club_name, c.type, c.description AS club_description, " +
            "u.user_id AS leader_id, u.username AS leader_name " +
            "FROM events e " +
            "LEFT JOIN clubs c ON e.club_id = c.club_id " +
            "LEFT JOIN users u ON c.leader_id = u.user_id " +
            "WHERE e.event_id = #{eventId} AND e.status = '审核通过'")
    @Results({
            @Result(property = "eventId", column = "event_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "date", column = "date"),
            @Result(property = "location", column = "location"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "club_name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "club_description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_name")
    })
    Event findEventById(Integer eventId);

    @Select("SELECT e.event_id, e.name, e.date, e.location, e.description, e.status, " +
            "c.club_id, c.name AS club_name, c.type, c.description AS club_description, " +
            "u.user_id AS leader_id, u.username AS leader_name " +
            "FROM events e " +
            "LEFT JOIN clubs c ON e.club_id = c.club_id " +
            "LEFT JOIN users u ON c.leader_id = u.user_id " +
            "WHERE e.name = #{name} AND e.status = '审核通过'")
    @Results({
            @Result(property = "eventId", column = "event_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "date", column = "date"),
            @Result(property = "location", column = "location"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "club_name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "club_description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_name")
    })
    Event findEventByName(String name);

    @Update("UPDATE events SET name = #{name}, date = #{date}, location = #{location}, description = #{description}, status = #{status} WHERE event_id = #{eventId}")
    void updateEvent(Event event);

    @Select("SELECT e.event_id, e.name, e.date, e.location, e.description, e.status, " +
            "c.club_id, c.name AS club_name, c.type, c.description AS club_description, " +
            "u.user_id AS leader_id, u.username AS leader_name " +
            "FROM events e " +
            "LEFT JOIN clubs c ON e.club_id = c.club_id " +
            "LEFT JOIN users u ON c.leader_id = u.user_id " +
            "WHERE e.club_id = #{clubId} AND (e.status = '待审核' OR e.status = '审核未通过')")
    @Results({
            @Result(property = "eventId", column = "event_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "date", column = "date"),
            @Result(property = "location", column = "location"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "club_name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "club_description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_name")
    })
    List<Event> listPendingOrRejectedEventsByClubId(Integer clubId);

    @Update("UPDATE events SET status = '审核通过' WHERE event_id = #{eventId}")
    void approveEvent(Integer eventId);

    @Update("UPDATE events SET status = '审核未通过' WHERE event_id = #{eventId}")
    void rejectEvent(Integer eventId);

    @Select("SELECT e.event_id, e.name, e.date, e.location, e.description, e.status, " +
            "c.club_id, c.name AS club_name, c.type, c.description AS club_description, " +
            "u.user_id AS leader_id, u.username AS leader_name " +
            "FROM events e " +
            "LEFT JOIN clubs c ON e.club_id = c.club_id " +
            "LEFT JOIN users u ON c.leader_id = u.user_id " +
            "WHERE e.club_id = #{clubId} ")
    @Results({
            @Result(property = "eventId", column = "event_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "date", column = "date"),
            @Result(property = "location", column = "location"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "club_name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "club_description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_name")
    })
    List<Event> eventList(Integer clubId);
}
