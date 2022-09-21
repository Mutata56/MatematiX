package mutata.com.github.test;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Arrays;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // IGNORE
public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private boolean active;
    private int[] nums;
    private Address mainAddress;
    private Address[] addresses;

    @Override
    public String toString() {
        return String.format("Student(%n id=%s,%n firstName=%s,%n lastName=%s,%n active=%s,%n mainAddress = %s,%n nums ="
                        + Arrays.toString(nums) + "\naddresses = " +
                        Arrays.toString(addresses) + "%n)"
                ,id,firstName,lastName,active,mainAddress);
    }
    @Data
    static class Address {
        private String zip;
        private String city;
        private String country;
        private String street;

        @Override
        public String toString() {
            return String.format("Address(zip=%s,country=%s,city=%s,street=%s)",zip,city,city,street);
        }
    }
}
