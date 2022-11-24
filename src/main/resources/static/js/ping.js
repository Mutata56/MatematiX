$(document).ready(
    function() {
        $.ajax({
            url: "/isAlive/setAlive",
            timeout: 120000,
            method:"GET"
            });
    }
);