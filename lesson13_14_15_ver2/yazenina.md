Наличие интернета в помещении
Выведите идентификатор и признак наличия интернета в помещении. Если интернет в сдаваемом жилье присутствует, то выведите «YES», иначе «NO».
Используйте конструкцию "AS has_internet" для вывода признака наличия интернета в помещении.
Поля в результирующей таблице:
id
has_internet

SELECT id, IF(has_internet, 'YES', 'NO') AS has_internet FROM Rooms

Удалить перелеты из Москвы
Удалить все перелеты, совершенные из Москвы (Moscow).

DELETE FROM Trip WHERE town_from='Moscow' 

Вторые пилоты в Нью-Йорк
Т-Банк
Напишите запрос, который выведет имена пилотов, которые в качестве второго пилота (second_pilot_id) в августе 2023 года летали в New York
Поля в результирующей таблице:
name

SELECT DISTINCT p.name FROM Flights f JOIN Pilots p ON f.second_pilot_id=p.pilot_id 
WHERE f.second_pilot_id>0 AND f.flight_date BETWEEN '2023-08-01T00:00:00.000Z' AND '2023-08-31T00:00:00.000Z' AND f.destination='New York'

Кто покупал картошку
Определить, кто из членов семьи покупал картошку (potato)
Поля в результирующей таблице:
status

SELECT DISTINCT f.status FROM Goods g JOIN Payments p ON g.good_id=p.good JOIN FamilyMembers f ON f.member_id=p.family_member 
WHERE g.good_name='potato' 

Товары, купленные более одного раза
Определить товары, которые покупали более 1 раза
Поля в результирующей таблице:
good_name

SELECT g.good_name FROM Goods g JOIN Payments p ON g.good_id = p.good GROUP BY g.good_name
HAVING COUNT(*) > 1

Средний возраст людей
Вывести средний возраст людей (в годах), хранящихся в базе данных. Результат округлите до целого в меньшую сторону.
Используйте конструкцию "as age" для агрегатной функции подсчета среднего возраста. Это необходимо для корректной проверки.
Поля в результирующей таблице:
age

SELECT FLOOR(AVG(TIMESTAMPDIFF(YEAR, birthday,CURDATE()))) AS age FROM FamilyMembers
