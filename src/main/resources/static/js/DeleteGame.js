function deleteGame() {
    console.log('Вызывается DeleteGame');
    const storedData = localStorage.getItem('gameData')
    const parsedData = JSON.parse(storedData);
    console.log(parsedData[0]);
    console.log(parsedData[0].id);// Это ваши сохраненные данные
    fetch(`/api/game/${parsedData[0].id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    localStorage.clear();
    location.reload();
    alert("Игра удалена");
}