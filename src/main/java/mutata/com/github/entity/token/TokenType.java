package mutata.com.github.entity.token;

import lombok.Getter;

public enum TokenType {
    RESET("resetPassword"),VERIFICATION("verification");

    @Getter
    private final String modelName;

    TokenType(String modelName) {
        this.modelName = modelName;
    }

}
