// https://stackoverflow.com/questions/1909441/how-to-delay-the-keyup-handler-until-the-user-stops-typing
function delay(callback, ms) {
    var timer;
    return function() {
        clearTimeout(timer);
        timer = setTimeout($.proxy(callback, this), ms || 0);
    };
}