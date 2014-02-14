DROP TABLE IF EXISTS `Keys`;

CREATE TABLE `Keys` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `service` varchar(255) NOT NULL,
  `api_key` varchar(255) NOT NULL,
  `api_secret` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Users`;

CREATE TABLE `Users` (
  `user_id` int(11) NOT NULL,
  `screen_name` varchar(15) NOT NULL,
  `access_token` varchar(255) DEFAULT NULL,
  `token_secret` varchar(255) DEFAULT NULL,
  `token_raw` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
