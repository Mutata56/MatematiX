$(document).ready(function() {
    let slider = document.getElementById("slider");
    let btn1 = document.getElementById("btn1")
    let btn2 = document.getElementById("btn2")
    let btn3 = document.getElementById("btn3")
    let elements = document.querySelectorAll("h1, h2, h3, h4, h5, h6, p, .link, .MJX-TEX")

    let initialFontSizes = new Map()
    for(let i = 0;i < elements.length;i++)
        initialFontSizes.set(elements[i],Number(window.getComputedStyle(elements[i]).fontSize.replaceAll("px","")))

    const setFontSize = (num) => {

        for (let i = 0; i < elements.length; i++)
            elements[i].style.fontSize = (initialFontSizes.get(elements[i]) + Number((num == undefined ? slider.value : num))) + "px";

    }

    slider.addEventListener("input",function(event) {
        event.preventDefault();
        setFontSize()
    })
    btn1.addEventListener("click",function() {
        if(slider.value >= -19) {
            slider.value--;
            setFontSize()
        }
    });
    btn2.addEventListener("click",function() {
        slider.value = 0;
        setFontSize(0)
    });
    btn3.addEventListener("click",function() {
        if(slider.value <= 19) {
            slider.value++;
            setFontSize()
        }
    });

})