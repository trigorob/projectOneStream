<?php
//Generate Tables using predefined data.
$GLOBALS['SERVERNAME'] = "104.155.180.191";
$GLOBALS['USERNAME'] = "root";
$GLOBALS['PASSWORD'] = NULL;
$GLOBALS['DBNAME'] = "onestream";

$GLOBALS['TABLES'] = array("CREATE TABLE Playlist2 (
	ListPosition int,
	SongName varchar(30),
	SongUri varchar(30),
	PRIMARY KEY (SongUri)
);");

function generateTables() {

// Create connection
$conn = new mysqli($GLOBALS['SERVERNAME'], $GLOBALS['USERNAME'],
$GLOBALS['PASSWORD'], $GLOBALS['DBNAME']);
 
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "CREATE DATABASE IF NOT EXISTS " . $GLOBALS['DBNAME'];

	if ($conn->query($sql) === TRUE) {
		echo "Database created successfully";
	} else {
		echo "Error: " . $sql . "\n" . $conn->error . "\n";
	}
$conn->close();

$i = 0;

$conn = new mysqli($GLOBALS['SERVERNAME'], $GLOBALS['USERNAME'],
 $GLOBALS['PASSWORD'],$GLOBALS['DBNAME']);
 
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

while ($i < count($GLOBALS['TABLES']))
{
	$sql = $GLOBALS['TABLES'][$i];

	if ($conn->query($sql) === TRUE) {
		echo "New table created successfully";
	} else {
		echo "Error: " . $sql . "\n" . $conn->error . "\n";
	}
	$i++;
}



$conn->close();

}
generateTables();

?>