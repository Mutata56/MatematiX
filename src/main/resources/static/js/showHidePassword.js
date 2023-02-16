const logicForButton = function() {
    let classList = this.classList;
    let cl = this.getAttribute("content");
    let isHidden = classList.contains("hidden");
    classList.remove("hidden", "visible");
    classList.add(isHidden ? "visible" : "hidden");
    this.setAttribute("name", isHidden ? "eye-off-outline" : "eye-outline");
    $(`#${cl}`).attr("type", isHidden ? "text" : "password");
}
$(".theEye").on("click", logicForButton);