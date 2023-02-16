package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.AllChatItemsDTO;
import com.cruisemobile.cruise.models.AllHistoryItemsDTO;
import com.cruisemobile.cruise.models.AllMessagesDTO;
import com.cruisemobile.cruise.models.ChangePasswordDTO;
import com.cruisemobile.cruise.models.CredentialsDTO;
import com.cruisemobile.cruise.models.LoginDTO;
import com.cruisemobile.cruise.models.MessageDTO;
import com.cruisemobile.cruise.models.NoteDTO;
import com.cruisemobile.cruise.models.NoteWithDateDTO;
import com.cruisemobile.cruise.models.SendMessageDTO;
import com.cruisemobile.cruise.models.ResetPasswordDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserEndpoints {

    String urlExtension = "user";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "Skip: true"
    })
    @POST(urlExtension + "/login")
    Call<LoginDTO> login(@Body CredentialsDTO credentialsDTO);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT(urlExtension + "/{id}/changePassword")
    Call<Void> resetPassword(@Path("id") Long id, @Body ChangePasswordDTO changePassword);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension + "/{id}/note")
    Call<NoteWithDateDTO> createNote(@Path("id") Long id, @Body NoteDTO note);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{id}/message")
    Call<AllMessagesDTO> getAllUserMessages(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{id}/chat-items")
    Call<AllChatItemsDTO> getAllUserChats(@Path("id") Long id, @Query("size") int pageSize,
                                          @Query("page") int currentPage);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{id}/history-items")
    Call<AllHistoryItemsDTO> getAllUserHistoryItems(@Path("id") Long id, @Query("size") int pageSize,
                                                    @Query("page") int currentPage);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/message/ride/{rideId}")
    Call<List<MessageDTO>> getAllRideMessages(@Path("rideId") Long rideId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/message/panic/{rideId}")
    Call<List<MessageDTO>> getAllPanicMessages(@Path("rideId") Long rideId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/message/support")
    Call<List<MessageDTO>> getAllSupportMessages();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension + "/{id}/message")
    Call<MessageDTO> sendMessage(@Path("id") Long id, @Body SendMessageDTO message);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{email}/resetPasswordAndroid")
    Call<Void> sendResetPasswordMail(@Path("email") String email);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{email}/checkCodeAndroid/{code}")
    Call<Boolean> checkCode(@Path("email") String email, @Path("code") String code);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT(urlExtension + "/{email}/resetPasswordAndroid")
    Call<Void> resetPassword(@Path("email") String email, @Body ResetPasswordDTO dto);

}
