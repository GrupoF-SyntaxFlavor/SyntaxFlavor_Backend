package bo.edu.ucb.syntax_flavor_backend.bill.api;

import bo.edu.ucb.syntax_flavor_backend.util.BillGenerationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.bill.bl.BillBL;
import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/api/v1/bill")
public class BillAPI {
    Logger LOGGER = LoggerFactory.getLogger(BillAPI.class);

    @Autowired
    private BillBL billBL;

    @Operation(summary = "Create bill for an Order", description = "Creates a bill for an order and sends the bill to the provided email.")
    @PostMapping()
    public ResponseEntity<SyntaxFlavorResponse<BillResponseDTO>> createBillFromOrder(
            @RequestBody BillRequestDTO billRequest, HttpServletRequest request) {
        LOGGER.info("Endpoint POST /api/v1/bill with billRequest: {}", billRequest);
        SyntaxFlavorResponse<BillResponseDTO> sfr = new SyntaxFlavorResponse<>();

        // Extract JWT from Authorization header
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            sfr.setResponseCode("BIL-401");
            sfr.setErrorMessage("Unauthorized: No token provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(sfr);
        }

        try {
            token = token.substring(7); // Remove "Bearer " prefix
            String userId = JWT.decode(token).getSubject(); // Decode JWT to get user ID

            LOGGER.info("Token verified, userId: {}", userId);

            // Process bill creation
            BillResponseDTO billResponse = billBL.createBillFromOrder(billRequest);
            sfr.setResponseCode("BIL-001");
            sfr.setPayload(billResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);

        } catch (JWTDecodeException e) {
            LOGGER.error("Invalid token: {}", e.getMessage());
            sfr.setResponseCode("BIL-401");
            sfr.setErrorMessage("Unauthorized: Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(sfr);

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
}
