<?php
$API = new API();

try {
	switch($_GET['action']) {
	case 'get_locations':
		$result = $API->getLocations();
		break;
	case 'get_location':
		$result = $API->getUserLocation(intval($_GET['id_user']));
		break;
	case 'set_location':
		$result = $API->setUserLocation(array(
			':id_user' => intval($_GET['id_user']),
			':longitude' => doubleval($_GET['longitude']),
			':latitude' => doubleval($_GET['latitude'])
		));
		break;
	case 'authenticate':
		$result = $API->authenticate((string)$_GET['username'], (string)$_GET['password']);
		break;
	case 'create_user':
		$result = $API->createUser(array(
			':username' => $_GET['username'],
			':password' => $_GET['password'],
			':firstname' => $_GET['firstname'],
			':lastname' => $_GET['lastname']
		));
		break;
	case 'get_chats':
		$result = $API->getChats(intval($_GET['id_user']));
		break;
	case 'send_chat':
		$result = $API->sendChat(array(
			':id_user_from' => (int)$_GET['from'],
			':id_user_to' => (int)$_GET['to'],
			':message' => $_GET['message']
		));
		break;
	default:
		throw new Exception("No API action given");
		break;
	}

	echo json_encode(array('success' => $result));
} catch (Exception $ex) {
	echo json_encode(array('error' => $ex->getMessage()));
}


class API {
	var $db;
	var $queries = array(
		'location' => 'SELECT `locations`.`id_user`,`time`,`latitude`,`longitude`,`username`,`firstname`,`lastname` FROM `locations` LEFT OUTER JOIN `users` ON `locations`.`id_user`=`users`.`id_user`',
		'updateloc' => 'INSERT INTO `locations` (`id_user`, `time`,`longitude`,`latitude`) VALUES (:id_user, NOW(), :longitude, :latitude) ON DUPLICATE KEY UPDATE `time`=NOW(),`longitude`=VALUES(longitude),`latitude`=VALUES(latitude)',
		'authenticate' => 'SELECT `id_user` FROM `users` WHERE `username`=:username AND `password`=:password LIMIT 1',
		'createUser' => 'INSERT INTO `users` (`username`, `password`, `firstname`, `lastname`) VALUES (:username, :password, :firstname, :lastname)',
		'getchats' => 'SELECT `message`, `sent`, `id_user_from`, `username`, `firstname`, `lastname` FROM `chats` LEFT OUTER JOIN `users` ON `chats`.`id_user_from`=`users`.`id_user` WHERE `id_user_to` = :id_user ORDER BY sent DESC',
		'sendchat' => 'INSERT INTO `chats` (`id_user_from`, `id_user_to`, `message`, `sent`) VALUES (:id_user_from, :id_user_to, :message, NOW())'
	);

	function __construct() {
		$this->db = new PDO('mysql:host=localhost;dbname=cmsc_628;charset=utf8', 'awesome', 'awesome');
	}

	function createUser($vals) {
		try {
			$query = $this->db->prepare($this->queries['createUser']);
			$result = $query->execute($vals);
			if($result == TRUE) {
				return $this->db->lastInsertId();
			} else {
				throw new Exception("Failed to create user");
			}
		} catch (PDOException $ex) {
			throw new Exception($ex->getMessage());
		}
	}

	function authenticate($username, $password) {
		try {
			$query = $this->db->prepare($this->queries['authenticate']);
			$result = $query->execute(array(':username' => $username, ':password' => $password));
			if($query->rowCount() > 0) {
				return $query->fetch(PDO::FETCH_OBJ)->id_user;
			} else {
				throw new Exception('Invalid username / password combination');
			}
		} catch(PDOException $ex) {
			throw new Exception($ex->getMessage());
		}
	}

	function getLocations() {
		try {
			$query = $this->db->query($this->queries['location']);
			$results = $query->fetchAll(PDO::FETCH_ASSOC);
			return $results;
		} catch(PDOException $ex) {
			throw new Exception($ex->getMessage());
		}
	}

	function getUserLocation($id_user) {
		try {
			$query = $this->db->prepare($this->queries['location'].' WHERE `locations`.`id_user`=:id_user');
			$query->bindValue(':id_user', $id_user, PDO::PARAM_INT);
			$query->execute();
			if($query->rowCount() > 0) {
				$results = $query->fetch(PDO::FETCH_ASSOC);
				return $results;
			} else {
				throw new Exception('No users with that id found');
			}
		} catch(PDOException $ex) {
			throw new Exception($ex->getMessage());
		}
	}

	function setUserLocation($vals) {
		try {
			$query = $this->db->prepare($this->queries['updateloc']);
			$query->execute($vals);
			return $this->getLocations();
		} catch(PDOException $ex) {
			throw new Exception($ex->getMessage());
		}
	}

	function getChats($id_user) {
		try {
			$query = $this->db->prepare($this->queries['getchats']);
			$query->bindValue(':id_user', $id_user, PDO::PARAM_INT);
			$query->execute();
			if($query->rowCount() > 0) {
				$results = $query->fetchAll(PDO::FETCH_ASSOC);
				return $results;
			} else {
				throw new Exception("No chats found for that user");
			}
		} catch(PDOException $ex) {
			throw new Exception($ex->getMessage());
		}
	}

	function sendChat($vals) {
		try {
			if(strlen(trim($vals[':message'])) == 0) throw new Exception('No message sent');

			$query = $this->db->prepare($this->queries['sendchat']);
			$query->execute($vals);
			return null;
		} catch(PDOException $ex) {
			throw new Exception($ex->getMessage());
		}
	}
}
?>
