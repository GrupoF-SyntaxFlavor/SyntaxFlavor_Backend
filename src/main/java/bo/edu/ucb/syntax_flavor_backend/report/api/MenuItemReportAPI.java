package bo.edu.ucb.syntax_flavor_backend.report.api;

import java.time.LocalDateTime;
import java.util.List;

import bo.edu.ucb.syntax_flavor_backend.report.bl.MenuItemReportBL;
import bo.edu.ucb.syntax_flavor_backend.report.dto.MostSoldMenuItemDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
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
    @GetMapping("/report/menu/most-sold")
    @Operation(summary = "Get most sold menu items", description = "Returns a report of the most sold menu items in a given date range")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range provided"),
            @ApiResponse(responseCode = "500", description = "Error generating the report")
        }
    )
    public ResponseEntity<SyntaxFlavorResponse<List<MostSoldMenuItemDTO>>> getMostSoldMenuItemsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        SyntaxFlavorResponse<List<MostSoldMenuItemDTO>> response = new SyntaxFlavorResponse<>();
        try {
            LOGGER.info("Generating report for most sold menu items from {} to {}", startDate, endDate);

            // Llama al método del BL para obtener los datos
            List<MostSoldMenuItemDTO> report = menuItemReportBL.getMostSoldMenuItems(startDate, endDate);

            response.setResponseCode("REP-001");
            response.setPayload(report);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid date range: {}", e.getMessage());
            response.setResponseCode("REP-400");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            LOGGER.error("Error generating the report: {}", e.getMessage());
            response.setResponseCode("REP-500");
            response.setErrorMessage("An unexpected error occurred while generating the report.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
