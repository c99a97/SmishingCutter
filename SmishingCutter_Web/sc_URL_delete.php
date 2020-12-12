<?php
    require_once "sc_header.php";
    require_once "sc_DB_conn.php";
?>
<?php
    $_GET['URL_no'] = mysqli_real_escape_string($db_conn, $_GET['URL_no']);
    $db_res = mysqli_query($db_conn, "delete from SC_URL where URL_no=".$_GET['URL_no']);
    $db_row = mysqli_fetch_row($db_res);
    echo "<script>location.href='./sc_index.php'</script>";
?>
<?php
    require_once "sc_footer.php";
?>
