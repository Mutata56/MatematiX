const logicForButton = function() {
    let classList = this.classList;
    let cl = this.getAttribute("content");
    if(classList.contains("hidden")) {
        classList.remove("hidden");
        classList.add("visible");
        this.setAttribute("name","eye-off-outline");
        $(`#${cl}`).attr('type','text');
    } else {
        classList.remove("visible");
        classList.add("hidden");
        this.setAttribute("name","eye-outline");
        $(`#${cl}`).attr('type','password');
    }
}

$(".theEye").on("click",logicForButton);