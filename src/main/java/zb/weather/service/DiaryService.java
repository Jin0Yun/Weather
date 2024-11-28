package zb.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zb.weather.domain.DateWeather;
import zb.weather.domain.Diary;
import zb.weather.repository.DateWeatherRepository;
import zb.weather.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.Date;
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
    private final DateWeatherRepository dateWeatherRepository;

    public DiaryService(DiaryRepository diaryRepository, WeatherApiClient weatherApiClient, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.weatherApiClient = weatherApiClient;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate () {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi() {
        String weatherData = weatherApiClient.getWeatherData();
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        return dateWeather;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        DateWeather dateWeather = getDateWeather(date);
        if (dateWeather == null) {
            throw new IllegalArgumentException("Failed to parse weather data");
        }

        Diary nowDiary = buildDiary(date, text, dateWeather);
        diaryRepository.save(nowDiary);
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findByDate(date);
        if (dateWeatherListFromDB.isEmpty()) {
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
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

    private Diary buildDiary(LocalDate date, String text, DateWeather dateWeather) {
        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText(text);
        return diary;
    }
}
