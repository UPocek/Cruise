package com.cruise.Cruise.panic.Controllers;

import com.cruise.Cruise.panic.DTO.PanicsDTO;
import com.cruise.Cruise.panic.Services.IPanicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/panic")
public class PanicController {
    @Autowired
    IPanicService panicService;

    @GetMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PanicsDTO getAll() {
        return panicService.getAll();
    }
}
