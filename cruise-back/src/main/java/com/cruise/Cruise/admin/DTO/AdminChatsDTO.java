package com.cruise.Cruise.admin.DTO;

import java.util.Set;

public class AdminChatsDTO {
    private Set<UserForAdminChatDTO> users;

    public AdminChatsDTO(Set<UserForAdminChatDTO> users) {
        this.users = users;
    }

    public AdminChatsDTO() {
    }

    public Set<UserForAdminChatDTO> getUsers() {
        return users;
    }

    public void setUsers(Set<UserForAdminChatDTO> users) {
        this.users = users;
    }
}
