package zb.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zb.weather.domain.Diary;
import zb.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @ApiOperation(value = "일기 생성", notes = "날짜와 일기 내용을 입력받아 해당 날짜의 일기를 저장합니다.")
    @PostMapping("/create/diary")
    public ResponseEntity<String> createDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "일기를 작성할 날짜", example = "2024-11-29") LocalDate date,
            @RequestBody
            @ApiParam(value = "일기 내용") String text
    ) {
        try {
            diaryService.createDiary(date, text);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Diary created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred");
        }
    }

    @ApiOperation(value = "특정 날짜의 일기 조회", notes = "입력한 날짜의 모든 일기 데이터를 반환합니다.")
    @GetMapping("/read/diary")
    public List<Diary> readDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 날짜", example = "2024-11-29") LocalDate date
    ) {
        return diaryService.readDiary(date);
    }

    @ApiOperation(value = "기간 내 일기 조회", notes = "입력한 시작 날짜부터 종료 날짜까지의 모든 일기 데이터를 반환합니다.")
    @GetMapping("/read/diaries")
    public List<Diary> readDiaries(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 시작 날짜", example = "2024-11-01") LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 종료 날짜", example = "2024-11-29") LocalDate endDate
    ) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation(value = "일기 수정", notes = "입력한 날짜의 일기 내용을 새로운 내용으로 수정합니다.")
    @PutMapping("/update/diary")
    public void updateDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "수정할 날짜", example = "2024-11-29") LocalDate date,
            @RequestBody
            @ApiParam(value = "새로운 일기 내용") String text
    ) {
        diaryService.updateDiary(date, text);
    }

    @ApiOperation(value = "일기 삭제", notes = "입력한 날짜의 모든 일기 데이터를 삭제합니다.")
    @DeleteMapping("/delete/diary")
    public void deleteDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "삭제할 날짜", example = "2024-11-29") LocalDate date
    ) {
        diaryService.deleteDiary(date);
    }
}
