package com.cruisemobile.cruise.tools;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.cruisemobile.cruise.models.LocationDTO;
import com.cruisemobile.cruise.models.LocationPairDTO;
import com.cruisemobile.cruise.models.OfferDTO;
import com.cruisemobile.cruise.models.RideInfoDTO;
import com.cruisemobile.cruise.models.UserChangesDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.models.UserForRideDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.services.ThirdPartyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class Helper {

    public static UserChangesDTO getUserInfoFromSharedPreferences(SharedPreferences sharedPreferences) {
        UserChangesDTO user = new UserChangesDTO();
        user.setName(sharedPreferences.getString("name", ""));
        user.setSurname(sharedPreferences.getString("surname", ""));
        user.setAddress(sharedPreferences.getString("address", ""));
        user.setTelephoneNumber(sharedPreferences.getString("number", ""));
        user.setEmail(sharedPreferences.getString("email", ""));
        user.setProfilePicture(sharedPreferences.getString("picture", ""));
        return user;
    }

    public static void clearSharedPreferences(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor sp_editor = sharedPreferences.edit();
        sp_editor.remove("jwt");
        sp_editor.remove("email");
        sp_editor.remove("role");
        sp_editor.remove("id");
        sp_editor.remove("name");
        sp_editor.remove("surname");
        sp_editor.remove("number");
        sp_editor.remove("address");
        sp_editor.remove("picture");
        sp_editor.commit();
    }

    public static void setUserDataInSharedPreferences(UserDTO user, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor sp_editor = sharedPreferences.edit();
        sp_editor.putString("name", user.getName());
        sp_editor.putString("surname", user.getSurname());
        sp_editor.putString("number", user.getTelephoneNumber());
        sp_editor.putString("address", user.getAddress());
        sp_editor.putString("picture", user.getProfilePicture());
        sp_editor.commit();
    }

    public static String encodeImage(Bitmap bitMap) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        byte[] b = outStream.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString.split(",")[1], Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            return null;
        }
    }

    public static OfferDTO getEstimationForRequest(LocationDTO from, LocationDTO to, String vehicleType, boolean babyTransport, boolean petTransport) throws IOException {
        List<LocationPairDTO> locations = new ArrayList<>();
        LocationPairDTO location = new LocationPairDTO(from, to);
        locations.add(location);
        RideInfoDTO ride = new RideInfoDTO(locations, vehicleType, babyTransport, petTransport);
        Call<OfferDTO> call = ServiceUtils.unregisteredUserEndpoints.requestRideEstimation(ride);
        return call.execute().body();
    }

    public static LocationDTO getLocationFromAddress(String address) throws IOException {
        Call<Map<String, Object>> call = ThirdPartyUtils.googleEndpoints.getLatLngFromAddress(address, ThirdPartyUtils.mapsApiKey);
        Map<String, Object> response = call.execute().body();
        List<Object> results = (List<Object>) response.get("results");
        Map<String, Object> resultToUse = (Map<String, Object>) results.get(0);
        Map<String, Object> geometry = (Map<String, Object>) resultToUse.get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");

        return new LocationDTO(address, (Double) location.get("lat"), (Double) location.get("lng"));
    }

    public static String formatISODateToOurDate(String isoDate) {
        String[] tokensDateTime = isoDate.split("\\.")[0].split("T");
        String[] dateTokens = tokensDateTime[0].split("-");
        String[] timeTokens = tokensDateTime[1].split(":");

        return String.format("%s.%s.%s %s:%s:%s", dateTokens[2], dateTokens[1], dateTokens[0], timeTokens[0], timeTokens[1], timeTokens[2]);
    }

    public static UserForRideDTO getLoggedInUserAsUserForRide(SharedPreferences sharedPreferences){
        return new UserForRideDTO(sharedPreferences.getLong("id", -1),sharedPreferences.getString("email", ""));
    }
}
