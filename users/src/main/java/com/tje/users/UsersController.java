package com.tje.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Controller
public class UsersController {

    @GetMapping("/")
    public String home(Model model) throws ParseException {
        String startDateString = "20/05/2007 07:32";
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date startDate = df.parse(startDateString);

        LocalDateTime registrationDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();
        long daysSinceRegistration = Duration.between(registrationDateTime, currentDateTime).toDays();

        User user = new User(2, "Artur", 29, User.UserType.ADMIN, startDate);
        model.addAttribute("user", user);
        model.addAttribute("userTypeStr", user.getUserType().toString());
        model.addAttribute("daysSinceRegistration", daysSinceRegistration);

        return "home";
    }
}
