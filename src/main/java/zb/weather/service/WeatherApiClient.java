package zb.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class WeatherApiClient {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApiClient.class);

    @Value("${openweathermap.key}")
    private String apiKey;

    @Value("${openweathermap.url}")
    private String apiUrl;

    public String getWeatherData() {
        String urlString = String.format("%s%s", apiUrl, apiKey);
        HttpURLConnection connection = null;
        BufferedReader br = null;

        try {
            // URL 연결 설정
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.info("Weather data fetched successfully.");
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                logger.error("Failed to fetch weather data. Response code: " + responseCode);
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            // 응답 받기
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } catch (Exception e) {
            logger.error("Error fetching weather data", e);
            return null;
        } finally {
            // 자원 해제
            try {
                if (br != null) br.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                logger.error("Error closing resources", e);
            }
        }
    }
}
