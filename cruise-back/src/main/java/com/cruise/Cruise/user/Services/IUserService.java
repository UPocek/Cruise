package com.cruise.Cruise.user.Services;

import com.cruise.Cruise.models.User;
import com.cruise.Cruise.user.DTO.*;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface IUserService {
    void sentResetPasswordMail(Long id);

    void resetPassword(Long id, ResetPasswordDTO resetPasswordDTO);

    Map<String, Object> getAllRidesByUserId(Long id, String from, String to, String sort);

    Map<String, Object> getAllUsers();

    Map<String, Object> getAllUserMessages(Long id);

    List<MessageDTO> getAllUserMessagesFromRide(Long rideId, Principal user);

    List<MessageDTO> getAllUserPanicMessages(Long rideId, Principal user);

    List<MessageDTO> getAllUserSupportMessages(Principal user);


    MessageDTO sendMessage(Long receiverId, SentMessageDTO message, Long id);

    void blockUser(Long id);

    void unblockUser(Long id);

    NoteDTO createNote(Long id, NoteBasicDTO basicNote);

    Map<String, Object> getAllUserNotes(Long id);

    boolean isUserBlocked(String email);

    User getUserByEmail(String email);

    UserDTO getUserDTOByEmail(String email);

    void changePassword(Long id, ChangePasswordDTO changePasswordDTO);

    AllChatItemsDTO getAllUserRideChatItemsByUserId(Long userId, Pageable pageable);

    AllHistoryItemsDTO getAllUserHistoryItemsByUserId(Long userId, Pageable pageable);

    void sentResetPasswordMailAndroid(String email);

    Boolean checkCode(String email, String code);

    void resetPasswordAndroid(String email, ResetPasswordDTO resetPasswordDTO);
}
