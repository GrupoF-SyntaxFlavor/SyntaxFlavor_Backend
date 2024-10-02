package bo.edu.ucb.syntax_flavor_backend.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BillGenerationException extends Exception {
    private int generationProcessErrorCode;

    public BillGenerationException(String message, int generationProcessErrorCode){
        super(message);
        this.generationProcessErrorCode = generationProcessErrorCode;
    }
}
