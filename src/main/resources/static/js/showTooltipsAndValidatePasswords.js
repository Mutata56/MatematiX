/**
 * Проверяет совпадение паролей и валидность длины основного пароля,
 * обновляя соответствующие индикаторы на странице.
 *
 * @param {boolean} data - результат сравнения паролей: true если пароли НЕ совпадают, false если совпадают
 */
let funcPasswordAgain = (data) => {
    /** @type {jQuery<HTMLElement>} Индикатор совпадения паролей */
    let doPasswordsMatch = $("#doPasswordsMatch"),
        /** @type {jQuery<HTMLElement>} Индикатор допустимой длины пароля */
        isPasswordLengthValid = $("#isPasswordLengthValid"),
        /** @type {jQuery<HTMLElement>} Поле ввода основного пароля */
        password = $("#password"),
        /** @type {number} Длина введённого основного пароля */
        passwordLength = password.val().length;

    // Если data === false (пароли совпадают), отмечаем успешное совпадение
    if (!data) {
        doPasswordsMatch.addClass("valid");
    } else {
        doPasswordsMatch.removeClass("valid");
    }

    // Проверяем допустимую длину пароля (>4 и <61 символов)
    if (passwordLength > 4 && passwordLength < 61) {
        isPasswordLengthValid.addClass("valid");
    } else {
        isPasswordLengthValid.removeClass("valid");
    }
};

/**
 * Инициализация обработчиков полей пароля при загрузке документа.
 */
$(document).ready(() => {
    /**
     * Отправляет AJAX-запрос на проверку совпадения паролей с дебаунсом.
     * @param {jQuery.Event} event - событие клавиатурного ввода
     */
    $("#passwordAgain").bind("keyup", delay(() => {
        $.ajax({
            url: "/auth/ajax/doPasswordsNotMatch",
            type: "GET",
            data: {
                passwordAgain: $("#passwordAgain").val(),
                password: $("#password").val()
            },
            success: funcPasswordAgain
        });
    }, 700));

    /**
     * Отправляет AJAX-запрос на проверку совпадения паролей при вводе основного пароля.
     * Дебаунс аналогично полю passwordAgain.
     */
    $("#password").bind("keyup", delay(() => {
        $.ajax({
            url: "/auth/ajax/doPasswordsNotMatch",
            type: "GET",
            data: {
                passwordAgain: $("#passwordAgain").val(),
                password: $("#password").val()
            },
            success: funcPasswordAgain
        });
    }, 700));

    /**
     * Настройка видимости тултипов при фокусе на соответствующих полях ввода.
     */
    $(".tooltip").each(function () {
        /** @type {string} Идентификатор элемента, к которому привязан тултип */
        let id = $(this).attr("content"),
            /** @type {jQuery<HTMLElement>} Элемент поля ввода по идентификатору */
            element = $("#" + id);

        // Показать тултип при фокусе
        element.focus(() => {
            $(this).css({ visibility: "visible", opacity: "1" });
        });
        // Скрыть тултип при потере фокуса
        element.focusout(() => {
            $(this).css({ visibility: "hidden", opacity: "0" });
        });
    });
});