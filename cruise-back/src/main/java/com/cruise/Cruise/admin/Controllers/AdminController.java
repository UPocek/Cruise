package com.cruise.Cruise.admin.Controllers;


import com.cruise.Cruise.admin.DTO.AdminChatsDTO;
import com.cruise.Cruise.admin.DTO.AdminDTO;
import com.cruise.Cruise.admin.DTO.RegisterAdminDTO;
import com.cruise.Cruise.admin.Services.IAdminService;
import com.cruise.Cruise.security.IdentityCheck;
import com.cruise.Cruise.security.jwt.JwtTokenUtil;
import com.cruise.Cruise.user.DTO.AllChatItemsDTO;
import com.cruise.Cruise.user.DTO.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    IAdminService adminService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public AdminDTO getById(@PathVariable Long id) {
        return adminService.getById(id);
    }

    @GetMapping("/username={username}")
    public AdminDTO getByUsername(@PathVariable String username) {
        return adminService.getByUsername(username);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public AdminDTO update(@PathVariable Long id, @RequestBody AdminDTO adminDTO) {
        return adminService.update(id, adminDTO);
    }

    @PostMapping()
    public AdminDTO create(@RequestBody RegisterAdminDTO adminDTO) {
        return adminService.create(adminDTO);
    }

    @GetMapping("/chat-items")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public AdminChatsDTO getAllUserChatItemsByUserId(@RequestHeader(value = "Authorization") String token) {
        return adminService.getAdminChats(jwtTokenUtil.getUserIdFromToken(token.split(" ")[1]));
    }

    @GetMapping("/messageWith/{id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<MessageDTO> getAllUserSupportMessages(@RequestHeader(value = "Authorization") String token, @PathVariable Long id) {
        return adminService.getAllUserSupportMessages(jwtTokenUtil.getUserIdFromToken(token.split(" ")[1]), id);
    }
}
