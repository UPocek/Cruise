package com.cruise.Cruise.admin.Services;


import com.cruise.Cruise.admin.DTO.AdminChatsDTO;
import com.cruise.Cruise.admin.DTO.AdminDTO;
import com.cruise.Cruise.admin.DTO.RegisterAdminDTO;
import com.cruise.Cruise.admin.DTO.UserForAdminChatDTO;
import com.cruise.Cruise.admin.Repositories.IAdminRepository;
import com.cruise.Cruise.models.Admin;
import com.cruise.Cruise.models.Message;
import com.cruise.Cruise.models.Picture;
import com.cruise.Cruise.models.User;
import com.cruise.Cruise.user.DTO.MessageDTO;
import com.cruise.Cruise.user.Repositories.IMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class AdminService implements IAdminService {
    @Autowired
    private IAdminRepository adminRepository;

    @Autowired
    private IMessageRepository messageRepository;

    @Override
    public AdminDTO getById(Long id) {
        Admin admin = adminRepository.findById(id).get();
        return new AdminDTO(admin.getId(), admin.getName(), admin.getSurname(), admin.getImage().getPictureContent(), admin.getTelephoneNumber(), admin.getUsername(), admin.getAddress());

    }

    @Override
    public List<AdminDTO> getAll() {
        List<AdminDTO> admins = new ArrayList<>();
        for (Admin admin : adminRepository.findAll())
            admins.add(new AdminDTO(admin.getId(), admin.getName(), admin.getSurname(), null, admin.getTelephoneNumber(), admin.getUsername(), admin.getAddress()));
        return admins;
    }

    @Override
    public AdminDTO getByUsername(String username) {
        Admin admin = adminRepository.findByUsername(username);
        return new AdminDTO(admin.getId(), admin.getName(), admin.getSurname(), admin.getImage().getPictureContent(), admin.getTelephoneNumber(), admin.getUsername(), admin.getAddress());
    }

    @Override
    public AdminDTO update(Long id, AdminDTO adminDTO) {
        Admin admin = adminRepository.findById(id).get();
        admin.setName(adminDTO.getName());
        admin.setSurname(adminDTO.getSurname());
        Picture picture = admin.getImage();
        picture.setPictureContent(adminDTO.getProfilePicture());
        admin.setImage(picture);
        admin.setTelephoneNumber(adminDTO.getTelephoneNumber());
        admin.setAddress(adminDTO.getAddress());
        adminRepository.save(admin);
        adminRepository.flush();
        return new AdminDTO(admin.getId(), admin.getName(), admin.getSurname(), admin.getImage().getPictureContent(), admin.getTelephoneNumber(), admin.getUsername(), admin.getAddress());
    }

    @Override
    public AdminDTO create(RegisterAdminDTO adminDTO) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        Admin admin = new Admin();
        admin.setUsername(adminDTO.getUsername());
        admin.setName(adminDTO.getName());
        admin.setSurname(adminDTO.getSurname());
        Picture picture = new Picture();
        picture.setPictureContent(" ");
        admin.setImage(picture);
        admin.setPassword(encoder.encode(adminDTO.getPassword()));
        admin.setTelephoneNumber(adminDTO.getTelephoneNumber());
        admin.setAddress(adminDTO.getAddress());
        adminRepository.save(admin);
        adminRepository.flush();
        return new AdminDTO(admin.getId(), admin.getName(), admin.getSurname(), admin.getImage().getPictureContent(), admin.getTelephoneNumber(), admin.getUsername(), admin.getAddress());
    }

    @Override
    public AdminChatsDTO getAdminChats(Long id)
    {
        AdminChatsDTO adminChatsDTO = new AdminChatsDTO(new HashSet<>());
        List<Message> messages = messageRepository.findByType("SUPPORT");
        for(Message message: messages)
        {
            if(message.getSender() != null && !isIdInSet(adminChatsDTO.getUsers(), message.getSender().getId()))
            {
                User user = message.getSender();
                UserForAdminChatDTO userDto = new UserForAdminChatDTO(user.getName(), user.getSurname(), user.getEmail(), user.getId());
                adminChatsDTO.getUsers().add(userDto);
                continue;
            }
            if(message.getReceiver() != null && !isIdInSet(adminChatsDTO.getUsers(), message.getReceiver().getId()))
            {
                User user = message.getReceiver();
                UserForAdminChatDTO userDto = new UserForAdminChatDTO(user.getName(), user.getSurname(), user.getEmail(), user.getId());
                adminChatsDTO.getUsers().add(userDto);
            }
        }

        return adminChatsDTO;
    }

    @Override
    public List<MessageDTO> getAllUserSupportMessages(Long adminId, Long id)
    {
        List<Message> messages = messageRepository.findBySenderIdAndTypeOrReceiverIdAndType(id, "SUPPORT", id, "SUPPORT");
//        messages.sort((o1, o2) -> o1.getSentTime().isAfter(o2.getSentTime()) ? 1 : -1);
        List<MessageDTO> userSupportMessages = new ArrayList<>();
        for (Message message : messages) {
            userSupportMessages.add(new MessageDTO(message));
        }
        return userSupportMessages;
    }

    private boolean isIdInSet(Set<UserForAdminChatDTO> set, Long id)
    {
        for(UserForAdminChatDTO user: set)
        {
            if(Objects.equals(user.getId(), id))
                return true;
        }
        return false;
    }
}
