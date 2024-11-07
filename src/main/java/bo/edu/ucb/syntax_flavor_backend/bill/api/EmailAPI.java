package bo.edu.ucb.syntax_flavor_backend.bill.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.service.EmailService;

@RestController
public class EmailAPI {

    // TODO: Eliminar o convertir en dev endpoint

    @Autowired
    private EmailService emailService;

    @PostMapping("/api/v1/send-email")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        emailService.sendEmail(to, subject, body);
        return "Email sent!";
    }
}
