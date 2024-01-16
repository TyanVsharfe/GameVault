window.onload = async function() {
    fetch('/api/games', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const resultList = document.getElementById('user-game-list');
            resultList.innerHTML = '';

            data.forEach(game => {
                const div = document.createElement('div')
                const divTextInfo = document.createElement('div')
                const gameName = document.createElement('a');
                const img = document.createElement('img');

                div.classList.add('game-preview');
                divTextInfo.classList.add('game-title')
                img.classList.add('cover-preview')

                if (game.status !== undefined) {
                    const status = game.status
                    if ([6,7].includes(status))
                        return
                }

                console.log(game.coverUrl)
                // Проверка, что обложка не пустая
                if (game.coverUrl !== null) {
                    img.src = game.coverUrl.replace('t_thumb', 't_cover_big');
                }

                let igdbId = game.igdbId

                gameName.textContent = game.title;
                gameName.href = `/game/${igdbId}`;

                divTextInfo.appendChild(gameName);

                div.appendChild(divTextInfo);
                div.appendChild(img);

                resultList.appendChild(div);
            });
        })
        .catch(error => {
            console.error('Ошибка при выполнении запроса:', error);
        });
}