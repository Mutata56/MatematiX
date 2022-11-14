let funcPasswordAgain = (data) => {
    let doPasswordsMatch = document.getElementById("doPasswordsMatch");
    let isPasswordLengthValid = document.getElementById("isPasswordLengthValid");
    let passwordLength = document.getElementById("password").value.length;
    if(!data)
        doPasswordsMatch.classList.add("valid");
    else
        doPasswordsMatch.classList.remove("valid");
    if(passwordLength > 4 && passwordLength < 61)
        isPasswordLengthValid.classList.add("valid");
    else
        isPasswordLengthValid.classList.remove("valid");
}
$(document).ready(function() {
    $("#passwordAgain").bind("keyup",
        delay(
            function () {
                $.ajax({
                    url: "/auth/ajax/doPasswordsNotMatch",
                    type: "GET",
                    data: ({passwordAgain: document.getElementById("passwordAgain").value,password: document.getElementById("password").value}),
                    success: funcPasswordAgain
                });
            }
            ,700
        )
    );
    $("#password").bind("keyup",
        delay(
            function () {
                $.ajax({
                    url: "/auth/ajax/doPasswordsNotMatch",
                    type: "GET",
                    data: ({passwordAgain: document.getElementById("passwordAgain").value,password: document.getElementById("password").value}),
                    success: funcPasswordAgain
                });
            }
            ,700
        )
    );
    $(".tooltip").each(function (index,item) {
        let id = $(item).attr("content");
        let element = document.getElementById(id);
        element.addEventListener("focus",function() {
            item.style.visibility = "visible";
            item.style.opacity = "1";
        });

        element.addEventListener("focusout",function () {
            item.style.visibility = "hidden";
            item.style.opacity = "0";
        });

    });
})