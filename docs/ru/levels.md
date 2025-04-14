# /api/v1/levels

<a href="https://logotipiwe.ru/honest-be/swagger-ui/index.html#/levels-controller/getLevels">Ссылка на сваггер</a>

В объекте уровня указан фон карты уровня, их бывает 2 вида:
### Фон с svg картинкой
Если для уровня указан id картинки, которую использовать как фоновую. Объект уровня будет иметь вид:
```json
[
  ...
  {
    ...
    "backgroundColor": {
      "type": "BACKGROUND_IMAGE",
      "imageId": "face"
    }
  }
]
```
### Фон с однотонным цветом
Используется, если не указан id фоновой картинки. Тогда должен быть указан цвет. Объект уровня будет иметь вид:
```json
[
  ...
  {
    ...
    "backgroundColor": {
      "type": "COLOR",
      "color": "100,200,255"
    }
  }
]
```