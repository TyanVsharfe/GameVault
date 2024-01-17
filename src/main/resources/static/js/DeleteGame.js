async function deleteGame() {
    console.log('Вызывается DeleteGame');
    const storedData = localStorage.getItem(window.location.pathname)
    const parsedData = JSON.parse(storedData);
    console.log(parsedData[0]);
    console.log(parsedData[0].id);// Это ваши сохраненные данные
    await fetch(`/api/game/${parsedData[0].id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    localStorage.removeItem(window.location.pathname);
    location.reload();
    alert("Игра удалена");
}