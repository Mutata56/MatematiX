/**
 * Перенаправляет пользователя на страницу авторизации через заданную паузу.
 *
 * @constant {number} REDIRECT_DELAY — задержка перед редиректом в миллисекундах
 */
const REDIRECT_DELAY = 8000;

// После задержки выполняем переход на /auth/login
setTimeout(() => {
  location.href = '/auth/login';
}, REDIRECT_DELAY);