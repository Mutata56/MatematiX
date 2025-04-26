
    $(document).ready(function() {
        /* AJAX-пагинация: при скролле подгружаем следующие 6 друзей через jQuery */

    const $section = $('#friendsSection');
    let isLoading = false;
    function loadMoreFriends(callback) {
        if (isLoading || (currentPage * 6) >= countOfFriends) { if (callback) callback(false); return; }
        isLoading = true;
        currentPage++;
        $.ajax({
            url: "/ajax/nextFriends",
            data: {usersWhoseAvatarsAreLoaded: Object.keys(avatars).join(','),currentPage: currentPage,user: name,totalUsers:countOfFriends},
            success: function(data) {
                console.log("data = " + data);
                if (data == null) {
                return;
                }
                let arr = Object.keys(data);
                for (let i = 0; i < arr.length; i++) {
                        let image = data[arr[i]].avatar == null ? "/img/UnsetAvatar.webp" : `data:image/webp;base64,${data[arr[i]].avatar}`;
                        let element = `<a style="margin-bottom: 20px;color:white;text-decoration: none" href="/profile/${data[arr[i]].username}"><img style="border-radius: 50%" class="avatar-wrapper-medium" src="${image}" alt=""><p style="font-size: 16px;margin-top:10px">${data[arr[i]].username}</p></a>`
                        $section.append(element);
                        console.log(element);
                    }
                    isLoading = false;
                    if (callback) callback(true);
                },
            error: function(xhr, status, err) {
                console.error('Ошибка при загрузке друзей:', err);
                if (callback) callback(false);
            },
             complete: function() {
                    // Сбрасываем флаг, чтобы можно было снова грузить при следующем скролле
                    isLoading = false;
                  }
        });
    }
    // Подгружаем, пока появится вертикальный скролл
        function ensureScrollable() {
            if ($(document).height() <= $(window).height() && (currentPage * 6) < countOfFriends) {
                loadMoreFriends(function(loaded) { if (loaded) ensureScrollable(); });
            }
        }
        ensureScrollable();
    $(window).on('scroll', function() {
            if ($(window).scrollTop() + $(window).height() >= $(document).height() - 200) {
                loadMoreFriends();
        }
    });
  });