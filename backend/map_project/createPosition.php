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
    echo json_encode(["success" => false, "message" => "Erreur de connexion: " . $e->getMessage()]);
    exit;
}

$latitude = isset($_POST['latitude']) ? $_POST['latitude'] : null;
$longitude = isset($_POST['longitude']) ? $_POST['longitude'] : null;
$date = isset($_POST['date_position']) ? $_POST['date_position'] : (isset($_POST['date']) ? $_POST['date'] : null);
$imei = isset($_POST['imei']) ? $_POST['imei'] : null;

if ($latitude === null || $longitude === null || $date === null || $imei === null) {
    echo json_encode(["success" => false, "message" => "Donnees manquantes"]);
    exit;
}

if (!is_numeric($latitude) || !is_numeric($longitude)) {
    echo json_encode(["success" => false, "message" => "Coordonnees invalides"]);
    exit;
}

try {
    $stmt = $conn->prepare(
        "INSERT INTO position (latitude, longitude, date_position, imei)
         VALUES (:latitude, :longitude, :date_position, :imei)"
    );
    $stmt->bindParam(':latitude', $latitude);
    $stmt->bindParam(':longitude', $longitude);
    $stmt->bindParam(':date_position', $date);
    $stmt->bindParam(':imei', $imei);
    $stmt->execute();

    echo json_encode(["success" => true, "message" => "Position enregistree avec succes"]);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Erreur: " . $e->getMessage()]);
}
?>
