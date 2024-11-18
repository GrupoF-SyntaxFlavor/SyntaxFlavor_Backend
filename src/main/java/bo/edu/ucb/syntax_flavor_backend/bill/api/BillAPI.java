package bo.edu.ucb.syntax_flavor_backend.bill.api;

import bo.edu.ucb.syntax_flavor_backend.util.BillGenerationException;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import bo.edu.ucb.syntax_flavor_backend.bill.bl.BillBL;
import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.user.bl.UserBL;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/bill")
public class BillAPI {
    Logger LOGGER = LoggerFactory.getLogger(BillAPI.class);

    @Autowired
    private BillBL billBL;

    @Autowired
    private UserBL userBL;

    @Operation(summary = "Create bill for an Order", description = "Creates a bill for an order and sends the bill to the provided email.")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Bill created successfully"),
            @ApiResponse(responseCode = "400", description = "Something failed while creating the bill, probably part of the process still got done"),
            @ApiResponse(responseCode = "500", description = "Everything failed, the bill was not created")
        }
    )
    @PostMapping()
    public ResponseEntity<SyntaxFlavorResponse<BillResponseDTO>> createBillFromOrder(
            @RequestBody BillRequestDTO billRequest, HttpServletRequest request) {
        LOGGER.info("Endpoint POST /api/v1/bill with billRequest: {}", billRequest);
        // FIXME: No es la mejor forma de manejar el token JWT.
        // TODO: Debería ser modularizado utilizando un middleware o función dedicada para la autenticación JWT.
        SyntaxFlavorResponse<BillResponseDTO> sfr = new SyntaxFlavorResponse<>();

        try {
            // Fetch user from JWT token
            String kcUserId = (String) request.getAttribute("kcUserId");
            User user = userBL.findUserByKcUserId(kcUserId);
            billRequest.setUserId(user.getId());
            // Process bill creation
            BillResponseDTO billResponse = billBL.createBillFromOrder(billRequest);
            sfr.setResponseCode("BIL-001");
            sfr.setPayload(billResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);

        } catch (BillGenerationException e) {
            LOGGER.error("Bill generation error: {}", e.getMessage());
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sfr);

        } catch (Exception e) {
            LOGGER.error("Error creating bill: {}", e.getMessage());
            sfr.setResponseCode("BIL-600");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @GetMapping("/weekly-sales")
    public ResponseEntity<SyntaxFlavorResponse<Map<String, List<BillResponseDTO>>>> getWeeklySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        SyntaxFlavorResponse<Map<String, List<BillResponseDTO>>> response = new SyntaxFlavorResponse<>();
        try {
            LOGGER.info("Generating weekly sales report for dates: {} - {}", startDate, endDate);
            Map<String, List<BillResponseDTO>> report = billBL.getWeeklySalesReport(startDate, endDate);
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
