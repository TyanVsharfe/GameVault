document.addEventListener("DOMContentLoaded", function () {
    document.querySelector('.game').style.display = 'none';
    getGame();
});

async function getGame() {
    // Получаем текущий путь страницы
    let currentPath = window.location.pathname;
    // Разбиваем путь на части по слешу '/'
    let pathParts = currentPath.split('/');
    // Получаем последний элемент массива (последний параметр в пути)
    let gameId = pathParts[pathParts.length - 1];
    console.log(gameId)
    console.log('Щас будет вызов метода')

    const result = await checkEntity(gameId)
    if (result) {
        console.log('Запись в бд есть');
        sendRequest(gameId)
    } else {
        console.log('Записи в бд нету, берем с сайта IGDB');
        sendRequest(gameId)

        const gameGenres = document.querySelector('.game-genres');
        const addGameButton = document.createElement('button');
        addGameButton.classList.add('add-game-button');
        addGameButton.textContent = "Add game";
        addGameButton.addEventListener('click', addGame);
        gameGenres.insertAdjacentElement('afterend', addGameButton)
    }
}

function sendRequest(gameId) {
    fetch(`/api/game/${gameId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            localStorage.setItem('gameData', JSON.stringify(data));
            data.forEach(game => {
                const gameName = document.querySelector('.game-title');
                const gameReleaseDate = document.querySelector('.game-release-date');
                const gameCover = document.querySelector('.game__cover');
                const gamePlatforms = document.querySelector('.game-platforms');
                const gameSummary = document.querySelector('.game__summary');
                const gameGenres = document.querySelector('.game-genres');

                // Обложка игры
                gameCover.src = game.cover.url.replace('t_thumb', 't_1080p');
                gameCover.style.width = '30%';
                gameCover.style.height = '30%';
                //let id = game.id

                // Список платформ
                console.log('Кол-во платформ', game.platforms.length)
                if (game.platforms !== undefined && game.platforms.length > 1) {
                    game.platforms.forEach(platform => {
                        const li = document.createElement('li')
                        li.textContent = platform.abbreviation
                        gamePlatforms.appendChild(li);
                    })
                }
                else {
                    const li = document.createElement('li')
                    li.textContent = game.platforms[0].abbreviation
                    gamePlatforms.appendChild(li);
                }

                // Список жанров
                if (game.genres !== undefined && game.genres.length > 1) {
                    game.genres.forEach(genre => {
                        const li = document.createElement('li')
                        li.textContent = genre.name
                        gameGenres.appendChild(li);
                    })
                }
                else if (game.genres !== undefined && game.genres.length === 1) {
                    const li = document.createElement('li')
                    li.textContent = game.genres[0].name
                    gameGenres.appendChild(li);
                }

                // Название игры
                console.log('Присваиваем название игры')
                gameName.textContent = game.name;
                console.log('Название: ', gameName.textContent)

                // Дата релиза
                if (game.first_release_date !== undefined) {
                    gameReleaseDate.textContent = new Date(game.first_release_date * 1000).toLocaleDateString();
                }
                else if (game.release_dates !== undefined) {
                    gameReleaseDate.textContent = game.release_dates[0].y;
                }

                // Описание
                gameSummary.textContent = game.summary

                const divLoading = document.getElementById('loading');
                divLoading.parentNode.removeChild(divLoading);
                document.querySelector('.game').style.display = 'flex';
            });
        })
        .catch(error => {
            console.error('Ошибка при выполнении запроса:', error);
        });
}
