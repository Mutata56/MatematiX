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


    const sendToTheServer = () => {
        $.ajax({
            url: "/admin/ajax/process",
            type: "GET",
            data: ({
                sortBy: sortByValue,
                findBy: findByValue,
                sortDirection: sortDirection,
                itemsPerPage: itemsPerPage,
                currentPage: currentPage,
                find: find,
                clazz: clazz
            }),
            success: successFunction
        });
    }


    if(sortByValue === undefined)
        sortByValue = "noSort";


    $(document).ready(
        function () {
            $(document).ajaxSend(function(e,xhr,options) {
                xhr.setRequestHeader("X-CSRF-TOKEN",csrf);
            });
        }
    );
    $("#enterThePage").keypress(function (event) {
        if(event.keyCode === 13) {
            currentPage = $("#enterThePage").val()
            sendToTheServer()
        }
    });

    const sharedLogic = () => {
        findByValue = $("#findBy").val();
        itemsPerPage = $("#ipp").val();
        sortByValue = $("#sortBy").val();
        sortDirection = $("#sortDirection").val();
        find = $("#findValue").val();
        total = Math.ceil(totalEntities / itemsPerPage);
        $("#totalCounter").text(`из ${total} страница`);
        sendToTheServer();
        $("#showSettingsModal").modal("toggle");
    }
    $("#submitSettings").click(function (event) {
        sharedLogic()
    })
    $("#find").click(function (event) {
        sharedLogic()
    })

    $(document).click(function(event) {
        let target = event.target;
        if(target.nodeName === "A" && target.textContent === "Редактировать") {

           editFunc(target);
        } else if((target.nodeName === "A" && target.textContent === "Применить") || (target.nodeName === "BUTTON" && target.textContent === "Сохранить")) {
            saveFunc(target);
        }
        else if (target.nodeName === "A" && target.textContent === "Удалить") {
            deleteFunc(target);
        }
        else if(target.nodeName === "BUTTON" && target.textContent === "Новая сущность") {
            newFunc(target);
        }
    })