package com.cruise.Cruise.user.Controllers;

import com.cruise.Cruise.admin.DTO.AdminDTO;
import com.cruise.Cruise.admin.Services.IAdminService;
import com.cruise.Cruise.models.User;
import com.cruise.Cruise.security.IdentityCheck;
import com.cruise.Cruise.security.jwt.JwtTokenUtil;
import com.cruise.Cruise.user.DTO.*;
import com.cruise.Cruise.user.Services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IAdminService adminService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/{id}/ride")
    @IdentityCheck
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public Map<String, Object> getAllUserRides(@PathVariable Long id, @RequestParam(value = "page", required = false, defaultValue = "-1") int page, @RequestParam(value = "size", required = false, defaultValue = "-1") int size, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "from", required = false, defaultValue = "") String from, @RequestParam(value = "to", required = false, defaultValue = "") String to) {
        return userService.getAllRidesByUserId(id, from, to, sort);
    }

    @GetMapping
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, Object> getAllUsers(@RequestParam(value = "page", required = false, defaultValue = "-1") int page, @RequestParam(value = "size", required = false, defaultValue = "-1") int size) {
        return userService.getAllUsers();
    }

    @GetMapping("/isBlocked/{email}")
    @Valid
    public boolean isUserBlocked(@PathVariable String email) {
        return userService.isUserBlocked(email);
    }


    @PostMapping(value = "/login")
    @Valid
    public LoginUserDTO login(@Valid @RequestBody CredentialsDTO credentialsDTO) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(credentialsDTO.getEmail(),
                credentialsDTO.getPassword());
        Authentication auth = authenticationManager.authenticate(authReq);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        User user = userService.getUserByEmail(credentialsDTO.getEmail());
        AdminDTO admin;
        String token;
        String refreshToken;
        if (user != null) {
            token = jwtTokenUtil.generateToken(credentialsDTO.getEmail(), sc.getAuthentication().getAuthorities().toArray()[0].toString(), user.getId());
            refreshToken = jwtTokenUtil.generateRefreshToken(credentialsDTO.getEmail(), sc.getAuthentication().getAuthorities().toArray()[0].toString(), user.getId());
        } else {
            admin = adminService.getByUsername(credentialsDTO.getEmail());
            token = jwtTokenUtil.generateToken(credentialsDTO.getEmail(), sc.getAuthentication().getAuthorities().toArray()[0].toString(), admin.getId());
            refreshToken = jwtTokenUtil.generateRefreshToken(credentialsDTO.getEmail(), sc.getAuthentication().getAuthorities().toArray()[0].toString(), admin.getId());
        }
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setAccessToken(token);
        loginUserDTO.setRefreshToken(refreshToken);
        return loginUserDTO;
    }

    @PostMapping(value = "/refreshToken")
    @Valid
    public LoginUserDTO refreshToken(@Valid @RequestBody LoginUserDTO dto) {
        if (jwtTokenUtil.isTokenExpired(dto.getRefreshToken()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your refresh token has expired, please log in again");

        String newJwt = jwtTokenUtil.generateToken(jwtTokenUtil.getUsernameFromToken(dto.getRefreshToken()), jwtTokenUtil.getRoleFromToken(dto.getRefreshToken()), jwtTokenUtil.getUserIdFromToken(dto.getRefreshToken()));
        dto.setAccessToken(newJwt);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(jwtTokenUtil.getUsernameFromToken(dto.getRefreshToken()), jwtTokenUtil.getRoleFromToken(dto.getRefreshToken()), jwtTokenUtil.getUserIdFromToken(dto.getRefreshToken()));
        dto.setRefreshToken(newRefreshToken);
        return dto;
    }

    @GetMapping("/email/{email}")
    @Valid
    public UserDTO getByEmail(@PathVariable String email) {
        return userService.getUserDTOByEmail(email);
    }

    @GetMapping("/{id}/message")
    @IdentityCheck
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public Map<String, Object> getAllUserMessages(@PathVariable Long id) {
        return userService.getAllUserMessages(id);
    }

    @PostMapping("/{id}/message")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public MessageDTO sendUserMessage(@PathVariable Long id, @Valid @RequestBody SentMessageDTO message, @RequestHeader("Authorization") String token) {
        return userService.sendMessage(id, message, jwtTokenUtil.getUserIdFromToken(token.split(" ")[1]));
    }

    @GetMapping("/message/ride/{rideId}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public List<MessageDTO> getAllUserMessagesFromRide(@PathVariable Long rideId, Principal user) {
        return userService.getAllUserMessagesFromRide(rideId, user);
    }

    @GetMapping("/message/panic/{rideId}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public List<MessageDTO> getAllUserPanicMessages(@PathVariable Long rideId, Principal user) {
        return userService.getAllUserPanicMessages(rideId, user);
    }

    @GetMapping("/message/support")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public List<MessageDTO> getAllUserSupportMessages(Principal user) {
        return userService.getAllUserSupportMessages(user);
    }

    @PutMapping("/{id}/block")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return new ResponseEntity<>("User is successfully blocked", HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/unblock")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return new ResponseEntity<>("User is successfully unblocked", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/note")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public Map<String, Object> getAllUserNotes(@PathVariable Long id, @RequestParam(value = "page", required = false, defaultValue = "-1") int page, @RequestParam(value = "size", required = false, defaultValue = "-1") int size) {
        return userService.getAllUserNotes(id);
    }

    @PostMapping("/{id}/note")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public NoteDTO createUserNote(@PathVariable Long id, @Valid @RequestBody NoteBasicDTO note) {
        return userService.createNote(id, note);
    }

    @GetMapping(value = "/{id}/resetPassword")
    @Valid
    public ResponseEntity<String> sendResetPasswordMail(@PathVariable Long id) {
        userService.sentResetPasswordMail(id);
        return new ResponseEntity<>("Email with reset code has been sent!", HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{email}/resetPasswordAndroid")
    @Valid
    public ResponseEntity<String> sendResetPasswordMailAndroid(@PathVariable String email) {
        userService.sentResetPasswordMailAndroid(email);
        return new ResponseEntity<>("Email with reset code has been sent!", HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{email}/checkCodeAndroid/{code}")
    public Boolean checkCode(@PathVariable String email, @PathVariable String code) {
        return userService.checkCode(email, code);
    }

    @PutMapping(value = "/{id}/resetPassword")
    @Valid
    public ResponseEntity<String> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(id, resetPasswordDTO);
        return new ResponseEntity<>("Password successfully changed!", HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{email}/resetPasswordAndroid")
    @Valid
    public void resetPasswordAndroid(@PathVariable String email, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPasswordAndroid(email, resetPasswordDTO);
    }

    @PutMapping(value = "/{id}/changePassword")
    @IdentityCheck
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public ResponseEntity<Object> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/chat-items")
    @IdentityCheck
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public AllChatItemsDTO getAllUserChatItemsByUserId(@PathVariable Long id, Pageable pageable) {
        return userService.getAllUserRideChatItemsByUserId(id, pageable);
    }

    @GetMapping("/{id}/history-items")
    @IdentityCheck
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public AllHistoryItemsDTO getAllUserHistoryItemsByUserId(@PathVariable Long id, Pageable pageable) {
        return userService.getAllUserHistoryItemsByUserId(id, pageable);
    }
}
