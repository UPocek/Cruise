package com.cruise.Cruise.unregisteredUser.Services;

import com.cruise.Cruise.models.VehicleType;
import com.cruise.Cruise.ride.DTO.LocationForRideDTO;
import com.cruise.Cruise.ride.DTO.RideEstimationDTO;
import com.cruise.Cruise.ride.DTO.RideRequestBasicDTO;
import com.cruise.Cruise.unregisteredUser.DTO.DistanceEstimationDTO;
import com.cruise.Cruise.unregisteredUser.DTO.EstimationDTO;
import com.cruise.Cruise.vehicle.Repositories.IVehicleTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UnregisteredUserService implements IUnregisteredUserService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IVehicleTypeRepository vehicleTypeRepository;

    @Override
    public RideEstimationDTO getRideEstimation(RideRequestBasicDTO requestBasicDTO) {
        VehicleType vehicleType = vehicleTypeRepository.findByName(requestBasicDTO.getVehicleType());
        if (vehicleType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle type not valid");
        }
        LocationForRideDTO fromLocation = requestBasicDTO.getLocations().get(0).getDeparture();
        LocationForRideDTO toLocation = requestBasicDTO.getLocations().get(0).getDestination();

        List<Double> estimations = getTimeAndDistanceEstimation(fromLocation, toLocation, -1);

        double durationInSeconds = estimations.get(0);
        double distanceInMeters = estimations.get(1);
        int numberOfSecondsInMinute = 60;
        int numberOfMetersInKm = 1000;

        return new RideEstimationDTO((int) Math.round(durationInSeconds / numberOfSecondsInMinute), (int) Math.round((distanceInMeters / numberOfMetersInKm) * 120 + vehicleType.getPricePerKm()), (int) distanceInMeters);
    }

    @Override
    public DistanceEstimationDTO getRideDistance(RideRequestBasicDTO rideRequestBasicDTO) {
        LocationForRideDTO fromLocation = rideRequestBasicDTO.getLocations().get(0).getDeparture();
        LocationForRideDTO toLocation = rideRequestBasicDTO.getLocations().get(0).getDestination();

        List<Double> estimations = getTimeAndDistanceEstimation(fromLocation, toLocation, -1);

        DistanceEstimationDTO distanceEstimationDTO = new DistanceEstimationDTO();
        distanceEstimationDTO.setValue(estimations.get(1));
        return distanceEstimationDTO;
    }

    public List<Double> getTimeAndDistanceEstimation(LocationForRideDTO origin, LocationForRideDTO destination, int timeInSecondsSinceMidnightJanuary1st1970) {
        List<Double> result = new ArrayList<>();
        String apiKey = (String) getConfigValue("GOOGLE_MAPS_API_KEY");

        String urlEndpoint = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin.getAddress() + "&destinations=" + destination.getAddress() + "&departure_time=";
        if (timeInSecondsSinceMidnightJanuary1st1970 == -1) {
            urlEndpoint += "now";
        } else {
            urlEndpoint += timeInSecondsSinceMidnightJanuary1st1970;
        }
        urlEndpoint += "&key=" + apiKey;

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(urlEndpoint)
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String jsonResponse = response.body().string();
            EstimationDTO estimation = objectMapper.readValue(jsonResponse, EstimationDTO.class);
            double duration = estimation.getRows().get(0).getElements().get(0).getDuration().getValue();
            double distance = estimation.getRows().get(0).getElements().get(0).getDistance().getValue();
            result.add(duration);
            result.add(distance);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid address");
        }
        return result;
    }

    private Object getConfigValue(String keyName) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File("src/main/resources/config.yaml"));
        } catch (IOException e) {
            try {
                inputStream = new FileInputStream(new File("cruise-back/src/main/resources/config.yaml"));
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
            }
        }

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        return data.get(keyName);
    }
}
