/* Events */

// On load
(function () {
    offsetFromParentRightEdge('addButton', 'itemsListContainer', 75);
})();


// On resize
window.onresize = function () {
    offsetFromParentRightEdge('addButton', 'itemsListContainer', 75);
}


/* Functions */

function changeNumberOfItemsPerPage(newNumberOfItemsPerPage) {
    window.location.href = '/items?type=' + itemType + '&items=' + newNumberOfItemsPerPage;
}

function sortItems(newSortParameters) {
    window.location.href = 'items?type=' + itemType + newSortParameters;
}

function enableChangePasswordButton() {
    const inputs = document.querySelectorAll('.pwd-change-input');

    for (let i = 0; i < inputs.length; i++) {
        if (inputs.item(i).value.trim() === '') {
            console.log(i + ': ' + inputs.item(i).value)
            document.getElementById('changePasswordButton').disabled = true;
            return;
        }
    }

    document.getElementById('changePasswordButton').disabled = false;
}

function offsetFromParentRightEdge(elementId, parentId, offsetRightInPixels) {
    const parent = document.getElementById(parentId);
    const element = document.getElementById(elementId);

    if (parent == null || element == null)
        return;

    const parentRightEdge = parent.getBoundingClientRect().right;
    const elementWidth = element.getBoundingClientRect().width;

    element.style.left = `${parentRightEdge - offsetRightInPixels - elementWidth}px`;
}

