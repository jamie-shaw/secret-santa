function limitTextArea(textArea) {

    var value = textArea.value;
    var maxlength = parseInt(textArea.maxlength);

    if (textArea.value.length > maxlength) {
        textArea.value = value.substring(0, maxlength);
    }

}
