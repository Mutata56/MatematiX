/**
 * Отправляет AJAX-запрос на сервер, чтобы пометить пользователя как активного (alive).
 * При ошибке выводит сообщение в консоль.
 *
 * @function
 * @returns {void}
 */
function checkAlive() {
    $.ajax({
        // URL для обновления статуса активности пользователя
        url: "/isAlive/setAlive",
        // Таймаут запроса: 4 минуты (240000 мс)
        timeout: 240000,
        method: "GET"
    }).fail(function(error) {
        // Логируем ошибку в случае сбоя запроса
        console.error(
            "An error occurred while checking whether the user is alive",
            error
        );
    });
}

/**
 * Инициализация проверки активности пользователя при готовности документа.
 * @returns {void}
 */
$(document).ready(checkAlive);