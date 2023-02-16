$(document).ready(() => {
    let header = $(".header"),
        elementAfterHeader = header.parent().next();
    $(window).on("scroll", () => {
        let scrollY = $(window).scrollTop();
        let shouldBeFixed = scrollY >= header.outerHeight();
        if (shouldBeFixed) {
            header.addClass("fixed-top fixed");
            elementAfterHeader.css("margin-top", header.outerHeight() + "px");
        } else {
            header.removeClass("fixed-top fixed");
            elementAfterHeader.css("margin-top", "0");
        }
    });
});