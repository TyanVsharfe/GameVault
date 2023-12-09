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
                const resultList = document.getElementById('main');
                resultList.innerHTML = '';

                data.forEach(game => {
                    const div = document.createElement('div')
                    const divTextInfo = document.createElement('div')
                    const gameName = document.createElement('a');
                    const gameReleaseDate = document.createElement('p');
                    const img = document.createElement('img');
                    div.classList.add('game');
                    divTextInfo.classList.add('game-info');

                    img.src = game.cover.url.replace('t_thumb', 't_cover_big');
             /*       img.style.width = '10%';
                    img.style.height = '10%';*/
                    let id = game.id

                    gameName.textContent = game.name;
                    gameName.href = `/game/${id}`;

                    // gameReleaseDate.textContent = date.toISOString().slice(0, 10);

                    gameReleaseDate.textContent = game.release_dates[0].y;

                    divTextInfo.appendChild(gameName);
                    divTextInfo.appendChild(gameReleaseDate);

                    div.appendChild(divTextInfo);
                    div.appendChild(img);

                    resultList.appendChild(div);
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