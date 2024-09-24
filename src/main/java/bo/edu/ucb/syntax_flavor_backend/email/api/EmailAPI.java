package bo.edu.ucb.syntax_flavor_backend.email.api;

import bo.edu.ucb.syntax_flavor_backend.email.bl.EmailBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailAPI {

    @Autowired
    private EmailBL emailService;

    @PostMapping("/api/v1/send-email")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        emailService.sendEmail(to, subject, body);
        return "Email sent!";
    }
}
