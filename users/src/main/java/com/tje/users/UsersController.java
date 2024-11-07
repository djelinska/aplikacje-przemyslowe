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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/list")
    public String list(Model model) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date1 = df.parse("01/01/2021 08:00");
        Date date2 = df.parse("15/03/2021 10:30");
        Date date3 = df.parse("10/05/2022 14:45");
        Date date4 = df.parse("22/08/2023 17:00");
        Date date5 = df.parse("11/11/2023 12:00");

        List<User> users = new ArrayList<>();
        users.add(new User(1, "Anna", 28, User.UserType.REGISTERED, date1));
        users.add(new User(2, "Bartosz", 40, User.UserType.ADMIN, date2));
        users.add(new User(3, "Celina", 22, User.UserType.GUEST, date3));
        users.add(new User(4, "Damian", 36, User.UserType.REGISTERED, date4));
        users.add(new User(5, "Ela", 29, User.UserType.ADMIN, date5));

        model.addAttribute("users", users);
        model.addAttribute("userTypeStrList", users.stream().map(user -> user.getUserType().toString()).collect(Collectors.toList()));

        return "list";
    }
}
