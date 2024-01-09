async function checkEntity(id) {
    try {
        const response = await fetch(`/api/checkEntity/${id}`);

        if (response.ok) {
            const result = await response.json();
            console.log(result);
            return result;
        } else {
            console.error('Ошибка при проверке сущности:', response.status);
            // Вероятно, вам нужно что-то вернуть в случае ошибки
            return null;
        }
    } catch (error) {
        console.error('Произошла ошибка:', error);
        // Обработка ошибки, если что-то пошло не так при выполнении запроса
        return null;
    }
}
