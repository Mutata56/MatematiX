/**
 * Инициализация функционала изменения размера шрифтов на странице через ползунок и кнопки.
 */
$(document).ready(function() {
    /** @type {HTMLInputElement} Ползунок регулировки размера шрифта */
    let slider = document.getElementById("slider");
    /** @type {HTMLButtonElement} Кнопка уменьшения шрифта */
    let btn1 = document.getElementById("btn1");
    /** @type {HTMLButtonElement} Кнопка сброса размера шрифта */
    let btn2 = document.getElementById("btn2");
    /** @type {HTMLButtonElement} Кнопка увеличения шрифта */
    let btn3 = document.getElementById("btn3");
    /**
     * Список всех текстовых элементов, на которые влияет регулировка шрифта
     * (заголовки H1–H6, параграфы, ссылки и формулы MathJax).
     * @type {NodeListOf<HTMLElement>}
     */
    let elements = document.querySelectorAll("h1, h2, h3, h4, h5, h6, p, .link, .MJX-TEX");

    /**
     * Словарь исходных размеров шрифтов для каждого элемента (в пикселях).
     * @type {Map<HTMLElement, number>}
     */
    let initialFontSizes = new Map();
    elements.forEach(el => {
        // Извлекаем текущий размер шрифта из computed style
        let size = Number(
            window.getComputedStyle(el).fontSize.replaceAll("px", "")
        );
        initialFontSizes.set(el, size);
    });

    /**
     * Устанавливает новый размер шрифта для всех элементов,
     * прибавляя указанное смещение к исходному значению.
     *
     * @param {number} [offset] — смещение размера шрифта (в пикселях);
     * если не передано, используется текущее значение ползунка.
     */
    const setFontSize = (offset) => {
        let delta = Number(offset === undefined ? slider.value : offset);
        elements.forEach(el => {
            let baseSize = initialFontSizes.get(el);
            el.style.fontSize = (baseSize + delta) + "px";
        });
    };

    /**
     * Обработчик перемещения ползунка — обновляет размер шрифта в реальном времени.
     */
    slider.addEventListener("input", function(event) {
        event.preventDefault();
        setFontSize();
    });

    /**
     * Обработчик кнопки уменьшения шрифта: декремент ползунка и обновление.
     */
    btn1.addEventListener("click", function() {
        if (slider.value >= -19) {
            slider.value--;
            setFontSize();
        }
    });

    /**
     * Обработчик кнопки сброса шрифта в исходное состояние (offset = 0).
     */
    btn2.addEventListener("click", function() {
        slider.value = 0;
        setFontSize(0);
    });

    /**
     * Обработчик кнопки увеличения шрифта: инкремент ползунка и обновление.
     */
    btn3.addEventListener("click", function() {
        if (slider.value <= 19) {
            slider.value++;
            setFontSize();
        }
    });
});