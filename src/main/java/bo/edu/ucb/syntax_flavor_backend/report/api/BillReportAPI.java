package bo.edu.ucb.syntax_flavor_backend.report.api;

import bo.edu.ucb.syntax_flavor_backend.report.bl.BillReportBL;
import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1")
public class BillReportAPI {
    Logger LOGGER = LoggerFactory.getLogger(BillReportAPI.class);

    @Autowired
    private BillReportBL billBL;
    @GetMapping("/report/weekly-sales")
    public ResponseEntity<SyntaxFlavorResponse<Map<String, BigDecimal>>> getWeeklySalesReportForLastSevenWeeks() {
        SyntaxFlavorResponse<Map<String, BigDecimal>> response = new SyntaxFlavorResponse<>();
        try {
            LOGGER.info("Generating weekly sales report for the last 7 weeks.");
            Map<String, BigDecimal> report = billBL.getWeeklySalesReportForLastSevenWeeks();
            response.setResponseCode("REP-001");
            response.setPayload(report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error generating weekly sales report: {}", e.getMessage());
            response.setResponseCode("REP-500");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
