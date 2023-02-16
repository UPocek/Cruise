package com.cruise.Cruise.user.Services;

import com.cruise.Cruise.admin.Repositories.IAdminRepository;
import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.helper.IHelperService;
import com.cruise.Cruise.models.*;
import com.cruise.Cruise.panic.Repositories.IPanicRepository;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.ride.DTO.RideForUserDTO;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.cruise.Cruise.user.Controllers.AndroidChatController;
import com.cruise.Cruise.user.DTO.*;
import com.cruise.Cruise.user.Repositories.IMessageRepository;
import com.cruise.Cruise.user.Repositories.INoteRepository;
import com.cruise.Cruise.user.Repositories.IResetPasswordRequestRepository;
import com.cruise.Cruise.user.Repositories.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.helpers.mail.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class UserService implements IUserService, UserDetailsService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IHelperService helper;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IAdminRepository adminRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private INoteRepository noteRepository;
    @Autowired
    private IPanicRepository panicRepository;
    @Autowired
    private IResetPasswordRequestRepository resetPasswordRequestRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<Passenger> passengerResult = passengerRepository.findByEmail(email);
        if (passengerResult.isPresent()) {
            return org.springframework.security.core.userdetails.User.withUsername(email).password(passengerResult.get().getPassword()).roles("PASSENGER").build();
        }
        Admin admin = adminRepository.findByUsername(email);
        if (admin != null) {
            return org.springframework.security.core.userdetails.User.withUsername(email).password(admin.getPassword()).roles("ADMIN").build();
        }
        Optional<Driver> driverResult = driverRepository.findByEmail(email);
        if (driverResult.isPresent()) {
            return org.springframework.security.core.userdetails.User.withUsername(email).password(driverResult.get().getPassword()).roles("DRIVER").build();
        }
        throw new UsernameNotFoundException("User not found with this email: " + email);
    }

    @Override
    public void sentResetPasswordMail(Long id) {
        Optional<User> result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }
        User user = result.get();
        UUID hash = UUID.randomUUID();
        ResetPasswordRequest request = new ResetPasswordRequest(user, LocalDateTime.now(), 24 * 60 * 60, hash.toString());
        resetPasswordRequestRepository.save(request);
        resetPasswordRequestRepository.flush();
        String mailTemplate = helper.prepareMailTemplate(Objects.requireNonNull(helper.getConfigValue("RESET_PASSWORD_URL")).toString() + id + "/" + hash, "src/main/resources/static/newPasswordEmailTemplate.html", "Confirm changing email address:\n");
        Mail mail = helper.prepareMail(Objects.requireNonNull(helper.getConfigValue("EMAIL")).toString(), user.getEmail(), Objects.requireNonNull(helper.getConfigValue("RESET_PASSWORD_EMAIL_SUBJECT")).toString(), mailTemplate);
        String apiKey = Objects.requireNonNull(helper.getConfigValue("SENDGRID_API_KEY")).toString();
        helper.sendEmail(apiKey, mail);
    }

    @Override
    public void sentResetPasswordMailAndroid(String email) {
        Optional<User> result = userRepository.findByEmail(email);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }
        User user = result.get();
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            codeBuilder.append(random.nextInt(10));
        }
        ResetPasswordRequest request = new ResetPasswordRequest(user, LocalDateTime.now(), 24 * 60 * 60, codeBuilder.toString());
        resetPasswordRequestRepository.save(request);
        resetPasswordRequestRepository.flush();
        List<String> data = new ArrayList<>();
        data.add(codeBuilder.toString());
        String mailTemplate = helper.prepareComplexMailTemplate(data, "src/main/resources/static/resetPasswordCodeAndroid.html", "Confirm changing email address:\n");
        Mail mail = helper.prepareMail(Objects.requireNonNull(helper.getConfigValue("EMAIL")).toString(), user.getEmail(), Objects.requireNonNull(helper.getConfigValue("RESET_PASSWORD_EMAIL_SUBJECT")).toString(), mailTemplate);
        String apiKey = Objects.requireNonNull(helper.getConfigValue("SENDGRID_API_KEY")).toString();
        helper.sendEmail(apiKey, mail);
    }

    @Override
    public Boolean checkCode(String email, String code) {
        return resetPasswordRequestRepository.findByUserEmailAndHash(email, code).isPresent();
    }

    @Override
    public void resetPassword(Long id, ResetPasswordDTO resetPasswordDTO) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<ResetPasswordRequest> resetPasswordRequest = resetPasswordRequestRepository.findByHash(resetPasswordDTO.getCode());
        if (resetPasswordRequest.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is expired or not correct");
        ResetPasswordRequest request = resetPasswordRequest.get();
        if (!Objects.equals(request.getUser().getId(), id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is expired or not correct");
        if (LocalDateTime.now().isAfter(request.getCreateTime().plus(request.getLifespanInSeconds(), ChronoUnit.SECONDS)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is expired or not correct");
        Optional<User> resultUser = userRepository.findById(id);
        if (resultUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        User user = resultUser.get();
        user.setPassword(encoder.encode(resetPasswordDTO.getNew_password()));
        userRepository.save(user);
        userRepository.flush();
        resetPasswordRequestRepository.delete(request);
        resetPasswordRequestRepository.flush();
    }

    @Override
    public void resetPasswordAndroid(String email, ResetPasswordDTO resetPasswordDTO) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<ResetPasswordRequest> resetPasswordRequest = resetPasswordRequestRepository.findByHash(resetPasswordDTO.getCode());
        if (resetPasswordRequest.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is expired or not correct");
        ResetPasswordRequest request = resetPasswordRequest.get();
        if (!Objects.equals(request.getUser().getEmail(), email))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is expired or not correct");
        if (LocalDateTime.now().isAfter(request.getCreateTime().plus(request.getLifespanInSeconds(), ChronoUnit.SECONDS)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is expired or not correct");
        Optional<User> resultUser = userRepository.findByEmail(email);
        if (resultUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        User user = resultUser.get();
        user.setPassword(encoder.encode(resetPasswordDTO.getNew_password()));
        userRepository.save(user);
        userRepository.flush();
        resetPasswordRequestRepository.delete(request);
        resetPasswordRequestRepository.flush();
    }

    @Override
    public Map<String, Object> getAllRidesByUserId(Long userId, String from, String to, String sortCriteria) {
        LocalDateTime fromDate;
        LocalDateTime toDate;
        if (from.equals("")) {
            fromDate = LocalDateTime.of(1970, 1, 1, 1, 1);
        } else {
            fromDate = LocalDateTime.parse(from);
        }
        if (to.equals("")) {
            toDate = LocalDateTime.now();
        } else {
            toDate = LocalDateTime.parse(to);
        }
        if (sortCriteria.equals("")) {
            sortCriteria = "startTime-asc";
        }

        String[] sortTokens = sortCriteria.split("-");
        Sort sort = Sort.by(Sort.Direction.ASC, sortTokens[0]);
        if (sortTokens[1].equals("desc")) {
            sort = Sort.by(Sort.Direction.DESC, sortTokens[0]);
        }

        List<RideForUserDTO> userRides = rideRepository.findByPassengersIdOrDriverId(userId, userId, fromDate, toDate, sort);

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", userRides.size());
        response.put("results", userRides);

        return response;
    }

    @Override
    public Map<String, Object> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : allUsers) {
            userDTOList.add(new UserDTO(user.getId(), user.getName(), user.getSurname(), user.getProfilePicture().getPictureContent(), user.getTelephoneNumber(), user.getEmail(), user.getAddress()));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", allUsers.size());
        response.put("results", userDTOList);
        return response;
    }

    @Override
    public Map<String, Object> getAllUserMessages(Long userid) {
        if (userRepository.findById(userid).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        }
        Set<Message> userMessages = messageRepository.findBySenderIdOrReceiverId(userid, userid);
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : userMessages) {
            messageDTOList.add(new MessageDTO(message));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", userMessages.size());
        response.put("results", messageDTOList);
        return response;
    }

    @Override
    public List<MessageDTO> getAllUserMessagesFromRide(Long rideId, Principal user) {
        List<Message> messages = messageRepository.findByRideIdAndSenderEmailOrRideIdAndReceiverEmail(rideId, user.getName(), rideId, user.getName(), Sort.by("sentTime"));
        List<MessageDTO> userMessagesForRide = new ArrayList<>();
        for (Message message : messages) {
            userMessagesForRide.add(new MessageDTO(message));
        }
        return userMessagesForRide;
    }

    @Override
    public List<MessageDTO> getAllUserPanicMessages(Long rideId, Principal user) {
        Optional<User> userResult = userRepository.findByEmail(user.getName());
        if (userResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        }
        User requester = userResult.get();
        List<Panic> panics = panicRepository.findAllByUserIdAndCurrentRideId(requester.getId(), rideId, Sort.by("time"));
        List<MessageDTO> userPanicMessages = new ArrayList<>();
        for (Panic panic : panics) {
            userPanicMessages.add(new MessageDTO(panic, requester.getId()));
        }
        return userPanicMessages;
    }

    @Override
    public List<MessageDTO> getAllUserSupportMessages(Principal principal) {
        List<Message> messages = messageRepository.findBySenderEmailAndTypeOrReceiverEmailAndType(principal.getName(), "SUPPORT", principal.getName(), "SUPPORT");
        List<MessageDTO> userSupportMessages = new ArrayList<>();
        for (Message message : messages) {
            userSupportMessages.add(new MessageDTO(message));
        }
        return userSupportMessages;
    }

    @Override
    public MessageDTO sendMessage(Long receiverId, SentMessageDTO message, Long id) {
        Optional<User> senderResult = userRepository.findById(id);
        if (senderResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender with that id does not exist");
        }
        User sender = senderResult.get();

        Message messageToSend = new Message();
        if (message.getType().equals("RIDE") || message.getType().equals("PANIC")) {
            messageToSend = sendMessageToOtherUser(sender, message, receiverId);
        } else if (message.getType().equals("SUPPORT")) {
            if (message.getRideId() != -1)
                messageToSend = sendMessageToSupport(sender, message);
            else
                messageToSend = sendMessageAdmin(sender, message, receiverId);
        }
        MessageDTO newMessage = new MessageDTO(messageToSend);
        if (AndroidChatController.openSessions.containsKey(receiverId)) {
            try {
                AndroidChatController.openSessions.get(receiverId).sendMessage(new TextMessage(objectMapper.writeValueAsString(newMessage)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        simpMessagingTemplate.convertAndSend("/socket-out/chat/" + newMessage.getRideId() + "/" + newMessage.getReceiverId(), newMessage);
        return newMessage;
    }

    private Message sendMessageAdmin(User sender, SentMessageDTO message, Long receiverId) {
        Optional<User> receiverResult = userRepository.findById(receiverId);
        if (receiverResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver with that id does not exist");
        }
        User receiver = receiverResult.get();
        Message messageToSend = new Message();
        messageToSend.setSender(sender);
        messageToSend.setReceiver(receiver);
        messageToSend.setMessage(message.getMessage());
        messageToSend.setRide(null);
        messageToSend.setType(message.getType());
        messageToSend.setSentTime(LocalDateTime.now());
        messageRepository.save(messageToSend);
        messageRepository.flush();
        return messageToSend;
    }

    private Message sendMessageToOtherUser(User sender, SentMessageDTO message, Long receiverId) {
        Optional<User> receiverResult = userRepository.findById(receiverId);
        if (receiverResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver with that id does not exist");
        }
        User receiver = receiverResult.get();
        Optional<Ride> rideResult = rideRepository.findById(message.getRideId());
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exist");
        }
        Ride ride = rideResult.get();
        Message messageToSend = new Message();
        messageToSend.setSender(sender);
        messageToSend.setReceiver(receiver);
        messageToSend.setMessage(message.getMessage());
        messageToSend.setRide(ride);
        messageToSend.setType(message.getType());
        messageToSend.setSentTime(LocalDateTime.now());
        messageRepository.save(messageToSend);
        messageRepository.flush();
        return messageToSend;
    }

    private Message sendMessageToSupport(User sender, SentMessageDTO message) {
        Message messageToSend = new Message();
        messageToSend.setSender(sender);
        messageToSend.setReceiver(null);
        messageToSend.setMessage(message.getMessage());
        messageToSend.setRide(null);
        messageToSend.setType(message.getType());
        messageToSend.setSentTime(LocalDateTime.now());
        messageRepository.save(messageToSend);
        messageRepository.flush();
        return messageToSend;
    }

    @Override
    public void blockUser(Long id) {
        Optional<User> result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that id does not exist");
        }
        User user = result.get();
        if (user.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already blocked!");
        }
        user.setBlocked(true);
        userRepository.save(user);
        userRepository.flush();
    }

    @Override
    public void unblockUser(Long id) {
        Optional<User> result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that id does not exist");
        }
        User user = result.get();
        if (!user.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not blocked!");
        }
        user.setBlocked(false);
        userRepository.save(user);
        userRepository.flush();
    }

    @Override
    public NoteDTO createNote(Long senderId, NoteBasicDTO basicNote) {
        Optional<User> result = userRepository.findById(senderId);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        }
        User sender = result.get();
        Note note = new Note();
        note.setUser(sender);
        note.setDateTime(LocalDateTime.now().toString());
        note.setMessage(basicNote.getMessage());
        noteRepository.save(note);
        noteRepository.flush();
        return new NoteDTO(note.getId(), note.getDateTime().toString(), note.getMessage());
    }

    @Override
    public Map<String, Object> getAllUserNotes(Long id) {
        Optional<User> result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        }
        User requester = result.get();
        Set<Note> userNotes = noteRepository.findByUser(requester);
        List<NoteDTO> noteDTOList = new ArrayList<>();
        for (Note note : userNotes) {
            noteDTOList.add(new NoteDTO(note.getId(), note.getDateTime().toString(), note.getMessage()));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", userNotes.size());
        response.put("results", noteDTOList);
        return response;
    }

    @Override
    public boolean isUserBlocked(String email) {
        Optional<User> response = userRepository.findByEmail(email);
        if (response.isPresent()) {
            return response.get().isBlocked();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> result = userRepository.findByEmail(email);
        return result.orElse(null);
    }

    @Override
    public UserDTO getUserDTOByEmail(String email) {
        Optional<User> result = userRepository.findByEmail(email);
        if (result.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        return new UserDTO(result.get());
    }

    @Override
    public void changePassword(Long id, ChangePasswordDTO changePasswordDTO) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<User> userResult = userRepository.findById(id);
        if (userResult.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist!");
        User user = userResult.get();
        if (!encoder.matches(changePasswordDTO.getOld_password(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is not matching");
        user.setPassword(encoder.encode(changePasswordDTO.getNew_password()));
        userRepository.saveAndFlush(user);
    }

    @Override
    public AllChatItemsDTO getAllUserRideChatItemsByUserId(Long userId, Pageable pageable) {
        ArrayList<ChatItemDTO> chatItemDTOS = new ArrayList<>();
        Pageable panicChatPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize() / 2);
        Page<Ride> panics = panicRepository.findDistinctPanicByRide(userId, panicChatPage);
        chatItemDTOS.addAll(getChatItems(panics, "PANIC"));

        Pageable rideChatPage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize() / 2, Sort.by("startTime"));
        Page<Ride> rides = rideRepository.findByPassengersIdOrDriverIdChatItems(userId, userId, rideChatPage);
        chatItemDTOS.addAll(getChatItems(rides, "RIDE"));

        chatItemDTOS.sort(Comparator.comparing(ChatItemDTO::getRideId));
        return new AllChatItemsDTO(chatItemDTOS, panics.getTotalPages(), rides.getTotalPages());
    }

    @Override
    public AllHistoryItemsDTO getAllUserHistoryItemsByUserId(Long userId, Pageable pageable) {
        Pageable ridePage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Ride> userHistoryItems = rideRepository.findAllUserHistoryItems(userId, ridePage);

        ArrayList<RideForUserDTO> historyItems = new ArrayList<>();
        for (Ride ride : userHistoryItems.getContent()) {
            historyItems.add(new RideForUserDTO(ride));
        }

        historyItems.sort(Comparator.comparing(RideForUserDTO::getStartTime).reversed());
        return new AllHistoryItemsDTO(historyItems, userHistoryItems.getTotalPages());
    }


    private ArrayList<ChatItemDTO> getChatItems(Page<Ride> rides, String type) {
        ArrayList<ChatItemDTO> chatItemDTOS = new ArrayList<>();
        for (Ride ride : rides) {
            List<Route> routes = new ArrayList<>(ride.getRoutes());
            chatItemDTOS.add(new ChatItemDTO(routes.get(0).getStartLocation().getAddress(), routes.get(0).getEndLocation().getAddress(), ride.getId(), type, ride.getRideState()));
        }
        return chatItemDTOS;
    }

}



