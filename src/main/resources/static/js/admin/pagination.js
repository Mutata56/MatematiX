// Фрагмент HTML-кнопки с выпадающим меню действий для каждой строки таблицы
let button = `<td style="text-align: center">
    <div class="btn-group">
        <button type="button" class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
            Действия
        </button>
        <ul style="width: 1px" class="dropdown-menu dropdown-menu-dark">
            <li><a class="dropdown-item" href="#">Редактировать</a></li>
            <li><a class="dropdown-item" href="#">Удалить</a></li>
        </ul>
    </div>
</td>`;

// Функция для отправки AJAX-запроса на сервер для получения данных
let sendToTheServer = () => {
    $.ajax({
        url: "/admin/ajax/process",   // URL эндпоинта
        type: "GET",                  // Метод запроса
        data: ({                       // Параметры запроса
            sortBy: sortByValue,
            findBy: findByValue,
            sortDirection: sortDirection,
            itemsPerPage: itemsPerPage,
            currentPage: currentPage,
            find: find,
            clazz: clazz
        }),
        success: successFunction       // Функция-обработчик успешного ответа
    });
}

// Если параметр сортировки не задан, используем заглушку noSort
if (sortByValue === undefined)
    sortByValue = "noSort";

// При загрузке страницы настраиваем CSRF-токен для всех AJAX-запросов
$(document).ready(function () {
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader("X-CSRF-TOKEN", csrf);
    });
});

// Обработка нажатия Enter в поле ввода номера страницы
$("#enterThePage").keypress(function (event) {
    if (event.keyCode === 13) {
        currentPage = $("#enterThePage").val();  // Считываем введённое значение
        sendToTheServer();                        // Обновляем данные
    }
});

// Общая логика обновления параметров фильтрации/пагинации
const sharedLogic = () => {
    findByValue = $("#findBy").val();            // Параметр поиска
    itemsPerPage = $("#ipp").val();              // Элементы на странице
    sortByValue = $("#sortBy").val();            // Поле сортировки
    sortDirection = $("#sortDirection").val();   // Направление сортировки
    find = $("#findValue").val();                // Строка поиска
    total = Math.ceil(totalEntities / itemsPerPage); // Всего страниц
    $("#totalCounter").text(`из ${total} страница`); // Обновляем счетчик
    sendToTheServer();                             // Отправляем запрос
    $("#showSettingsModal").modal("toggle");  // Скрываем модальное окно настроек
}

// По клику на кнопки "Применить" или аналогичные вызываем sharedLogic
$("#submitSettings").click(function (event) {
    sharedLogic();
});
$("#find").click(function (event) {
    sharedLogic();
});

// Делегирование кликов по документу для динамической обработки кнопок
$(document).click(function(event) {
    let target = event.target;
    // Если клик по пункту меню "Редактировать"
    if (target.nodeName === "A" && target.textContent === "Редактировать") {
        editFunc(target);
    }
    // Если клик по пункту "Применить" или кнопке "Сохранить"
    else if ((target.nodeName === "A" && target.textContent === "Применить") 
             || (target.nodeName === "BUTTON" && target.textContent === "Сохранить")) {
        saveFunc(target);
    }
    // Если клик по пункту меню "Удалить"
    else if (target.nodeName === "A" && target.textContent === "Удалить") {
        deleteFunc(target);
    }
    // Если клик по кнопке создания новой сущности
    else if (target.nodeName === "BUTTON" && target.textContent === "Новая сущность") {
        newFunc(target);
    }
});