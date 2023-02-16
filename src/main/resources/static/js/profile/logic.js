import autosize from "/js/autosize.js";
let loadCommentsButton = document.getElementById("loadComments");
loadCommentsButton.addEventListener("click",(event) => {
    $("#loadComments").remove();
    let commentsContent = $(".comments-content");
    commentsContent.css("align-items","");
    // Render
    let renderedAvatar = hasMyAvatar ? `<img src="data:image/webp;base64,${avatars[currentName].avatar}" id="avatarComments">` : '<img src="/img/UnsetAvatar.webp" id="avatarComments" alt="">\n'
    commentsContent.append(' <div class="writeCommentSuggestionDiv">\n' +
        '<div style="display: flex;gap:50px;">\n' +
        '<div  class="avatar-wrapper-medium avatar-wrapper" style="min-width: 72px;max-width:72px;min-height: 72px;max-height:72px;border: none !important;">\n' +
        `<a href="/profile">\n${renderedAvatar}\n</a>\n` +
        '</div>\n' +
        '<form content="0" id="sendCommentForm" method="POST" action="/createComment" style="width: 100%">\n' +
        '<textarea style="position: relative" minlength="1" maxlength="300" placeholder="Введите ваш комментарий..." name="content" id="content"></textarea>\n' +
        `<input type="hidden" name="receiver" id="receiver" value="${name}">\n` +
        '</form>\n' +
        '</div>\n' +
        '<div style="margin-top:5px">\n' +
        '<input type="submit" id="sendButton">\n' +
        '</div>\n' +
        '</div>');
    commentsContent.append(`<div id="theDiv">
            
        </div>`);
    let str = null,format = null;
    let textArea = document.getElementById("content");
    let sendButton = document.getElementById("sendButton");
    let buttonIsVanished = true;
    let commentForm = document.getElementById("sendCommentForm");
    let buttonDisappear = () => {
        sendButton.style.visibility = "hidden";
    };
    let buttonAppear = () => {
        sendButton.style.visibility = "visible";
    };
    let func = () => {
        if(textArea.value.length === 0) {
            buttonDisappear();
            buttonIsVanished = true;
        } else if(buttonIsVanished) {
            buttonAppear();
            buttonIsVanished = false;
        }
    }
    textArea.addEventListener("input",() => {
        func();
        let length = textArea.value.length;
        commentForm.setAttribute("content",length);
    });

    const pingAutoSize = () => {
        autosize(document.getElementsByClassName("toBeAutoGrown"));
    }
    function formatLikes(num) {
        return Math.abs(num) > 999 ? Math.sign(num)*((Math.abs(num)/1000).toFixed(1)) + 'K' : Math.sign(num)*Math.abs(num)
    }
    let stompClient = null;
    function connect() {
        let socket = new SockJS("/listenToNewEvents")
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function() {
            stompClient.subscribe(`/user/` + name + `/queue/messages`, function(data) {
                prepareToAddComment(JSON.parse(data.body));
            });
            stompClient.subscribe(`/user/` + name + `/queue/changeLikeAmount`, function(data) {
                let arr = JSON.parse(data.body).split(' ');
                let element = document.querySelector(`p[content="${arr[0]}"] .rating`);
                element.textContent = arr[1];
            });
        },function () {
            console.log("Error connecting...")
        });
    }
    $(document).ready( function() {
            connect();
            autosize(textArea);
        }
    );
    let theDiv = document.getElementById("theDiv");
    let idToTimer = new Map();

    let timerLogic = function (id,textContent,map) {
        stompClient.send(`/application/changeLikeAmount`, {},
            JSON.stringify({"id":id,"username":name,"like":textContent == "👍"}));
        map.delete(id);
    };

    theDiv.addEventListener("click",(event) => {
        let target = event.target;
        if(target.nodeName == "SPAN" && (target.textContent == "👍" || target.textContent == "👎")) {

            let id = target.parentElement.getAttribute("content");
            if(idToTimer.has(id)) {
                let result = idToTimer.get(id);
                clearTimeout(result);
            }
            idToTimer.set(id,setTimeout(() => {timerLogic(id,target.textContent,idToTimer)},1500));

        }
    })

    window.onbeforeunload = function() {
        // disable onclose handler first
        socket.onclose = function () {};
        socket.close();
    };
    function sendMessage() {
        stompClient.send("/application/sendComment", {},
            JSON.stringify({
                'username': currentName,
                'recipient': name,
                'content':$("#content").val(),
                "date": new Date().getTime(),
                "isActive": 1}));
    }
    $("#sendButton").on("click",sendMessage);


    function getComment(obj) {
        let img = null;
        let tempName = Object.keys(obj).includes("author") ? "author" : "username";
        if(obj[tempName] == null || avatars[obj[tempName]] == null || avatars[obj[tempName]].avatar == null)
            img = `<img src="/img/UnsetAvatar.webp" style="width:72px;height: 72px;" alt="${obj.tempName}'s avatar">`;
        else
            img = `<img src="data:image/webp;base64,${avatars[obj[tempName]].avatar}" style="width:72px;height: 72px;" alt="${obj[tempName]}'s avatar">`;

        let element =
            `<div style="margin-top:70px;position: relative" class="comment">
                <div style="color:#e1e1e1;font-size:11px;position: absolute;right: 0;width: 100%;height: 20px;top:-21px;display: flex;justify-content: flex-end">${obj.stringDate}</div>
                <div style="position:relative; display: flex;gap:50px;">
                <div>
                    <div class="avatar-wrapper-medium avatar-wrapper" style="position: relative;border: none !important;max-width: 72px;min-width: 72px;min-height: 72px;max-height: 72px;">
                        <a style="position: relative" href="/profile/${obj[tempName]}">
                        ${img}
                    </a>
                </div>
                <div style="position: relative">
                    <a href="/profile/${obj[tempName]}" style="text-decoration:none;position: absolute;left: 50%;transform: translate(-50%, 0);top: 12px;color: white;font-size: 14px;">${obj[tempName]}</a>
                </div>
                </div>
                <textarea readonly style="cursor: pointer;overflow: hidden;overflow-wrap:break-word;" class="toBeAutoGrown">${obj.content}</textarea>
            </div>
                <div style="display: flex;justify-content: flex-end;position: absolute;right: 2px;height: 20px;width: 100%  ">
                    <p content="${obj.id}" style="color: white"><span style="font-size: 14px;cursor:pointer"  class="dislike">👎</span><span class="rating" style="font-size:14px;margin-left: 5px;margin-right: 5px">${formatLikes(obj.rating)}</span><span style="font-size: 15px;cursor:pointer" class="like">👍</span></p>
                </div>
            </div>`
        return element;
    }
    let newMessages = [];
    function prepareToAddComment(obj) {
        if(null === name || obj.username === currentName)
            addComment(obj);
        else {
            let loadNewComments =  document.getElementById("loadNewComments");
            if(loadNewComments == null) {
                let elem = `<button style="position: absolute;top:112px;left: 430px" id="loadNewComments">Новые комментарии</button>`;
                $(elem).insertAfter("#content");
                loadNewComments =  document.getElementById("loadNewComments");
                loadNewComments.addEventListener("click",function(event) {
                    event.preventDefault();
                    addComments(newMessages);
                });
            }
            newMessages.push(obj);
            $("#content").val('');
            commentForm.setAttribute("content",0);
            func()
        }
    }
    function addComments(data) {
        $(data).each(function(index,item) {
            addComment(item);
        });
        newMessages = [];
        $("#loadNewComments").remove();
    }
    function addComment(obj) {
        let elem = getComment(obj);
        $("#theDiv").prepend(elem);
        $("#content").val('');
        commentForm.setAttribute("content",0);
        func();
        pingAutoSize();
    }
    if(totalPages >= 1) {
        let currentPage = 0;
        const showComments = (data) => {
            let uploadedAvatars = data.r;
            let newComments = data.t;
            let oK = Object.keys(uploadedAvatars);
            const mainLogic = () => {
                $(newComments).each(function(index,item) {
                    let elem = getComment(item);
                    if(totalPages == currentPage) {
                        $("#theDiv").append(elem);
                        $("#loadMoreButtonDiv").remove();
                    }
                    else
                        $(elem).insertBefore("#loadMoreButtonDiv")
                });
            }

            if(oK.length != 0) {
                for (let i = 0; i < oK.length; i++) {
                    Object.defineProperty(avatars, oK[i], {
                        value: uploadedAvatars[oK[i]],
                    });
                }
            }
            mainLogic()
        }
        $(document).ready(function() {
            if(totalPages >= 2) {
                $("#theDiv").append(`<div id="loadMoreButtonDiv" style="margin-top: 100px;width: 100%;text-align: center;">
                <button id="loadMoreButton">Больше комментариев</button>
            </div>`);
                $("#loadMoreButton").click(function(event) {
                    event.preventDefault();
                    currentPage+=1;
                    $.ajax({
                        url: "/ajax/nextComments",
                        data: {usersWhoseAvatarsAreLoaded: Object.keys(avatars).join(','),currentPage: currentPage,user: name},
                        success: showComments
                    });
                });
            }
            $.ajax({
                url: "/ajax/nextComments",
                data: {usersWhoseAvatarsAreLoaded: Object.keys(avatars).join(','),currentPage: currentPage,user: name},
                success: showComments
            });
            currentPage = 1;
        });
    }
});



