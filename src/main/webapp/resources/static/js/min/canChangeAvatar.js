let croppingDiv=document.getElementById("divForCropping"),fileInput=document.getElementById("secretInput"),croppingImage=document.getElementById("croppingImage"),main=document.getElementById("mainDiv"),header=document.getElementById("headerDiv"),body=document.getElementById("body"),overlay=document.getElementById("overlay"),submitChangeAvatar=document.getElementById("submitChangeAvatar"),theForm=document.getElementById("theForm");submitChangeAvatar.addEventListener("click",e=>{e.preventDefault();e=cropper.getData();document.getElementById("x").value=e.x,document.getElementById("y").value=e.y,document.getElementById("width").value=e.width,document.getElementById("height").value=e.height,document.getElementById("scaleX").value=e.scaleX,document.getElementById("scaleY").value=e.scaleY,theForm.submit()});let uploadedImage="",choosingAvatar=!1,cropper=null,cropperExists=!1,close=()=>{choosingAvatar=!1,croppingDiv.style.display="none",main.style.filter="none",header.style.filter="none",overlay.style.visibility="hidden",fileInput.value=null};overlay.addEventListener("click",e=>{choosingAvatar&&(e.preventDefault(),close())}),document.addEventListener("keydown",e=>{"Escape"===e.key&&choosingAvatar&&close()}),fileInput.addEventListener("change",()=>{if(fileInput.files[0]&&fileInput.files){const n=new FileReader;n.addEventListener("load",()=>{choosingAvatar=!0,uploadedImage=n.result,croppingImage.setAttribute("src",uploadedImage);var e=document.getElementById("previewDiv"),t=document.getElementById("previewDivComments");croppingDiv.style.display="block",overlay.style.visibility="unset",cropperExists?cropper.replace(uploadedImage):(cropper=new Cropper(croppingImage,{aspectRatio:1,viewMode:2,preview:[e,t],minCropBoxHeight:138,minCropBoxWidth:138,dragMode:"none",cropBoxResizable:!1}),cropperExists=!0)}),n.readAsDataURL(fileInput.files[0])}});