package mutata.com.github.MatematixProject.rest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserErrorResponse {

    private int status;
    private String message;
    private long timeStamp;

}
