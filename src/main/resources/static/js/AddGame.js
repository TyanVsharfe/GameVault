async function addGame() {
    console.log('Вызывается AddGame');
    const storedData = localStorage.getItem(window.location.pathname)
    const parsedData = JSON.parse(storedData);
    console.log(parsedData[0]); // Это ваши сохраненные данные
    console.log('Тут что я отправляю');
    console.log(parsedData[0].id);
    console.log(parsedData[0].name);
    console.log(parsedData[0].cover.url);
    await fetch("/api/game", {
        method: "POST",
        headers: {"Accept": "application/json", "Content-Type": "application/json"},
        body: JSON.stringify({
            "id": parsedData[0].id,
            "title": parsedData[0].name,
            "coverUrl": parsedData[0].cover.url
        })
    });
    localStorage.removeItem(window.location.pathname);
    location.reload();
    alert("Игра добавлена");
}