package com.project.mapper;

import com.project.pojo.ActivityParticipation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityParticipationMapper {

    // 查找活动参与记录
    @Select("SELECT ap.*, e.*, c.*, m.*, u1.username AS participant_name, u2.username AS club_leader_name, c.name AS club_name, c.type AS club_type, c.description AS club_description, m.join_date, m.status AS membership_status " +
            "FROM activity_participation ap " +
            "JOIN events e ON ap.event_id = e.event_id " +
            "JOIN clubs c ON e.club_id = c.club_id " +
            "JOIN memberships m ON ap.membership_id = m.membership_id " +
            "JOIN users u1 ON m.user_id = u1.user_id " +
            "JOIN users u2 ON c.leader_id = u2.user_id " +
            "WHERE ap.membership_id = #{membershipId} AND ap.event_id = #{eventId}")
    @Results({
            @Result(property = "membership.membershipId", column = "membership_id"),
            @Result(property = "membership.user.userId", column = "user_id"),
            @Result(property = "membership.user.username", column = "participant_name"),
            @Result(property = "membership.club.clubId", column = "club_id"),
            @Result(property = "membership.club.name", column = "club_name"),
            @Result(property = "membership.club.type", column = "club_type"),
            @Result(property = "membership.club.description", column = "club_description"),
            @Result(property = "membership.club.leader.userId", column = "leader_id"),
            @Result(property = "membership.club.leader.username", column = "club_leader_name"),
            @Result(property = "membership.joinDate", column = "join_date"),
            @Result(property = "membership.status", column = "membership_status"),
            @Result(property = "event.eventId", column = "event_id"),
            @Result(property = "event.name", column = "name"),
            @Result(property = "event.date", column = "date"),
            @Result(property = "event.location", column = "location"),
            @Result(property = "event.description", column = "description"),
            @Result(property = "event.club.clubId", column = "club_id"),
            @Result(property = "event.club.name", column = "club_name"),
            @Result(property = "event.club.type", column = "club_type"),
            @Result(property = "event.club.description", column = "club_description"),
            @Result(property = "event.club.leader.userId", column = "leader_id"),
            @Result(property = "event.club.leader.username", column = "club_leader_name")
    })
    ActivityParticipation findParticipation(@Param("membershipId") Integer membershipId, @Param("eventId") Integer eventId);

    // 添加活动参与记录
    @Insert("INSERT INTO activity_participation(membership_id, event_id, status) VALUES(#{membership.membershipId}, #{event.eventId}, #{status})")
    void addParticipation(ActivityParticipation participation);

    // 报名参加活动并完成签到
    @Update("UPDATE activity_participation SET status = #{status} WHERE participation_id = #{participationId}")
    void updateParticipationStatus(@Param("participationId") Integer participationId, @Param("status") ActivityParticipation.Status status);

    // 列出活动参与记录（根据活动ID）
    @Select("SELECT ap.*, e.*, c.*, m.*, u1.username AS participant_name, u2.username AS club_leader_name, c.name AS club_name, c.type AS club_type, c.description AS club_description, m.join_date, m.status AS membership_status " +
            "FROM activity_participation ap " +
            "JOIN events e ON ap.event_id = e.event_id " +
            "JOIN clubs c ON e.club_id = c.club_id " +
            "JOIN memberships m ON ap.membership_id = m.membership_id " +
            "JOIN users u1 ON m.user_id = u1.user_id " +
            "JOIN users u2 ON c.leader_id = u2.user_id " +
            "WHERE ap.event_id = #{eventId}")
    @Results({
            @Result(property = "membership.membershipId", column = "membership_id"),
            @Result(property = "membership.user.userId", column = "user_id"),
            @Result(property = "membership.user.username", column = "participant_name"),
            @Result(property = "membership.club.clubId", column = "club_id"),
            @Result(property = "membership.club.name", column = "club_name"),
            @Result(property = "membership.club.type", column = "club_type"),
            @Result(property = "membership.club.description", column = "club_description"),
            @Result(property = "membership.club.leader.userId", column = "leader_id"),
            @Result(property = "membership.club.leader.username", column = "club_leader_name"),
            @Result(property = "membership.joinDate", column = "join_date"),
            @Result(property = "membership.status", column = "membership_status"),
            @Result(property = "event.eventId", column = "event_id"),
            @Result(property = "event.name", column = "name"),
            @Result(property = "event.date", column = "date"),
            @Result(property = "event.location", column = "location"),
            @Result(property = "event.description", column = "description"),
            @Result(property = "event.club.clubId", column = "club_id"),
            @Result(property = "event.club.name", column = "club_name"),
            @Result(property = "event.club.type", column = "club_type"),
            @Result(property = "event.club.description", column = "club_description"),
            @Result(property = "event.club.leader.userId", column = "leader_id"),
            @Result(property = "event.club.leader.username", column = "club_leader_name")
    })
    List<ActivityParticipation> listParticipationsByEventId(@Param("eventId") Integer eventId);

    // 列出活动参与记录（根据用户ID）
    @Select("SELECT ap.*, e.*, c.*, m.*, u1.username AS participant_name, u2.username AS club_leader_name, c.name AS club_name, c.type AS club_type, c.description AS club_description, m.join_date, m.status AS membership_status " +
            "FROM activity_participation ap " +
            "JOIN events e ON ap.event_id = e.event_id " +
            "JOIN clubs c ON e.club_id = c.club_id " +
            "JOIN memberships m ON ap.membership_id = m.membership_id " +
            "JOIN users u1 ON m.user_id = u1.user_id " +
            "JOIN users u2 ON c.leader_id = u2.user_id " +
            "WHERE m.user_id = #{userId}")
    @Results({
            @Result(property = "membership.membershipId", column = "membership_id"),
            @Result(property = "membership.user.userId", column = "user_id"),
            @Result(property = "membership.user.username", column = "participant_name"),
            @Result(property = "membership.club.clubId", column = "club_id"),
            @Result(property = "membership.club.name", column = "club_name"),
            @Result(property = "membership.club.type", column = "club_type"),
            @Result(property = "membership.club.description", column = "club_description"),
            @Result(property = "membership.club.leader.userId", column = "leader_id"),
            @Result(property = "membership.club.leader.username", column = "club_leader_name"),
            @Result(property = "membership.joinDate", column = "join_date"),
            @Result(property = "membership.status", column = "membership_status"),
            @Result(property = "event.eventId", column = "event_id"),
            @Result(property = "event.name", column = "name"),
            @Result(property = "event.date", column = "date"),
            @Result(property = "event.location", column = "location"),
            @Result(property = "event.description", column = "description"),
            @Result(property = "event.club.clubId", column = "club_id"),
            @Result(property = "event.club.name", column = "club_name"),
            @Result(property = "event.club.type", column = "club_type"),
            @Result(property = "event.club.description", column = "club_description"),
            @Result(property = "event.club.leader.userId", column = "leader_id"),
            @Result(property = "event.club.leader.username", column = "club_leader_name")
    })
    List<ActivityParticipation> listParticipationsByUserId(@Param("userId") Integer userId);

    // 删除指定活动的所有参与记录（根据活动ID）
    @Delete("DELETE FROM activity_participation WHERE event_id = #{eventId}")
    void deleteParticipationsByEventId(@Param("eventId") Integer eventId);
}
