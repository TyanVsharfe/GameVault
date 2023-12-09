window.onload = async function () {
function sendRequest() {
    const gameName = document.getElementById('gameNameInput').value; // Получаем значение из поля ввода

fetch('/api/games', {
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

    data.forEach(game => {
        const div = document.createElement('div')
        const divTextInfo = document.createElement('div')
        const gameName = document.createElement('a');
        const gameReleaseDate = document.createElement('p');
        const img = document.createElement('img');
        div.classList.add('game-item');
        divTextInfo.classList.add('game-item__text-info');

        img.src = game.cover.url.replace('t_thumb', 't_cover_big');
        img.style.width = '10%';
        img.style.height = '10%';
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

// Находим кнопку и добавляем обработчик события при ее нажатии
const button = document.getElementById('submitButton');
button.addEventListener('click', sendRequest);
};
