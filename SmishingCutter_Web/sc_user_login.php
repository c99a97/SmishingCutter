<?php
    require_once "sc_header.php";
    require_once "sc_DB_conn.php";
?>
<?php
    if(isset($_SESSION['userID'])){
        echo "<script>location.href='./sc_index.php';</script>";
    } else if(isset($_POST['user_id']) && isset($_POST['user_pw'])){
    	// SQL Injection 방지 = mysqli_real_escape_string
        $userID = mysqli_real_escape_string($db_conn, $_POST['user_id']);
    	$userPW = mysqli_real_escape_string($db_conn, $_POST['user_pw']);
        // 계정 잠김 여부
        $db_res = mysqli_query($db_conn, "select TIMESTAMPDIFF(minute, (select login_lock_date from SC_USER where user_id='$userID'), NOW()) AS time_diff");
    	$db_row = mysqli_fetch_row($db_res);
        if($db_row[0]!=null && $db_row[0]<5){
            echo "<script>alert('현재 잠긴 계정입니다. 잠금 해제까지 ".(5-$db_row[0])."분 남았습니다.')</script>";
        } else{
            // 계정 로그인
            $db_res = mysqli_query($db_conn, "select user_name, user_position, login_fail from SC_USER where user_id='$userID' and user_pw=SHA2('$userPW', 256)");
        	$db_row = mysqli_fetch_row($db_res);
        	if(isset($db_row[0])){
                $_SESSION[userID] = $userID;
        		$_SESSION[userName] = $db_row[0];
                $_SESSION[userPosition] = $db_row[1];
                // 실패 횟수 초기화
                $db_res = mysqli_query($db_conn, "update SC_USER set login_fail=0, login_lock_date=NULL where user_id='$userID'");
                echo "<script>location.href='./sc_index.php';</script>";
        	}else{
                // 비밀번호가 틀렸다면 존재하는 계정인가?
                echo "<script>alert('존재하지 않은 계정이거나 잘못된 비밀번호를 입력하셨습니다.')</script>";
                $db_res = mysqli_query($db_conn,"select login_fail from SC_USER where user_id='$userID'");
                $db_row = mysqli_fetch_row($db_res);
                if(isset($db_row[0])){
                    if($db_row[0]==4){
                        $db_res = mysqli_query($db_conn,"update SC_USER set login_fail=0, login_lock_date=now() where user_id='$userID'");
                    }else{
                        $db_res = mysqli_query($db_conn,"update SC_USER set login_fail=login_fail+1 where user_id='$userID'");
                    }
                }
            }
        }
    }
?>
    <table style='margin: auto;'>
    <form name='table_login' class='formNoLine' method='POST' action='sc_user_login.php'>
    <tr height='35'><td class='thGrayC' width='400' colspan='2' style='letter-spacing: 3px;'><b>스미싱커터 로그인</b></td></tr>
    <tr height='30'>
        <td class='tdGrayCH' width='100'><b>아이디</b></td>
        <td class='tdCenter' width='310'><input type='text' id='user_id' name='user_id' class='inputNanum' style='width:300px' placeholder='아이디' <?php echo "value=".$_POST['user_id'];?>></td>
    </tr>
    <tr height='30'>
        <td class='tdGrayCH' width='100'><b>비밀번호</b></td>
        <td class='tdCenter' width='310'><input type='password' id='user_pw' name='user_pw' class='inputNanum' style='width:300px' placeholder='비밀번호' pattern='^([a-z0-9!@#$%^*_]).{2,20}$'></td>
    </tr>
    <tr>
        <td class='tdRightU' colspan='2' style="padding: 3px 3px 5px 3px;"><input type='submit' id='login' class='butWhiteH' style='width:60px' value='로그인' disabled></td>
    </tr>
    </form>
    </table>
<script>
    // 아이디, 비밀번호 입력시에만 전송
    $('input').keyup(function(){
        var objUserID = $('input#user_id').val();
        var objUserPW = $('input#user_pw').val();

        if(objUserID!="" && objUserPW!=""){
            $('input#login').attr('disabled', false);
        }else{
            $('input#login').attr('disabled', true);
        }
    });
</script>
<?php
	mysqli_close($db_conn);
    require_once "sc_footer.php";
?>
