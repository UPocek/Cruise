package com.cruise.Cruise.admin.DTO;

public class UserForAdminChatDTO
{
    private String name;
    private String surname;
    private String email;
    private Long id;

    public UserForAdminChatDTO()
    {
    }

    public UserForAdminChatDTO(String name, String surname, String email, Long id)
    {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
}
