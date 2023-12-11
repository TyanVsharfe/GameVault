window.onload = async function () {
    function sendRequest(gameId) {

        fetch(`/api/game/${gameId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                data.forEach(game => {
                    const gameName = document.querySelector('.game-title');
                    const gameReleaseDate = document.querySelector('.game-release-date');
                    const gameCover = document.querySelector('.game__cover');
                    const gamePlatforms = document.querySelector('.game-platforms');
                    const gameSummary = document.querySelector('.game__summary');

                    // Обложка игры
                    gameCover.src = game.cover.url.replace('t_thumb', 't_1080p');
                    gameCover.style.width = '30%';
                    gameCover.style.height = '30%';
                    //let id = game.id

                    // Список платформ
                    console.log('Кол-во платформ', game.platforms.length)
                    if (game.platforms.length > 1) {
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

                    // Заголовок
                    console.log('Присваиваем заголовок игры')
                    gameName.textContent = game.name;
                    console.log('Game title', gameName.textContent)

                    // Год релиза
                    // gameReleaseDate.textContent = date.toISOString().slice(0, 10);
                    gameReleaseDate.textContent = game.release_dates[0].y;

                    // Описание
                    gameSummary.textContent = game.summary
                });
            })
            .catch(error => {
                console.error('Ошибка при выполнении запроса:', error);
            });
    }

    // Получаем текущий путь страницы
    let currentPath = window.location.pathname;
    // Разбиваем путь на части по слешу '/'
    let pathParts = currentPath.split('/');
    // Получаем последний элемент массива (последний параметр в пути)
    let gameId = pathParts[pathParts.length - 1];
    console.log(gameId)
    console.log('Щас будет вызов метода')

    sendRequest(gameId)
}

