package com.project.mapper;

import com.project.pojo.Club;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClubMapper {

    // 添加社团信息
    @Insert("INSERT INTO clubs(name, type, leader_id, description, status) VALUES(#{name}, #{type}, #{leader.userId}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "clubId")
    void add(Club club);

    // 通过社团ID查找社团信息
    @Select("SELECT c.*, u.username AS leader_username, u.role AS leader_role, u.password AS leader_password, u.user_pic AS leader_user_pic " +
            "FROM clubs c " +
            "LEFT JOIN users u ON c.leader_id = u.user_id " +
            "WHERE c.club_id = #{clubId}")
    @Results({
            @Result(property = "clubId", column = "club_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "leader.userId", column = "leader_id"),
            @Result(property = "leader.username", column = "leader_username"),
            @Result(property = "leader.role", column = "leader_role"),
            @Result(property = "leader.password", column = "leader_password"),
            @Result(property = "leader.userPic", column = "leader_user_pic")
    })
    Club findById(Integer clubId);



    // 通过社团名字查找社团信息
    @Select("SELECT c.*, u.username AS leader_username FROM clubs c LEFT JOIN users u ON c.leader_id = u.user_id WHERE c.name LIKE CONCAT('%', #{clubName}, '%')")
    @Results({
            @Result(property = "clubId", column = "club_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "leader.userId", column = "leader_id"),
            @Result(property = "leader.username", column = "leader_username")
    })
    List<Club> findByName(String clubName);

    // 获取所有社团信息
    @Select("SELECT c.*, u.username AS leader_username FROM clubs c LEFT JOIN users u ON c.leader_id = u.user_id")
    @Results({
            @Result(property = "clubId", column = "club_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "leader.userId", column = "leader_id"),
            @Result(property = "leader.username", column = "leader_username")
    })
    List<Club> findAll();

    // 审核社团
    @Select("SELECT c.*, u.username AS leader_username FROM clubs c LEFT JOIN users u ON c.leader_id = u.user_id WHERE c.status = #{status}")
    @Results({
            @Result(property = "clubId", column = "club_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "leader.userId", column = "leader_id"),
            @Result(property = "leader.username", column = "leader_username")
    })
    List<Club> findByStatus(String status);

    // 编辑社团信息
    @Update("UPDATE clubs SET name = #{name}, type = #{type}, leader_id = #{leader.userId}, description = #{description}, status = #{status} WHERE club_id = #{clubId}")
    void update(Club club);

    // 删除社团信息
    @Delete("DELETE FROM clubs WHERE club_id = #{clubId}")
    void deleteClubById(@Param("clubId") Integer clubId);

    // 根据社团ID删除通知
    @Delete("DELETE FROM notifications WHERE club_id = #{clubId}")
    void deleteNotificationsByClubId(@Param("clubId") Integer clubId);

    // 根据团长ID查询社团
    @Select("SELECT c.*, u.username AS leader_username FROM clubs c LEFT JOIN users u ON c.leader_id = u.user_id WHERE c.leader_id = #{leaderId}")
    @Results({
            @Result(property = "clubId", column = "club_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "description", column = "description"),
            @Result(property = "status", column = "status"),
            @Result(property = "leader.userId", column = "leader_id"),
            @Result(property = "leader.username", column = "leader_username")
    })
    List<Club> findByLeaderId(@Param("leaderId") Integer leaderId);
}
