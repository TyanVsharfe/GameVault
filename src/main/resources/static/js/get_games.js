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
        const listItem = document.createElement('li');

        const img = document.createElement('img');
        img.src = game.cover.url;

        listItem.textContent = game.name;
        resultList.appendChild(listItem);

        resultList.appendChild(listItem);
        resultList.appendChild(img);
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
