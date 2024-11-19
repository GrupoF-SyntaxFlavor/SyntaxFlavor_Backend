package bo.edu.ucb.syntax_flavor_backend.report.bl;

import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import bo.edu.ucb.syntax_flavor_backend.bill.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BillReportBL {
    @Autowired
    private BillRepository billRepository;

    public Map<String, List<BillResponseDTO>> getWeeklySalesReport(Date startDate, Date endDate) {
        // Retrieve bills from repository
        List<Bill> bills = billRepository.findBillsBetweenDates(startDate, endDate);

        Map<String, List<BillResponseDTO>> weeklyReport = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();

        for (Bill bill : bills) {
            // Convert Bill entity to DTO
            BillResponseDTO billDTO = new BillResponseDTO(bill);

            // Calculate the week key
            calendar.setTime(bill.getCreatedAt());
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            String yearWeekKey = calendar.get(Calendar.YEAR) + "-W" + week;

            // Group DTOs by week
            weeklyReport.computeIfAbsent(yearWeekKey, k -> new ArrayList<>()).add(billDTO);
        }

        return weeklyReport;
    }
}
