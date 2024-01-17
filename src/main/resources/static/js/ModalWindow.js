function openModalWindow(modalId) {
    console.log("ModalId " + modalId)
    const modalWindow = document.querySelector('.modal[data-modal="' + modalId + '"]'),
          closeModalButton = document.querySelector('.modal__close[data-modal="' + modalId + '"]'),
          acceptModalButton = document.querySelector('.modal__accept[data-modal="' + modalId + '"]');

    closeModalButton.addEventListener('click', function () {
        closeModalWindow(modalId)
    });
    acceptModalButton.addEventListener('click', function () {
        acceptModalWindow(modalId)
    });

    ratingForm();

    modalWindow.classList.add('active');

     document.addEventListener('keydown', function (e) {
         if (e.key === "Escape")
             closeModalWindow(modalId);
     })
}

function closeModalWindow(modalId) {
    const modalWindow = document.querySelector('.modal[data-modal="' + modalId + '"]');
    modalWindow.classList.remove('active');
}

async function acceptModalWindow(modalId) {
    if (modalId === 1) {
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
            localStorage.removeItem(window.location.pathname);
            location.reload();
        }
        else {
            alert("The status is not selected")
        }
    }
    else if (modalId === 2) {
        await fetch(`/api/game/${getGameId()}`, {
            method: "PUT",
            headers: {"Accept": "application/json", "Content-Type": "application/json"},
            body: JSON.stringify({
                "userRating": parseFloat(getRating()),
            })
        });
        localStorage.removeItem(window.location.pathname);
        location.reload();
        alert("Оценка обновлена")
    }
}

function ratingForm() {
    const ratingGraphicsSlider = document.getElementById('rating-graphics');
    const ratingGraphicsSliderValue = document.getElementById('rating-graphics-value');
    ratingGraphicsSliderValue.textContent = ratingGraphicsSlider.value;

    ratingGraphicsSlider.oninput = function () {
        ratingGraphicsSliderValue.textContent = this.value;
    }

    const ratingStorySlider = document.getElementById('rating-story');
    const ratingStorySliderValue = document.getElementById('rating-story-value');
    ratingStorySliderValue.textContent = ratingGraphicsSlider.value;

    ratingStorySlider.oninput = function () {
        ratingStorySliderValue.textContent = this.value;
    }

    const ratingGameplaySlider = document.getElementById('rating-gameplay');
    const ratingGameplaySliderValue = document.getElementById('rating-gameplay-value');
    ratingGameplaySliderValue.textContent = ratingGraphicsSlider.value;

    ratingGameplaySlider.oninput = function () {
        ratingGameplaySliderValue.textContent = this.value;
    }
}

function getRating() {
    const ratingGraphicsSlider = document.getElementById('rating-graphics');
    const ratingStorySlider = document.getElementById('rating-story');
    const ratingGameplaySlider = document.getElementById('rating-gameplay');

    return ratingGraphicsSlider.value * 2 + ratingStorySlider.value * 3.5 + ratingGameplaySlider.value * 4.5
}