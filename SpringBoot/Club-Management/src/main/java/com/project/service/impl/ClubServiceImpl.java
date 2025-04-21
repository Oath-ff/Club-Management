package com.project.service.impl;

import com.project.mapper.ClubMapper;
import com.project.pojo.Club;
import com.project.pojo.User;
import com.project.service.ClubService;
import com.project.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClubServiceImpl implements ClubService {

    @Autowired
    private ClubMapper clubMapper;

    @Override
    public void createClub(Club club) {
        // 补充获取社团团长ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("user_id");
        String username = (String) map.get("username");
        User leader = new User();
        leader.setUserId(userId);
        leader.setUsername(username);
        club.setLeader(leader);
        clubMapper.add(club);
    }

    @Override
    public Club findById(Integer clubId) {
        return clubMapper.findById(clubId);
    }

    @Override
    public List<Club> findByName(String clubName) {
        return clubMapper.findByName(clubName);
    }

    @Override
    public List<Club> findAll() {
        return clubMapper.findAll();
    }

    @Override
    public List<Club> findByStatus(String status) {
        return clubMapper.findByStatus(status);
    }

    @Override
    public void update(Club club) {
        clubMapper.update(club);
    }

    @Override
    public void deleteById(Integer clubId) {
        clubMapper.deleteClubById(clubId);
    }

    @Override
    public List<Club> findByLeaderId(Integer leaderId) {
        return clubMapper.findByLeaderId(leaderId);
    }
}
