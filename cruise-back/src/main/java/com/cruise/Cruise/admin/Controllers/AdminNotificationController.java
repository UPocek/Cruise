package com.cruise.Cruise.admin.Controllers;

import com.cruise.Cruise.admin.DTO.AdminDTO;
import com.cruise.Cruise.admin.Services.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller

public class AdminNotificationController {
    @Autowired
    IAdminService adminService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/panic-notification")
    public void panicNotification(String message) {
        for (AdminDTO admin : adminService.getAll())
            simpMessagingTemplate.convertAndSend("/socket-out/panic/" + admin.getId(), message);
    }

    @MessageMapping("/driver-change-notification")
    public void driverChangeNotification(String message) {
        for (AdminDTO admin : adminService.getAll())
            simpMessagingTemplate.convertAndSend("/socket-out/driverChanges/" + admin.getId(), message);
    }
}
