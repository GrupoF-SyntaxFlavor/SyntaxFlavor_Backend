package bo.edu.ucb.syntax_flavor_backend.report.api;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.report.bl.OrderReportBL;
import bo.edu.ucb.syntax_flavor_backend.report.dto.OrderReportKPIResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(value = "/api/v1")
public class OrderReportAPI {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderReportAPI.class);

    @Autowired
    private OrderReportBL orderReportBL;


    //TODO: añadir operation y api responses
    @Operation(summary = " ", description = " ")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "List of orders returned successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/report/order-kpi")
    public ResponseEntity<SyntaxFlavorResponse<OrderReportKPIResponseDTO>> getOrderKpi(@RequestParam("startDate") LocalDateTime startDate,
                                                                                        @RequestParam("endDate") LocalDateTime endDate
    ) {
        LOGGER.info("Endpoint GET /api/v1/report/order-kpi with startDate {}, endDate {}", startDate, endDate);
        SyntaxFlavorResponse<OrderReportKPIResponseDTO> sfr = new SyntaxFlavorResponse<>();
        
        try {
            OrderReportKPIResponseDTO orders = orderReportBL.calculateOrderKPI(startDate, endDate);
            if(orders.getTotalOrders()<1){
                sfr.setResponseCode("ORD-404");
                sfr.setErrorMessage("No orders found between "+startDate+" and "+endDate);
                return ResponseEntity.ok(sfr);
            }
            sfr.setResponseCode("ORD-000");
            sfr.setPayload(orders);
            return ResponseEntity.ok(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-600");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }
}
