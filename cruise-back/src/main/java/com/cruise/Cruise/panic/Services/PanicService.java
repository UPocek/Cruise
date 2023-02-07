package com.cruise.Cruise.panic.Services;

import com.cruise.Cruise.models.Panic;
import com.cruise.Cruise.panic.DTO.PanicDTO;
import com.cruise.Cruise.panic.DTO.PanicsDTO;
import com.cruise.Cruise.panic.DTO.UserForPanicDTO;
import com.cruise.Cruise.panic.Repositories.IPanicRepository;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PanicService implements IPanicService {
    @Autowired
    private IPanicRepository panicRepository;
    @Autowired
    private IPassengerRepository iPassengerRepository;

    @Override
    public PanicsDTO getAll() {
        PanicsDTO panicsDTO = new PanicsDTO();
        List<Panic> panics = panicRepository.findAll();
        List<PanicDTO> results = new ArrayList<PanicDTO>();
        for (Panic panic : panics) {
            PanicDTO panicDTO = new PanicDTO();
            panicDTO.setId(panic.getId());
            panicDTO.setUser(new UserForPanicDTO(panic.getUser()));
            panicDTO.setRide(new RideForTransferDTO(panic.getCurrentRide()));
            panicDTO.setTime(panic.getTime());
            panicDTO.setReason(panic.getReason());
            results.add(panicDTO);
        }
        panicsDTO.setResults(results);
        panicsDTO.setTotalCount(results.size());
        return panicsDTO;
    }


}
