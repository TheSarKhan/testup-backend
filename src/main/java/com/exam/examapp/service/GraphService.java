package com.exam.examapp.service;

import com.exam.examapp.dto.response.GraphResponse;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.repository.PaymentResultRepository;
import com.exam.examapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GraphService {
    private final UserRepository userRepository;

    private final PaymentResultRepository paymentResultRepository;

    private static Map<Integer, Double> mapYearsToValues(List<Double> yearValues) {
        Map<Integer, Double> yearToMap = new LinkedHashMap<>();
        int currentYear = LocalDate.now().getYear();

        for (int i = 0; i < yearValues.size(); i++) {
            int targetYear = currentYear - i;
            yearToMap.put(targetYear, yearValues.get(i));
        }

        return yearToMap;
    }

    private static Map<String, Double> mapDaysToValues(List<Double> dayValues) {
        Map<String, Double> dayToMap = new LinkedHashMap<>();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        String[] dayNames = {
                "Bazar ertəsi", "Çərşənbə axşamı", "Çərşənbə",
                "Cümə axşamı", "Cümə", "Şənbə", "Bazar"
        };

        for (int i = 0; i < dayValues.size(); i++) {
            LocalDate targetDate = today.minusDays(i);
            DayOfWeek dow = targetDate.getDayOfWeek();
            String dayName = dayNames[dow.getValue() - 1];
            dayToMap.put(dayName, dayValues.get(i));
        }

        return dayToMap;
    }

    private static Map<String, Double> mapMonthsToValues(List<Double> monthValues) {
        Map<String, Double> monthToMap = new LinkedHashMap<>();
        ZoneId zone = ZoneId.systemDefault();
        YearMonth currentMonth = YearMonth.now(zone);

        String[] monthNames = {
                "Yanvar", "Fevral", "Mart", "Aprel", "May", "İyun",
                "İyul", "Avqust", "Sentyabr", "Oktyabr", "Noyabr", "Dekabr"
        };

        for (int i = 0; i < monthValues.size(); i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            String monthName = monthNames[targetMonth.getMonthValue() - 1];
            monthToMap.put(monthName, monthValues.get(i));
        }

        return monthToMap;
    }

    public GraphResponse fillGraph(List<List<Double>> lists) {
        Map<Integer, Double> yearToMap = mapYearsToValues(lists.getFirst());
        Map<String, Double> monthToMap = mapMonthsToValues(lists.get(1));
        Map<String, Double> dayToMap = mapDaysToValues(lists.get(2));

        return new GraphResponse(yearToMap, monthToMap, dayToMap);
    }

    public List<List<Double>> getStudentRegisterGraph() {
        List<Double> yearStudentRegister = getYearlyRegistrationCounts(Role.STUDENT);

        List<Double> monthStudentRegister = getMonthlyRegistrationCounts(Role.STUDENT);

        List<Double> dailyStudentRegister = getDailyRegistrationCounts(Role.STUDENT);

        return new ArrayList<>(Arrays.asList(yearStudentRegister, monthStudentRegister, dailyStudentRegister));
    }

    public List<List<Double>> getTeacherRegisterGraph() {
        List<Double> yearTeacherRegister = getYearlyRegistrationCounts(Role.TEACHER);

        List<Double> monthTeacherRegister = getMonthlyRegistrationCounts(Role.TEACHER);

        List<Double> dailyTeacherRegister = getDailyRegistrationCounts(Role.TEACHER);

        return new ArrayList<>(Arrays.asList(yearTeacherRegister, monthTeacherRegister, dailyTeacherRegister));
    }

    private List<Double> getDailyRegistrationCounts(Role role) {
        List<Double> dailyRegister = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        for (int i = 0; i < 7; i++) {
            LocalDate day = today.minusDays(i);

            Instant startOfDay = day.atStartOfDay(zone).toInstant();
            Instant endOfDay = day.plusDays(1).atStartOfDay(zone).toInstant();

            Double count = (double) userRepository.countByCreatedAtBetweenAndRole(startOfDay, endOfDay, role);
            dailyRegister.add(count);
        }
        return dailyRegister;
    }

    private List<Double> getMonthlyRegistrationCounts(Role role) {
        List<Double> monthRegister = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();
        YearMonth currentMonth = YearMonth.now(zone);

        for (int i = 0; i < 12; i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);

            LocalDate startOfMonth = targetMonth.atDay(1);
            LocalDate startOfNextMonth = targetMonth.plusMonths(1).atDay(1);

            Instant startInstant = startOfMonth.atStartOfDay(zone).toInstant();
            Instant endInstant = startOfNextMonth.atStartOfDay(zone).toInstant();

            Double count = (double) userRepository.countByCreatedAtBetweenAndRole(startInstant, endInstant, role);
            monthRegister.add(count);
        }
        return monthRegister;
    }

    private List<Double> getYearlyRegistrationCounts(Role role) {
        List<Double> yearRegister = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();
        int currentYear = LocalDate.now(zone).getYear();
        for (int i = 0; i < 5; i++) {
            LocalDate startOfYear = LocalDate.of(currentYear - i, 1, 1);
            LocalDate endOfYear = LocalDate.of(currentYear - i + 1, 1, 1);

            Instant startInstant = startOfYear.atStartOfDay(zone).toInstant();
            Instant endInstant = endOfYear.atStartOfDay(zone).toInstant();

            Double count = (double) userRepository.countByCreatedAtBetweenAndRole(startInstant, endInstant, role);

            yearRegister.add(count);
        }
        return yearRegister;
    }

    public List<List<Double>> getProfitGraph() {
        List<Double> yearProfit = calculateYearlyProfits();

        List<Double> monthProfit = calculateMonthlyProfits();

        List<Double> dailyProfit = calculateDailyProfit();

        return new ArrayList<>(Arrays.asList(yearProfit, monthProfit, dailyProfit));
    }

    private List<Double> calculateDailyProfit() {
        List<Double> dailyProfit = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        for (int i = 0; i < 7; i++) {
            LocalDate day = today.minusDays(i);

            Instant startOfDay = day.atStartOfDay(zone).toInstant();
            Instant endOfDay = day.plusDays(1).atStartOfDay(zone).toInstant();

            Double total = paymentResultRepository.getAmountPaidByRange(startOfDay, endOfDay);
            dailyProfit.add(total != null ? total : 0.0);
        }
        return dailyProfit;
    }

    private List<Double> calculateMonthlyProfits() {
        List<Double> monthProfit = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();
        YearMonth currentMonth = YearMonth.now(zone);

        for (int i = 0; i < 12; i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);

            LocalDate startOfMonth = targetMonth.atDay(1);
            LocalDate startOfNextMonth = targetMonth.plusMonths(1).atDay(1);

            Instant startInstant = startOfMonth.atStartOfDay(zone).toInstant();
            Instant endInstant = startOfNextMonth.atStartOfDay(zone).toInstant();

            Double total = paymentResultRepository.getAmountPaidByRange(startInstant, endInstant);
            monthProfit.add(total != null ? total : 0.0);
        }
        return monthProfit;
    }

    private List<Double> calculateYearlyProfits() {
        List<Double> yearProfit = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();
        int currentYear = LocalDate.now(zone).getYear();
        for (int i = 0; i < 5; i++) {
            LocalDate startOfYear = LocalDate.of(currentYear - i, 1, 1);
            LocalDate endOfYear = LocalDate.of(currentYear - i + 1, 1, 1);

            Instant startInstant = startOfYear.atStartOfDay(zone).toInstant();
            Instant endInstant = endOfYear.atStartOfDay(zone).toInstant();

            Double total = paymentResultRepository.getAmountPaidByRange(startInstant, endInstant);

            yearProfit.add(total != null ? total : 0.0);
        }
        return yearProfit;
    }
}
