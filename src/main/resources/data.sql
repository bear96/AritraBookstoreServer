DELETE FROM book;
ALTER TABLE book AUTO_INCREMENT = 1001;

DELETE FROM category;
ALTER TABLE category AUTO_INCREMENT = 1001;

INSERT INTO `category` (`name`) VALUES ('Classics'),('Fantasy'),('Crime'),('Romance'),('Horror');

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Call of the Wild', 'Jack London', '', 599, 0, FALSE, TRUE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Brothers Karamazov', 'Fyodor Dostoevsky', '', 1399, 0, FALSE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Little Dorrit', 'Charles Dickens', '', 599, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Pride and Prejudice', 'Jane Austen', '', 399, 0, TRUE, FALSE, 1001);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Legendborn', 'Tracy Deonn', '', 699, 0, FALSE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Merlin Trilogy', 'Mary Stewart', '', 299, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Midnight Sun', 'Stephanie Meyer', '', 99, 0, TRUE, TRUE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Call of the Night, Vol. 9', 'Kotoyama', '', 1399, 0, FALSE, FALSE, 1002);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Silent Patient', 'Alex Michaelides', '', 499, 0, FALSE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('And Then There Were None', 'Agatha Christe', '', 299, 0, TRUE, TRUE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Paris Apartment', 'Lucy Foley', '', 499, 0, FALSE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Adventures of Sherlock Holmes', 'Sir Arthur Conan Doyle', '', 99, 0, TRUE, FALSE, 1003);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('It Ends With Us', 'Colleen Hoover', '', 199, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Dreamland', 'Nicholas Sparks', '', 399, 0, FALSE, TRUE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Men Without Women: Stories', 'Haruki Murakami', '', 699, 0, FALSE, TRUE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Challenge', 'Danielle Steel', '', 199, 0, TRUE, FALSE, 1004);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('It', 'Stephen King', '', 999, 0, FALSE, TRUE, 1005);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Uzumaki (3-in-1 Deluxe Edition)', 'Junji Ito', '', 1699, 0, FALSE, FALSE, 1005);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Silent Companions', 'Laura Purcell', '', 599, 0, FALSE, TRUE, 1005);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Penpal', 'Dathan Auerbach', '', 499, 0, FALSE, FALSE, 1005);