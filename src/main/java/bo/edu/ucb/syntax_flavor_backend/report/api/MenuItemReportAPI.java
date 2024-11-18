package bo.edu.ucb.syntax_flavor_backend.report.api;

import java.util.List;

import bo.edu.ucb.syntax_flavor_backend.report.bl.MenuItemReportBL;
import bo.edu.ucb.syntax_flavor_backend.report.dto.MostSoldMenuItemDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1")
public class MenuItemReportAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemReportAPI.class);

    private final MenuItemReportBL menuItemReportBL;

    public MenuItemReportAPI(MenuItemReportBL menuItemReportBL) {
        this.menuItemReportBL = menuItemReportBL;
    }

    // Endpoint para obtener el reporte de los platos más vendidos
    @Operation(summary = "Get most sold menu items", description = "Returns a report of the most sold menu items based on the selected period")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Report retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid period specified"),
            @ApiResponse(responseCode = "500", description = "Error generating the report")
        }
    )
    @GetMapping("/report/menu/items")
    public ResponseEntity<SyntaxFlavorResponse<List<MostSoldMenuItemDTO>>> getMostSoldMenuItems(
            @RequestParam String period, HttpServletRequest request) {
        LOGGER.info("Endpoint GET /api/v1/report/menu/items called with period: {}", period);
        SyntaxFlavorResponse<List<MostSoldMenuItemDTO>> sfrResponse = new SyntaxFlavorResponse<>();

        try {
            // Llamar a la lógica de negocio para obtener el reporte
            List<MostSoldMenuItemDTO> report = menuItemReportBL.getMostSoldMenuItems(period);

            sfrResponse.setResponseCode("REP-000");
            sfrResponse.setPayload(report);
            LOGGER.info("Returning report: {}", report);
            return ResponseEntity.ok(sfrResponse);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid period specified: {}", e.getMessage());
            sfrResponse.setResponseCode("REP-400");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error generating report: {}", e.getMessage());
            sfrResponse.setResponseCode("REP-500");
            sfrResponse.setErrorMessage("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }
}
