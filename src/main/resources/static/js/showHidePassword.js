/**
 * Переключает видимость поля пароля при клике на иконку «глазика».
 * Меняет класс иконки между скрытым и видимым состоянием,
 * меняет атрибут `name` для корректного отображения иконки,
 * а также переключает тип связанного поля между "password" и "text".
 *
 * @this {HTMLElement} элемент-иконка, по которому кликнули
 * @param {MouseEvent} event объект события клика
 */
const logicForButton = function(event) {
  // Получаем список CSS-классов и идентификатор связанного input
  /** @type {DOMTokenList} */
  let classList = this.classList;
  /** @type {string} */
  let cl = this.getAttribute("content");
  /** @type {boolean} */
  let isHidden = classList.contains("hidden");

  // Убираем оба состояния и добавляем противоположное
  classList.remove("hidden", "visible");
  classList.add(isHidden ? "visible" : "hidden");

  // Меняем атрибут name — для корректного выбора иконки
  this.setAttribute("name", isHidden ? "eye-off-outline" : "eye-outline");

  // Переключаем тип поля между текстовым и паролем
  $(`#${cl}`).attr("type", isHidden ? "text" : "password");
};

// Навешиваем обработчик клика на все элементы с классом .theEye
$(".theEye").on("click", logicForButton);