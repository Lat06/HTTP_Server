# JWT Servlet API

## Проєкт: Система автентифікації з JWT на Java Servlet API

Цей проєкт реалізує просту backend-систему з автентифікацією за допомогою JWT. Після входу користувач отримує токен, з яким можна виконувати запити до API для управління товарами.


## Технології

- Java 9
- Servlet API 4.0.1
- Apache Tomcat 9
- JSON Web Token (JWT)
- Gson (для роботи з JSON)
- Maven
- Postman (для тестування)

## 📂 Структура

###  LoginServlet.java

- `POST /login`
- Приймає JSON з `login` та `password`
- Якщо успішно — повертає JWT-токен

### JwtUtil.java

- Статичний клас для генерації та перевірки JWT
- Створює токен із `sub`, `iat`, `exp`
- Алгоритм HMAC256

### GoodServlet.java

Обробляє запити на `/api/good/*` з JWT токеном:

| Метод | Шлях            | Опис                                 |
|-------|------------------|----------------------------------------|
| PUT   | /api/good        | Створити товар                         |
| GET   | /api/good/{id}   | Отримати товар за ID                   |
| POST  | /api/good/{id}   | Оновити існуючий товар                 |
| DELETE| /api/good/{id}   | Видалити товар                         |

** Усі запити вимагають заголовок:**

```

Authorization: Bearer <токен>

````

---

## Приклад сценарію

1. Надіслати POST-запит на `/login` з тілом:
```json
{
  "login": "admin",
  "password": "1234"
}


2. Отримати токен:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}


<img width="946" alt="Pasted Graphic 1" src="https://github.com/user-attachments/assets/a4c747df-d391-453e-9759-da0416cb4308" />


```

3. Виконати PUT-запит на `/api/good` з заголовком:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

І тілом:

```json
{
  "name": "Phone",
  "price": 499.99,
  "category": "Electronics"
}
```
<img width="889" alt="Pasted Graphic" src="https://github.com/user-attachments/assets/8e8424df-93d6-4d35-a6dd-509f68b18ea6" />

Також приклади GET DELETE запитів

<img width="661" alt="Pasted Graphic 1" src="https://github.com/user-attachments/assets/91d78244-8b63-47ea-87a0-4c4e92ca2c86" />

<img width="840" alt="Pasted Graphic 3" src="https://github.com/user-attachments/assets/4a4be728-116e-4f58-ba7a-acd82833e9be" />


---

## Розгортання

1. Зібрати `.war` файл:

```bash
mvn clean package
```

2. Скопіювати до Tomcat:

```bash
cp target/jwt-servlet-api-1.0-SNAPSHOT.war ~/Desktop/apache-tomcat-9.0.106/webapps/
```

3. Запустити Tomcat:

```bash
cd ~/Desktop/apache-tomcat-9.0.106/bin
chmod +x *.sh
./startup.sh
```

---

## Тестування

Використовував Postman для виконання всіх запитів:

* `POST /login`
* `PUT /api/good`
* `GET /api/good/{id}`
* `POST /api/good/{id}`
* `DELETE /api/good/{id}`


