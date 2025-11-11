INSERT INTO category(name)
VALUES		 ("ダックスフンド")
			,("パグ");

INSERT INTO tag(name)
VALUES		 ("おとなしい")
			,("やんちゃ");

INSERT INTO pet(category_id, tag_id, name, photoUrls)
VALUES		 (1, 1, "ポチ", "url1")
			,(1, 2, "タロ", "url2")
			,(2, 1, "ハナ", "url3");
			
INSERT INTO orders(pet_id, quantity, shipDate)
VALUES		 (1, 10, "2025-11-01 23:50:50")
			,(2, 20, "2025-11-02 18:30:10")
			,(3, 15, "2025-11-03 13:10:00");
			
INSERT INTO user(username, firstName, lastName, email, password, phone)
VALUES		('test_user', 'test', 'user', 'user@example.com', '$2a$07$oz0MQIs4RnF070l5wmt9xulXjaBrgri9gLCcmvIqSXw5BewGsQQuS', '0123456789');