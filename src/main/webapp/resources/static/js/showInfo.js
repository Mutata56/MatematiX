let forms = document.getElementsByClassName("form-info");
let infos = document.getElementsByClassName("info-logo");

let func = (i) => {
    infos[i].addEventListener("mouseover",() => {
        forms[i].style.visibility = "visible";
    });
    infos[i].addEventListener("mouseout",() => {
        forms[i].style.visibility = "hidden";
    });
}

for(let i = 0; i < infos.length; i++) {
    func(i);
}
