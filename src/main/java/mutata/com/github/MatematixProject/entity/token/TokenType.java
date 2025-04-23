package mutata.com.github.MatematixProject.entity.token;

import lombok.Getter;

/**
 * Енумерация Токенов по виду resetPassword и verification.
 */
public enum TokenType {
    RESET("resetPassword"),VERIFICATION("verification");

    @Getter
    private final String modelName;

    TokenType(String modelName) {
        this.modelName = modelName;
    }

}
