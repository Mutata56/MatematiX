import autosize from "/js/autosize.js";  // Подключаем плагин автоподгонки высоты textarea

/**
     * Обработчик клика по кнопке «Загрузить комментарии».
     * Удаляет кнопку и инициализирует форму ввода, область для новых и существующих комментариев.
     * @param {MouseEvent} event - событие клика по кнопке
*/
let loadCommentsButton = document.getElementById("loadComments");
/**
 * Обработчик клика по кнопке загрузки комментариев.
 * Удаляет кнопку и вставляет форму для ввода, а также контейнер для комментариев.
 * @param {MouseEvent} event - событие клика
 */
loadCommentsButton.addEventListener("click", (event) => {
    // Удаляем саму кнопку, чтобы не было повторных вставок формы
    $("#loadComments").remove();

    /**
     * Контейнер для формы и комментариев.
     * @type {jQuery<HTMLElement>}
     */

    let commentsContent = $(".comments-content");

    // Сбрасываем центрирование для вставленных элементов
    commentsContent.css("align-items", "");

    /**
     * HTML-разметка аватара текущего пользователя.
     * @type {string}
     */
    let renderedAvatar = hasMyAvatar
        ? `<img src="data:image/webp;base64,${avatars[currentName].avatar}" id="avatarComments">`
        : '<img src="/img/UnsetAvatar.webp" id="avatarComments" alt="">\n';

    // Вставляем форму для нового комментария
    commentsContent.append(
        ' <div class="writeCommentSuggestionDiv">\n' +
        '  <div style="display: flex;gap:50px;">\n' +
        '    <div class="avatar-wrapper-medium avatar-wrapper" ' +
                'style="min-width: 72px;max-width:72px;min-height: 72px;max-height:72px;border: none !important;">\n' +
        `      <a href="/profile">\n${renderedAvatar}\n</a>\n` +
        '    </div>\n' +
        '    <form content="0" id="sendCommentForm" method="POST" action="/createComment" style="width: 100%">\n' +
        '      <textarea style="position: relative" minlength="1" maxlength="300" ' +
                'placeholder="Введите ваш комментарий..." name="content" id="content"></textarea>\n' +
        `      <input type="hidden" name="receiver" id="receiver" value="${name}">\n` +
        '    </form>\n' +
        '  </div>\n' +
        '  <div style="margin-top:5px">\n' +
        '    <input type="submit" id="sendButton">\n' +
        '  </div>\n' +
        '</div>'
    );

    // Контейнер для существующих комментариев
    commentsContent.append(`<div id="theDiv"></div>`);

    // Переменные для работы с формой
    /** @type {HTMLTextAreaElement} */
    let textArea = document.getElementById("content");
    /** @type {HTMLInputElement} */
    let sendButton = document.getElementById("sendButton");
    /** @type {HTMLFormElement} */
    let commentForm = document.getElementById("sendCommentForm");
    /** Состояние видимости кнопки отправки */
    let buttonIsVanished = true;

    // Функции для скрытия и показа кнопки отправки
    /** Скрыть кнопку отправки */
    let buttonDisappear = () => { sendButton.style.visibility = "hidden"; };
    /** Показать кнопку отправки */
    let buttonAppear   = () => { sendButton.style.visibility = "visible"; };

    /**
     * Переключает видимость кнопки отправки в зависимости от содержимого textarea.
     */
    let toggleButton = () => {
        if (textArea.value.length === 0) {
            buttonDisappear();
            buttonIsVanished = true;
        } else if (buttonIsVanished) {
            buttonAppear();
            buttonIsVanished = false;
        }
    };

    // Обработчик ввода текста — переключаем кнопку и обновляем атрибут формы
    textArea.addEventListener("input", () => {
        toggleButton();
        // Сохраняем текущую длину текста в атрибут формы для placeholder-показателя
        commentForm.setAttribute("content", textArea.value.length);
    });

    /**
     * Инициализация автоподгонки высоты textarea.
     */
    const pingAutoSize = () => {
        autosize(document.getElementsByClassName("toBeAutoGrown"));
    };

    /**
     * Форматирование количества лайков (e.g. 1500 -> "1.5K").
     * @param {number} num — число лайков
     * @returns {string|number} отформатированное значение
     */
    function formatLikes(num) {
        return Math.abs(num) > 999
            ? Math.sign(num) * ((Math.abs(num) / 1000).toFixed(1)) + 'K'
            : Math.sign(num) * Math.abs(num);
    }

    /**
     * STOMP-клиент для сообщений реального времени.
     * @type {?Stomp.Client}
     */
    let stompClient = null;

    /**
     * Устанавливает WebSocket-подключение и подписки.
     */
    function connect() {
        let socket = new SockJS("/listenToNewEvents");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function() {
            // Подписка на новые комментарии для текущего пользователя
            stompClient.subscribe(`/user/${name}/queue/messages`, function(data) {
                prepareToAddComment(JSON.parse(data.body));
            });
            // Подписка на обновление лайков/дизлайков
            stompClient.subscribe(`/user/${name}/queue/changeLikeAmount`, function(data) {
                let [id, rating] = JSON.parse(data.body).split(' ');
                let element = document.querySelector(`p[content="${id}"] .rating`);
                element.textContent = rating;
            });
        }, function() {
            console.log("Error connecting...");
        });
    }


    // Запуск подключения и autosize после загрузки документа
    $(document).ready(function() {
        connect();
        autosize(textArea);
    });

    /**
     * Контейнер для динамически добавляемых комментариев.
     * @type {HTMLElement}
     */
    let theDiv = document.getElementById("theDiv");

    /** Map для таймеров дебаунса лайков */
    let idToTimer = new Map();


    /**
     * Отправляет запрос на изменение количества лайков после задержки.
     * @param {string} id — идентификатор комментария
     * @param {string} textContent — символ лайка или дизлайка
     * @param {Map<string, number>} map — карта таймеров
     */
    let timerLogic = function(id, textContent, map) {
        stompClient.send(
            `/application/changeLikeAmount`,
            {},
            JSON.stringify({ id: id, username: currentName, like: textContent == "👍" })
        );
        map.delete(id);
    };
    // Обработчик кликов по лайкам/дизлайкам — реализует дебаунс
    theDiv.addEventListener("click", (event) => {
        let target = event.target;
        if (target.nodeName === "SPAN" && (target.textContent === "👍" || target.textContent === "👎")) {
            let id = target.parentElement.getAttribute("content");
            // Если для этого ID уже есть отложенный таймер — сбрасываем его
            if (idToTimer.has(id)) {
                clearTimeout(idToTimer.get(id));
            }
            // Ставим новый таймер на 1.5 секунды
            idToTimer.set(
                id,
                setTimeout(() => { timerLogic(id, target.textContent, idToTimer); }, 1500)
            );
        }
    });

    // Закрытие сокета при выходе со страницы
    window.onbeforeunload = function() {
        socket.onclose = function() {};
        socket.close();
    };

    // Функция отправки нового комментария через STOMP
    function sendMessage() {
        stompClient.send(
            "/application/sendComment",
            {},
            JSON.stringify({
                username: currentName,
                recipient: name,
                content: $("#content").val(),
                date: new Date().getTime(),
                isActive: 1
            })
        );
    }
    $("#sendButton").on("click", sendMessage);

    /**
     * Генерация HTML-разметки одного комментария.
     * @param {{id: string, author?: string, username?: string, content: string, rating: number, stringDate: string}} obj
     * @returns {string} HTML-блок комментария
     */
    function getComment(obj) {
        // Определяем, у кого брать аватар: автор или username
        let authorKey = obj.author != null ? "author" : "username";
        let img;
        if (!obj[authorKey] || !avatars[obj[authorKey]] || !avatars[obj[authorKey]].avatar) {
            img = `<img src="/img/UnsetAvatar.webp" style="width:72px;height:72px;" alt="${obj[authorKey]}'s avatar">`;
        } else {
            img = `<img src="data:image/webp;base64,${avatars[obj[authorKey]].avatar}" ` +
                  `style="width:72px;height:72px;" alt="${obj[authorKey]}'s avatar">`;
        }
        // Собираем полный блок комментария с аватаром, автором, текстом и рейтингом
        return `
            <div class="comment" style="margin-top:70px;position: relative">
                <div class="comment-date"
                     style="color:#e1e1e1;font-size:11px;position:absolute;right:0;top:-21px;
                            width:100%;height:20px;display:flex;justify-content:flex-end;">
                    ${obj.stringDate}
                </div>
                <div class="comment-body" style="display:flex;gap:50px;position:relative;">
                    <div>
                        <div class="avatar-wrapper-medium avatar-wrapper"
                             style="position:relative;border:none!important;
                                    min-width:72px;max-width:72px;min-height:72px;max-height:72px;">
                            <a href="/profile/${obj[authorKey]}">${img}</a>
                        </div>
                        <div class="comment-author"
                             style="position:absolute;left:50%;transform:translateX(-50%);
                                    top:12px;color:white;font-size:14px;">
                            <a href="/profile/${obj[authorKey]}" style="text-decoration:none;color:white;">
                                ${obj[authorKey]}
                            </a>
                        </div>
                    </div>
                    <textarea readonly class="toBeAutoGrown" style="cursor:pointer;overflow:hidden;overflow-wrap:break-word;">${obj.content.trim()}</textarea>
                </div>
                <div class="comment-rating"
                     style="display:flex;justify-content:flex-end;
                            position:absolute;right:2px;height:20px;width:100%;">
                    <p content="${obj.id}" style="color:white">
                        <!-- Дизлайк -->
                        <span class="dislike" style="font-size:14px;cursor:pointer;">👎</span>
                        <!-- Рейтинг -->
                        <span class="rating"
                              style="font-size:14px;margin:0 5px;">
                            ${formatLikes(obj.rating)}
                        </span>
                        <!-- Лайк -->
                        <span class="like" style="font-size:15px;cursor:pointer;">👍</span>
                    </p>
                </div>
            </div>`;
    }

    // Массив для буферизации новых сообщений (если они приходят не от меня)
    let newMessages = [];

    // Готовим новый комментарий к выводу или буферизации
    function prepareToAddComment(obj) {
        if (!name || obj.username === currentName) {
            // Если это мой комментарий — сразу добавляем
            addComment(obj);
        } else {
            // Иначе показываем кнопку "Новые комментарии" и буферизуем
            let loadNewComments = document.getElementById("loadNewComments");
            if (!loadNewComments) {
                let btn = `<button id="loadNewComments"
                                   style="position:absolute;top:112px;left:430px">
                               Новые комментарии
                           </button>`;
                $(btn).insertAfter("#content");
                loadNewComments = document.getElementById("loadNewComments");
                loadNewComments.addEventListener("click", function(event) {
                    event.preventDefault();
                    addComments(newMessages);
                });
            }
            newMessages.push(obj);
            $("#content").val("");
            commentForm.setAttribute("content", 0);
            toggleButton();
        }
    }

    // Добавляем сразу все буферизированные комментарии
    function addComments(data) {
        data.forEach(item => addComment(item));
        newMessages = [];
        $("#loadNewComments").remove();
    }

    // Вставляем один комментарий в начало блока
    function addComment(obj) {
        let elem = getComment(obj);
        $("#theDiv").prepend(elem);
        $("#content").val("");
        commentForm.setAttribute("content", 0);
        toggleButton();
        pingAutoSize();
    }

    // Загрузка старых комментариев при пагинации
    if (totalPages >= 1) {
        let currentPage = 0;
        const showComments = (data) => {
            let { r: uploadedAvatars, t: newComments } = data;
            // Если есть новые аватарки — добавляем в память
            Object.keys(uploadedAvatars).forEach(key => {
                avatars[key] = uploadedAvatars[key];
            });
            // Вставляем блоки комментариев
            newComments.forEach((item, idx) => {
                let elem = getComment(item);
                if (totalPages === currentPage) {
                    $("#theDiv").append(elem);
                    $("#loadMoreButtonDiv").remove();
                } else {
                    $(elem).insertBefore("#loadMoreButtonDiv");
                }
            });
        };

        $(document).ready(function() {
            // Если есть больше одной страницы — добавляем кнопку "Больше комментариев"
            if (totalPages >= 2) {
                $("#theDiv").append(`
                    <div id="loadMoreButtonDiv" style="margin-top:100px;text-align:center;">
                        <button id="loadMoreButton">Больше комментариев</button>
                    </div>
                `);
                $("#loadMoreButton").click(event => {
                    event.preventDefault();
                    currentPage++;
                    $.ajax({
                        url: "/ajax/nextComments",
                        data: {
                            usersWhoseAvatarsAreLoaded: Object.keys(avatars).join(','),
                            currentPage, user: name
                        },
                        success: showComments
                    });
                });
            }
            // Подгрузка первой страницы комментариев
            $.ajax({
                url: "/ajax/nextComments",
                data: {
                    usersWhoseAvatarsAreLoaded: Object.keys(avatars).join(','),
                    currentPage, user: name
                },
                success: showComments
            });
            currentPage = 1;
        });
    }
});
