package com.wms.backend.config;

import com.wms.backend.domain.enums.BinType;
import com.wms.backend.domain.enums.Role;
import com.wms.backend.domain.model.CollectionSchedule;
import com.wms.backend.domain.model.User;
import com.wms.backend.repo.CollectionScheduleRepository;
import com.wms.backend.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(
            CollectionScheduleRepository scheduleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (scheduleRepository.count() == 0) {
                // Example LGA rules for local dev/demo; replace with actual council rules.
                seedSchedule(scheduleRepository, "Ikeja", BinType.GENERAL_WASTE, DayOfWeek.MONDAY, LocalTime.of(6, 0));
                seedSchedule(scheduleRepository, "Ikeja", BinType.RECYCLING, DayOfWeek.WEDNESDAY, LocalTime.of(6, 0));
                seedSchedule(scheduleRepository, "Ikeja", BinType.GARDEN, DayOfWeek.FRIDAY, LocalTime.of(6, 0));
            }

            if (userRepository.findByEmailIgnoreCase("finance@demo.ng").isEmpty()) {
                User finance = new User();
                finance.setEmail("finance@demo.ng");
                finance.setRole(Role.FINANCE_OFFICER);
                finance.setPasswordHash(passwordEncoder.encode("OTP_LOGIN_ONLY"));
                userRepository.save(finance);
            }

            if (userRepository.findByEmailIgnoreCase("superadmin@demo.ng").isEmpty()) {
                User admin = new User();
                admin.setEmail("superadmin@demo.ng");
                admin.setRole(Role.SUPER_ADMIN);
                admin.setPasswordHash(passwordEncoder.encode("OTP_LOGIN_ONLY"));
                userRepository.save(admin);
            }

            if (userRepository.findByEmailIgnoreCase("resident@demo.ng").isEmpty()) {
                User resident = new User();
                resident.setEmail("resident@demo.ng");
                resident.setRole(Role.RESIDENT);
                resident.setPasswordHash(passwordEncoder.encode("OTP_LOGIN_ONLY"));
                userRepository.save(resident);
            }

            if (userRepository.findByEmailIgnoreCase("fleet@demo.ng").isEmpty()) {
                User fleet = new User();
                fleet.setEmail("fleet@demo.ng");
                fleet.setRole(Role.FLEET_MANAGER);
                fleet.setPasswordHash(passwordEncoder.encode("OTP_LOGIN_ONLY"));
                userRepository.save(fleet);
            }

            if (userRepository.findByEmailIgnoreCase("route@demo.ng").isEmpty()) {
                User route = new User();
                route.setEmail("route@demo.ng");
                route.setRole(Role.ROUTE_SUPERVISOR);
                route.setPasswordHash(passwordEncoder.encode("OTP_LOGIN_ONLY"));
                userRepository.save(route);
            }

            if (userRepository.findByEmailIgnoreCase("contractor@demo.ng").isEmpty()) {
                User contractor = new User();
                contractor.setEmail("contractor@demo.ng");
                contractor.setRole(Role.CONTRACTOR);
                contractor.setPasswordHash(passwordEncoder.encode("OTP_LOGIN_ONLY"));
                userRepository.save(contractor);
            }
        };
    }

    private void seedSchedule(CollectionScheduleRepository repo, String lga, BinType type, DayOfWeek day, LocalTime time) {
        CollectionSchedule s = new CollectionSchedule();
        s.setLga(lga);
        s.setBinType(type);
        s.setCollectionDay(day);
        s.setCollectionTime(time);
        s.setActive(true);
        repo.save(s);
    }
}

