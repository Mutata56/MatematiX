$(document).ready(function() {
    let fileInput = $("#secretInput"),
        croppingImage = $("#croppingImage"),
        submitChangeAvatar = $("#submitChangeAvatar"),
        theForm = $("#theForm"),
        uploadedImage = "",
        cropper = null,
        cropperExists = false,
        changeYourAvatarModal = $("#changeYourAvatarModal");

    submitChangeAvatar.click(function(e) {
        e.preventDefault();
        e = cropper.getData();
        $("#x").val(e.x);
        $("#y").val(e.y);
        $("#width").val(e.width);
        $("#height").val(e.height);
        $("#scaleX").val(e.scaleX);
        $("#scaleY").val(e.scaleY);
        theForm.submit();
    });

    fileInput.change(function() {
        changeYourAvatarModal.modal('toggle');
    });

    changeYourAvatarModal.on("shown.bs.modal", function() {
        if (fileInput.prop("files")[0]) {
            const n = new FileReader();
            n.addEventListener("load", function() {
                uploadedImage = n.result;
                croppingImage.attr("src", uploadedImage);
                let previewDiv = $("#previewDiv"),
                    previewDivComments = $("#previewDivComments");
                if (!cropperExists) {
                    cropperExists = true;
                    cropper = new Cropper(croppingImage[0], {
                        aspectRatio: 1,
                        viewMode: 2,
                        preview: [previewDiv[0], previewDivComments[0]],
                        minCropBoxHeight: 100,
                        minCropBoxWidth: 100,
                        dragMode: "none",
                        cropBoxResizable: false
                    });
                } else {
                    cropper.replace(uploadedImage);
                }
            });
            n.readAsDataURL(fileInput.prop("files")[0]);
        }
    });
});