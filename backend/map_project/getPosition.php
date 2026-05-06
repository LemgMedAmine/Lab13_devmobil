<?php
header('Content-Type: application/json; charset=utf-8');

$host = "localhost";
$db_name = "localisation";
$username = "root";
$password = "";

try {
    $conn = new PDO("mysql:host=$host;dbname=$db_name;charset=utf8", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "positions" => [], "message" => "Erreur de connexion: " . $e->getMessage()]);
    exit;
}

try {
    $stmt = $conn->query(
        "SELECT id, latitude, longitude, date_position AS date, imei
         FROM position
         ORDER BY date_position DESC, id DESC
         LIMIT 100"
    );
    $positions = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode(["success" => true, "positions" => $positions]);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "positions" => [], "message" => "Erreur: " . $e->getMessage()]);
}
?>
