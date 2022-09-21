"use strict";
let counter = 1;
let wasSelected = false;
let elements = document.getElementsByClassName("manual-btn");

// Auto-Scroll
setInterval(function() {
    if(!wasSelected) {
        document.getElementById(`radio` + counter).checked = true;
        if(++counter > 5)
            counter = 1;
    }
},5000);


const func = function(obj) {
    let forStr = obj.getAttribute("for");
    let num = forStr.charAt(forStr.length - 1);
    return function() {
        wasSelected = true;
        counter = num;
        setInterval(function() {
            wasSelected = false // Reset the variable so the carousel continues to work
        },60 * 1000)
    }
};

for(let i = 0; i < elements.length; i++) {
    elements[i].addEventListener(`click`,func(elements[i]));

}
