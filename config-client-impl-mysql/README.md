## 建表语句
```sql
CREATE TABLE config (
  config_name VARCHAR(64),
  config_schema TEXT,
  version INT,
  primary key (config_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
```sql
CREATE TABLE config_notify (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `config_name` varchar(64) COLLATE utf8mb4_bin NOT NULL,
  `config_item_id` varchar(64) NOT NULL,
  `notify_time` timestamp NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
```
## 调试语句
```sql
INSERT INTO config_notify (config_name, config_item_id, notify_time) VALUES ("name", "id", now());
```