let error1 = document.querySelector(".error1");
let usernameInput = document.getElementById("username");
let error2 = document.querySelector(".error2");
let passwordInput = document.getElementById("password");
usernameInput.addEventListener("keyup",() => {
    if(usernameInput.value.length >= 1 && error1 != null) {
        error1.classList.add("disappear");
    }
})
passwordInput.addEventListener("keyup", () => {
    if(passwordInput.value.length >= 1 && error2 != null) {
        error2.classList.add("disappear");
    }
})