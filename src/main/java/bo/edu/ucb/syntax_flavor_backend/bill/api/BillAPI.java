package bo.edu.ucb.syntax_flavor_backend.bill.api;

import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.bill.bl.BillBL;
import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.service.EmailService;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping(value = "/api/v1/bill")
public class BillAPI {
    Logger LOGGER = LoggerFactory.getLogger(BillAPI.class);

    @Autowired
    private BillBL billBL;

    @Autowired
    private EmailService emailService;

    public BillAPI(BillBL billBL) {
        this.billBL = billBL;
    }

    @Operation(summary = "Create bill for an Order", description = "Creates a bill for an order and sends the bill to the provided email.")
    @PostMapping()
    public ResponseEntity<SyntaxFlavorResponse<BillResponseDTO>> createBillFromOrder(@RequestBody BillRequestDTO billRequest) {
        LOGGER.info("Endpoint POST /api/v1/bill with billRequest: {}", billRequest);
        SyntaxFlavorResponse<BillResponseDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            // Create the bill from order
            Bill createdBill = billBL.createBillFromOrder(billRequest);

            // Create the BillResponseDTO from the created Bill entity
            BillResponseDTO billResponse = new BillResponseDTO(createdBill);

            // Generate the PDF of the bill using the created Bill object
            ByteArrayOutputStream pdfStream = billBL.generateBillPdf(createdBill);

            // Send the email with the bill attached
            String emailSubject = "Your Bill from Syntax Flavor";
            String emailBody = "Dear " + billRequest.getCustomerName() + ",\n\nAttached is your bill. Thank you for your purchase.";
            emailService.sendEmailWithAttachment(
                    billRequest.getCustomerEmail(),
                    emailSubject,
                    emailBody,
                    pdfStream.toByteArray(),
                    "bill.pdf"
            );

            // Respond with success
            sfr.setResponseCode("BIL-001");
            sfr.setPayload(billResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("BIL-600");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

}
