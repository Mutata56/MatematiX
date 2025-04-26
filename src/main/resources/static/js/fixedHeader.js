/**
 * Инициализирует «липкий» (fixed) заголовок при прокрутке страницы.
 *
 * При прокрутке вниз, когда высота прокрутки превышает высоту элемента .header,
 * заголовок получает классы для фиксирования сверху, а для следующего за ним элемента
 * задаётся отступ сверху, равный высоте заголовка — чтобы контент не «прыгал».
 * При прокрутке вверх стили сбрасываются.
 *
 * @returns {void}
 */
$(document).ready(() => {
    /** @type {jQuery<HTMLElement>} Элемент заголовка страницы */
    let header = $(".header"),
        /** @type {jQuery<HTMLElement>} Элемент сразу после заголовка */
        elementAfterHeader = header.parent().next();

    // Отслеживаем событие прокрутки окна
    $(window).on("scroll", () => {
        /** @type {number} Текущая вертикальная прокрутка в пикселях */
        let scrollY = $(window).scrollTop();
        /** @type {boolean} Нужно ли закрепить заголовок? */
        let shouldBeFixed = scrollY >= header.outerHeight();

        if (shouldBeFixed) {
            // Добавляем классы для фиксирования заголовка сверху
            header.addClass("fixed-top fixed");
            // Создаём отступ у следующего элемента, чтобы компенсировать высоту заголовка
            elementAfterHeader.css("margin-top", header.outerHeight() + "px");
        } else {
            // Убираем классы фиксирования
            header.removeClass("fixed-top fixed");
            // Сбрасываем отступ
            elementAfterHeader.css("margin-top", "0");
        }
    });
});
