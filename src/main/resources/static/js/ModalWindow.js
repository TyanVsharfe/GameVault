function openModalWindow() {
    const modalWindow = document.querySelector('.modal'),
          closeModalButton = document.querySelector('.modal__close'),
          acceptModalButton = document.querySelector('.modal__accept');
    closeModalButton.addEventListener('click', closeModalWindow);
    acceptModalButton.addEventListener('click', acceptModalWindow);
    modalWindow.classList.add('active');
}

function closeModalWindow() {
    const modalWindow = document.querySelector('.modal');
    modalWindow.classList.remove('active');
}

async function acceptModalWindow() {
    let selectedValue = undefined;
    const radioButtons = document.getElementsByName('radio');
    radioButtons.forEach( btn => {
        if (btn.checked)
            selectedValue = btn.value;
        })

    if (selectedValue !== undefined) {
        await fetch(`/api/game/${getGameId()}`, {
            method: "PUT",
            headers: {"Accept": "application/json", "Content-Type": "application/json"},
            body: JSON.stringify({
                "status": parseInt(selectedValue),
            })
        });
        localStorage.clear();
        location.reload();
    }
    else {
        alert("The status is not selected")
    }
}