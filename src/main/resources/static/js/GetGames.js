document.addEventListener("DOMContentLoaded", function () {
    const button = document.getElementById('submitButton');
    button.addEventListener('click', async function () {

        const result = document.getElementById('search-result');
        result.style.display = 'none';
        const divLoading = document.getElementById('loading');
        divLoading.style.display = 'block'

        await sendRequest()
        loading();
    });

   /* const filterButton = document.getElementById('filter-by-date-button');
    filterButton.addEventListener('click', filterByDate);*/

    const inputField = document.getElementById('gameNameInput');
    inputField.addEventListener('keypress', async function(event) {
        if (event.key === 'Enter') {

            const result = document.getElementById('search-result');
            result.style.display = 'none';
            const divLoading = document.getElementById('loading');
            divLoading.style.display = 'block';

            await sendRequest();
            loading();
        }
    });
});

function loading() {
    console.log("Добавлена крутилка");
    const result = document.getElementById('search-result');
    result.style.display = 'block';

    const divLoading = document.getElementById('loading');
    divLoading.style.display = 'none';
}

async function sendRequest() {
    const gameName = document.getElementById('gameNameInput').value; // Получаем значение из поля ввода

    return new Promise((resolve, reject) => {
        fetch('/api/igdb/games', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: gameName
        })
            .then(response => response.json())
            .then(data => {
                const resultList = document.getElementById('resultList');
                resultList.innerHTML = '';

                localStorage.setItem("games-search", JSON.stringify(data));

                data.forEach(game => {
                    const div = document.createElement('div')
                    const divTextInfo = document.createElement('div')
                    const gameName = document.createElement('a');
                    const gameReleaseDate = document.createElement('p');
                    const img = document.createElement('img');

                    div.classList.add('game-item');
                    divTextInfo.classList.add('game-item__text-info');

                    if (game.status !== undefined) {
                        const status = game.status
                        if ([6,7].includes(status))
                            return
                    }

                    if (game.cover !== undefined) {
                        img.src = game.cover.url.replace('t_thumb', 't_cover_big');
                        img.style.width = '10%';
                        img.style.height = '10%';
                    }

                    if (game.platforms !== undefined) {
                        game.platforms.forEach((platform) => {
                            if (platform.abbreviation !== undefined) {
                                const p = platform.abbreviation.replace(' ','-');
                                div.classList.add(`${p}`);
                            }
                        })
                    }

                    let id = game.id

                    gameName.textContent = game.name;
                    gameName.href = `/game/${id}`;


                    if (game.first_release_date !== undefined) {
                        gameReleaseDate.textContent = new Date(game.first_release_date * 1000).toLocaleDateString();
                    }
                    else if (game.release_dates !== undefined) {
                        gameReleaseDate.textContent = game.release_dates[0].y;
                    }

                    divTextInfo.appendChild(gameName);
                    divTextInfo.appendChild(gameReleaseDate);

                    div.appendChild(divTextInfo);
                    div.appendChild(img);

                    resultList.appendChild(div);
                    resolve();
                });
            })
            .catch(error => {
                console.error('Ошибка при выполнении запроса:', error);
                reject();
            });
    });
}

function filterByDate() {
    console.log("Сортировка")

    // Получаем все элементы с классом 'game-item'
    const gameItems = document.querySelectorAll('.game-item');

    // Преобразуем NodeList в массив для использования метода sort()
    const gameArray = Array.from(gameItems);

    // Функция для сравнения дат в формате 'DD.MM.YYYY'
    const compareDates = (a, b) => {
        const dateA = new Date(a.querySelector('p').textContent.split('.').reverse().join('-'));
        const dateB = new Date(b.querySelector('p').textContent.split('.').reverse().join('-'));
        return dateB - dateA;
    };

    // Сортируем массив элементов
    gameArray.sort(compareDates);

    // Очищаем родительский контейнер
    const resultList = document.getElementById('resultList');
    resultList.innerHTML = '';

    // Вставляем отсортированные элементы обратно в контейнер
    gameArray.forEach(gameItem => {
        resultList.appendChild(gameItem);
    });
}