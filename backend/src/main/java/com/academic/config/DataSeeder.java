package com.academic.config;

import com.academic.model.Course;
import com.academic.model.User;
import com.academic.repository.CourseRepository;
import com.academic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (!userRepository.existsByEmail("admin@acadchain.com")) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@acadchain.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Default admin created: admin@acadchain.com / admin123");
        }

        if (courseRepository.count() == 0) {
            String[][] courses = {
                    {"CS301", "Database Management Systems", "4", "CSE/IT"},
                    {"CS302", "Operating Systems", "3", "CSE"},
                    {"CS303", "Computer Networks", "3", "CSE/IT"},
                    {"CS304", "Design & Analysis of Algorithms", "4", "CSE"},
                    {"CS305", "Software Engineering", "3", "CSE/IT"},
                    {"CS306", "Web Technologies", "3", "CSE/IT"},
                    {"IT301", "Data Structures", "4", "IT"},
                    {"IT302", "Computer Organization", "3", "IT"}
            };

            for (String[] c : courses) {
                Course course = new Course();
                course.setCourseCode(c[0]);
                course.setCourseName(c[1]);
                course.setMaxCredits(Integer.parseInt(c[2]));
                course.setDepartment(c[3]);
                courseRepository.save(course);
            }
            System.out.println("✅ Sample courses seeded into database");
        }
    }
}