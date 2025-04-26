package mutata.com.github.MatematixProject.util;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * Утилитарный класс для формирования JavaScript-сообщений через плагин Toastr.
 * Позволяет отображать уведомления разных типов (успех, ошибка, предупреждение, информация)
 * и работать с результатами валидации в Spring MVC.
 */
public class Toastr {
    /**
     * Формирует JS-код для отображения сообщения об ошибке.
     * @param header заголовок уведомления
     * @param message текст уведомления
     * @param options дополнительные параметры Toastr (строки вида "preventDuplicates = true" и т.д.)
     * @return строка с JS-кодом для вызова toastr.error
     */
    public static String showError(String header, String message, String... options) {
        return parse(Action.ERROR, header, message, options);
    }

    /**
     * Формирует JS-код для отображения предупреждающего сообщения.
     * @param header заголовок уведомления
     * @param message текст уведомления
     * @param options дополнительные параметры Toastr
     * @return строка с JS-кодом для вызова toastr.warning
     */
    public static String showWarning(String header, String message, String... options) {
        return parse(Action.WARNING, header, message, options);
    }

    /**
     * Формирует JS-код для отображения сообщения об успешной операции.
     * @param header заголовок уведомления
     * @param message текст уведомления
     * @param options дополнительные параметры Toastr
     * @return строка с JS-кодом для вызова toastr.success
     */
    public static String showSuccess(String header, String message, String... options) {
        return parse(Action.SUCCESS, header, message, options);
    }

    /**
     * Формирует JS-код для отображения информационного сообщения.
     * @param header заголовок уведомления
     * @param message текст уведомления
     * @param options дополнительные параметры Toastr
     * @return строка с JS-кодом для вызова toastr.info
     */
    public static String showInfo(String header, String message, String... options) {
        return parse(Action.INFO, header, message, options);
    }

    /**
     * Вспомогательный метод для генерации строки с настройками toastr.options.*
     * @param options массив строк, описывающих параметры плагина Toastr
     * @return JS-код установки опций Toastr
     */
    private static String parseOptions(String... options) {
        final String preString = "toastr.options.";
        StringBuilder builder = new StringBuilder();
        for (String opt : options) {
            builder.append(preString).append(opt).append(";\n");
        }
        return builder.toString();
    }

    /**
     * Основной метод сборки JS-кода вызова toastr.
     * @param action тип уведомления (ERROR, WARNING, SUCCESS, INFO)
     * @param header заголовок
     * @param message текст уведомления
     * @param options дополнительные параметры Toastr
     * @return строка с полным JS-кодом для вставки на страницу
     */
    private static String parse(Action action, String header, String message, String... options) {
        return parseOptions(options)
                + "toastr." + action.action + "(\"" + message + "\",\"" + header + "\");";
    }

    /**
     * Добавляет все ошибки валидации в модель и формирует параметр для Toastr.
     * @param model объект Model MVC, в который добавляются атрибуты "message" и "success"
     * @param result результат валидации BindingResult
     */
    public static void addErrorsToModel(Model model, BindingResult result) {
        StringBuilder builder = new StringBuilder();
        for (ObjectError err : result.getAllErrors()) {
            builder.append(err.getDefaultMessage()).append("|");
        }
        model.addAttribute("message", builder.toString());
        model.addAttribute("success", 0);
    }

    /**
     * Добавляет единичное текстовое сообщение об ошибке в модель для Toastr.
     * @param model объект Model MVC
     * @param message текст ошибки
     */
    public static void addErrorToModel(Model model, String message) {
        Object existing = model.getAttribute("message");
        String combined = (existing != null ? existing + "\n" : "") + message;
        model.addAttribute("message", combined);
        model.addAttribute("success", 0);
    }

    /**
     * Внутреннее перечисление типов уведомлений Toastr.
     */
    private enum Action {
        SUCCESS("success"),
        WARNING("warning"),
        ERROR("error"),
        INFO("info");

        private final String action;
        Action(String action) { this.action = action; }
    }
}
