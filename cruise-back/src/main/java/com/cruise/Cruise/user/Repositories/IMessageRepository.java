package com.cruise.Cruise.user.Repositories;

import com.cruise.Cruise.models.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IMessageRepository extends JpaRepository<Message, Long> {

    Set<Message> findBySenderIdOrReceiverId(Long senderId, Long receiverId);

    List<Message> findBySenderIdAndTypeOrReceiverIdAndType(Long senderId, String type, Long receiverId, String type2);

    List<Message> findByRideIdAndSenderEmailOrRideIdAndReceiverEmail(Long rideId, String senderEmail, Long rideId2, String receiverEmail, Sort sort);

    List<Message> findBySenderEmailAndTypeOrReceiverEmailAndType(String senderEmail, String type, String receiverEmail, String type1);

    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Message> findByType(String type);
}
