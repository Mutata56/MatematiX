function checkAlive() {
    $.ajax({
        url: "/isAlive/setAlive",
        timeout: 240000,
        method: "GET"
    }).fail(function(error) {
        console.error("An error occurred while checking whether the user is alive",error)
    });
}
$(document).ready(checkAlive);