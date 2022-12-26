package com.beyt.anouncy.location.service;

import com.beyt.anouncy.common.entity.enumeration.LocationType;
import com.beyt.anouncy.location.dto.LocationDTO;
import com.beyt.anouncy.location.entity.Location;
import com.beyt.anouncy.location.model.GeoJsonItem;
import com.beyt.anouncy.location.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bson.BsonArray;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by tdilber at 08-Sep-19
 */
@Slf4j
@Service
public class LocationService {
    @Value("${anouncy.location.google.key}")
    private String locationGoogleKey;

    @Autowired
    private LocationRepository locationRepository;

    public LocationDTO findAllDTOByParentId(Long parentId) {
        List<Location> locationList = locationRepository.findAllByParentLocationIdIsIn(List.of(parentId));
        return new LocationDTO(locationList);
    }

    public List<Location> findAllByParentId(Long parentId) {
        return locationRepository.findById(parentId).map(l ->
                        locationRepository.findAllByIdIsIn(l.getParentLocationIdWithSelfList()).stream()
                                .filter(loc -> !LocationType.REGION.equals(loc.getType())).toList())
                .orElseGet(ArrayList::new);
    }

    private void fillGeometryTurkey(List<Location> locationList) throws IOException {
        JSONObject locationJsonArray = new JSONObject(FileUtils.readFileToString(new File("anouncy/backend/location/src/main/resources/geo-data/gadm41_TUR_0.json"), StandardCharsets.UTF_8));

        JSONArray features = locationJsonArray.getJSONArray("features");
        Location country = locationList.stream().filter(l -> slugify(l.getName()).equalsIgnoreCase(slugify("Türkiye"))).findFirst().orElse(null);
        if (Objects.nonNull(country)) {
            JSONArray coordinates = features.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
            String type = features.getJSONObject(0).getJSONObject("geometry").getString("type");
            country.setBoundary(new GeoJsonItem("Feature", new GeoJsonItem.Geometry(type, BsonArray.parse(coordinates.toString())), null));
        } else {
            System.err.println("Turkey not found!");
        }
    }

    private final static Pattern PATTERN_NORMALIZE_TRIM_DASH = Pattern.compile("^-|-$");

    public static String slugify(String input) {
        input = input.replace('ı', 'i');
        String text = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("/", "")
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-");
        text = PATTERN_NORMALIZE_TRIM_DASH.matcher(text).replaceAll("");
        return text;
    }

    private void fillGeometryCity(List<Location> locationList) throws IOException {
        JSONObject locationJsonArray = new JSONObject(FileUtils.readFileToString(new File("anouncy/backend/location/src/main/resources/geo-data/gadm41_TUR_1.json"), StandardCharsets.UTF_8));

        JSONArray features = locationJsonArray.getJSONArray("features");
        for (int i = 0; i < features.length(); i++) {
            String cityName = features.getJSONObject(i).getJSONObject("properties").getString("NAME_1");
            Location city = locationList.stream().filter(l -> slugify(l.getName()).equalsIgnoreCase(slugify(cityName))).findFirst().orElse(null);
            if (Objects.nonNull(city)) {
                JSONArray coordinates = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
                String type = features.getJSONObject(i).getJSONObject("geometry").getString("type");
                city.setBoundary(new GeoJsonItem("Feature", new GeoJsonItem.Geometry(type, BsonArray.parse(coordinates.toString())), null));
            } else {
                System.err.println(cityName + " not found!");
            }

        }
    }

    private void fillGeometryDistinct(List<Location> locationList) throws IOException {
        JSONObject locationJsonArray = new JSONObject(FileUtils.readFileToString(new File("anouncy/backend/location/src/main/resources/geo-data/gadm41_TUR_2.json"), StandardCharsets.UTF_8));

        JSONArray features = locationJsonArray.getJSONArray("features");
        for (int i = 0; i < features.length(); i++) {
            String cityName = features.getJSONObject(i).getJSONObject("properties").getString("NAME_1");
            String distinctName = features.getJSONObject(i).getJSONObject("properties").getString("NAME_2");

            Location city = locationList.stream().filter(l -> slugify(l.getName()).equalsIgnoreCase(slugify(cityName))).findFirst().orElse(null);
            Location distinct = locationList.stream().filter(l -> slugify(l.getName()).equalsIgnoreCase(slugify(distinctName))).findFirst().orElse(null);
            if (Objects.nonNull(city) && Objects.nonNull(distinct) && city.getId().equals(distinct.getParentLocationId())) {
                JSONArray coordinates = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
                String type = features.getJSONObject(i).getJSONObject("geometry").getString("type");
                distinct.setBoundary(new GeoJsonItem("Feature", new GeoJsonItem.Geometry(type, BsonArray.parse(coordinates.toString())), null));
            } else {
                System.err.println(cityName + " " + distinctName + " not found!");
            }

        }
    }

    public Location getLatLonFromGoogleMap(String locationAddress) {
        Location result = null;
        try {
            String locationAddres = locationAddress.replaceAll(" ", "%20");
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?key=" + locationGoogleKey + "&" +
                    "address=" + locationAddres + "&sensor=true");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output = "", full = "";
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                full += output;
            }

            JSONObject json;
            Double lat, lon;
            json = new JSONObject(full);
            JSONObject geoMetryObject = new JSONObject();
            JSONObject locations = new JSONObject();
            JSONArray jarr = json.getJSONArray("results");
            int i;
            for (i = 0; i < jarr.length(); i++) {
                json = jarr.getJSONObject(i);
                geoMetryObject = json.getJSONObject("geometry");
                locations = geoMetryObject.getJSONObject("location");
                lat = Double.parseDouble(locations.getString("lat"));
                lon = Double.parseDouble(locations.getString("lng"));
                result = new Location();
                result.setLatitude(lat);
                result.setLongitude(lon);
                result.setGoogleMapApiInfo(full);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }
}
