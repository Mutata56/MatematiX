"use strict";
let search = document.querySelector("#search");
let searchInput = document.querySelector("#search-input");
let findBy = document.querySelector("#findBy");
let sortBy = document.querySelector("#sortBy");
let currentPage = document.querySelector(".bottom-nav").getAttribute("value");
let itemsPerPage = document.querySelector(".pagination-settings").getAttribute("value");
let pagination15 = document.querySelector("#pagination15");
let pagination50 = document.querySelector("#pagination50");
let pagination100 = document.querySelector("#pagination100");
let currentPage1st = document.querySelector("#currentPage1st");
let currentPage2nd = document.querySelector("#currentPage2nd");
let currentPage3rd = document.querySelector("#currentPage3rd");
let currentPageLast = document.querySelector("#currentPageLast");
let activateObjects = document.getElementsByClassName("activateClass");
let deactivateObjects = document.getElementsByClassName("deactivateClass");
let blockObjects = document.getElementsByClassName("blockClass");
let unblockObjects = document.getElementsByClassName("unblockClass");

let sortArrowsAsc = document.querySelector("#sortArrowsAsc");
let sortArrowsDesc = document.querySelector("#sortArrowsDesc");

console.log(blockObjects)
console.log(unblockObjects)
console.log(activateObjects)
console.log(deactivateObjects)

const initializeBeforeSendingToServer = () => {
    let urlParams = new URLSearchParams(window.location.search);
    let isSearchEmpty = false;
    if(searchInput.value || searchInput.value.trim().length <= 0) {
        isSearchEmpty = true;
    }
    if(sortBy.value != null)
        urlParams.set("sortBy",sortBy.value);

    if(!isSearchEmpty && findBy.value != null && searchInput.value != null) {
        urlParams.set("findBy", findBy.value);

    }
    if(!isSearchEmpty)
        urlParams.set("find",searchInput.value)
    urlParams.set("itemsPerPage",itemsPerPage);
    let previousItemsPerPage = urlParams.get("previousItemsPerPage");
    if(previousItemsPerPage != null)
        urlParams.set("previousItemsPerPage",null)
    urlParams.set("currentPage",currentPage);
    return urlParams;
}
searchInput.addEventListener("keydown",(key) => {
    if(key.keyCode == 13) {
        let urlParams = initializeBeforeSendingToServer();
        urlParams.set("find",searchInput.value)
        urlParams.set("findBy",findBy.value)
        window.location.search = urlParams;
    }
});
pagination15.addEventListener("click",() => {
    let urlParams = initializeBeforeSendingToServer();
    urlParams.set("previousItemsPerPage",urlParams.get("itemsPerPage"));
    urlParams.set("itemsPerPage","15");
    urlParams.set("currentPage","1");
    window.location.search = urlParams;
});
pagination50.addEventListener("click",() => {
    let urlParams = initializeBeforeSendingToServer();
    urlParams.set("previousItemsPerPage",urlParams.get("itemsPerPage"));
    urlParams.set("itemsPerPage","50");
    urlParams.set("currentPage","1");
    window.location.search = urlParams;
});
pagination100.addEventListener("click",() => {
    let urlParams = initializeBeforeSendingToServer();
    urlParams.set("previousItemsPerPage",urlParams.get("itemsPerPage"));
    urlParams.set("itemsPerPage","100");
    urlParams.set("currentPage","1");
    window.location.search = urlParams;
});
sortBy.addEventListener("change",() => {
   let urlParams = initializeBeforeSendingToServer();
   urlParams.set("sortBy",sortBy.value);
    window.location.search = urlParams;
});
currentPage1st.addEventListener("click",() => {
   let urlParams = initializeBeforeSendingToServer();
   urlParams.set("currentPage",currentPage1st.getAttribute("content"));
    window.location.search = urlParams;
});
if(currentPage2nd != null) {
    currentPage2nd.addEventListener("click",() => {
        let urlParams = initializeBeforeSendingToServer();
        urlParams.set("currentPage",currentPage2nd.getAttribute("content"));
        window.location.search = urlParams;
    });
}
if(currentPage3rd !=  null) {
    currentPage3rd.addEventListener("click",() => {
        let urlParams = initializeBeforeSendingToServer();
        urlParams.set("currentPage",currentPage3rd.getAttribute("content"));
        window.location.search = urlParams;
    });
}
if(currentPageLast != null) {
    currentPageLast.addEventListener("click",() => {
        let urlParams = initializeBeforeSendingToServer();
        urlParams.set("currentPage",currentPageLast.getAttribute("content"));
        window.location.search = urlParams;
    });
}

for (let i = 0;i<activateObjects.length;i++) {
    activateObjects[i].addEventListener("click",function (event) {
        event.preventDefault();
        let urlParams = initializeBeforeSendingToServer();
        window.location.href = activateObjects[i].getAttribute("href") +"?" +urlParams.toString();
    })
}
for (let i = 0;i<deactivateObjects.length;i++) {
    deactivateObjects[i].addEventListener("click",function (event) {
        event.preventDefault();
        let urlParams = initializeBeforeSendingToServer();
        window.location.href = deactivateObjects[i].getAttribute("href") +"?" +urlParams.toString();
    })
}
for (let i = 0;i<blockObjects.length;i++) {
    blockObjects[i].addEventListener("click",function (event) {
        event.preventDefault();
        let urlParams = initializeBeforeSendingToServer();
        window.location.href = blockObjects[i].getAttribute("href") +"?" +urlParams.toString();
    })
}
for (let i = 0;i<unblockObjects.length;i++) {
    unblockObjects[i].addEventListener("click",function (event) {
        event.preventDefault();
        let urlParams = initializeBeforeSendingToServer();
        window.location.href = unblockObjects[i].getAttribute("href") +"?" +urlParams.toString();
    })
}
if(sortArrowsAsc != null) {
    sortArrowsAsc.addEventListener("click",function () {
        let urlParams = initializeBeforeSendingToServer();
        urlParams.set("sortDirection","desc");
        window.location.search = urlParams;
    });
}

if(sortArrowsDesc != null) {
    sortArrowsDesc.addEventListener("click",function () {
        let urlParams = initializeBeforeSendingToServer();
        urlParams.set("sortDirection","asc");
        window.location.search = urlParams;
    });
}
search.addEventListener("click",(event) => {
    event.preventDefault();
    let urlParams = initializeBeforeSendingToServer();
    urlParams.set("findBy", findBy.value);
    urlParams.set("find",searchInput.value);
   window.location.search = urlParams;
});