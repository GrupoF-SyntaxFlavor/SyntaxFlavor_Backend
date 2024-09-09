package bo.edu.ucb.syntax_flavor_backend.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntaxFlavorResponse<O> {
    private String responseCode;
    private O payload;
    private String errorMessage;
}
