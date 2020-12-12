<?php
	require_once "sc_header.php";
    require_once "sc_DB_conn.php";
	require_once "sc_menubar.php";
?>
        </td>
    </tr>
    <tr><td class='thGrayC' colspan='7' style='letter-spacing: 8px;'>
        <b>URL 추가</b>
    </td></tr>
    <tr><td colspan='7'>
<?php
    $_POST['add_block'] = mysqli_real_escape_string($db_conn, $_POST['add_block']);
    $_POST['add_URL'] = mysqli_real_escape_string($db_conn, $_POST['add_URL']);
    // 이미 존재하는 URL인지 확인
    $db_res = mysqli_query($db_conn, "select URL_no from SC_URL where URL='".$_POST['add_URL']."'");
    $db_row = mysqli_fetch_row($db_res);
    if(isset($db_row)){
        echo "<b><p style='color:red;'>이미 존재하는 URL입니다.</p></b>";
        echo "잠시 후, 목록으로 이동됩니다.";
    } else{
        $db_res = mysqli_query($db_conn, "insert into SC_URL(URL, file_name, receiver_number, sender_number, is_block, report_num) values('".$_POST['add_URL']."','add_by_administrator','".$_SESSION['userID']."','-',".$_POST['add_block'].", 0)");
        $db_row = mysqli_fetch_row($db_res);
        echo "<b><p style='color:blue;'>URL 추가에 성공했습니다.</p></b>";
        echo "잠시 후, 목록으로 이동됩니다.";
    }
    echo "<script>setTimeout('location.href=\"./sc_index.php\"',2000)</script>";
?>
    </td></tr>
	</table>
<?php
    require_once "sc_footer.php";
?>
