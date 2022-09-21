package mutata.com.github.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        final String PATH = "C:\\Users\\Kirill\\Downloads\\1.json";
        try {
             // Create object mapper
            ObjectMapper objectMapper = new ObjectMapper();
             // Read JSON file and convert to Java POJO
            Student theStudent = objectMapper.readValue(new File(PATH),Student.class);
             // Print obj
            System.out.println(theStudent);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
}
