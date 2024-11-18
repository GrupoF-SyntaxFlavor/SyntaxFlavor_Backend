package bo.edu.ucb.syntax_flavor_backend.report.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MostSoldMenuItemDTO {

    private Integer menuItemId;      // ID del plato (opcional si no es necesario en frontend)
    private String menuItemName;    // Nombre del plato
    private BigDecimal totalPrice;  // Ingresos totales generados por el plato
    private Long totalQuantity;     // Cantidad total vendida
    private Double percentage;      // Porcentaje de ventas
}
