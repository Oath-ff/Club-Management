package com.project.mapper;

import com.project.pojo.Membership;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MembershipMapper {

    // 根据用户id查询成员信息
    @Select("SELECT m.membership_id, m.join_date, m.status, " +
            "u.user_id, u.username, u.nickname, u.phone_number, " + // 加入 nickname 和 phone_number
            "c.club_id, c.name, c.type, c.description, " +
            "l.user_id AS leader_id, l.username AS leader_username " +
            "FROM memberships m " +
            "LEFT JOIN users u ON m.user_id = u.user_id " +
            "LEFT JOIN clubs c ON m.club_id = c.club_id " +
            "LEFT JOIN users l ON c.leader_id = l.user_id " +
            "WHERE m.user_id = #{userId} AND m.status = '已批准'")
    @Results({
            @Result(property = "membershipId", column = "membership_id"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.nickname", column = "nickname"), // 映射昵称
            @Result(property = "user.phoneNumber", column = "phone_number"), // 映射联系电话
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_username")
    })
    Membership findMembershipByUserId(@Param("userId") Integer userId);

    // 根据用户名查询成员信息
    @Select("SELECT m.membership_id, m.join_date, m.status, " +
            "u.user_id, u.username, u.nickname, u.phone_number, " + // 加入 nickname 和 phone_number
            "c.club_id, c.name, c.type, c.description, " +
            "l.user_id AS leader_id, l.username AS leader_username " +
            "FROM memberships m " +
            "LEFT JOIN users u ON m.user_id = u.user_id " +
            "LEFT JOIN clubs c ON m.club_id = c.club_id " +
            "LEFT JOIN users l ON c.leader_id = l.user_id " +
            "WHERE u.username = #{username} AND m.status = '已批准'")
    @Results({
            @Result(property = "membershipId", column = "membership_id"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.nickname", column = "nickname"), // 映射昵称
            @Result(property = "user.phoneNumber", column = "phone_number"), // 映射联系电话
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_username")
    })
    Membership findMembershipByUsername(@Param("username") String username);

    // 添加成员
    @Insert("INSERT INTO memberships(user_id, club_id, join_date, status) VALUES(#{user.userId}, #{club.clubId}, #{joinDate}, #{status})")
    void addMember(Membership membership);

    // 更新成员状态
    @Update("UPDATE memberships SET status = #{status} WHERE membership_id = #{membershipId}")
    void updateMemberStatus(Membership membership);

    // 删除成员
    @Delete("DELETE FROM memberships WHERE membership_id = #{membershipId}")
    void removeMember(Integer membershipId);

    // 列出所有状态为“已批准”的成员
    @Select("SELECT m.membership_id, m.join_date, m.status, " +
            "u.user_id, u.username, u.nickname, u.phone_number, " +
            "c.club_id, c.name, c.type, c.description, " +
            "l.user_id AS leader_id, l.username AS leader_username " +
            "FROM memberships m " +
            "LEFT JOIN users u ON m.user_id = u.user_id " +
            "LEFT JOIN clubs c ON m.club_id = c.club_id " +
            "LEFT JOIN users l ON c.leader_id = l.user_id " +
            "WHERE m.club_id = #{clubId} AND m.status = '已批准'")
    @Results({
            @Result(property = "membershipId", column = "membership_id"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "user.phoneNumber", column = "phone_number"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_username")
    })
    List<Membership> listApprovedMembers(@Param("clubId") Integer clubId);

    // 列出所有状态为“待审”的成员
    @Select("SELECT m.membership_id, m.join_date, m.status, " +
            "u.user_id, u.username, u.nickname, u.phone_number, " +
            "c.club_id, c.name, c.type, c.description, " +
            "l.user_id AS leader_id, l.username AS leader_username " +
            "FROM memberships m " +
            "LEFT JOIN users u ON m.user_id = u.user_id " +
            "LEFT JOIN clubs c ON m.club_id = c.club_id " +
            "LEFT JOIN users l ON c.leader_id = l.user_id " +
            "WHERE m.club_id = #{clubId} AND m.status = '待审'")
    @Results({
            @Result(property = "membershipId", column = "membership_id"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "user.phoneNumber", column = "phone_number"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_username")
    })
    List<Membership> listPendingMembers(@Param("clubId") Integer clubId);

    // 查询当前用户申请加入的社团记录
    @Select("SELECT m.membership_id, m.join_date, m.status, " +
            "u.user_id, u.username, u.nickname, u.phone_number, " +
            "c.club_id, c.name, c.type, c.description, " +
            "l.user_id AS leader_id, l.username AS leader_username " +
            "FROM memberships m " +
            "LEFT JOIN users u ON m.user_id = u.user_id " +
            "LEFT JOIN clubs c ON m.club_id = c.club_id " +
            "LEFT JOIN users l ON c.leader_id = l.user_id " +
            "WHERE m.user_id = #{userId} AND (m.status = '待审' OR m.status = '不批准' OR m.status = '已批准')")
    @Results({
            @Result(property = "membershipId", column = "membership_id"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "user.phoneNumber", column = "phone_number"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_username")
    })
    List<Membership> findMembershipsByUserId(@Param("userId") Integer userId);

    //根据 username 查询成员记录
    @Select("SELECT m.membership_id, m.join_date, m.status, " +
            "u.user_id, u.username, u.nickname, u.phone_number, " +
            "c.club_id, c.name, c.type, c.description, " +
            "l.user_id AS leader_id, l.username AS leader_username " +
            "FROM memberships m " +
            "LEFT JOIN users u ON m.user_id = u.user_id " +
            "LEFT JOIN clubs c ON m.club_id = c.club_id " +
            "LEFT JOIN users l ON c.leader_id = l.user_id " +
            "WHERE u.username = #{username} AND (m.status = '待审' OR m.status = '不批准' OR m.status = '已批准')")
    @Results({
            @Result(property = "membershipId", column = "membership_id"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "user.phoneNumber", column = "phone_number"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_username")
    })
    List<Membership> findMembershipsByUsername(@Param("username") String username);

    // 根据membershipId查询成员信息
    @Select("SELECT m.membership_id, m.join_date, m.status, " +
            "u.user_id, u.username, u.nickname, u.phone_number, " +
            "c.club_id, c.name, c.type, c.description, " +
            "l.user_id AS leader_id, l.username AS leader_username " +
            "FROM memberships m " +
            "LEFT JOIN users u ON m.user_id = u.user_id " +
            "LEFT JOIN clubs c ON m.club_id = c.club_id " +
            "LEFT JOIN users l ON c.leader_id = l.user_id " +
            "WHERE m.membership_id = #{membershipId}")
    @Results({
            @Result(property = "membershipId", column = "membership_id"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.nickname", column = "nickname"),
            @Result(property = "user.phoneNumber", column = "phone_number"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_username")
    })
    Membership findMembershipByMembershipId(Integer membershipId);

    // 按用户ID、社团ID和状态查询成员信息
    @Select("SELECT m.membership_id, m.join_date, m.status, " +
            "u.user_id, u.username, u.nickname, u.phone_number, " + // 加入 nickname 和 phone_number
            "c.club_id, c.name, c.type, c.description, " +
            "l.user_id AS leader_id, l.username AS leader_username " +
            "FROM memberships m " +
            "LEFT JOIN users u ON m.user_id = u.user_id " +
            "LEFT JOIN clubs c ON m.club_id = c.club_id " +
            "LEFT JOIN users l ON c.leader_id = l.user_id " +
            "WHERE m.user_id = #{userId} AND c.club_id = #{clubId} AND (m.status = '待审' OR m.status = '已批准')")
    @Results({
            @Result(property = "membershipId", column = "membership_id"),
            @Result(property = "joinDate", column = "join_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "user.userId", column = "user_id"),
            @Result(property = "user.username", column = "username"),
            @Result(property = "user.nickname", column = "nickname"), // 映射昵称
            @Result(property = "user.phoneNumber", column = "phone_number"), // 映射联系电话
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_username")
    })
    Membership findMembershipByUserIdAndClubId(@Param("userId") Integer userId, @Param("clubId") Integer clubId);

}
