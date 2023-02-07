package com.cruise.Cruise.admin.Services;

import com.cruise.Cruise.admin.DTO.AdminChatsDTO;
import com.cruise.Cruise.admin.DTO.AdminDTO;
import com.cruise.Cruise.admin.DTO.RegisterAdminDTO;
import com.cruise.Cruise.user.DTO.MessageDTO;

import java.util.List;

public interface IAdminService {
    AdminDTO getById(Long id);

    List<AdminDTO> getAll();

    AdminDTO getByUsername(String username);

    AdminDTO update(Long id, AdminDTO adminDTO);

    AdminDTO create(RegisterAdminDTO admin);

    AdminChatsDTO getAdminChats(Long id);

    List<MessageDTO> getAllUserSupportMessages(Long adminId, Long id);
}
