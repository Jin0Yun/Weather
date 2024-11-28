package zb.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zb.weather.domain.Diary;
import zb.weather.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apiKey;
    private final DiaryRepository diaryRepository;
    private final WeatherApiClient weatherApiClient;

    public DiaryService(DiaryRepository diaryRepository, WeatherApiClient weatherApiClient) {
        this.diaryRepository = diaryRepository;
        this.weatherApiClient = weatherApiClient;
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        String weatherData = weatherApiClient.getWeatherData();
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        if (parsedWeather == null) {
            throw new IllegalArgumentException("Failed to parse weather data");
        }

        Diary nowDiary = buildDiary(date, text, parsedWeather);
        diaryRepository.save(nowDiary);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }
    @Transactional
    public void deleteDiary(LocalDate date) {
        List<Diary> diaries = diaryRepository.findAllByDate(date);
        if (diaries.isEmpty()) {
            throw new IllegalArgumentException("No diaries found for the specified date");
        }
        diaryRepository.deleteAllByDate(date);
    }

    private Map<String, Object> parseWeather(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing weather data", e);
        }

        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");

        if (mainData != null) {
            resultMap.put("temp", mainData.get("temp"));
        }

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        if (weatherArray != null && !weatherArray.isEmpty()) {
            JSONObject weatherData = (JSONObject) weatherArray.get(0);
            resultMap.put("main", weatherData.get("main"));
            resultMap.put("icon", weatherData.get("icon"));
        }
        return resultMap;
    }

    private Diary buildDiary(LocalDate date, String text, Map<String, Object> weatherData) {
        Diary diary = new Diary();
        diary.setWeather(weatherData.get("main").toString());
        diary.setIcon(weatherData.get("icon").toString());
        diary.setTemperature((Double) weatherData.get("temp"));
        diary.setText(text);
        diary.setDate(date);
        return diary;
    }
}
