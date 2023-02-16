let funcPasswordAgain = (data) => {
    let doPasswordsMatch = $("#doPasswordsMatch"),
        isPasswordLengthValid = $("#isPasswordLengthValid"),
        password = $("#password"),
        passwordLength = password.val().length;
    if(!data) {
        doPasswordsMatch.addClass("valid");
    } else {
        doPasswordsMatch.removeClass("valid");
    }
    if(passwordLength > 4 && passwordLength < 61) {
        isPasswordLengthValid.addClass("valid");
    } else {
        isPasswordLengthValid.removeClass("valid");
    }
}

$(document).ready(() => {
    $("#passwordAgain").bind("keyup", delay(() => {
        $.ajax({
            url: "/auth/ajax/doPasswordsNotMatch",
            type: "GET",
            data: {passwordAgain: $("#passwordAgain").val(),password: $("#password").val()},
            success: funcPasswordAgain
        });
    }, 700));

    $("#password").bind("keyup", delay(() => {
        $.ajax({
            url: "/auth/ajax/doPasswordsNotMatch",
            type: "GET",
            data: {passwordAgain: $("#passwordAgain").val(),password: $("#password").val()},
            success: funcPasswordAgain
        });
    }, 700));

    $(".tooltip").each(function () {
        let id = $(this).attr("content"),
            element = $("#" + id);
        element.focus(() => {
            $(this).css({ visibility: "visible", opacity: "1"});
        });
        element.focusout(() => {
            $(this).css({ visibility: "hidden", opacity: "0"});
        });
    });
});
