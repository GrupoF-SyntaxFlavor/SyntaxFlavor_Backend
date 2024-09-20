package bo.edu.ucb.syntax_flavor_backend.bill.api;

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
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/api/v1/bill")
public class BillAPI {
    Logger LOGGER = LoggerFactory.getLogger(BillAPI.class);

    @Autowired
    private BillBL billBL;

    public BillAPI(BillBL billBL) {
        this.billBL = billBL;
    }

    @Operation(summary = "Create bill for and Order", description = "Creates a bill for an order, the rquest contains the orderId, the name for the bill and the payment, a NIT code, and the total cost of the order.")
    @PostMapping()
    public ResponseEntity<SyntaxFlavorResponse<BillResponseDTO>> createBillFromOrder(@RequestBody BillRequestDTO billRequest) {
        LOGGER.info("Endpoint POST /api/v1/bill with billRequest: {}", billRequest);
        SyntaxFlavorResponse<BillResponseDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            BillResponseDTO billResponse = new BillResponseDTO(billBL.createBillFromOrder(billRequest));
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
