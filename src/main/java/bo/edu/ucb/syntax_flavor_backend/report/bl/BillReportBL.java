package bo.edu.ucb.syntax_flavor_backend.report.bl;

import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import bo.edu.ucb.syntax_flavor_backend.bill.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class BillReportBL {
    @Autowired
    private BillRepository billRepository;

    public Map<String, BigDecimal> getWeeklySalesReportForLastSevenWeeks() {
        // Initialize result map
        Map<String, BigDecimal> weeklySalesReport = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();

        // Dynamically calculate the starting date (7 weeks ago)
        calendar.add(Calendar.WEEK_OF_YEAR, -6); // Go back 6 weeks
        Date sevenWeeksAgo = calendar.getTime();
        Date today = new Date(); // Current date

        // Retrieve all bills from the last 7 weeks
        List<Bill> bills = billRepository.findBillsBetweenDates(sevenWeeksAgo, today);

        // Prepare the week keys for the last 7 weeks
        calendar.setTime(today);
        for (int i = 0; i < 7; i++) {
            String weekKey = calendar.get(Calendar.YEAR) + "-W" + calendar.get(Calendar.WEEK_OF_YEAR);
            weeklySalesReport.put(weekKey, BigDecimal.ZERO); // Initialize each week with 0 sales
            calendar.add(Calendar.WEEK_OF_YEAR, -1); // Move to the previous week
        }

        // Group bills and sum their totalCost for each week
        Calendar billCalendar = Calendar.getInstance();
        for (Bill bill : bills) {
            billCalendar.setTime(bill.getCreatedAt());
            String weekKey = billCalendar.get(Calendar.YEAR) + "-W" + billCalendar.get(Calendar.WEEK_OF_YEAR);

            // Update the total sales for the corresponding week
            if (weeklySalesReport.containsKey(weekKey)) {
                weeklySalesReport.put(weekKey, weeklySalesReport.get(weekKey).add(bill.getTotalCost()));
            }
        }

        return weeklySalesReport;
    }
}
