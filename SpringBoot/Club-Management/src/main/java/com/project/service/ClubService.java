package com.project.service;

import com.project.pojo.Club;

import java.util.List;

import java.util.List;

public interface ClubService {
    void createClub(Club club);
    Club findById(Integer clubId);
    List<Club> findByName(String clubName);
    List<Club> findAll();
    List<Club> findByStatus(String status);
    void update(Club club);
    void deleteById(Integer clubId);
    List<Club> findByLeaderId(Integer leaderId); // 修改返回类型为 List<Club>
}
