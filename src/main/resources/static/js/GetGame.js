document.addEventListener("DOMContentLoaded", function () {
    document.querySelector('.main-info').style.display = 'none';
    getGame();
});

async function getGame() {
    const gameId = getGameId();
    console.log(gameId);
    console.log('Щас будет вызов метода');

    const result = await checkEntity(gameId)
    if (result) {
        console.log('Запись в бд есть');
        await sendRequest(gameId)

        const gameGenres = document.querySelector('.game-genres');

        const deleteGameButton = document.createElement('button');
        deleteGameButton.classList.add('game-button');
        deleteGameButton.textContent = "Delete game";
        deleteGameButton.addEventListener('click', deleteGame);
        gameGenres.insertAdjacentElement('afterend', deleteGameButton)

        const gameStatusButton= document.createElement('button');
        gameStatusButton.classList.add('game-button');
        gameStatusButton.textContent = "Change status";
        gameGenres.insertAdjacentElement('afterend', gameStatusButton)
        gameStatusButton.addEventListener('click', function () {
            openModalWindow(1);
        });

        await fetch(`/api/game/igdb/${gameId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(game => {
                const gameStatusTitle = document.createElement('label');
                gameStatusTitle.classList.add("status-title")
                gameStatusTitle.textContent = game.status;

                const numStatus = enumStatus[game.status]
                const radioButtons = document.getElementsByName('radio');
                radioButtons.forEach( btn => {
                    if (parseInt(btn.value) === numStatus) {
                        btn.checked = true;
                    }
                })

                console.log("Num status " + numStatus);

                if (numStatus !== 2 && numStatus !== undefined) {
                    const gameRatingButton= document.createElement('button');
                    gameRatingButton.classList.add('game-button');
                    gameRatingButton.textContent = "Change Rating";
                    gameGenres.insertAdjacentElement('afterend', gameRatingButton)
                    gameRatingButton.addEventListener('click', function () {
                        openModalWindow(2);
                    });

                    if (game.userRating !== null) {
                        const gameUserRating = document.createElement('label');
                        gameUserRating.classList.add("status-title")
                        gameUserRating.textContent = game.userRating;
                        gameGenres.insertAdjacentElement('afterend', gameUserRating);
                    }
                }

                gameGenres.insertAdjacentElement('afterend', gameStatusTitle);
            })
    } else {
        console.log('Записи в бд нету, берем с сайта IGDB');
        await sendRequest(gameId)

        const gameGenres = document.querySelector('.game-genres');
        const addGameButton = document.createElement('button');
        addGameButton.classList.add('game-button');
        addGameButton.textContent = "Add game";
        addGameButton.addEventListener('click', addGame);
        gameGenres.insertAdjacentElement('afterend', addGameButton)
    }
}

async function sendRequest(gameId) {
    fetch(`/api/igdb/game/${gameId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const keyStorage = `/game/${gameId}`;
            localStorage.setItem(keyStorage, JSON.stringify(data));
            data.forEach(game => {
                const gameName = document.querySelector('.game-title'),
                      gameReleaseDate = document.querySelector('.game-release-date'),
                      gameCover = document.querySelector('.game__cover'),
                      gamePlatforms = document.querySelector('.game-platforms'),
                      gameSummary = document.querySelector('.game__summary'),
                      gameGenres = document.querySelector('.game-genres');

                // Обложка игры
                gameCover.src = game.cover.url.replace('t_thumb', 't_1080p');
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
                document.querySelector('.main-info').style.display = 'flex';
            });
        })
        .catch(error => {
            console.error('Ошибка при выполнении запроса:', error);
        });
}

function getGameId() {
    // Получаем текущий путь страницы
    let currentPath = window.location.pathname;
    // Разбиваем путь на части по слешу '/'
    let pathParts = currentPath.split('/');
    // Получаем последний элемент массива (последний параметр в пути)
    return pathParts[pathParts.length - 1];
}