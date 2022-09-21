let nameInput = document.querySelector(".name-input");
let topicInput = document.querySelector(".topic-input");
let textarea = document.querySelector(".textarea-input");
let emailInput = document.querySelector(".email-input");
let messageDiv = document.querySelector(".message-div");
nameInput.addEventListener("input",() => {
    if(nameInput.value.length !== 0) {
        nameInput.classList.remove("input-error")
    }
})

topicInput.addEventListener("input",() => {
    if(topicInput.value.length !== 0) {
        topicInput.classList.remove("input-error")
    }
})

textarea.addEventListener("input",() => {
    let length = textarea.value.length;
    if(length !== 0) {
        textarea.classList.remove("input-error-textarea")
    }
    messageDiv.setAttribute("content",length);
})
emailInput.addEventListener("input",() => {
    if(emailInput.value.length !== 0) {
        emailInput.classList.remove("input-error")
    }
})