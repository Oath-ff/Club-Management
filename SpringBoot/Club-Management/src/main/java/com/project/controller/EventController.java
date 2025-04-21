package com.project.controller;

import com.project.pojo.Event;
import com.project.pojo.Result;
import com.project.service.ActivityParticipationService;
import com.project.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@Validated // Spring验证框架
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ActivityParticipationService activityParticipationService;

    @PostMapping("/add") // 添加活动
    public Result addEvent(@Valid @RequestBody Event event) {
        event.setStatus("待审核"); // 设置活动状态为待审核
        eventService.addEvent(event);
        return Result.success();
    }

    @PatchMapping("/update/{eventId}") // 更新活动
    public Result updateEvent(@PathVariable Integer eventId, @Valid @RequestBody Event event) {
        event.setEventId(eventId);
        eventService.updateEvent(event);
        return Result.success();
    }

    @DeleteMapping("/remove/{eventId}") // 删除活动
    public Result removeEvent(@PathVariable Integer eventId) {
        activityParticipationService.deleteParticipationsByEventId(eventId);
        eventService.removeEvent(eventId);
        return Result.success();
    }

    @GetMapping("/list/{clubId}") // 获取社团的所有审核通过的活动
    public Result<List<Event>> listEventsByClubId(@PathVariable Integer clubId) {
        List<Event> events = eventService.listEventsByClubId(clubId);
        return Result.success(events);
    }

    @GetMapping("/pending/{clubId}") // 获取社团的所有待审核或审核未通过的活动
    public Result<List<Event>> listPendingOrRejectedEventsByClubId(@PathVariable Integer clubId) {
        List<Event> events = eventService.listPendingOrRejectedEventsByClubId(clubId);
        return Result.success(events);
    }

    @GetMapping("/getList/{clubId}") // 获取活动列表
    public Result<List<Event>> getEventList(@PathVariable Integer clubId) {

        List<Event> events = eventService.getEventList(clubId);
        if (events == null) {
            return Result.error("未找到该活动信息");
        }
        return Result.success(events);
    }

    @PatchMapping("/approve/{eventId}") // 审核通过活动
    public Result approveEvent(@PathVariable Integer eventId) {
        eventService.approveEvent(eventId);
        return Result.success();
    }

    @PatchMapping("/reject/{eventId}") // 审核未通过活动
    public Result rejectEvent(@PathVariable Integer eventId) {
        eventService.rejectEvent(eventId);
        return Result.success();
    }

    @GetMapping("/find") // 根据活动ID或活动名称获取审核通过的活动详情
    public Result<Event> findEventByIdOrName(@RequestParam(required = false) Integer eventId, @RequestParam(required = false) String name) {
        Event event = null;
        if (eventId != null) {
            event = eventService.findEventById(eventId);
        } else if (name != null) {
            event = eventService.findEventByName(name);
        }
        if (event == null) {
            return Result.error("未找到该活动信息");
        }
        return Result.success(event);
    }
}
