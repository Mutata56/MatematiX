/**
 * Инициализирует логику загрузки и обрезки аватара пользователя.
 * Поддерживает выбор файла, отображение модального окна
 * и конфигурацию Cropper.js для квадратичной обрезки с предпросмотром.
 */
$(document).ready(function() {
    /**
     * Поле ввода файла (скрытое) для выбора изображения.
     * @type {jQuery<HTMLInputElement>}
     */
    let fileInput = $("#secretInput"),
        /**
         * Элемент <img> для отображения загруженного изображения и инициализации Cropper.
         * @type {jQuery<HTMLImageElement>}
         */
        croppingImage = $("#croppingImage"),
        /**
         * Кнопка отправки формы после обрезки.
         * @type {jQuery<HTMLElement>}
         */
        submitChangeAvatar = $("#submitChangeAvatar"),
        /**
         * jQuery-объект формы, содержащей скрытые поля для координат обрезки.
         * @type {jQuery<HTMLFormElement>}
         */
        theForm = $("#theForm"),
        /**
         * Данные загруженного изображения в формате Data URL.
         * @type {string}
         */
        uploadedImage = "",
        /**
         * Экземпляр Cropper.js для обрезки.
         * @type {Cropper|null}
         */
        cropper = null,
        /**
         * Флаг, указывающий, что Cropper уже был инициализирован.
         * @type {boolean}
         */
        cropperExists = false,
        /**
         * Модальное окно Bootstrap для обрезки аватара.
         * @type {jQuery<HTMLElement>}
         */
        changeYourAvatarModal = $("#changeYourAvatarModal");

    /**
     * Обработчик клика по кнопке отправки формы.
     * Извлекает данные обрезки из Cropper и заполняет скрытые поля формы.
     * Затем отправляет форму на сервер.
     * @param {Event} e — событие клика
     */
    submitChangeAvatar.click(function(e) {
        e.preventDefault();
        // Получаем объект с координатами и размерами обрезки
        let data = cropper.getData();
        // Заполняем скрытые input-ы
        $("#x").val(data.x);
        $("#y").val(data.y);
        $("#width").val(data.width);
        $("#height").val(data.height);
        $("#scaleX").val(data.scaleX);
        $("#scaleY").val(data.scaleY);
        // Отправляем форму
        theForm.submit();
    });

    /**
     * При выборе файла автоматически открывает модальное окно обрезки.
     */
    fileInput.change(function() {
        changeYourAvatarModal.modal('toggle');
    });

    /**
     * При показе модального окна и наличии выбранного файла
     * инициализирует или обновляет Cropper.js на элементе <img>.
     */
    changeYourAvatarModal.on("shown.bs.modal", function() {
        let file = fileInput.prop("files")[0];
        if (file) {
            let reader = new FileReader();
            reader.addEventListener("load", function() {
                uploadedImage = reader.result; // Data URL
                // Устанавливаем src для <img>
                croppingImage.attr("src", uploadedImage);
                // Элементы предпросмотра
                let previewDiv = $("#previewDiv"),
                    previewDivComments = $("#previewDivComments");
                if (!cropperExists) {
                    // Первый запуск — создаём новый Cropper
                    cropperExists = true;
                    cropper = new Cropper(croppingImage[0], {
                        aspectRatio: 1,           // Квадрат
                        viewMode: 2,              // Ограничение границ обрезки
                        preview: [previewDiv[0], previewDivComments[0]],
                        minCropBoxHeight: 100,
                        minCropBoxWidth: 100,
                        dragMode: "none",
                        cropBoxResizable: false
                    });
                } else {
                    // Обновляем изображение в существующем Cropper
                    cropper.replace(uploadedImage);
                }
            });
            reader.readAsDataURL(file);
        }
    });
});